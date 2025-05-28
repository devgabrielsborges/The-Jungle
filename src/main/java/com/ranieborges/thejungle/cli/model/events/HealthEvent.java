package com.ranieborges.thejungle.cli.model.events;

import com.ranieborges.thejungle.cli.controller.TurnController;
import com.ranieborges.thejungle.cli.model.Event;
import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.events.utils.enums.AfflictionType;
import com.ranieborges.thejungle.cli.model.world.Ambient;
import com.ranieborges.thejungle.cli.view.Message;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;
import lombok.Getter;

@Getter
public class HealthEvent extends Event {
    private final AfflictionType afflictionType;
    private final float healthImpact;
    private final float sanityImpact;
    private final float energyImpact;

    public HealthEvent(String name, String description, double baseProbability,
                       AfflictionType afflictionType, float healthImpact, float sanityImpact, float energyImpact) {
        super(name, description, baseProbability);
        this.afflictionType = afflictionType;
        this.healthImpact = healthImpact;
        this.sanityImpact = sanityImpact;
        this.energyImpact = energyImpact;
    }

    @Override
    public String execute(Character player, Ambient ambient, TurnController turnController) {
        announceEvent();
        String outcomeSummary = "Suffered from " + afflictionType.getDisplayName() + ".";
        Message.displayOnScreen(TerminalStyler.warning("You've been afflicted with: " + afflictionType.getDisplayName() + "!"));

        if (healthImpact != 0) player.changeHealth(healthImpact);
        if (sanityImpact != 0) player.changeSanity(sanityImpact);
        if (energyImpact != 0) player.changeEnergy(energyImpact);

        switch (afflictionType) {
            case SPRAINED_ANKLE:
                Message.displayOnScreen("Your ankle throbs with pain, making movement difficult.");
                outcomeSummary += " Movement is impaired.";
                break;
            case INFECTION:
                Message.displayOnScreen(TerminalStyler.BRIGHT_RED + "An old wound seems to be infected! It's getting worse." + TerminalStyler.RESET);
                outcomeSummary += " An infection has set in.";
                break;
            case HYPOTHERMIA:
                Message.displayOnScreen(TerminalStyler.BRIGHT_BLUE + "You're shivering uncontrollably. The cold is seeping into your bones." + TerminalStyler.RESET);
                outcomeSummary += " Hypothermia is setting in.";
                break;
            default:
                break;
        }
        player.displayStatus();
        return outcomeSummary;
    }

    @Override
    public boolean canOccur(Character player, Ambient ambient) {
        if (this.afflictionType == AfflictionType.HYPOTHERMIA) {
            return ambient.getCurrentWeather().contains("Cold") || ambient.getCurrentWeather().contains("Blizzard") || ambient.getName().contains("Mountain");
        }
        if (this.afflictionType == AfflictionType.INFECTION) {
            return player.getHealth() < Character.characterDefaultMaxHealth * 0.5; // Example: more likely if already wounded
        }
        return true;
    }
}
