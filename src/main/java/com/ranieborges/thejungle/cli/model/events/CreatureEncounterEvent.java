package com.ranieborges.thejungle.cli.model.events;

import com.ranieborges.thejungle.cli.controller.TurnController;
import com.ranieborges.thejungle.cli.model.Event;
import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Creature;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.Hostility;
import com.ranieborges.thejungle.cli.model.world.Ambient;
import com.ranieborges.thejungle.cli.view.Message;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;
import lombok.Getter;

import java.util.List;

@Getter
public class CreatureEncounterEvent extends Event {

    private final String creatureClassName;

    public CreatureEncounterEvent(String name, String description, double baseProbability,
                                  Class<? extends Creature> creatureClassToSpawn) {
        super(name, description, baseProbability);
        if (creatureClassToSpawn == null) {
            throw new IllegalArgumentException("Creature class to spawn cannot be null.");
        }
        this.creatureClassName = creatureClassToSpawn.getName();
    }

    public Class<? extends Creature> getCreatureClass() {
        try {
            return (Class<? extends Creature>) Class.forName(creatureClassName);
        } catch (ClassNotFoundException e) {
            System.err.println(TerminalStyler.error("Error finding creature class for event: " + creatureClassName + " - " + e.getMessage()));
            return null;
        }
    }


    @Override
    public String execute(Character player, Ambient ambient, TurnController turnController) {
        announceEvent();
        String outcomeSummary = "";

        Class<? extends Creature> creatureClass = getCreatureClass();
        if (creatureClass == null) {
            Message.displayOnScreen(TerminalStyler.error("Failed to identify creature for event due to missing class: " + creatureClassName));
            return "A mysterious presence was felt, but nothing materialized.";
        }

        try {
            Creature creature = creatureClass.getDeclaredConstructor().newInstance();
            Message.displayOnScreen(TerminalStyler.style("A wild " + creature.getName() + " appears!", TerminalStyler.BRIGHT_RED, TerminalStyler.BOLD));
            outcomeSummary = "Encountered a " + creature.getName() + ".";

            if (turnController != null) {
                turnController.initiateCombat(creature);
                if (!player.isAlive()) {
                    outcomeSummary += " The " + creature.getName() + " proved too strong!";
                } else if (!creature.isAlive()) {
                    outcomeSummary += " The " + creature.getName() + " was defeated.";
                } else {
                    outcomeSummary += " The encounter with the " + creature.getName() + " concluded.";
                }
            } else {
                Message.displayOnScreen(TerminalStyler.warning("Combat with " + creature.getName() + " would begin here (TurnController not available)."));
                if (creature.getHostility() == Hostility.HOSTILE) {
                    Message.displayOnScreen(creature.getName() + " looks aggressive!");
                    creature.attack(player);
                    outcomeSummary += " It attacked immediately!";
                }
            }

        } catch (Exception e) {
            Message.displayOnScreen(TerminalStyler.error("Failed to spawn creature ("+ creatureClass.getSimpleName() +") for event: " + e.getMessage()));
            outcomeSummary = "Something stirred in the shadows, but then vanished.";
            e.printStackTrace(); // For debugging
        }

        return outcomeSummary;
    }

    @Override
    public boolean canOccur(Character player, Ambient ambient) {
        if (ambient == null) return false;
        List<Class<? extends Creature>> typicalCreatures = ambient.getTypicalCreatures();
        Class<? extends Creature> creatureClassToSpawn = getCreatureClass();

        if (creatureClassToSpawn == null) return false;

        return (typicalCreatures != null && typicalCreatures.contains(creatureClassToSpawn)) ||
                (typicalCreatures == null || typicalCreatures.isEmpty());
    }
}
