package com.ranieborges.thejungle.cli.controller;

import com.ranieborges.thejungle.cli.controller.utils.GameStatus;
import com.ranieborges.thejungle.cli.model.Recipe;
import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Creature;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.itens.Food;
import com.ranieborges.thejungle.cli.model.factions.Faction; // For displaying faction info
import com.ranieborges.thejungle.cli.service.CraftingService;
import com.ranieborges.thejungle.cli.view.Art;
import com.ranieborges.thejungle.cli.view.Message;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;
import com.ranieborges.thejungle.cli.Main;
import com.ranieborges.thejungle.cli.view.utils.TerminalUtils;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

public class TurnController {
    private final Character playerCharacter;
    @Getter private transient final Scanner scanner;
    private transient final Random random;
    private final AmbientController ambientController;
    private final EventManager eventManager;
    private final CraftingService craftingService;
    @Getter private final FactionManager factionManager; // Added FactionManager
    private String previousTurnSummary;

    private static final int VICTORY_TURN_THRESHOLD = 30;

    public TurnController(Character playerCharacter, Scanner scanner, Random random,
                          AmbientController ambientController, EventManager eventManager,
                          FactionManager factionManager) { // Added FactionManager
        if (playerCharacter == null || scanner == null || random == null ||
            ambientController == null || eventManager == null || factionManager == null) { // Check FactionManager
            throw new IllegalArgumentException("All controller dependencies cannot be null.");
        }
        this.playerCharacter = playerCharacter;
        this.scanner = scanner;
        this.random = random;
        this.ambientController = ambientController;
        this.eventManager = eventManager;
        this.craftingService = new CraftingService();
        this.factionManager = factionManager; // Initialize FactionManager

        if (playerCharacter.getCurrentAmbient() != null) {
            this.previousTurnSummary = "The adventure continues in the " + playerCharacter.getCurrentAmbient().getName() + ".";
        } else {
            this.previousTurnSummary = "The adventure begins, but the current location is unknown!";
        }
    }

    // executeTurn, executeInitialPhase methods remain largely the same, just ensure FactionManager is available if needed by them.

    public GameStatus executeTurn(int currentTurn) {
        TerminalUtils.clearScreen();
        Message.displayOnScreen(TerminalStyler.style("\n" + Art.THE_JUNGLE.substring(0, Art.THE_JUNGLE.indexOf('\n')) + "--- Turn " + currentTurn + " / " + VICTORY_TURN_THRESHOLD + " ---", TerminalStyler.BOLD));

        executeInitialPhase();
        if (!checkSurvivalConditions()) return determineGameOverStatus();

        GameStatus actionStatus = executePlayerActionPhase();
        if (actionStatus != GameStatus.CONTINUE) {
            if (actionStatus == GameStatus.PLAYER_QUIT) {
                this.previousTurnSummary = playerCharacter.getName() + " decided to pause their journey.";
            }
            return actionStatus;
        }
        if (!checkSurvivalConditions()) return determineGameOverStatus();

        executeRandomEventPhase();
        if (!checkSurvivalConditions()) return determineGameOverStatus();

        executeMaintenancePhase();
        if (!checkSurvivalConditions()) return determineGameOverStatus();

        if (currentTurn >= VICTORY_TURN_THRESHOLD) {
            this.previousTurnSummary = playerCharacter.getName() + " has bravely survived for " + VICTORY_TURN_THRESHOLD + " turns against all odds!";
            return GameStatus.OBJECTIVE_MET;
        }

        Message.displayWithDelay(TerminalStyler.style("--- End of Turn " + currentTurn + " ---", TerminalStyler.BOLD), 1000);
        return GameStatus.CONTINUE;
    }

