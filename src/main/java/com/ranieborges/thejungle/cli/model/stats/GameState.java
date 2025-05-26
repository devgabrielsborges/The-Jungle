package com.ranieborges.thejungle.cli.model.stats;

import com.ranieborges.thejungle.cli.controller.AmbientController;
import com.ranieborges.thejungle.cli.controller.EventManager;
import com.ranieborges.thejungle.cli.model.entity.Character;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the state of the game that can be saved and loaded.
 * This class acts as a container for all essential game data.
 */
@Getter
@Setter
public class GameState {
    private Character playerCharacter;
    private int turnCounter;
    private AmbientController ambientController; // Contains current ambient and world map
    private EventManager eventManager;           // Contains available events

    // Add any other relevant game state variables here.
    // For example:
    // private Map<String, Boolean> completedQuests;
    // private List<ActiveEvent> activeGlobalEvents;

    /**
     * Default constructor required by GSON for deserialization.
     */
    public GameState() {
        // GSON needs a no-arg constructor
    }

    /**
     * Constructs a GameState object with all necessary components.
     * @param playerCharacter The current player character.
     * @param turnCounter The current turn number.
     * @param ambientController The controller managing ambients.
     * @param eventManager The controller managing events.
     */
    public GameState(Character playerCharacter, int turnCounter, AmbientController ambientController, EventManager eventManager) {
        this.playerCharacter = playerCharacter;
        this.turnCounter = turnCounter;
        this.ambientController = ambientController;
        this.eventManager = eventManager;
    }
}
