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
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Jungle;
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
    private final boolean saveExists;
    private final SaveManager saveManager;
    private Screen actualScreen;
    private final Main game;
    private static final Logger logger = LoggerFactory.getLogger(AmbientController.class);

    private GameState gameState; // This will be the authoritative GameState instance
    private EventController eventController;
    private Clime globalClime;
    private Set<Ambient> ambients; // Set of all ambient types encountered or available
    private List<Ambient> visitedAmbients; // History of distinct ambients visited

    public AmbientController(Main game) {
        this.saveManager = new SaveManager();
        this.saveExists = saveManager.getSaveFiles().length > 0;
        this.game = game;

        this.gameState = new GameState();
        this.eventController = new EventController(this.gameState);
        this.gameState.setEventController(this.eventController);
        this.gameState.setAmbientController(this); // Link this controller to the GameState

        this.ambients = new HashSet<>();
        this.visitedAmbients = new ArrayList<>();
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
            // Logic for MainMenuScreen to decide load or new game is handled within its button listeners
            // This method is more for sequential screen flow like Loading -> Letter -> Stats
            setScreen(new LoadingScreen(game)); // Default path if not loading save
        } else if (actualScreen instanceof LoadingScreen) {
            setScreen(new LetterScreen(game));
        } else if (actualScreen instanceof LetterScreen) {
            setScreen(new StatsScreen(game));
        }
    }

    public void loadSavedGame() {
        try {
            File[] saveFiles = saveManager.getSaveFiles();
            if (saveFiles != null && saveFiles.length > 0) {
                // Prioritize "autosave.json" if it exists, otherwise take the most recent
                File fileToLoad = null;
                for (File f : saveFiles) {
                    if ("autosave.json".equalsIgnoreCase(f.getName())) {
                        fileToLoad = f;
                        break;
                    }
                }
                if (fileToLoad == null) {
                    Arrays.sort(saveFiles, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
                    fileToLoad = saveFiles[0];
                }
                logger.info("Attempting to load save file: {}", fileToLoad.getAbsolutePath());
                GameState loadedGameState = saveManager.loadGame(fileToLoad.getAbsolutePath());
                setupLoadedGame(loadedGameState);
            } else {
                logger.info("No save files found. Starting a new game.");
                startNewGame(); // Or navigate to new game sequence
            }
        } catch (Exception e) {
            logger.error("Failed to load saved game: {}. Starting new game.", e.getMessage(), e);
            startNewGame(); // Or navigate to new game sequence
        }
    }

    public void loadSpecificSaveGame(String saveName) {
        try {
            String fullFilename = saveName.endsWith(".json") ? saveName : saveName + ".json";
            String savePath = "saves/" + fullFilename;

            logger.info("Loading specific save game from: {}", savePath);
            GameState loadedGameState = saveManager.loadGame(savePath);

            if (loadedGameState != null) {
                setupLoadedGame(loadedGameState);
            } else {
                logger.error("Failed to load game state from: {}. Starting new game.", savePath);
                startNewGame();
            }
        } catch (Exception e) {
            logger.error("Failed to load specific save game '{}': {}. Starting new game.", saveName, e.getMessage(), e);
            startNewGame();
        }
    }

    private void setupLoadedGame(GameState loadedGameState) {
        logger.info("Setting up loaded game.");
        this.gameState = loadedGameState; // Use the GameState loaded from the file

        Character player = this.gameState.getPlayerCharacter();
        Ambient currentAmbient = this.gameState.getCurrentAmbient();

        if (player == null) {
            logger.error("Loaded GameState has no player character! Cannot proceed.");
            // Potentially revert to main menu or show an error
            setScreen(new MainMenuScreen(game)); // Example fallback
            return;
        }

        if (currentAmbient == null) {
            logger.warn("Loaded GameState has no current ambient. Defaulting to Jungle.");
            currentAmbient = new Jungle();
            this.gameState.setCurrentAmbient(currentAmbient);
        }
        logger.info("Ambient from loaded GameState: {}", currentAmbient.getName());

        // Re-initialize EventController with the loaded GameState if it wasn't properly linked
        this.eventController = this.gameState.getEventController();
        if (this.eventController == null || this.eventController.getGameState() != this.gameState) {
            logger.warn("Re-initializing EventController for loaded GameState.");
            this.eventController = new EventController(this.gameState);
            this.gameState.setEventController(this.eventController);
        }
        this.gameState.setAmbientController(this);


        // Initialize ambients and visitedAmbients sets/lists from GameState if needed,
        // though these are more for tracking during a session rather than direct save/load.
        // The critical part is that gameState.currentAmbient and gameState.currentMap are correct.
        this.ambients.clear();
        this.visitedAmbients.clear();
        if(this.gameState.getVisitedAmbients() != null){
            for(String ambientName : this.gameState.getVisitedAmbients().keySet()){
                // This is tricky because Ambient objects themselves are not fully serialized, only their type/name.
                // For now, just add the currentAmbient to our tracking lists.
            }
        }
        if (currentAmbient != null) {
            this.ambients.add(currentAmbient); // Add the current one for tracking
            this.visitedAmbients.add(currentAmbient);
        }


        // Pass the loaded gameState, player, and the confirmed currentAmbient
        setScreen(new ProceduralMapScreen(this.gameState, player, currentAmbient));
    }

    public void startNewGame() {
        // This typically navigates to the character creation sequence
        logger.info("Starting new game sequence.");
        setScreen(new LoadingScreen(game)); // Or StatsScreen if skipping intro
    }

    public void startGameWithCharacter(Character character) {
        logger.info("Starting game with new character: {}", character.getName());
        this.gameState = new GameState(); // Fresh GameState for a new game
        this.gameState.setPlayerCharacter(character);

        Ambient startingAmbient = new Jungle(); // New games always start in Jungle
        this.gameState.setCurrentAmbient(startingAmbient);

        // Ensure EventController is set for the new GameState
        this.eventController = new EventController(this.gameState);
        this.gameState.setEventController(this.eventController);
        this.gameState.setAmbientController(this);

        this.ambients = new HashSet<>();
        this.ambients.add(startingAmbient);
        this.visitedAmbients = new ArrayList<>();
        this.visitedAmbients.add(startingAmbient);

        // ProceduralMapScreen will generate the initial map for the startingAmbient
        // based on the GameState it receives.
        setScreen(new ProceduralMapScreen(this.gameState, character, startingAmbient));
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

    public void setGlobalClime(Clime clime) {
        try {
            if (clime == null) {
                logger.debug("Cannot set global clime: clime is null. This might be intended if ambient has no climes.");
                this.globalClime = null; // Allow null if no specific clime
                return;
            }

            this.globalClime = clime;

            for (Ambient ambientInstance : ambients) { // Use 'ambients' which tracks distinct ambient types encountered
                if (ambientInstance.getClimes().isEmpty()) {
                    // This logic might be too broad; climes should be inherent to ambient types.
                    // Consider if globalClime should override or be a fallback.
                    // ambientInstance.addClime(clime);
                }
            }
            logger.info("Set global clime to {}", clime);
        } catch (Exception e) {
            logger.error("Failed to set global clime: {}", e.getMessage());
            throw new AmbientControllerException("Failed to set global clime", e);
        }
    }

    public Clime getGlobalClime() {
        return globalClime;
    }

    public Set<Ambient> getAmbients() {
        return ambients;
    }

    public List<Ambient> getVisitedAmbients() {
        return visitedAmbients;
    }

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

    public AmbientController getScenarioController() {
        return this;
    }

    public GameState getGameState() {
        return gameState;
    }
}
