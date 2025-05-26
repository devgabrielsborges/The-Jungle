package com.ranieborges.thejungle.cli.controller;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.world.Ambient;
import com.ranieborges.thejungle.cli.model.world.ambients.*;
import com.ranieborges.thejungle.cli.view.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class AmbientController {
    // Setter for playerCharacter if GameState needs to set it during load before reinitialization
    // Getter for playerCharacter, useful for reinitialization logic if needed by GameState or Main
    @Setter
    @Getter
    private Character playerCharacter;
    private Map<String, Ambient> worldMap;
    private Ambient currentAmbient;

    // Transient fields: these will not be saved by GSON and must be re-initialized after loading
    private transient Scanner scanner;
    private transient Random random;

    public AmbientController(Character playerCharacter, Scanner scanner, Random randomInstance) {
        this.playerCharacter = playerCharacter;
        this.scanner = scanner;
        this.random = randomInstance;
        this.worldMap = new HashMap<>();
        initializeWorldMap();

        if (this.playerCharacter != null && this.playerCharacter.getCurrentAmbient() == null) {
            this.currentAmbient = worldMap.get("Jungle");
            if (this.currentAmbient == null && !worldMap.isEmpty()) { // Fallback if "Jungle" isn't the exact key
                this.currentAmbient = worldMap.values().iterator().next();
            } else if (this.currentAmbient == null) { // If worldMap is also empty
                Message.displayOnScreen("Warning: World map is empty. Creating a default Jungle ambient.");
                this.currentAmbient = new Jungle();
                this.worldMap.put(this.currentAmbient.getName(), this.currentAmbient);
            }
            this.playerCharacter.setCurrentAmbient(this.currentAmbient);
        } else if (this.playerCharacter != null) {
            this.currentAmbient = worldMap.get(playerCharacter.getCurrentAmbient().getName());
            if (this.currentAmbient == null) { // Should not happen if save data is consistent
                Message.displayOnScreen("Warning: Loaded character's current ambient not found in worldMap. Re-assigning.");
                this.currentAmbient = playerCharacter.getCurrentAmbient(); // Use the character's loaded ambient
                if (this.currentAmbient != null) { // If it's not null, add it to the map
                    this.worldMap.put(this.currentAmbient.getName(), this.currentAmbient);
                } else { // This would be a critical load error
                    Message.displayOnScreen("Error: Player's loaded ambient is null. Defaulting to Jungle.");
                    this.currentAmbient = new Jungle();
                    this.worldMap.put(this.currentAmbient.getName(), this.currentAmbient);
                    this.playerCharacter.setCurrentAmbient(this.currentAmbient);
                }
            }
        } else if (worldMap.isEmpty()) {
            // Both player and worldMap are null, initialize minimally.
            initializeWorldMap();
            this.currentAmbient = worldMap.getOrDefault("Jungle", new Jungle());
            if (!worldMap.containsKey(this.currentAmbient.getName())) {
                worldMap.put(this.currentAmbient.getName(), this.currentAmbient);
            }
        }
    }

    /**
     * Initializes or re-initializes the world map with standard ambients.
     */
    private void initializeWorldMap() {
        if (this.worldMap == null) {
            this.worldMap = new HashMap<>();
        } else {
            // If re-initializing, decide if you want to clear or add to existing
            // For a save/load system, GSON should populate worldMap.
            // This method is more for initial setup of a new game.
            // If called after load and worldMap is already populated by GSON, this might overwrite.
            // Let's assume if worldMap is populated, we don't need to re-init fully.
            if (!this.worldMap.isEmpty() && this.worldMap.values().stream().anyMatch(Jungle.class::isInstance)) {
                // Potentially already loaded, skip full re-init to preserve loaded state
                return;
            }
            this.worldMap.clear(); // Clear for a fresh setup
        }

        Ambient jungle = new Jungle();
        Ambient mountain = new Mountain();
        Ambient cave = new Cave();
        Ambient lakeRiver = new LakeRiver();
        Ambient ruins = new Ruins();

        worldMap.put(jungle.getName(), jungle);
        worldMap.put(mountain.getName(), mountain);
        worldMap.put(cave.getName(), cave);
        worldMap.put(lakeRiver.getName(), lakeRiver);
        worldMap.put(ruins.getName(), ruins);
    }

    public Ambient getCurrentAmbient() {
        // Sync currentAmbient with playerCharacter's ambient after potential deserialization
        if (playerCharacter != null && playerCharacter.getCurrentAmbient() != null) {
            // Ensure this.currentAmbient refers to the instance from worldMap
            Ambient characterAmbient = playerCharacter.getCurrentAmbient();
            String ambientName = characterAmbient.getName();
            if (worldMap.containsKey(ambientName)) {
                this.currentAmbient = worldMap.get(ambientName);
                // Ensure player's reference is also to the map's instance if different
                if (playerCharacter.getCurrentAmbient() != this.currentAmbient) {
                    playerCharacter.setCurrentAmbient(this.currentAmbient);
                }
            } else {
                // If not in map, it means the loaded ambient instance for player isn't in controller's map.
                // This might happen if worldMap itself was not fully restored or got re-initialized.
                // Add it to the map and set currentAmbient.
                Message.displayOnScreen("Warning: Player's current ambient '"+ambientName+"' not in worldMap. Adding it.");
                worldMap.put(ambientName, characterAmbient);
                this.currentAmbient = characterAmbient;
            }
        } else if (this.currentAmbient == null && worldMap != null && !worldMap.isEmpty()) {
            // If currentAmbient is null but worldMap is not, pick a default
            this.currentAmbient = worldMap.getOrDefault("Jungle", worldMap.values().iterator().next());
            if (playerCharacter != null) {
                playerCharacter.setCurrentAmbient(this.currentAmbient);
            }
        } else if (this.currentAmbient == null) {
            Message.displayOnScreen("Critical: Current ambient is null and cannot be determined. Defaulting to a new Jungle.");
            this.currentAmbient = new Jungle();
            if(worldMap != null && !worldMap.containsKey(this.currentAmbient.getName())) {
                worldMap.put(this.currentAmbient.getName(), this.currentAmbient);
            }
            if(playerCharacter != null) {
                playerCharacter.setCurrentAmbient(this.currentAmbient);
            }
        }
        return this.currentAmbient;
    }


    public void moveToAmbient(String newAmbientKey) {
        Ambient targetAmbient = worldMap.get(newAmbientKey);
        if (targetAmbient != null) {
            Ambient oldAmbient = getCurrentAmbient(); // Use getter to ensure it's synced
            Message.displayOnScreen(playerCharacter.getName() + " travels from " + (oldAmbient != null ? oldAmbient.getName() : "an unknown place") + " to " + targetAmbient.getName() + "...");
            this.currentAmbient = targetAmbient;
            playerCharacter.setCurrentAmbient(targetAmbient);
            playerCharacter.changeEnergy(-15);
            Message.displayOnScreen("You have arrived at " + targetAmbient.getName() + ".");
        } else {
            Message.displayOnScreen("Cannot move to '" + newAmbientKey + "'. Location unknown or inaccessible from here.");
        }
    }

    public void offerMovementChoice() {
        if (scanner == null) {
            Message.displayOnScreen("Error: Scanner not initialized in AmbientController. Cannot offer movement.");
            return;
        }
        Ambient current = getCurrentAmbient(); // Use getter
        if (current == null) {
            Message.displayOnScreen("Error: Current ambient is unknown. Cannot determine movement options.");
            return;
        }

        Message.displayOnScreen("\nWhere would you like to go?");
        int i = 1;
        Map<Integer, String> movementOptions = new HashMap<>();
        if (worldMap != null) {
            for (String ambientName : worldMap.keySet()) {
                if (!ambientName.equals(current.getName())) {
                    Message.displayOnScreen(i + ". Go to " + ambientName);
                    movementOptions.put(i, ambientName);
                    i++;
                }
            }
        }
        Message.displayOnScreen("0. Stay here");

        Message.displayOnScreen("Enter choice: ");
        String choiceStr = scanner.nextLine().trim();
        try {
            int choice = Integer.parseInt(choiceStr);
            if (choice == 0) {
                Message.displayOnScreen(playerCharacter.getName() + " decides to stay in the " + current.getName() + ".");
            } else if (movementOptions.containsKey(choice)) {
                moveToAmbient(movementOptions.get(choice));
            } else {
                Message.displayOnScreen("Invalid movement choice.");
            }
        } catch (NumberFormatException e) {
            Message.displayOnScreen("Invalid input for movement choice.");
        }
    }

    public void updateAmbientResources() {
        Ambient current = getCurrentAmbient(); // Use getter
        if (current != null) {
            current.updateResources();
        }
    }

    public void updateGlobalWeather() {
        if (this.random == null) {
            Message.displayOnScreen("Warning: Random not initialized in AmbientController. Skipping global weather update.");
            return;
        }
        if (worldMap == null || worldMap.isEmpty()) {
            Message.displayOnScreen("Warning: World map not initialized. Skipping global weather update.");
            return;
        }
        Message.displayOnScreen("The global weather patterns are shifting... (Conceptual)");
        for (Ambient ambient : worldMap.values()) {
            if (this.random.nextBoolean()) {
                Message.displayOnScreen(ambient.changeWeather());
            }
        }
    }

    public void reinitializeTransientFields(Scanner scanner, Random random) {
        this.scanner = scanner;
        this.random = random;

        if (this.worldMap == null || this.worldMap.isEmpty()) {
            Message.displayOnScreen("AmbientController: worldMap is null or empty after load. Re-initializing.");
            initializeWorldMap();
        }

        // Sync currentAmbient and playerCharacter's currentAmbient with instances from the (potentially re-initialized) worldMap.
        if (this.playerCharacter != null && this.playerCharacter.getCurrentAmbient() != null) {
            String loadedPlayerAmbientName = this.playerCharacter.getCurrentAmbient().getName();
            Ambient mapInstance = this.worldMap.get(loadedPlayerAmbientName);
            if (mapInstance != null) {
                this.currentAmbient = mapInstance;
                this.playerCharacter.setCurrentAmbient(mapInstance); // Ensure player also refers to the map instance
            } else {
                // This means the player's loaded ambient doesn't exist in our re-initialized map
                // Add it, or default player to a known ambient.
                Message.displayOnScreen("Warning: Player's loaded ambient '" + loadedPlayerAmbientName + "' not found in re-initialized worldMap. Adding it or defaulting.");
                this.worldMap.put(loadedPlayerAmbientName, this.playerCharacter.getCurrentAmbient());
                this.currentAmbient = this.playerCharacter.getCurrentAmbient();
            }
        }

        if (this.currentAmbient == null && this.worldMap != null && !this.worldMap.isEmpty()) {
            this.currentAmbient = this.worldMap.getOrDefault("Jungle", this.worldMap.values().iterator().next());
            if (this.playerCharacter != null) {
                this.playerCharacter.setCurrentAmbient(this.currentAmbient);
            }
        } else if (this.currentAmbient == null) {
            Message.displayOnScreen("Critical error: AmbientController.currentAmbient could not be re-initialized. Defaulting to a new Jungle.");
            this.currentAmbient = new Jungle();
            if(this.worldMap != null && !this.worldMap.containsKey(this.currentAmbient.getName())) {
                this.worldMap.put(this.currentAmbient.getName(), this.currentAmbient);
            }
            if(this.playerCharacter != null) this.playerCharacter.setCurrentAmbient(this.currentAmbient);
        }
    }

}
