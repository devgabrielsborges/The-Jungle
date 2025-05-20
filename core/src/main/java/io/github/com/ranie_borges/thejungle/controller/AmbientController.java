package io.github.com.ranie_borges.thejungle.controller;

import com.badlogic.gdx.Screen;
import io.github.com.ranie_borges.thejungle.controller.exceptions.ambient.AmbientControllerException;
import io.github.com.ranie_borges.thejungle.controller.exceptions.ambient.InvalidAmbientException;
import io.github.com.ranie_borges.thejungle.controller.exceptions.ambient.ResourceOperationException;
import io.github.com.ranie_borges.thejungle.controller.systems.SaveManager;
import io.github.com.ranie_borges.thejungle.core.Main;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;
import io.github.com.ranie_borges.thejungle.model.events.Event;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Jungle;
import io.github.com.ranie_borges.thejungle.view.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ScenarioController manages both game flow and ambient control functionality.
 * This class directly integrates all ambient control functionality, eliminating
 * the need
 * for a separate AmbientController class.
 */
public class AmbientController {
    // if have a save -> MainMenu, ProceduralMapScreen
    // else -> MainMenu, LoadingScreen, LetterScreen, StatsScreen,
    // ProceduralMapScreen
    private final boolean saveExists;
    private final SaveManager saveManager;
    private Screen actualScreen;
    private final Main game;
    private static final Logger logger = LoggerFactory.getLogger(AmbientController.class);

    private GameState gameState;
    private EventController eventController;
    private Clime globalClime;
    private Set<Ambient> ambients;
    private List<Ambient> visitedAmbients;

    public AmbientController(Main game) {
        this.saveManager = new SaveManager();
        this.saveExists = saveManager.getSaveFiles().length > 0;
        this.game = game;

        this.gameState = new GameState();
        this.eventController = new EventController(this.gameState);
        this.gameState.setEventController(this.eventController);

        this.ambients = new HashSet<>();
        this.visitedAmbients = new ArrayList<>();

        this.gameState.setAmbientController(this);
    }

    /**
     * Initialize the game with MainMenuScreen
     */
    public void initializeGame() {
        setScreen(new MainMenuScreen(game));
    }

    /**
     * Set the current screen and update the game
     */
    public void setScreen(Screen screen) {
        if (actualScreen != null) {
            actualScreen.dispose();
        }
        actualScreen = screen;
        game.setScreen(screen);
    }

    /**
     * Navigate to next screen based on the current screen
     */
    public void navigateToNextScreen() {
        if (actualScreen instanceof MainMenuScreen) {
            if (saveExists) {
                loadSavedGame();
            } else {
                setScreen(new LoadingScreen(game));
            }
        } else if (actualScreen instanceof LoadingScreen) {
            setScreen(new LetterScreen(game));
        } else if (actualScreen instanceof LetterScreen) {
            setScreen(new StatsScreen(game));
        }
    }

    /**
     * Load a saved game and transition to game screen
     */
    public void loadSavedGame() {
        File[] saveFiles = saveManager.getSaveFiles();
        logger.info("Found {} save files", saveFiles.length);

        if (saveFiles.length > 0) {
            try {
                // Get first save file
                String saveName = String.valueOf(saveFiles[saveFiles.length - 1]);
                logger.info("Attempting to load save file: {}", saveName);

                // Load game state
                this.gameState = saveManager.loadGame(saveName);

                if (this.gameState == null) {
                    logger.error("Game state is null after loading save: {}", saveName);
                    startNewGame();
                    return;
                }

                if (this.gameState.getPlayerCharacter() == null) {
                    logger.error("Player character is null in save: {}", saveName);
                    startNewGame();
                    return;
                }

                // Initialize ambient control
                EventController loadedEventController = this.gameState.getEventController();
                this.eventController = loadedEventController == null ? new EventController(this.gameState)
                        : loadedEventController;
                this.gameState.setEventController(this.eventController);

                // If there was a previous ambient controller, copy its data
                if (this.gameState.getAmbientController() != null && this.gameState.getAmbientController() != this) {
                    AmbientController oldController = this.gameState.getAmbientController();

                    try {
                        // Get data from the old controller
                        Set<Ambient> oldAmbients = oldController.getAmbients();
                        if (oldAmbients != null) {
                            this.ambients = new HashSet<>(oldAmbients);
                        }

                        List<Ambient> oldVisitedAmbients = oldController.getVisitedAmbients();
                        if (oldVisitedAmbients != null) {
                            this.visitedAmbients = new ArrayList<>(oldVisitedAmbients);
                        }

                        this.globalClime = oldController.getGlobalClime();
                    } catch (Exception e) {
                        logger.warn("Could not get data from previous ambient controller", e);
                    }
                }

                // Set this scenario controller as the ambient controller for the game state
                this.gameState.setAmbientController(this);

                Character characterName = this.gameState.getPlayerCharacter();
                String profession = getProfessionFromCharacter(this.gameState.getPlayerCharacter());
                Ambient currentAmbient = this.gameState.getCurrentAmbient();
                logger.info("Successfully loaded character: {}, profession: {}",
                        characterName.getClass().getSimpleName(), profession);

                setScreen(new ProceduralMapScreen(characterName, currentAmbient));
            } catch (Exception e) {
                logger.error("Error loading saved game", e);
                startNewGame();
            }
        } else {
            logger.warn("No save files found despite saveExists flag being true");
            startNewGame();
        }
    }

