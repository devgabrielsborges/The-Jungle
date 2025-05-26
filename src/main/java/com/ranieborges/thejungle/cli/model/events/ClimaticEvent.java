package com.ranieborges.thejungle.cli.model.events;

import com.ranieborges.thejungle.cli.controller.TurnController; // Ensure this is imported
import com.ranieborges.thejungle.cli.model.Event;
import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.events.utils.enums.ClimateType;
import com.ranieborges.thejungle.cli.model.world.Ambient;
import com.ranieborges.thejungle.cli.view.Message;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;
import lombok.Getter;

@Getter
public class ClimaticEvent extends Event {
    private final ClimateType climateType;
    private final int durationTurns;

    public ClimaticEvent(String name, String description, double baseProbability,
                         ClimateType climateType, int durationTurns) {
        super(name, description, baseProbability);
        this.climateType = climateType;
        this.durationTurns = durationTurns;
    }

    @Override
    public String execute(Character player, Ambient ambient, TurnController turnController) { // Added turnController parameter
        announceEvent();
        String outcomeSummary = "The weather has drastically changed to a " + climateType.getDisplayName() + ".";
        Message.displayOnScreen(TerminalStyler.info("The current weather in " + ambient.getName() + " is now: " + climateType.getDisplayName()));

        ambient.setCurrentWeather(climateType.getDisplayName());

        switch (climateType) {
            case BLIZZARD:
                Message.displayOnScreen(TerminalStyler.warning("The biting cold of the blizzard chills you to the bone!"));
                player.changeEnergy(-20);
                player.changeThirst(-10); // Cold can make you thirsty, or it could be less due to snow
                player.changeSanity(-10);
                outcomeSummary += " It's freezing and hard to see.";
                break;
            case HEAVY_RAIN:
                Message.displayOnScreen(TerminalStyler.info("Heavy rain starts pouring down, soaking everything."));
                player.changeEnergy(-5);
                player.changeSanity(-3);
                outcomeSummary += " You are getting soaked.";
                break;
            case HEAT_WAVE:
                Message.displayOnScreen(TerminalStyler.warning("An intense heat wave descends, making the air thick and heavy."));
                player.changeThirst(-25);
                player.changeEnergy(-15);
                outcomeSummary += " You feel parched and exhausted.";
                break;
            case THICK_FOG:
                Message.displayOnScreen(TerminalStyler.info("A thick fog rolls in, reducing visibility significantly."));
                player.changeSanity(-5);
                outcomeSummary += " It's hard to see anything around you.";
                break;
            default:
                outcomeSummary += " The weather shift is noticeable.";
                break;
        }

        // The TurnController or an ActiveEventManager would be responsible for tracking the duration.
        // This event just sets the initial state.
        Message.displayOnScreen(TerminalStyler.debug("This climatic condition ("+ climateType.getDisplayName() +") might persist for approximately " + durationTurns + " turns. (Tracking not yet implemented)"));

        // Climatic events usually don't need the turnController directly for their immediate execution,
        // but it's passed for consistency with the Event interface.
        // The turnController might later query active climatic events to apply ongoing effects.

        return outcomeSummary;
    }

    @Override
    public boolean canOccur(Character player, Ambient ambient) {
        // Example: Blizzards might only occur in Mountains or cold weather
        if (this.climateType == ClimateType.BLIZZARD &&
                !(ambient.getName().toLowerCase().contains("mountain") || ambient.getCurrentWeather().toLowerCase().contains("cold"))) {
            return false;
        }
        // Heat wave less likely in very cold places
        if (this.climateType == ClimateType.HEAT_WAVE && ambient.getName().toLowerCase().contains("mountain") && ambient.getCurrentWeather().toLowerCase().contains("blizzard")) {
            return false;
        }
        return true;
    }
}
