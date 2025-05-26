package com.ranieborges.thejungle.cli.model.events;

import com.ranieborges.thejungle.cli.controller.TurnController;
import com.ranieborges.thejungle.cli.model.Event;
import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.world.Ambient;
import com.ranieborges.thejungle.cli.view.Message;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DiscoveryEvent extends Event {

    public enum DiscoveryType {
        ABANDONED_SHELTER("Abrigo Abandonado"),
        WATER_SOURCE("Fonte de Água"),
        MYSTERIOUS_RUINS_DETAIL("Detalhe em Ruínas Misteriosas"), // More specific than just being in Ruins ambient
        RARE_RESOURCE_NODE("Nódulo de Recurso Raro"),
        HIDDEN_CACHE("Esconderijo Secreto");

        private final String displayName;
        DiscoveryType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    private final DiscoveryType discoveryType;
    // "Recursos encontrados" - could be a list of potential items or a direct item.
    private final List<Item> itemsFound; // For simplicity, let's say this event can grant specific items.

    public DiscoveryEvent(String name, String description, double baseProbability,
                          DiscoveryType discoveryType, List<Item> itemsFound) {
        super(name, description, baseProbability);
        this.discoveryType = discoveryType;
        this.itemsFound = (itemsFound != null) ? itemsFound : new ArrayList<>();
    }
    public DiscoveryEvent(String name, String description, double baseProbability,
                          DiscoveryType discoveryType) {
        this(name, description, baseProbability, discoveryType, new ArrayList<>());
    }


    @Override
    public String execute(Character player, Ambient ambient, TurnController turnController) {
        announceEvent();
        String outcomeSummary = "Discovered a " + discoveryType.getDisplayName() + ".";
        Message.displayOnScreen(TerminalStyler.success("You've made a discovery: " + discoveryType.getDisplayName() + "!"));

        if (!itemsFound.isEmpty()) {
            Message.displayOnScreen(TerminalStyler.info("You found the following items:"));
            for (Item item : itemsFound) {
                Message.displayOnScreen(" - " + item.getName() + " (" + item.getDescription() + ")");
                if (player.getInventory().addItem(item)) {
                    outcomeSummary += " Found " + item.getName() + ".";
                } else {
                    Message.displayOnScreen(TerminalStyler.warning("Could not pick up " + item.getName() + ", inventory full!"));
                    outcomeSummary += " Found " + item.getName() + " but couldn't carry it.";
                }
            }
        }

        // Specific effects based on discovery type
        switch (discoveryType) {
            case ABANDONED_SHELTER:
                Message.displayOnScreen("This shelter could offer temporary protection or contain more supplies.");
                // Could change ambient, or offer a "rest here" option with bonuses.
                // Could also have a chance of being occupied (triggering another event/encounter).
                if (random.nextDouble() < 0.2) { // 20% chance it's occupied
                    Message.displayOnScreen(TerminalStyler.warning("But wait... you hear sounds from inside!"));
                    // TODO: Trigger a CreatureEncounterEvent or NPC encounter
                    outcomeSummary += " The shelter seemed occupied!";
                }
                break;
            case WATER_SOURCE:
                Message.displayOnScreen("A clean source of water! You can drink or fill containers.");
                // Player could interact to get Drinkable items.
                player.changeThirst(30); // Immediate small thirst quench
                outcomeSummary += " Quenched some thirst at the source.";
                break;
            case MYSTERIOUS_RUINS_DETAIL:
                Message.displayOnScreen("You notice an unusual carving or artifact. It might be significant.");
                player.changeSanity(5); // Intrigue or minor understanding
                // Could give a clue or a unique item.
                outcomeSummary += " Found an intriguing detail.";
                break;
            default:
                break;
        }
        return outcomeSummary;
    }

    @Override
    public boolean canOccur(Character player, Ambient ambient) {
        // Example: Mysterious Ruins Detail only in Ruins ambient
        if (this.discoveryType == DiscoveryType.MYSTERIOUS_RUINS_DETAIL && !(ambient.getName().contains("Ruins"))) {
            return false;
        }
        // Water source more likely near Lake/River or in Jungle
        if (this.discoveryType == DiscoveryType.WATER_SOURCE && (ambient.getName().contains("Mountain") || ambient.getName().contains("Ruins"))) {
            return random.nextDouble() < 0.1; // Less likely in these ambients
        }
        return true;
    }
}