    private String getProfessionFromCharacter(io.github.com.ranie_borges.thejungle.model.entity.Character character) {
        return character.getClass().getSimpleName(); // Returns "Survivor", "Hunter", etc.
    }

    /**
     * Start a new game from the beginning
     */
    public void startNewGame() {
        setScreen(new LoadingScreen(game));
    }

    /**
     * Start game with a specific character
     */
    public void startGameWithCharacter(Character character) {
        // Initialize a new game state
        this.gameState = new GameState();
        this.gameState.setPlayerCharacter(character);

        // Create initial ambient
        Ambient startingAmbient = new Jungle();
        this.gameState.setCurrentAmbient(startingAmbient);

        // Initialize event controller
        this.eventController = new EventController(this.gameState);
        this.gameState.setEventController(this.eventController);

        // Set this scenario controller as the ambient controller for the game state
        this.gameState.setAmbientController(this);

        // Add the starting ambient to the list of ambients
        this.ambients = new HashSet<>();
        this.ambients.add(startingAmbient);

        // Initialize visited ambients list
        this.visitedAmbients = new ArrayList<>();
        this.visitedAmbients.add(startingAmbient);

        // Start the game with the initial ambient
        setScreen(new ProceduralMapScreen(character, startingAmbient));
    }

    /**
     * Generate an event for an ambient
     *
     * @param ambient The ambient to generate an event for
     */
    public void generateEvent(Ambient ambient) {
        try {
            if (ambient == null) {
                logger.error("Cannot generate event: ambient is null");
                throw new InvalidAmbientException("Ambient cannot be null");
            }

            Event event = eventController.drawEvent(ambient);
            if (event != null) {
                eventController.applyEvent(event, gameState.getPlayerCharacter(), ambient);
                logger.info("Applied event {} in ambient {}", event.getName(), ambient.getName());
            } else {
                logger.debug("No event generated for ambient {}", ambient.getName());
            }
        } catch (Exception e) {
            logger.error("Failed to generate event: {}", e.getMessage());
            throw new AmbientControllerException("Error generating event", e);
        }
    }

    /**
     * Modify resources in an ambient
     *
     * @param ambient The ambient to modify
     * @param item    The item to remove
     * @return True if the resource was successfully removed
     */
    public boolean modifyResources(Ambient ambient, Item item) {
        try {
            if (ambient == null) {
                logger.error("Cannot modify resources: ambient is null");
                throw new InvalidAmbientException("Ambient cannot be null");
            }
            if (item == null) {
                logger.error("Cannot modify resources: item is null");
                throw new IllegalArgumentException("Item cannot be null");
            }

            Set<Item> currentResources = new HashSet<>(ambient.getResources());
            boolean removed = currentResources.remove(item);

            if (removed) {
                ambient.setResources(currentResources);
                logger.info("Resource {} removed from ambient {}", item.getName(), ambient.getName());
                return true;
            }

            logger.debug("Resource {} not found in ambient {}", item.getName(), ambient.getName());
            return false;
        } catch (Exception e) {
            logger.error("Failed to modify resources: {}", e.getMessage());
            throw new ResourceOperationException("Failed to modify resources");
        }
    }

