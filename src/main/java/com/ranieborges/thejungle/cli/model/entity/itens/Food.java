package com.ranieborges.thejungle.cli.model.entity.itens;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.FoodType;
import com.ranieborges.thejungle.cli.view.Message;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler; // For styled messages
import lombok.Getter;

/**
 * Represents a food item, corresponding to "Alimentos" from the PDF.
 * These items primarily restore hunger and can have various types and spoilage characteristics.
 */
@Getter
public class Food extends Item {

    private final float nutritionalValue;
    private final FoodType foodType;
    private int turnsUntilSpoiled;
    private final float sicknessChanceOnSpoil;
    private final float sicknessChanceWhenRaw;

    public Food(String name, String description, float weight, float nutritionalValue, FoodType foodType,
                int turnsUntilSpoiled, float sicknessChanceOnSpoil, float sicknessChanceWhenRaw) {
        super(name, description, weight);
        if (nutritionalValue < 0) {
            throw new IllegalArgumentException("Nutritional value cannot be negative.");
        }
        if (sicknessChanceOnSpoil < 0 || sicknessChanceOnSpoil > 1.0) {
            throw new IllegalArgumentException("Sickness chance on spoil must be between 0.0 and 1.0.");
        }
        if (sicknessChanceWhenRaw < 0 || sicknessChanceWhenRaw > 1.0) {
            throw new IllegalArgumentException("Sickness chance when raw must be between 0.0 and 1.0.");
        }

        this.nutritionalValue = nutritionalValue;
        this.foodType = foodType;
        this.turnsUntilSpoiled = turnsUntilSpoiled;
        this.sicknessChanceOnSpoil = sicknessChanceOnSpoil;

        this.sicknessChanceWhenRaw = (foodType == FoodType.MEAT_RAW || foodType == FoodType.MUSHROOM /* Add other risky raw types here */) ? sicknessChanceWhenRaw : 0.0f;
    }

    public Food(String name, String description, float weight, float nutritionalValue, FoodType foodType, float sicknessChanceWhenRawIfApplicable) {
        this(name, description, weight, nutritionalValue, foodType, Integer.MAX_VALUE, 0.0f, sicknessChanceWhenRawIfApplicable);
    }

    public Food(String name, String description, float weight, float nutritionalValue, FoodType foodType) {
        this(name, description, weight, nutritionalValue, foodType, Integer.MAX_VALUE, 0.0f, 0.0f);
    }

    public boolean isSpoiled() {
        return this.turnsUntilSpoiled <= 0;
    }

    @Override
    public boolean use(Character user) {
        Message.displayOnScreen(user.getName() + " eats " + getName() + ".");

        if (isSpoiled()) {
            Message.displayOnScreen(TerminalStyler.warning("The " + getName() + " is spoiled! Eating it is risky..."));
            user.changeHunger(this.nutritionalValue / 3);
            Message.displayOnScreen(user.getName() + "'s hunger is now " + String.format("%.1f", user.getHunger()) + "/" + Character.characterDefaultMaxStat);
            if (Math.random() < this.sicknessChanceOnSpoil) {
                Message.displayOnScreen(TerminalStyler.error(user.getName() + " feels sick after eating the spoiled " + getName() + "!"));
                user.changeHealth(-15);
                user.changeSanity(-10);
            }
        } else if (this.foodType == FoodType.MEAT_RAW && this.sicknessChanceWhenRaw > 0 && Math.random() < this.sicknessChanceWhenRaw) {
            Message.displayOnScreen(TerminalStyler.warning("Eating raw meat is risky..."));
            user.changeHunger(this.nutritionalValue);
            Message.displayOnScreen(user.getName() + "'s hunger is now " + String.format("%.1f", user.getHunger()) + "/" + Character.characterDefaultMaxStat);
            Message.displayOnScreen(TerminalStyler.error(user.getName() + " gets sick from the raw meat!"));
            user.changeHealth(-10);
            user.changeSanity(-5);
        } else if (this.foodType == FoodType.MUSHROOM && this.sicknessChanceWhenRaw > 0 && Math.random() < this.sicknessChanceWhenRaw) {
            Message.displayOnScreen(TerminalStyler.warning("This " + getName() + " tastes strange..."));
            user.changeHunger(this.nutritionalValue);
            if (Math.random() < 0.7) {
                Message.displayOnScreen(TerminalStyler.error(user.getName() + " feels disoriented and nauseous!"));
                user.changeSanity(-15);
                user.changeHealth(-5);
            } else {
                Message.displayOnScreen("It wasn't so bad... or was it?");
            }
        }
        else {
            user.changeHunger(this.nutritionalValue);
            Message.displayOnScreen(user.getName() + "'s hunger is now " + String.format("%.1f", user.getHunger()) + "/" + Character.characterDefaultMaxStat);
        }

        return true;
    }

    @Override
    public String toString() {
        String spoilInfo = "";
        if (turnsUntilSpoiled > 0 && turnsUntilSpoiled < Integer.MAX_VALUE) {
            spoilInfo = ", Spoils in: " + turnsUntilSpoiled + " turns";
        } else if (isSpoiled()) {
            spoilInfo = TerminalStyler.style(", SPOILED!", TerminalStyler.BRIGHT_RED);
        }
        return String.format("%s (Hunger: +%.1f, Type: %s, Wt: %.1f%s)",
                getName(), nutritionalValue, foodType.getDisplayName(), getWeight(), spoilInfo);
    }

    public void passTurn() {
        if (this.turnsUntilSpoiled > 0 && this.turnsUntilSpoiled != Integer.MAX_VALUE) {
            this.turnsUntilSpoiled--;
            if (this.turnsUntilSpoiled == 0) {
                Message.displayOnScreen(TerminalStyler.warning(getName() + " has spoiled!"));
            }
        }
    }
}
