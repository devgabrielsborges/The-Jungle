package com.ranieborges.thejungle.cli.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ranieborges.thejungle.cli.gsonextras.RuntimeTypeAdapterFactory;
import com.ranieborges.thejungle.cli.model.Event;
import com.ranieborges.thejungle.cli.model.Faction;
import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Creature;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.characters.*;
import com.ranieborges.thejungle.cli.model.entity.creatures.*;
import com.ranieborges.thejungle.cli.model.entity.itens.*;
import com.ranieborges.thejungle.cli.model.events.*;

import com.ranieborges.thejungle.cli.model.factions.BrutalHunters;
import com.ranieborges.thejungle.cli.model.factions.DesperateSurvivors;
import com.ranieborges.thejungle.cli.model.factions.PeacefulNomads;
import com.ranieborges.thejungle.cli.model.factions.ResourceMerchants;
import com.ranieborges.thejungle.cli.model.stats.GameState;
import com.ranieborges.thejungle.cli.model.world.Ambient;
import com.ranieborges.thejungle.cli.model.world.ambients.*;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Paths;


public class SaveLoadService {

    public static final String SAVE_FILE_DIRECTORY = "saves/";
    public static final String SAVE_FILE_EXTENSION = ".json";
    public static final String AUTOSAVE_FILENAME = "autosave";

    private final Gson gson;

    public SaveLoadService() {
        RuntimeTypeAdapterFactory<Item> itemAdapterFactory = RuntimeTypeAdapterFactory
            .of(Item.class, "itemType")
            .registerSubtype(Food.class)
            .registerSubtype(Drinkable.class)
            .registerSubtype(Material.class)
            .registerSubtype(Medicine.class)
            .registerSubtype(Tool.class)
            .registerSubtype(Weapon.class)
            .registerSubtype(Ammunition.class);

        RuntimeTypeAdapterFactory<Event> eventAdapterFactory = RuntimeTypeAdapterFactory
            .of(Event.class, "eventType")
            .registerSubtype(ClimaticEvent.class)
            .registerSubtype(CreatureEncounterEvent.class)
            .registerSubtype(DiscoveryEvent.class)
            .registerSubtype(HealthEvent.class)
            .registerSubtype(FactionInteractionEvent.class);

        RuntimeTypeAdapterFactory<Ambient> ambientAdapterFactory = RuntimeTypeAdapterFactory
            .of(Ambient.class, "ambientType")
            .registerSubtype(Jungle.class)
            .registerSubtype(Mountain.class)
            .registerSubtype(Cave.class)
            .registerSubtype(LakeRiver.class)
            .registerSubtype(Ruins.class);

        RuntimeTypeAdapterFactory<Character> characterAdapterFactory = RuntimeTypeAdapterFactory
            .of(Character.class, "characterClass")
            .registerSubtype(Doctor.class)
            .registerSubtype(Hunter.class)
            .registerSubtype(Lumberjack.class)
            .registerSubtype(Survivor.class);

        RuntimeTypeAdapterFactory<Creature> creatureAdapterFactory = RuntimeTypeAdapterFactory
            .of(Creature.class, "creatureType")
            .registerSubtype(Bear.class)
            .registerSubtype(Deer.class)
            .registerSubtype(Fish.class)
            .registerSubtype(Wolf.class);

        RuntimeTypeAdapterFactory<Faction> factionAdapterFactory = RuntimeTypeAdapterFactory
            .of(Faction.class, "factionType")
            .registerSubtype(PeacefulNomads.class)
            .registerSubtype(ResourceMerchants.class)
            .registerSubtype(BrutalHunters.class)
            .registerSubtype(DesperateSurvivors.class);

        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
            .registerTypeAdapterFactory(itemAdapterFactory)
            .registerTypeAdapterFactory(eventAdapterFactory)
            .registerTypeAdapterFactory(ambientAdapterFactory)
            .registerTypeAdapterFactory(characterAdapterFactory)
            .registerTypeAdapterFactory(creatureAdapterFactory)
            .registerTypeAdapterFactory(factionAdapterFactory)
            .registerTypeAdapter(Class.class, new ClassTypeAdapter())
            .enableComplexMapKeySerialization()
            .create();

        File savesDir = new File(SAVE_FILE_DIRECTORY);
        if (!savesDir.exists() && !savesDir.mkdirs()) {
                System.err.println("Warning: Could not create saves directory at " + SAVE_FILE_DIRECTORY);
            }

    }

    public void saveGame(GameState gameState, String saveName) {
        if (saveName == null || saveName.trim().isEmpty()) {
            System.out.println("Save name cannot be empty.");
            return;
        }
        String sanitizedSaveName = saveName.replaceAll("[^a-zA-Z0-9_.-]", "_");
        String filePath = SAVE_FILE_DIRECTORY + sanitizedSaveName + SAVE_FILE_EXTENSION;

        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(gameState, writer);
            if (!saveName.equals(AUTOSAVE_FILENAME)) {
                System.out.println("Game saved successfully as " + filePath);
            }
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while saving game to " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public GameState loadGame(String saveName) {
        if (saveName == null || saveName.trim().isEmpty()) {
            System.out.println("Save name cannot be empty for loading.");
            return null;
        }
        String sanitizedSaveName = saveName.replaceAll("[^a-zA-Z0-9_.-]", "_");
        String filePath = SAVE_FILE_DIRECTORY + sanitizedSaveName + SAVE_FILE_EXTENSION;
        File saveFile = new File(filePath);

        if (!saveFile.exists()) {
            System.out.println("Save file not found: " + filePath);
            return null;
        }

        try (FileReader reader = new FileReader(filePath)) {
            GameState gameState = gson.fromJson(reader, GameState.class);
            System.out.println("Game loaded successfully from " + filePath);
            return gameState;
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while loading game from " + filePath + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public boolean autoSaveExists() {
        File autosaveFile = new File(SAVE_FILE_DIRECTORY + AUTOSAVE_FILENAME + SAVE_FILE_EXTENSION);
        return autosaveFile.exists();
    }

    public void deleteSaveGame(String saveName) {
        if (saveName == null || saveName.trim().isEmpty()) {
            System.out.println("Save name cannot be empty for deletion.");
            return;
        }
        String sanitizedSaveName = saveName.replaceAll("[^a-zA-Z0-9_.-]", "_");
        String filePath = SAVE_FILE_DIRECTORY + sanitizedSaveName + SAVE_FILE_EXTENSION;
        try {
            if (Files.deleteIfExists(Paths.get(filePath))) {
                System.out.println("Save file deleted: " + filePath);
            }
        } catch (IOException e) {
            System.err.println("Error deleting save file " + filePath + ": " + e.getMessage());
        }
    }

}