    /**
     * Regenerate resources in an ambient
     *
     * @param currentAmbient The ambient to regenerate resources in
     * @param resourceCount  The number of resources to regenerate
     */
    public void regenerateResources(Ambient currentAmbient, int resourceCount) {
        try {
            if (currentAmbient == null) {
                logger.error("Cannot regenerate resources: ambient is null");
                throw new InvalidAmbientException("Ambient cannot be null");
            }

            // Implementation will be added later
            logger.warn("Resource regeneration not yet implemented");
            throw new UnsupportedOperationException("Resource regeneration not yet implemented");
        } catch (UnsupportedOperationException e) {
            throw e; // Re-throw as is
        } catch (Exception e) {
            logger.error("Failed to regenerate resources: {}", e.getMessage());
            throw new ResourceOperationException("Failed to regenerate resources");
        }
    }

    /**
     * Remove a resource from an ambient
     *
     * @param ambient  The ambient to remove the resource from
     * @param resource The resource to remove
     * @return True if the resource was successfully removed
     */
    public boolean removeResource(Ambient ambient, Item resource) {
        try {
            if (ambient == null) {
                logger.error("Cannot remove resource: ambient is null");
                throw new InvalidAmbientException("Ambient cannot be null");
            }
            if (resource == null) {
                logger.error("Cannot remove resource: resource is null");
                throw new IllegalArgumentException("Resource cannot be null");
            }

            boolean removed = ambient.getResources().remove(resource);
            if (removed) {
                logger.info("Resource {} removed from ambient {}", resource.getName(), ambient.getName());
            } else {
                logger.debug("Resource {} not found in ambient {}", resource.getName(), ambient.getName());
            }
            return removed;
        } catch (Exception e) {
            logger.error("Failed to remove resource: {}", e.getMessage());
            throw new ResourceOperationException("Failed to remove resource");
        }
    }

    /**
     * Set the global climate
     *
     * @param clime The climate to set
     */
    public void setGlobalClime(Clime clime) {
        try {
            if (clime == null) {
                logger.error("Cannot set global clime: clime is null");
                throw new IllegalArgumentException("Global clime cannot be null");
            }

            this.globalClime = clime;

            for (Ambient ambient : ambients) {
                if (ambient.getClimes().isEmpty()) {
                    ambient.addClime(clime);
                }
            }
            logger.info("Set global clime to {}", clime);
        } catch (Exception e) {
            logger.error("Failed to set global clime: {}", e.getMessage());
            throw new AmbientControllerException("Failed to set global clime", e);
        }
    }

    /**
     * Get the global climate
     *
     * @return The global climate
     */
    public Clime getGlobalClime() {
        return globalClime;
    }

    /**
     * Get all ambients in the game
     *
     * @return The set of ambients
     */
    public Set<Ambient> getAmbients() {
        return ambients;
    }

    /**
     * Get all visited ambients
     *
     * @return The list of visited ambients
     */
    public List<Ambient> getVisitedAmbients() {
        return visitedAmbients;
    }

    /**
     * Set ambients in the game
     *
     * @param ambients The ambients to set
     */
    public void setAmbients(Set<Ambient> ambients) {
        try {
            if (ambients == null) {
                logger.error("Cannot set ambients: ambients is null");
                throw new IllegalArgumentException("Ambients cannot be null");
            }
            this.ambients = ambients;
            logger.debug("Set {} ambients", ambients.size());
        } catch (Exception e) {
            logger.error("Failed to set ambients: {}", e.getMessage());
            throw new AmbientControllerException("Failed to set ambients", e);
        }
    }

    /**
     * Set visited ambients
     *
     * @param visitedAmbients The visited ambients to set
     */
    public void setVisitedAmbients(List<Ambient> visitedAmbients) {
        try {
            if (visitedAmbients == null) {
                logger.error("Cannot set visited ambients: visited ambients is null");
                throw new IllegalArgumentException("Visited ambients cannot be null");
            }
            this.visitedAmbients = visitedAmbients;
            logger.debug("Set {} visited ambients", visitedAmbients.size());
        } catch (Exception e) {
            logger.error("Failed to set visited ambients: {}", e.getMessage());
            throw new AmbientControllerException("Failed to set visited ambients", e);
        }
    }
}
