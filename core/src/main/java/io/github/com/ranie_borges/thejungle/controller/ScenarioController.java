package io.github.com.ranie_borges.thejungle.controller;

import com.badlogic.gdx.Screen;
import io.github.com.ranie_borges.thejungle.controller.systems.SaveManager;
import io.github.com.ranie_borges.thejungle.core.Main;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Cave;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Jungle;
import io.github.com.ranie_borges.thejungle.view.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class ScenarioController {
    // if have a save -> MainMenu, ProceduralMapScreen
    // else -> MainMenu, LoadingScreen, LetterScreen, StatsScreen, ProceduralMapScreen
    private boolean saveExists;
    private final SaveManager saveManager;
    private Screen actualScreen;
    private final Main game;
    private static final Logger logger = LoggerFactory.getLogger(ScenarioController.class);

    public ScenarioController(Main game) {
        this.saveManager = new SaveManager();
        this.saveExists = saveManager.getSaveFiles().length > 0;
        this.game = game;
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
                GameState gameState = saveManager.loadGame(saveName);

                if (gameState == null) {
                    logger.error("Game state is null after loading save: {}", saveName);
                    startNewGame();
                    return;
                }

                if (gameState.getPlayerCharacter() == null) {
                    logger.error("Player character is null in save: {}", saveName);
                    startNewGame();
                    return;
                }

                Character characterName = gameState.getPlayerCharacter();
                String profession = getProfessionFromCharacter(gameState.getPlayerCharacter());
                Ambient currentAmbient = gameState.getCurrentAmbient();
                logger.info("Successfully loaded character: {}, profession: {}", characterName.getClass().getSimpleName(), profession);

                setScreen(new ProceduralMapScreen(characterName, currentAmbient));
            } catch (Exception e) {
                logger.error("Error loading saved game", e);
                e.printStackTrace(); // Add this to see full stack trace
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
        setScreen(new ProceduralMapScreen(character,  new Jungle()));
    }

    /**
     * Refresh save status
     */
    public void refreshSaveStatus() {
        saveExists = saveManager.getSaveFiles().length > 0;
    }

    /**
     * Get current screen
     */
    public Screen getCurrentScreen() {
        return actualScreen;
    }

    /**
     * Check if save exists
     */
    public boolean hasSaveGame() {
        return saveExists;
    }
}
