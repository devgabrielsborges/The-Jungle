package com.ranieborges.thejungle.cli.model;

import com.ranieborges.thejungle.cli.controller.TurnController; // Added
import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.world.Ambient;
import com.ranieborges.thejungle.cli.view.Message;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;
import lombok.Getter;

import java.util.Random;


@Getter
public abstract class Event {

    private final String name;
    private final String description;
    private final double baseProbability;

    protected static final Random random = new Random();

    public Event(String name, String description, double baseProbability) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Event name cannot be null or empty.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Event description cannot be null or empty.");
        }
        if (baseProbability < 0.0 || baseProbability > 1.0) {
            throw new IllegalArgumentException("Event probability must be between 0.0 and 1.0.");
        }
        this.name = name;
        this.description = description;
        this.baseProbability = baseProbability;
    }

    public abstract String execute(Character player, Ambient ambient, TurnController turnController);

    public boolean canOccur(Character player, Ambient ambient) {
        return true;
    }

    protected void announceEvent() {
        Message.displayOnScreen(TerminalStyler.style("\n--- EVENT: " + getName() + " ---", TerminalStyler.BRIGHT_YELLOW, TerminalStyler.BOLD));
        Message.displayCharByCharWithDelay(TerminalStyler.style(getDescription(), TerminalStyler.YELLOW), 50);
    }
}
