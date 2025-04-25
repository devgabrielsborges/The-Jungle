package io.github.com.ranie_borges.thejungle.controller.systems;

import io.github.com.ranie_borges.thejungle.controller.AmbientController;
import io.github.com.ranie_borges.thejungle.controller.EventController;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.events.Event;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Main controller for The Jungle game
 * Coordinates between other controllers and manages the overall game state
 */
public class GameController<C extends Character, A extends Ambient> {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    private final GameState gameState;
    private final EventController eventController;
    private final AmbientController<A, C> ambientController;
    private final SaveManager saveManager;

    private boolean gameInitialized = false;

    public GameController() {
        this.gameState = new GameState();
        this.saveManager = new SaveManager();
        this.eventController = new EventController(gameState);
        this.ambientController = new AmbientController<>(gameState, eventController);

        gameState.setEventController(eventController);
        gameState.setAmbientController(ambientController);
    }

    /**
     * Initializes a new game with the player character
     *
     * @param playerCharacter The character controlled by the player
     * @param startingAmbient The ambient where the game starts
     * @return true if successful, false otherwise
     */
    public boolean initializeNewGame(C playerCharacter, A startingAmbient) {
        if (playerCharacter == null || startingAmbient == null) {
            // Implementation details...
            return false;
        }

        try {
            // Implementation details...
            return true;
        } catch (Exception e) {
            // Error handling...
            return false;
        }
    }

    /**
     * Makes character move to a different ambient
     *
     * @param targetAmbient The ambient to move to
     * @return true if movement successful, false otherwise
     */
    public boolean moveToAmbient(Ambient targetAmbient) {
        if (!gameInitialized || targetAmbient == null) {
            // Implementation details...
            return false;
        }

        Ambient currentAmbient = gameState.getCurrentAmbient();
        logger.info("Moving from {} to {}",
            currentAmbient.getName(), targetAmbient.getName());

        gameState.setCurrentAmbient(targetAmbient);
        return true;
    }

    /**
     * Collects a resource item from the current ambient
     *
     * @param item The resource to collect
     * @return true if the resource was successfully collected, false otherwise
     */
    public boolean collectResource(Item item) {
        if (!gameInitialized || item == null) {
            // Implementation details...
            return false;
        }

        Ambient currentAmbient = gameState.getCurrentAmbient();
        if (currentAmbient == null) {
            // Implementation details...
            return false;
        }

        boolean removed = ambientController.removeResource(currentAmbient, item);
        if (removed) {
            // Implementation details...
            return true;
        }

        return false;
    }

    /**
     * Saves the current game state
     *
     * @param saveName Name to identify the save
     * @return true if save successful, false otherwise
     */
    public boolean saveGame(String saveName) {
        if (!gameInitialized) {
            // Implementation details...
            return false;
        }

        return saveManager.saveGame(gameState, saveName);
    }

    /**
     * Loads a saved game
     *
     * @param saveName Name of the save to load
     * @return true if load successful, false otherwise
     */
    public boolean loadGame(String saveName) {
        GameState loadedState = saveManager.loadGame(saveName);
        if (loadedState != null) {
            // Implementation details...
            return true;
        }

        logger.error("Failed to load game: {}", saveName);
        return false;
    }

    /**
     * Adds a new event to the possible events list
     *
     * @param event The event to add
     */
    public void registerEvent(Event event) {
        if (event != null) {
            // Implementation details...
        }
    }

    /**
     * Gets the player character
     *
     * @return The player character
     */
    public Character getPlayerCharacter() {
        return gameState.getPlayerCharacter();
    }

    /**
     * Gets the current ambient
     *
     * @return The current ambient
     */
    public Ambient getCurrentAmbient() {
        return gameState.getCurrentAmbient();
    }

    /**
     * Gets the current game time
     *
     * @return The current in-game time
     */
    public OffsetDateTime getCurrentGameTime() {
        return gameState.getOffsetDateTime();
    }

    /**
     * Gets the days survived so far
     *
     * @return Number of days survived
     */
    public int getDaysSurvived() {
        return gameState.getDaysSurvived();
    }

    /**
     * Gets the current active events affecting the game
     *
     * @return List of active events
     */
    public List<Event> getActiveEvents() {
        return gameState.getActiveEvents();
    }

    /**
     * Gets the event history
     *
     * @return List of past events
     */
    public List<Event> getEventHistory() {
        return eventController.getEventHistory();
    }

    /**
     * Checks if the game is initialized and ready
     *
     * @return true if game is initialized, false otherwise
     */
    public boolean isGameInitialized() {
        return gameInitialized;
    }
}
