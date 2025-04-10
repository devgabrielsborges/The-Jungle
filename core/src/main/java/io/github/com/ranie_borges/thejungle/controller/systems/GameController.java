package io.github.com.ranie_borges.thejungle.controller.systems;

import io.github.com.ranie_borges.thejungle.controller.AmbientController;
import io.github.com.ranie_borges.thejungle.controller.EventController;
import io.github.com.ranie_borges.thejungle.controller.TurnController;
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
public class GameController<C extends Character<?>, A extends Ambient> {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    private final GameState<C, A> gameState;
    private final EventController<C> eventController;
    private final TurnController<C, A> turnController;
    private final AmbientController<A, C> ambientController;
    private final SaveManager saveManager;

    private boolean gameInitialized = false;

    public GameController() {
        this.gameState = new GameState<>();
        this.saveManager = new SaveManager();
        this.eventController = new EventController<>(gameState);
        this.ambientController = new AmbientController<>(gameState, eventController);
        this.turnController = new TurnController<>(gameState, eventController, saveManager);

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
            logger.error("Cannot initialize game: Player character or starting ambient is null");
            return false;
        }

        try {
            gameState.setPlayerCharacter(playerCharacter);
            gameState.setCurrentAmbient(startingAmbient);
            gameState.setOffsetDateTime(OffsetDateTime.now());
            gameState.setDaysSurvived(0);

            logger.info("Game initialized successfully with character: {}", playerCharacter.getName());
            gameInitialized = true;
            return true;
        } catch (Exception e) {
            logger.error("Error initializing game", e);
            return false;
        }
    }

    /**
     * Executes a player action as part of a turn
     *
     * @param action The action the player wants to perform
     * @return Summary of the turn's effects
     */
    public TurnController<C, A>.TurnSummary executePlayerAction(TurnController.PlayerAction<C> action) {
        if (!gameInitialized) {
            logger.error("Cannot execute action: Game not initialized");
            return null;
        }

        return turnController.executeTurn(action);
    }

    /**
     * Makes character move to a different ambient
     *
     * @param targetAmbient The ambient to move to
     * @return true if movement successful, false otherwise
     */
    public boolean moveToAmbient(A targetAmbient) {
        if (!gameInitialized || targetAmbient == null) {
            return false;
        }

        A currentAmbient = gameState.getCurrentAmbient();
        logger.info("Moving from {} to {}",
            currentAmbient != null ? currentAmbient.getName() : "nowhere",
            targetAmbient.getName());

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
            return false;
        }

        A currentAmbient = gameState.getCurrentAmbient();
        if (currentAmbient == null) {
            return false;
        }

        // removeResource is defined in AmbientController
        boolean removed = ambientController.removeResource(currentAmbient, item);
        if (removed) {
            C player = gameState.getPlayerCharacter();
            player.insertItemInInventory(item);
            logger.info("Player collected {} from {}", item.getName(), currentAmbient.getName());
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
            logger.error("Cannot save: Game not initialized");
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
        GameState<C, A> loadedState = saveManager.loadGame(saveName);
        if (loadedState != null) {
            this.gameState.copyFrom(loadedState);
            this.eventController.setGameState(gameState);

            gameInitialized = true;
            logger.info("Game loaded successfully: {}", saveName);
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
            eventController.addPossibleEvent(event);
            logger.info("Registered new event: {}", event.getName());
        }
    }

    /**
     * Gets the player character
     *
     * @return The player character
     */
    public C getPlayerCharacter() {
        return gameState.getPlayerCharacter();
    }

    /**
     * Gets the current ambient
     *
     * @return The current ambient
     */
    public A getCurrentAmbient() {
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
     * Gets the turn history
     *
     * @return List of turn summaries
     */
    public List<TurnController<C, A>.TurnSummary> getTurnHistory() {
        return turnController.getTurnHistory();
    }

    /**
     * Gets the current turn number
     *
     * @return Current turn number
     */
    public int getCurrentTurn() {
        return turnController.getCurrentTurn();
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
