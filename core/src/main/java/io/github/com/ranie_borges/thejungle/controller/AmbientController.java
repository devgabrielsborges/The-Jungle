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
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.*;
import io.github.com.ranie_borges.thejungle.view.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AmbientController {
    private final SaveManager saveManager;
    private Screen actualScreen;
    private final Main game;
    private static final Logger logger = LoggerFactory.getLogger(AmbientController.class);

    private GameState gameState;
    private EventController eventController;
    private Clime globalClime;
    private Set<Ambient> ambients;
    private List<Ambient> visitedAmbients;
    private String currentSaveFileName; // To keep track of the loaded/active save file name

    public AmbientController(Main game) {
        this.saveManager = new SaveManager();
        this.game = game;
        this.gameState = new GameState();
        this.eventController = new EventController(this.gameState);
        this.gameState.setEventController(this.eventController);
        this.gameState.setAmbientController(this);

        this.ambients = new HashSet<>();
        this.visitedAmbients = new ArrayList<>();
        updateSaveExistsStatus();
    }

    private void updateSaveExistsStatus() {
        File[] saveFiles = saveManager.getSaveFiles();
    }

    public Screen getActualScreen() {
        return actualScreen;
    }

    public MapManager getCurrentMapManager() {
        if (actualScreen instanceof ProceduralMapScreen) {
            return ((ProceduralMapScreen) actualScreen).getMapManager();
        }
        return null;
    }

    public void initializeGame() {
        updateSaveExistsStatus(); // Ensure status is fresh
        setScreen(new MainMenuScreen(game));
    }

    public void setScreen(Screen screen) {
        if (actualScreen != null) {
            actualScreen.dispose();
        }
        actualScreen = screen;
        if (game != null) {
            game.setScreen(screen);
        }
    }

    public void navigateToNextScreen() {
        if (actualScreen instanceof MainMenuScreen) {
            setScreen(new LoadingScreen(game));
        } else if (actualScreen instanceof LoadingScreen) {
            setScreen(new LetterScreen(game));
        } else if (actualScreen instanceof LetterScreen) {
            setScreen(new StatsScreen(game));
        }
    }

    // Method to handle game over transition
    public void triggerGameOver(String saveFileNameToDelete) {
        logger.info("Game Over triggered. Save file to delete (if applicable): {}", saveFileNameToDelete);
        this.currentSaveFileName = null; // Reset current save file name
        setScreen(new GameOverScreen(game, saveFileNameToDelete));
    }


    public void loadSavedGame() { // This is typically called by "Continue" from MainMenu
        try {
            File[] saveFiles = saveManager.getSaveFiles();
            if (saveFiles != null && saveFiles.length > 0) {
                File fileToLoad = null;
                for (File f : saveFiles) {
                    if ("autosave.json".equalsIgnoreCase(f.getName())) {
                        fileToLoad = f;
                        break;
                    }
                }
                if (fileToLoad == null) { // If no autosave, load the most recent one
                    Arrays.sort(saveFiles, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
                    if (saveFiles.length > 0) fileToLoad = saveFiles[0];
                }

                if (fileToLoad != null) {
                    logger.info("Attempting to load save file: {}", fileToLoad.getName());
                    currentSaveFileName = fileToLoad.getName(); // Store the name of the save being loaded
                    GameState loadedGameState = saveManager.loadGame(fileToLoad.getAbsolutePath());
                    if (loadedGameState != null) {
                        setupLoadedGame(loadedGameState);
                    } else {
                        logger.error("Failed to load GameState from {}. Starting new game.", fileToLoad.getName());
                        currentSaveFileName = null;
                        startNewGame();
                    }
                } else {
                    logger.info("No save files found for 'Continue'. Starting new game.");
                    currentSaveFileName = null;
                    startNewGame();
                }
            } else {
                logger.info("No save files directory or no files found. Starting new game.");
                currentSaveFileName = null;
                startNewGame();
            }
        } catch (Exception e) {
            logger.error("Failed to load saved game: {}. Starting new game.", e.getMessage(), e);
            currentSaveFileName = null;
            startNewGame();
        }
    }

    public void loadSpecificSaveGame(String saveName) { // Called if multiple saves were listed and one chosen
        try {
            String fullFilename = saveName.endsWith(".json") ? saveName : saveName + ".json";
            String savePath = "saves/" + fullFilename;

            logger.info("Loading specific save game from: {}", savePath);
            GameState loadedGameState = saveManager.loadGame(savePath);

            if (loadedGameState != null) {
                currentSaveFileName = fullFilename; // Store the name of the loaded save
                setupLoadedGame(loadedGameState);
            } else {
                logger.error("Failed to load game state from: {}. Starting new game.", savePath);
                currentSaveFileName = null;
                startNewGame();
            }
        } catch (Exception e) {
            logger.error("Failed to load specific save game '{}': {}. Starting new game.", saveName, e.getMessage(), e);
            currentSaveFileName = null;
            startNewGame();
        }
    }

    private void setupLoadedGame(GameState loadedGameState) {
        logger.info("Setting up loaded game.");
        this.gameState = loadedGameState;

        Character player = this.gameState.getPlayerCharacter();
        Ambient currentAmbient = this.gameState.getCurrentAmbient();

        if (player == null) {
            logger.error("Loaded GameState has no player character! Cannot proceed.");
            setScreen(new MainMenuScreen(game));
            return;
        }

        if (currentAmbient == null) {
            logger.warn("Loaded GameState has no current ambient. Defaulting to Jungle.");
            currentAmbient = new Jungle();
            this.gameState.setCurrentAmbient(currentAmbient);
        }
        logger.info("Ambient from loaded GameState: {}", currentAmbient.getName());

        this.eventController = this.gameState.getEventController();
        if (this.eventController == null || this.eventController.getGameState() != this.gameState) {
            logger.warn("Re-initializing EventController for loaded GameState.");
            this.eventController = new EventController(this.gameState);
            this.gameState.setEventController(this.eventController);
        }
        this.gameState.setAmbientController(this);

        this.ambients.clear();
        this.visitedAmbients.clear();
        if (currentAmbient != null) {
            this.ambients.add(currentAmbient);
            this.visitedAmbients.add(currentAmbient);
        }

        setScreen(new ProceduralMapScreen(this.game, this.gameState, player, currentAmbient));
    }

    public void startNewGame() {
        logger.info("Starting new game sequence.");
        currentSaveFileName = "autosave.json"; // New games will use autosave by default
        setScreen(new LoadingScreen(game));
    }

    public void startGameWithCharacter(Character character) {
        logger.info("Starting game with new character: {}", character.getName());
        this.gameState = new GameState();
        this.gameState.setPlayerCharacter(character);
        currentSaveFileName = "autosave.json"; // Default save name for new games

        Ambient startingAmbient = new LakeRiver();
        this.gameState.setCurrentAmbient(startingAmbient);

        this.eventController = new EventController(this.gameState);
        this.gameState.setEventController(this.eventController);
        this.gameState.setAmbientController(this);

        this.ambients = new HashSet<>();
        this.ambients.add(startingAmbient);
        this.visitedAmbients = new ArrayList<>();
        this.visitedAmbients.add(startingAmbient);

        setScreen(new ProceduralMapScreen(this.game, this.gameState, character, startingAmbient));
    }

    public String getCurrentSaveFileName() {
        if (currentSaveFileName != null && !currentSaveFileName.trim().isEmpty()) {
            return currentSaveFileName;
        }
        if (actualScreen instanceof ProceduralMapScreen) {
            return "autosave.json"; // Default for active games not explicitly loaded from a named save
        }
        return null; // No active game or save context
    }


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

            boolean removed = ambient.getResources().remove(resource); // This might not work correctly if Item doesn't have good equals/hashCode
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
    public void setGlobalClime(Clime clime) {
        try {
            if (clime == null) {
                logger.debug("Cannot set global clime: clime is null. This might be intended if ambient has no climes.");
                this.globalClime = null;
                return;
            }
            this.globalClime = clime;
            logger.info("Set global clime to {}", clime);
        } catch (Exception e) {
            logger.error("Failed to set global clime: {}", e.getMessage());
            throw new AmbientControllerException("Failed to set global clime", e);
        }
    }

    public Clime getGlobalClime() { return globalClime; }
    public Set<Ambient> getAmbients() { return ambients; }
    public List<Ambient> getVisitedAmbients() { return visitedAmbients; }
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
    public AmbientController getScenarioController() { return this; }
    public GameState getGameState() { return gameState; }
}
