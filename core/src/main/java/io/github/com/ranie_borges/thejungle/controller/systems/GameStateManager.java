package io.github.com.ranie_borges.thejungle.controller.systems;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the game state and autosaving functionality
 */
public class GameStateManager {
    private static final Logger logger = LoggerFactory.getLogger(GameStateManager.class);

    private GameState gameState;
    private final SaveManager saveManager;
    private float autosaveTimer = 0f;

    /**
     * Create a new GameStateManager
     *
     * @param gameState The initial game state
     */
    public GameStateManager(GameState gameState) {
        this.gameState = gameState;
        this.saveManager = new SaveManager();
    }

    /**
     * Update the autosave timer and save if necessary
     *
     * @param delta     Time since last frame
     * @param character Current character
     * @param ambient   Current ambient
     * @param map       Current map
     */
    public void update(float delta, Character character, Ambient ambient, int[][] map) {
        autosaveTimer += delta;
        // Autosave every 60 seconds
        float AUTOSAVE_INTERVAL = 60f;
        if (autosaveTimer >= AUTOSAVE_INTERVAL) {
            autosaveTimer = 0;
            autosave(character, ambient, map);
        }
    }

    /**
     * Perform an autosave operation
     *
     * @param character Current character
     * @param ambient   Current ambient
     * @param map       Current map
     * @return true if the save was successful
     */
    public boolean autosave(Character character, Ambient ambient, int[][] map) {
        try {
            gameState.setCharacter(character);
            gameState.setCurrentAmbient(ambient);
            gameState.setCurrentMap(map);

            boolean success = saveManager.saveGame(gameState, "autosave");
            if (success) {
                logger.info("Game autosaved successfully");
            } else {
                logger.warn("Autosave failed");
            }
            return success;
        } catch (Exception e) {
            logger.error("Error during autosave: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Manual save with a custom save name
     *
     * @param character Current character
     * @param ambient   Current ambient
     * @param map       Current map
     * @param saveName  Name for the save file
     * @return true if the save was successful
     */
    public boolean save(Character character, Ambient ambient, int[][] map, String saveName) {
        try {
            gameState.setCharacter(character);
            gameState.setCurrentAmbient(ambient);
            gameState.setCurrentMap(map);

            boolean success = saveManager.saveGame(gameState, saveName);
            if (success) {
                logger.info("Game saved successfully as '{}'", saveName);
            } else {
                logger.warn("Save failed for '{}'", saveName);
            }
            return success;
        } catch (Exception e) {
            logger.error("Error during save: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get the current game state
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Set the current game state
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Reset the autosave timer
     */
    public void resetAutosaveTimer() {
        autosaveTimer = 0;
    }
}
