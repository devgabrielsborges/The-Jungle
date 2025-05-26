package com.ranieborges.thejungle.cli;

import com.ranieborges.thejungle.cli.controller.AmbientController;
import com.ranieborges.thejungle.cli.controller.EventManager;
import com.ranieborges.thejungle.cli.controller.TurnController;
import com.ranieborges.thejungle.cli.controller.utils.GameStatus;
import com.ranieborges.thejungle.cli.model.stats.GameState;
import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.characters.*;
import com.ranieborges.thejungle.cli.service.SaveLoadService;
import com.ranieborges.thejungle.cli.view.MainMenu;
import com.ranieborges.thejungle.cli.view.Message;
import com.ranieborges.thejungle.cli.view.utils.TerminalUtils;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;
import lombok.Getter;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    private static Character playerCharacter;
    @Getter
    private static int turnCounter = 1;
    private static AmbientController ambientController;
    private static EventManager eventManager;

    private static final SaveLoadService saveLoadService = new SaveLoadService();

    public static void main(String[] args) throws IOException {
        TerminalUtils.clearScreen();
        MainMenu.displayInitialMenu();
        boolean exitApplication = false;

        while (!exitApplication) {
            MainMenu.displayMenuOptions();
            Message.displayOnScreen("Enter your choice: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1": // Start New Game
                    TerminalUtils.clearScreen();
                    handleNewGame();
                    TerminalUtils.clearScreen();
                    MainMenu.displayInitialMenu();
                    break;
                case "2": // Continue Last Game
                    TerminalUtils.clearScreen();
                    continueGameSession();
                    TerminalUtils.clearScreen();
                    MainMenu.displayInitialMenu();
                    break;
                case "3": // Exit
                    TerminalUtils.clearScreen();
                    if (playerCharacter != null && playerCharacter.isAlive() && ambientController != null && eventManager != null) {
                        Message.displayOnScreen("Saving progress before exiting...");
                        GameState currentGameState = new GameState(playerCharacter, turnCounter, ambientController, eventManager);
                        saveLoadService.saveGame(currentGameState, SaveLoadService.AUTOSAVE_FILENAME);
                        Message.displayWithDelay("", 500);
                    }
                    Message.displayOnScreen("Exiting The Jungle. Goodbye!");
                    exitApplication = true;
                    break;
                default:
                    Message.displayOnScreen("Invalid choice. Please try again.");
                    Message.displayWithDelay("", 1000);
                    TerminalUtils.clearScreen();
                    MainMenu.displayInitialMenu();
            }
        }
        scanner.close();
        Message.displayOnScreen("Application closed.");
    }

    private static void handleNewGame() throws IOException {
        if (saveLoadService.autoSaveExists()) {
            Message.displayOnScreen("An existing autosave was found. Starting a new game will delete it.");
            Message.displayOnScreen("Do you want to start a new game and delete the autosave? (yes/no)");
            String confirm = scanner.nextLine().trim();
            if ("yes".equalsIgnoreCase(confirm)) {
                saveLoadService.deleteSaveGame(SaveLoadService.AUTOSAVE_FILENAME);
                Message.displayOnScreen("Previous autosave deleted.");
                runGameSession(null);
            } else {
                Message.displayOnScreen("New game cancelled. Returning to main menu.");
                Message.displayWithDelay("", 1500);
            }
        } else {
            runGameSession(null);
        }
    }

    private static void continueGameSession() throws IOException {
        TerminalUtils.clearScreen();
        Message.displayOnScreen("Attempting to continue last game...");
        GameState loadedState = saveLoadService.loadGame(SaveLoadService.AUTOSAVE_FILENAME);
        if (loadedState != null) {
            TerminalUtils.clearScreen();
            runGameSession(loadedState);
        } else {
            Message.displayOnScreen("No saved game found to continue. Please start a new game.");
            Message.displayWithDelay("", 2000);
        }
    }

    private static void runGameSession(GameState loadedGameState) throws IOException {
        Random random = new Random();

        if (loadedGameState == null) {
            Message.displayCharByCharWithDelay("Starting new game session...", 50);
            playerCharacter = selectCharacter();
            if (playerCharacter == null) {
                Message.displayOnScreen("Game start cancelled, returning to main menu.");
                Message.displayWithDelay("", 1500);
                return;
            }
            turnCounter = 1;
            ambientController = new AmbientController(playerCharacter, scanner, random);
            eventManager = new EventManager(random, scanner);

            Message.displayOnScreen("\nWelcome, " + playerCharacter.getName() + ", the " + playerCharacter.getClass().getSimpleName() + "!");
            Message.displayWithDelay("You find yourself in the " + (playerCharacter.getCurrentAmbient() != null ? playerCharacter.getCurrentAmbient().getName() : "an unknown land") + ".", 1500);

        } else {
            Message.displayCharByCharWithDelay("Loading game session from saved state...", 50);
            playerCharacter = loadedGameState.getPlayerCharacter();
            turnCounter = loadedGameState.getTurnCounter();
            ambientController = loadedGameState.getAmbientController();
            eventManager = loadedGameState.getEventManager();

            if (ambientController != null) {
                ambientController.setPlayerCharacter(playerCharacter);
                ambientController.reinitializeTransientFields(scanner, random);
            } else {
                Message.displayOnScreen(TerminalStyler.error("Critical Error: AmbientController was null after loading. Attempting to re-initialize."));
                ambientController = new AmbientController(playerCharacter, scanner, random);
            }
            if (eventManager != null) {
                eventManager.reinitializeTransientFields(random, scanner);
            } else {
                Message.displayOnScreen(TerminalStyler.error("Critical Error: EventManager was null after loading. Attempting to re-initialize."));
                eventManager = new EventManager(random, scanner);
            }

            if (ambientController.getCurrentAmbient() != null) {
                playerCharacter.setCurrentAmbient(ambientController.getCurrentAmbient());
            } else if (playerCharacter != null && playerCharacter.getCurrentAmbient() != null) {
                ambientController.setPlayerCharacter(playerCharacter);
                ambientController.reinitializeTransientFields(scanner, random);
                Message.displayOnScreen(TerminalStyler.warning("AmbientController's current ambient was re-synced from player data."));
            }


            TerminalUtils.clearScreen();
            Message.displayOnScreen("\nWelcome back, " + playerCharacter.getName() + "!");
            Message.displayWithDelay("Resuming your adventure in the " + (playerCharacter.getCurrentAmbient() != null ? playerCharacter.getCurrentAmbient().getName() : "a mysterious place") + ".", 1500);
        }

        TurnController turnController = new TurnController(playerCharacter, scanner, random, ambientController, eventManager);
        GameStatus gameStatus = GameStatus.CONTINUE;

        while (gameStatus == GameStatus.CONTINUE) {
            gameStatus = turnController.executeTurn(turnCounter);
            if (gameStatus == GameStatus.CONTINUE) {
                GameState currentGameState = new GameState(playerCharacter, turnCounter, ambientController, eventManager);
                saveLoadService.saveGame(currentGameState, SaveLoadService.AUTOSAVE_FILENAME);
                turnCounter++;
            }
        }

        TerminalUtils.clearScreen();
        Message.displayOnScreen("\n--- GAME SESSION ENDED ---");
        switch (gameStatus) {
            case PLAYER_DEFEATED:
                Message.displayCharByCharWithDelay(playerCharacter.getName() + " did not survive The Jungle. Health reached zero.", 100);
                Message.displayOnScreen("Your progress has been lost to the jungle...");
                saveLoadService.deleteSaveGame(SaveLoadService.AUTOSAVE_FILENAME);
                break;
            case SURVIVAL_FAILURE:
                Message.displayCharByCharWithDelay(playerCharacter.getName() + " succumbed to the harsh conditions (e.g., sanity lost, or other critical failure).", 100);
                Message.displayOnScreen("Your progress has been lost to the jungle...");
                saveLoadService.deleteSaveGame(SaveLoadService.AUTOSAVE_FILENAME);
                break;
            case PLAYER_QUIT:
                Message.displayOnScreen(playerCharacter.getName() + " decided to leave the jungle for now.");
                if (playerCharacter.isAlive()) {
                    GameState currentGameState = new GameState(playerCharacter, turnCounter, ambientController, eventManager);
                    saveLoadService.saveGame(currentGameState, SaveLoadService.AUTOSAVE_FILENAME);
                    Message.displayOnScreen("Progress saved.");
                }
                break;
            case OBJECTIVE_MET:
                Message.displayOnScreen(TerminalStyler.success("Congratulations, " + playerCharacter.getName() + "!"));
                Message.displayCharByCharWithDelay("You have survived the perils of The Jungle for " + (turnCounter-1) + " turns and achieved your goal!", 100);
                Message.displayOnScreen("You have mastered the Última Fronteira!");
                saveLoadService.deleteSaveGame(SaveLoadService.AUTOSAVE_FILENAME);
                break;
            default:
                Message.displayOnScreen("The adventure ends.");
                break;
        }
        Message.displayOnScreen("\nPress Enter to return to main menu...");
        scanner.nextLine();
        playerCharacter = null;
        ambientController = null;
        eventManager = null;
    }

    private static Character selectCharacter() {
        TerminalUtils.clearScreen(); // Clears for character class selection
        Message.displayOnScreen("\nChoose your character class:");
        Message.displayOnScreen("1. Doctor");
        Message.displayOnScreen("2. Hunter");
        Message.displayOnScreen("3. Lumberjack");
        Message.displayOnScreen("4. Survivor");
        Message.displayOnScreen("0. Back to Main Menu");

        while (true) {
            Message.displayOnScreen("Enter character choice: ");
            String choice = scanner.nextLine().trim();
            String characterName = "Adventurer";

            if ("0".equals(choice)) {
                TerminalUtils.clearScreen();
                return null;
            }

            Character selectedCharacter;
            String className;

            switch(choice) {
                case "1": className = "Doctor"; selectedCharacter = new Doctor(characterName); break;
                case "2": className = "Hunter"; selectedCharacter = new Hunter(characterName); break;
                case "3": className = "Lumberjack"; selectedCharacter = new Lumberjack(characterName); break;
                case "4": className = "Survivor"; selectedCharacter = new Survivor(characterName); break;
                default:
                    Message.displayOnScreen(TerminalStyler.error("Invalid character choice. Please try again."));
                    Message.displayWithDelay("", 1000);
                    // Re-display options without an extra clear, as the screen is already set up.
                    Message.displayOnScreen("\nChoose your character class:");
                    Message.displayOnScreen("1. Doctor");
                    Message.displayOnScreen("2. Hunter");
                    Message.displayOnScreen("3. Lumberjack");
                    Message.displayOnScreen("4. Survivor");
                    Message.displayOnScreen("0. Back to Main Menu");
                    continue;
            }

            // TerminalUtils.clearScreen(); // <-- REMOVED THIS LINE
            // The screen was cleared at the start of selectCharacter.
            // No need to clear again just before asking for the name.
            // This makes the transition smoother.
            Message.displayOnScreen("\nYou chose: " + className); // Added newline for better spacing
            Message.displayOnScreen("Enter your character's name (or press Enter for default '" + characterName + "'): ");
            String inputName = scanner.nextLine().trim();
            if (!inputName.isEmpty()) {
                selectedCharacter.setName(inputName);
            }
            return selectedCharacter;
        }
    }
}
