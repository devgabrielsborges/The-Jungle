// core/src/main/java/io/github/com/ranie_borges/thejungle/controller/AmbientController.java
package io.github.com.ranie_borges.thejungle.controller;

import com.badlogic.gdx.Screen;
import io.github.com.ranie_borges.thejungle.controller.exceptions.ambient.AmbientControllerException;
import io.github.com.ranie_borges.thejungle.controller.exceptions.ambient.InvalidAmbientException;
import io.github.com.ranie_borges.thejungle.controller.exceptions.ambient.ResourceOperationException;
import io.github.com.ranie_borges.thejungle.controller.managers.MapManager;
import io.github.com.ranie_borges.thejungle.controller.managers.SaveManager;
import io.github.com.ranie_borges.thejungle.core.Main;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;
import io.github.com.ranie_borges.thejungle.model.stats.AmbientData;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Jungle;
import io.github.com.ranie_borges.thejungle.view.*;
import io.github.com.ranie_borges.thejungle.view.interfaces.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
     * Get the currently active screen.
     * @return The current Screen instance.
     */
    public Screen getActualScreen() {
        return actualScreen;
    }

    /**
     * Get the MapManager from the currently active ProceduralMapScreen.
     * @return The MapManager instance, or null if the current screen is not ProceduralMapScreen.
     */
    public MapManager getCurrentMapManager() {
        if (actualScreen instanceof ProceduralMapScreen) {
            return ((ProceduralMapScreen) actualScreen).getMapManager();
        }
        return null;
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
        if (game != null) {
            game.setScreen(screen);
        }
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
        try {
            SaveManager saveManager = new SaveManager();
            File[] saveFiles = saveManager.getSaveFiles();

            if (saveFiles != null && saveFiles.length > 0) {
                Arrays.sort(saveFiles, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

                GameState gameState = saveManager.loadGame(saveFiles[0].getAbsolutePath());
                setupLoadedGame(gameState);
            } else {
                startNewGame();
            }
        } catch (Exception e) {
            logger.error("Failed to load saved game: {}", e.getMessage());
            startNewGame();
        }
    }

    /**
     * Loads a specific save game by filename
     *
     * @param saveName The name of the save file to load
     */
    public void loadSpecificSaveGame(String saveName) {
        try {
            String fullFilename = saveName.endsWith(".json") ? saveName : saveName + ".json";
            String savePath = "saves/" + fullFilename;

            logger.info("Loading save game from: {}", savePath);
            GameState loadedGameState = saveManager.loadGame(savePath);

            if (loadedGameState != null) {
                setupLoadedGame(loadedGameState);
            } else {
                logger.error("Failed to load game state from: {}", savePath);
                startNewGame();
            }
        } catch (Exception e) {
            logger.error("Failed to load specific save game: {}", e.getMessage(), e);
            startNewGame();
        }
    }

    /**
     * Setup the game with a loaded game state
     *
     * @param gameState The loaded game state
     */
    private void setupLoadedGame(GameState gameState) {
        logger.info("Setting up loaded game from save file");
        this.gameState = gameState;

        Character player = gameState.getPlayerCharacter();

        Ambient currentAmbient = gameState.getCurrentAmbient();
        if (currentAmbient == null) {
            logger.warn("No ambient found in save, creating default Jungle ambient");
            currentAmbient = new Jungle();
        } else {
            logger.info("Loaded ambient: {}", currentAmbient.getName());
        }

        MapManager mapManager = new MapManager(currentAmbient);

        int[][] mapToLoad = null;

        if (gameState.getCurrentMap() != null && gameState.getCurrentMap().length > 0) {
            logger.info("Using directly stored current map with dimensions: {}x{}",
                gameState.getCurrentMap().length,
                gameState.getCurrentMap()[0].length);
            mapToLoad = gameState.getCurrentMap();
        } else if (gameState.getVisitedAmbients() != null && gameState.getVisitedAmbients().containsKey(currentAmbient.getName())) {
            AmbientData ambientData = gameState.getVisitedAmbients().get(currentAmbient.getName());
            if (ambientData != null && ambientData.getMap() != null) {
                logger.info("Using map from visited ambient data for: {}", currentAmbient.getName());
                mapToLoad = ambientData.getMap();
            }
        }

        if (mapToLoad != null) {
            mapManager.setCurrentMap(mapToLoad);
            mapManager.setCurrentAmbient(currentAmbient);
            gameState.setCurrentMap(mapToLoad);
            if (gameState.getVisitedAmbients() != null && gameState.getVisitedAmbients().containsKey(currentAmbient.getName())) {
                AmbientData ambientData = gameState.getVisitedAmbients().get(currentAmbient.getName());
                if (ambientData != null && ambientData.getRemainingResources() != null) {
                    logger.info("Restoring {} resources for ambient {}",
                        ambientData.getRemainingResources().size(),
                        currentAmbient.getName());
                    currentAmbient.setResources(new HashSet<>(ambientData.getRemainingResources()));
                }
            }
        } else {
            logger.warn("No saved map found, generating a new one for ambient: {}",
                currentAmbient.getName());
            // Updated call to generateNextMap and retrieve the map from mapManager.getMap()
            mapManager.checkAndRotateAmbient(); // This will increment count for initial map if it was 0, but no rotation yet.
            mapManager.generateMapForCurrentAmbient(); // Call generateMapForCurrentAmbient
            int[][] newMap = mapManager.getMap(); // Get the generated map
            gameState.setCurrentMap(newMap);
        }

        eventController = gameState.getEventController();
        if (eventController == null) {
            eventController = new EventController(gameState);
            gameState.setEventController(eventController);
        }

        if (!this.visitedAmbients.contains(currentAmbient)) {
            this.visitedAmbients.add(currentAmbient);
        }

        this.ambients.add(currentAmbient);

        setScreen(new ProceduralMapScreen(player, currentAmbient));
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
        this.gameState = new GameState();
        this.gameState.setPlayerCharacter(character);

        Ambient startingAmbient = new Jungle();
        this.gameState.setCurrentAmbient(startingAmbient);

        this.eventController = new EventController(this.gameState);
        this.gameState.setEventController(this.eventController);
        this.gameState.setAmbientController(this);

        this.ambients = new HashSet<>();
        this.ambients.add(startingAmbient);

        this.visitedAmbients = new ArrayList<>();
        this.visitedAmbients.add(startingAmbient);

        // Generate the initial map and store it in GameState
        int[][] map = startingAmbient.generateMap(UI.MAP_WIDTH, UI.MAP_HEIGHT);
        this.gameState.addVisitedMap(startingAmbient.getName(), map);
        this.gameState.setCurrentMap(map);

        setScreen(new ProceduralMapScreen(character, startingAmbient));
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

            logger.warn("Resource regeneration not yet implemented");
            throw new UnsupportedOperationException("Resource regeneration not yet implemented");
        } catch (UnsupportedOperationException e) {
            throw e;
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
