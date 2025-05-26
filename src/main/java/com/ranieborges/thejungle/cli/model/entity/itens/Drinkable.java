package com.ranieborges.thejungle.cli.model.entity.itens;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.Purity;
import lombok.Getter;

/**
 * Represents a drinkable item in the game.
 * Drinkable items can restore thirst and may have different purity levels.
 * The purity level affects the chance of contracting diseases when consumed.
 */
@Getter
public class Drinkable extends Item {

    private final float thirstRestored;
    private final Purity purity;
    private final float diseaseChance;

    /**
     * Constructor for a Drinkable item.
     * Drinkable items are typically consumable and not durable.
     *
     * @param name           The name of the drinkable item (e.g., "Clean Water", "Murky Puddle Water").
     * @param description    A brief description.
     * @param weight         The weight of the item.
     * @param thirstRestored The amount of thirst this item restores.
     * @param purity         The purity level of the drinkable item.
     * @param diseaseChance  The chance (0.0 to 1.0) of contracting a disease if purity is CONTAMINATED.
     */
    public Drinkable(String name, String description, float weight, float thirstRestored, Purity purity, float diseaseChance) {
        super(name, description, weight);
        if (thirstRestored < 0) {
            throw new IllegalArgumentException("Thirst restored cannot be negative.");
        }
        if (diseaseChance < 0 || diseaseChance > 1.0) {
            throw new IllegalArgumentException("Disease chance must be between 0.0 and 1.0.");
        }
        this.thirstRestored = thirstRestored;
        this.purity = purity;
        this.diseaseChance = (purity == Purity.CONTAMINATED) ? diseaseChance : 0; // Disease only if contaminated
    }

    /**
     * Simplified constructor for clearly potable water with no disease chance.
     */
    public Drinkable(String name, String description, float weight, float thirstRestored) {
        this(name, description, weight, thirstRestored, Purity.POTABLE, 0f);
    }

    /**
     * The character consumes the drinkable item. Thirst is restored.
     * If the water is contaminated, there's a chance of negative effects (e.g., disease, sanity loss).
     *
     * @param user The character using (drinking) the item.
     * @return true, as drinkable items are typically consumed.
     */
    @Override
    public boolean use(Character user) {
        System.out.println(user.getName() + " drinks " + getName() + ".");
        user.changeThirst(this.thirstRestored); // Positive value should increase thirst stat towards max
        System.out.println(user.getName() + "'s thirst is now " + String.format("%.1f", user.getThirst()) + "/" + Character.CHARACTER_DEFAULT_MAX_STAT);

        if (this.purity == Purity.CONTAMINATED) {
            System.out.println("The " + getName() + " looks " + this.purity.getDisplayName().toLowerCase() + "...");
            if (Math.random() < this.diseaseChance) {
                // Apply negative effects - this is conceptual and needs further game mechanics
                // For example, set a "diseased" status on the character, or reduce health/sanity.
                System.out.println(user.getName() + " feels unwell after drinking it!");
                user.changeHealth(-10); // Example: lose 10 health
                user.changeSanity(-5);  // Example: lose 5 sanity
            } else {
                System.out.println(user.getName() + " seems to have gotten away with it... for now.");
            }
        } else if (this.purity == Purity.UNKNOWN) {
            System.out.println("The purity of " + getName() + " is uncertain. " + user.getName() + " takes a risk.");
            // Could have a smaller chance of negative effects or a mix of outcomes
            if (Math.random() < (this.diseaseChance / 2)) { // Example: half chance if unknown
                System.out.println(user.getName() + " feels a bit queasy.");
                user.changeSanity(-2);
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s (Thirst: +%.1f, Purity: %s, Wt: %.1f)",
                getName(), thirstRestored, purity.getDisplayName(), getWeight());
    }
}
