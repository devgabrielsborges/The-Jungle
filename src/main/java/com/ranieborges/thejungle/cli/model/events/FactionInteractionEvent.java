package com.ranieborges.thejungle.cli.model.events;

import com.ranieborges.thejungle.cli.controller.FactionManager;
import com.ranieborges.thejungle.cli.controller.TurnController;
import com.ranieborges.thejungle.cli.model.Event;
import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.Faction;
import com.ranieborges.thejungle.cli.model.world.Ambient;
import com.ranieborges.thejungle.cli.view.Message;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class FactionInteractionEvent extends Event {

    private final String targetFactionId;

    public FactionInteractionEvent(String name, String description, double baseProbability, String targetFactionId) {
        super(name, description, baseProbability);
        this.targetFactionId = targetFactionId;
    }

    public FactionInteractionEvent(String name, String description, double baseProbability) {
        this(name, description, baseProbability, null);
    }


    @Override
    public String execute(Character player, Ambient ambient, TurnController turnController) {
        announceEvent();
        FactionManager factionManager = turnController.getFactionManager(); // Assumes TurnController has getter
        Scanner scanner = turnController.getScanner(); // Assumes TurnController has getter

        if (factionManager == null || scanner == null) {
            Message.displayOnScreen(TerminalStyler.error("Faction system not available for this event."));
            return "A strange feeling passed, but nothing happened.";
        }

        Faction factionToInteract;
        if (this.targetFactionId != null) {
            Optional<Faction> factionOpt = factionManager.getFactionById(this.targetFactionId);
            if (factionOpt.isEmpty()) {
                Message.displayOnScreen(TerminalStyler.warning("Could not find the specified faction for interaction."));
                return "A planned encounter fizzled out.";
            }
            factionToInteract = factionOpt.get();
        } else {
            List<Faction> allFactions = factionManager.getAllFactions();
            if (allFactions.isEmpty()) {
                Message.displayOnScreen(TerminalStyler.info("No factions are known in these lands."));
                return "The wilderness remains devoid of organized groups.";
            }
            factionToInteract = allFactions.get(random.nextInt(allFactions.size()));
        }

        Message.displayOnScreen("You encounter members of " + TerminalStyler.style(factionToInteract.getName(), TerminalStyler.CYAN) + ".");
        Message.displayOnScreen("Their current disposition towards you appears " + TerminalStyler.style(factionManager.getEffectiveDisposition(factionToInteract, player).getDisplayName(), TerminalStyler.YELLOW) + ".");
        Message.displayOnScreen("Reputation: " + player.getReputationLevel(factionToInteract).getDisplayName() + " (" + player.getReputationPoints(factionToInteract) + " pts)");


        Message.displayOnScreen("\nWhat do you do?");
        Message.displayOnScreen("1. Offer a small gift (if you have a common material). [-1 Material, +Rep]");
        Message.displayOnScreen("2. Act cautiously and move on. [No change]");
        Message.displayOnScreen("3. Make a demanding remark. [-Rep]");

        String choice = scanner.nextLine().trim();
        String outcomeSummary = "You encountered " + factionToInteract.getName();

        switch (choice) {
            case "1":
                if (!player.getInventory().getItems().isEmpty()) {
                    Message.displayOnScreen("You offer a token of goodwill.");
                    player.changeReputation(factionToInteract, 10);
                    outcomeSummary += " and offered them a small gift, improving relations.";
                } else {
                    Message.displayOnScreen("You have nothing to offer as a gift.");
                    outcomeSummary += ", but had nothing to offer.";
                }
                break;
            case "2":
                Message.displayOnScreen("You decide to be cautious and move on without incident.");
                outcomeSummary += " and decided to move on cautiously.";
                break;
            case "3":
                Message.displayOnScreen("You make a remark that clearly offends them.");
                player.changeReputation(factionToInteract, -15); // Negative reputation change
                outcomeSummary += " and managed to offend them.";
                break;
            default:
                Message.displayOnScreen("Unsure how to proceed, you do nothing memorable.");
                outcomeSummary += " but the interaction was uneventful.";
                break;
        }
        return outcomeSummary;
    }

}