    private void executeInitialPhase() {
        Message.displayOnScreen(TerminalStyler.style("\n-- Fase de Início --", TerminalStyler.CYAN, TerminalStyler.BOLD));
        Message.displayOnScreen(TerminalStyler.style("\nSummary of Last Turn: " + previousTurnSummary, TerminalStyler.ITALIC, TerminalStyler.BRIGHT_BLACK));
        playerCharacter.displayStatus(); // displayStatus in Character now shows faction reps
        com.ranieborges.thejungle.cli.model.world.Ambient currentAmbient = ambientController.getCurrentAmbient();
        if (currentAmbient != null) {
            Message.displayOnScreen("Current Location: " + TerminalStyler.style(currentAmbient.getName(), TerminalStyler.BLUE) + " - " + currentAmbient.getDescription());
            Message.displayOnScreen("Weather: Currently " + TerminalStyler.style(currentAmbient.getCurrentWeather(), TerminalStyler.BLUE));
        } else {
            Message.displayOnScreen(TerminalStyler.warning("Current location is unknown!"));
        }
        Message.displayWithDelay("", 1000);
    }


    private GameStatus executePlayerActionPhase() {
        Message.displayOnScreen(TerminalStyler.style("\n-- Fase de Ação --", TerminalStyler.YELLOW, TerminalStyler.BOLD));
        boolean actionTaken = false;
        String actionSummary = playerCharacter.getName() + " considered their options.";
        com.ranieborges.thejungle.cli.model.world.Ambient currentAmbientForAction = ambientController.getCurrentAmbient();

        while (!actionTaken) {
            var delay = 70; // Slightly faster
            Message.displayCharByCharWithDelay("Choose your action:", delay, TerminalStyler.YELLOW);
            Message.displayCharByCharWithDelay("1. Explore surroundings", delay, TerminalStyler.BRIGHT_WHITE);
            Message.displayCharByCharWithDelay("2. Rest", delay, TerminalStyler.BRIGHT_WHITE);
            Message.displayCharByCharWithDelay("3. Use Special Ability", delay, TerminalStyler.BRIGHT_WHITE);
            Message.displayCharByCharWithDelay("4. View Inventory", delay, TerminalStyler.BRIGHT_WHITE);
            Message.displayCharByCharWithDelay("5. Use Item", delay, TerminalStyler.BRIGHT_WHITE);
            Message.displayCharByCharWithDelay("6. Craft Item", delay, TerminalStyler.BRIGHT_WHITE);
            Message.displayCharByCharWithDelay("7. View Faction Reputations", delay, TerminalStyler.BRIGHT_WHITE); // New
            Message.displayCharByCharWithDelay("8. Move to another area", delay, TerminalStyler.BRIGHT_WHITE); // Adjusted
            Message.displayCharByCharWithDelay("0. Quit Game Session", delay, TerminalStyler.RED);
            Message.displayOnScreen("Enter action: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    actionSummary = exploreCurrentAmbient();
                    actionTaken = true;
                    break;
                case "2":
                    actionSummary = rest();
                    actionTaken = true;
                    break;
                case "3":
                    TerminalUtils.clearScreen();
                    playerCharacter.useSpecialAbility();
                    actionSummary = playerCharacter.getName() + " used their special ability.";
                    Message.displayOnScreen("Press Enter to continue...");
                    scanner.nextLine();
                    actionTaken = true;
                    break;
                case "4":
                    TerminalUtils.clearScreen();
                    playerCharacter.getInventory().displayInventory();
                    Message.displayOnScreen("Press Enter to return to actions...");
                    scanner.nextLine();
                    // Re-display context after viewing inventory
                    TerminalUtils.clearScreen();
                    Message.displayOnScreen(TerminalStyler.style("\n" + Art.THE_JUNGLE.substring(0, Art.THE_JUNGLE.indexOf('\n')) + "--- Turn " + Main.getTurnCounter() + " (Action Phase Continued) ---", TerminalStyler.BOLD));
                    executeInitialPhase();
                    Message.displayOnScreen(TerminalStyler.style("\n-- Fase de Ação --", TerminalStyler.YELLOW, TerminalStyler.BOLD));
                    break;
                case "5":
                    TerminalUtils.clearScreen();
                    actionSummary = useItemFromInventory();
                    actionTaken = true;
                    break;
                case "6":
                    TerminalUtils.clearScreen();
                    actionSummary = handleCrafting();
                    actionTaken = true;
                    break;
                case "7": // View Faction Reputations
                    TerminalUtils.clearScreen();
                    actionSummary = viewFactionReputations();
                    actionTaken = true;
                    break;
                case "8": // Move
                    TerminalUtils.clearScreen();
                    String previousAmbientName = currentAmbientForAction != null ? currentAmbientForAction.getName() : "Unknown";
                    ambientController.offerMovementChoice();
                    com.ranieborges.thejungle.cli.model.world.Ambient newAmbient = ambientController.getCurrentAmbient();
                    String currentAmbientName = newAmbient != null ? newAmbient.getName() : "Unknown";
                    if (!currentAmbientName.equals(previousAmbientName)) {
                        actionSummary = playerCharacter.getName() + " moved from " + TerminalStyler.style(previousAmbientName, TerminalStyler.BLUE) + " to the " + TerminalStyler.style(currentAmbientName, TerminalStyler.BLUE) + ".";
                    } else {
                        actionSummary = playerCharacter.getName() + " considered moving but stayed in the " + TerminalStyler.style(currentAmbientName, TerminalStyler.BLUE) + ".";
                    }
                    actionTaken = true;
                    break;
                case "0":
                    Message.displayOnScreen("Are you sure you want to quit this session? (yes/no)");
                    if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                        this.previousTurnSummary = playerCharacter.getName() + " decided to end the session.";
                        return GameStatus.PLAYER_QUIT;
                    }
                    actionSummary = playerCharacter.getName() + " decided not to quit yet.";
                    actionTaken = true;
                    break;
                default:
                    Message.displayOnScreen(TerminalStyler.error("Invalid action. Try again."));
                    actionSummary = playerCharacter.getName() + " was indecisive.";
            }
            if (!playerCharacter.isAlive() || playerCharacter.getSanity() <=0) { // Check sanity here too
                return determineGameOverStatus(); // This will set appropriate summary
            }
        }
        this.previousTurnSummary = actionSummary;
        Message.displayWithDelay("", 500);
        return GameStatus.CONTINUE;
    }

    private String viewFactionReputations() {
        Message.displayOnScreen(TerminalStyler.style("--- Faction Reputations ---", TerminalStyler.YELLOW, TerminalStyler.BOLD));
        List<Faction> factions = factionManager.getAllFactions();
        if (factions.isEmpty()) {
            Message.displayOnScreen("There are no known factions in these lands.");
        } else {
            for (Faction faction : factions) {
                Message.displayOnScreen(String.format("%s: %s (%d pts) - Overall: %s",
                    TerminalStyler.style(faction.getName(), TerminalStyler.CYAN),
                    playerCharacter.getReputationLevel(faction).getDisplayName(),
                    playerCharacter.getReputationPoints(faction),
                    TerminalStyler.style(factionManager.getEffectiveDisposition(faction, playerCharacter).getDisplayName(), TerminalStyler.YELLOW)
                ));
                Message.displayOnScreen("  " + TerminalStyler.style(faction.getDescription(), TerminalStyler.BRIGHT_BLACK));
            }
        }
        Message.displayOnScreen("Press Enter to continue...");
        scanner.nextLine();
        return playerCharacter.getName() + " reviewed their standing with the local factions.";
    }

    // exploreCurrentAmbient, rest, useItemFromInventory, handleCrafting, initiateCombat,
    // executeRandomEventPhase, executeMaintenancePhase, checkSurvivalConditions, determineGameOverStatus
    // methods remain the same as in the previous version (with victory/defeat updates)
    // Ensure they use this.factionManager if they need to interact with factions.
    // For example, if defeating a "Brutal Hunter's Wolf" should decrease rep with "Caçadores Brutais".

    private String exploreCurrentAmbient() {
        TerminalUtils.clearScreen();
        com.ranieborges.thejungle.cli.model.world.Ambient current = ambientController.getCurrentAmbient();
        if (current == null) {
            Message.displayOnScreen(TerminalStyler.error("Cannot explore: Current ambient is unknown!"));
            return playerCharacter.getName() + " tried to explore but was disoriented.";
        }
        String summary = current.explore(playerCharacter); // explore might trigger faction events via EventManager
        Message.displayOnScreen("Press Enter to continue...");
        scanner.nextLine();
        return summary;
    }

    private String rest() {
        TerminalUtils.clearScreen();
        com.ranieborges.thejungle.cli.model.world.Ambient current = ambientController.getCurrentAmbient();
        Message.displayOnScreen(playerCharacter.getName() + " finds a relatively safe spot to rest in the " + TerminalStyler.style(current != null ? current.getName() : "unknown area", TerminalStyler.BLUE) + ".");
        float energyRestored = 20f + random.nextInt(11);
        playerCharacter.changeEnergy(energyRestored);
        Message.displayOnScreen(TerminalStyler.success("You feel somewhat rested. Energy restored by " + String.format("%.1f", energyRestored)));
        playerCharacter.changeHunger(-3);
        playerCharacter.changeThirst(-4);
        Message.displayWithDelay("", 1000);
        Message.displayOnScreen("Press Enter to continue...");
        scanner.nextLine();
        return playerCharacter.getName() + " rested and recovered some energy.";
    }

    private String useItemFromInventory() {
        playerCharacter.getInventory().displayInventory();
        if (playerCharacter.getInventory().getItems().isEmpty()) {
            Message.displayOnScreen(TerminalStyler.info("Inventory is empty. Nothing to use."));
            Message.displayOnScreen("Press Enter to continue...");
            scanner.nextLine();
            return playerCharacter.getName() + " tried to use an item, but the inventory was empty.";
        }
        Message.displayOnScreen("Enter the name of the item to use (or type 'cancel'): ");
        String itemName = scanner.nextLine().trim();
        if (itemName.equalsIgnoreCase("cancel") || itemName.isEmpty()) {
            return playerCharacter.getName() + " decided not to use an item.";
        }
        boolean used = playerCharacter.getInventory().useItemByName(itemName, playerCharacter);
        Message.displayWithDelay("", 1000);
        Message.displayOnScreen("Press Enter to continue...");
        scanner.nextLine();
        return playerCharacter.getName() + (used ? " used " + TerminalStyler.style(itemName, TerminalStyler.CYAN) + "." : " could not use " + itemName + " or chose not to.");
    }

    private String handleCrafting() {
        TerminalUtils.clearScreen();
        Message.displayOnScreen(TerminalStyler.style("--- Crafting ---", TerminalStyler.CYAN, TerminalStyler.BOLD));
        List<Recipe> craftableRecipes = craftingService.getCraftableRecipes(playerCharacter.getInventory(), playerCharacter);

        if (craftableRecipes.isEmpty()) {
            Message.displayOnScreen(TerminalStyler.info("You don't have the necessary ingredients, tools, or skills for any known recipes right now."));
            playerCharacter.getInventory().displayInventory();
            Message.displayOnScreen("Press Enter to continue...");
            scanner.nextLine();
            return playerCharacter.getName() + " looked for something to craft but found no options.";
        }

        Message.displayOnScreen("Available recipes you can craft:");
        for (int i = 0; i < craftableRecipes.size(); i++) {
            Message.displayOnScreen(String.format("%d. %s", i + 1, craftableRecipes.get(i).recipeName() + " (-> " + craftableRecipes.get(i).resultItemPrototype().getName() + ")"));
        }
        Message.displayOnScreen("0. Cancel Crafting");
        Message.displayOnScreen("Enter recipe number to craft: ");

        String choiceStr = scanner.nextLine().trim();
        String craftSummary = playerCharacter.getName() + " considered crafting.";
        try {
            int choice = Integer.parseInt(choiceStr);
            if (choice == 0) {
                craftSummary = playerCharacter.getName() + " decided not to craft anything.";
            } else if (choice > 0 && choice <= craftableRecipes.size()) {
                Recipe selectedRecipe = craftableRecipes.get(choice - 1);
                Message.displayOnScreen(TerminalStyler.style("\nSelected Recipe Details:", TerminalStyler.YELLOW));
                Message.displayOnScreen(selectedRecipe.toString());
                Message.displayOnScreen(TerminalStyler.style("\nAttempt to craft '" + selectedRecipe.recipeName() + "'? (yes/no)", TerminalStyler.YELLOW));
                String confirm = scanner.nextLine().trim();
                if ("yes".equalsIgnoreCase(confirm)) {
                    if (craftingService.craftItem(selectedRecipe, playerCharacter.getInventory(), playerCharacter)) {
                        craftSummary = playerCharacter.getName() + " successfully crafted " + selectedRecipe.resultItemPrototype().getName() + ".";
                    } else {
                        craftSummary = playerCharacter.getName() + " failed to craft " + selectedRecipe.recipeName() + ".";
                    }
                } else {
                    craftSummary = playerCharacter.getName() + " decided not to craft " + selectedRecipe.recipeName() + ".";
                }
            } else {
                Message.displayOnScreen(TerminalStyler.error("Invalid recipe choice."));
                craftSummary = playerCharacter.getName() + " made an invalid crafting choice.";
            }
        } catch (NumberFormatException e) {
            Message.displayOnScreen(TerminalStyler.error("Invalid input. Please enter a number."));
            craftSummary = playerCharacter.getName() + " was confused by the crafting options.";
        }

        Message.displayOnScreen("Press Enter to continue...");
        scanner.nextLine();
        return craftSummary;
    }

    public void initiateCombat(Creature creature) {
        TerminalUtils.clearScreen();
        com.ranieborges.thejungle.cli.model.world.Ambient current = ambientController.getCurrentAmbient();
        Message.displayOnScreen(TerminalStyler.style("\n--- COMBAT START in " + (current != null ? current.getName() : "an unknown location") + " ---", TerminalStyler.BRIGHT_RED, TerminalStyler.BOLD));
        creature.displayStatus();

        while (playerCharacter.isAlive() && creature.isAlive()) {
            Message.displayOnScreen(TerminalStyler.style("\nYour turn, " + playerCharacter.getName() + ":", TerminalStyler.YELLOW));
            playerCharacter.displayStatus();
            Message.displayOnScreen("Choose combat action: 1. Attack  2. Use Item  3. Attempt to Flee");
            String choice = scanner.nextLine().trim();
            boolean playerActed = false;

            switch (choice) {
                case "1":
                    Message.displayOnScreen(TerminalStyler.style(playerCharacter.getName() + " attacks " + creature.getName() + "!", TerminalStyler.BRIGHT_YELLOW));
                    creature.takeDamage(playerCharacter.getAttackDamage() + (random.nextFloat() * 10 - 5));
                    playerActed = true;
                    break;
                case "2":
                    TerminalUtils.clearScreen();
                    useItemFromInventory();
                    playerActed = true;
                    break;
                case "3":
                    Message.displayOnScreen(playerCharacter.getName() + " attempts to flee!");
                    if (random.nextFloat() < (playerCharacter.getSpeed() / (playerCharacter.getSpeed() + creature.getSpeed() + 0.1f))) {
                        Message.displayOnScreen(TerminalStyler.success("Successfully fled from " + creature.getName() + "!"));
                        Message.displayOnScreen(TerminalStyler.style("--- COMBAT END ---", TerminalStyler.BRIGHT_RED, TerminalStyler.BOLD));
                        Message.displayOnScreen("Press Enter to continue...");
                        scanner.nextLine();
                        return;
                    } else {
                        Message.displayOnScreen(TerminalStyler.warning("Failed to flee!"));
                    }
                    playerActed = true;
                    break;
                default:
                    Message.displayOnScreen(TerminalStyler.error("Invalid combat action. Turn lost."));
                    playerActed = true;
            }

            if (!creature.isAlive()) {
                List<Item> loot = creature.dropLoot();
                if (!loot.isEmpty()) {
                    Message.displayOnScreen(TerminalStyler.info(creature.getName() + " dropped:"));
                    for (Item item : loot) {
                        Message.displayOnScreen(TerminalStyler.style(" - " + item.getName(), TerminalStyler.CYAN));
                        playerCharacter.getInventory().addItem(item);
                    }
                }
                break;
            }

            if (playerActed && creature.isAlive()) {
                Message.displayOnScreen(TerminalStyler.style("\n" + creature.getName() + "'s turn:", TerminalStyler.RED));
                creature.act(playerCharacter);
                if (!playerCharacter.isAlive()) break;
            }
            Message.displayWithDelay("", 500);
        }
        Message.displayOnScreen(TerminalStyler.style("--- COMBAT END ---", TerminalStyler.BRIGHT_RED, TerminalStyler.BOLD));
        Message.displayWithDelay("", 1000);
        Message.displayOnScreen("Press Enter to continue...");
        scanner.nextLine();
    }

    private void executeRandomEventPhase() {
        Message.displayOnScreen(TerminalStyler.style("\n-- Fase de Evento Aleatório --", TerminalStyler.MAGENTA, TerminalStyler.BOLD));
        String eventOutcomeSummary = eventManager.triggerRandomEvent(playerCharacter, ambientController.getCurrentAmbient(), this); // Pass this TurnController

        if (eventOutcomeSummary != null && !eventOutcomeSummary.toLowerCase().contains("uneventful") && !eventOutcomeSummary.toLowerCase().contains("calm")) {
            this.previousTurnSummary += " " + eventOutcomeSummary;
        }
        // Pause after event is handled by FactionInteractionEvent if it uses scanner,
        // or by the "Press Enter to continue after the event..." in Event.execute() if it's a general event.
    }

    private void executeMaintenancePhase() {
        Message.displayOnScreen(TerminalStyler.style("\n-- Fase de Manutenção --", TerminalStyler.BLUE, TerminalStyler.BOLD));
        String maintenanceSummary = "Routine effects: ";

        playerCharacter.changeHunger(-10);
        maintenanceSummary += "got hungrier, ";
        playerCharacter.changeThirst(-15);
        maintenanceSummary += "thirstier. ";

        if (playerCharacter.getHunger() <= 0) {
            Message.displayOnScreen(TerminalStyler.warning(playerCharacter.getName() + " is starving!"));
            playerCharacter.changeHealth(-5);
            maintenanceSummary += "Suffered from starvation. ";
        }
        if (playerCharacter.getThirst() <= 0) {
            Message.displayOnScreen(TerminalStyler.warning(playerCharacter.getName() + " is dying of thirst!"));
            playerCharacter.changeHealth(-7);
            maintenanceSummary += "Suffered from dehydration. ";
        }

        if (playerCharacter.getHunger() > 10 && playerCharacter.getThirst() > 10) {
            playerCharacter.changeEnergy(5);
            maintenanceSummary += "Recovered some energy. ";
        }

        Message.displayOnScreen("Checking for spoiled food...");
        int spoiledCount = 0;
        if (playerCharacter.getInventory() != null && playerCharacter.getInventory().getItems() != null) {
            for (Item item : new java.util.ArrayList<>(playerCharacter.getInventory().getItems())) {
                if (item instanceof Food) {
                    Food foodItem = (Food) item;
                    boolean wasSpoiledBefore = foodItem.isSpoiled();
                    foodItem.passTurn();
                    if (!wasSpoiledBefore && foodItem.isSpoiled()) {
                        spoiledCount++;
                    }
                }
            }
        }
        if (spoiledCount > 0) {
            Message.displayOnScreen(TerminalStyler.warning(spoiledCount + (spoiledCount > 1 ? " items have" : " item has") + " spoiled in the inventory!"));
            maintenanceSummary += spoiledCount + " food item(s) spoiled. ";
        }

        if (ambientController != null) {
            ambientController.updateAmbientResources();
        }
        this.previousTurnSummary += " " + maintenanceSummary.trim();
        Message.displayOnScreen(TerminalStyler.info("Time passes..."));
        Message.displayWithDelay("", 1000);
        Message.displayOnScreen("Press Enter to end turn...");
        scanner.nextLine();
    }

    private boolean checkSurvivalConditions() {
        if (!playerCharacter.isAlive()) {
            return false;
        }
        if (playerCharacter.getSanity() <= 0) {
            Message.displayOnScreen(TerminalStyler.error(playerCharacter.getName() + " has lost their grip on reality... The jungle claims another mind."));
            return false;
        }
        return true;
    }

    private GameStatus determineGameOverStatus() {
        if (!playerCharacter.isAlive()) {
            this.previousTurnSummary = playerCharacter.getName() + " could not endure the hardships and perished.";
            return GameStatus.PLAYER_DEFEATED;
        }
        if (playerCharacter.getSanity() <= 0) {
            this.previousTurnSummary = playerCharacter.getName() + "'s mind shattered under the strain of survival.";
            return GameStatus.SURVIVAL_FAILURE;
        }
        this.previousTurnSummary = playerCharacter.getName() + " succumbed to the unforgiving jungle.";
        return GameStatus.SURVIVAL_FAILURE;
    }
}
