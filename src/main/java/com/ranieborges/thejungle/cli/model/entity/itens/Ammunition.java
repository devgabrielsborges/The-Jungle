package com.ranieborges.thejungle.cli.model.entity.itens;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.AmmunitionType;
import com.ranieborges.thejungle.cli.view.Message;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;
import lombok.Getter;

/**
 * Represents an ammunition item in the game.
 * Ammunition is consumed by weapons.
 */
@Getter
public class Ammunition extends Item {
    private final AmmunitionType ammunitionType;
    private int quantity;

    /**
     * Constructor for Ammunition item.
     *
     * @param name             The name of the ammunition (e.g., "Arrow", "9mm Bullets").
     * @param description      A brief description.
     * @param weight           The weight of a single unit of this ammunition.
     * @param ammunitionType   The type of ammunition.
     * @param initialQuantity  The initial quantity of this ammunition stack.
     */
    public Ammunition(String name, String description, float weight, AmmunitionType ammunitionType, int initialQuantity) {
        super(name, description, weight * initialQuantity); // Total weight is per unit * quantity
        if (initialQuantity <= 0) {
            throw new IllegalArgumentException("Initial quantity must be positive.");
        }
        this.ammunitionType = ammunitionType;
        this.quantity = initialQuantity;
    }

    /**
     * "Using" ammunition directly doesn't do much. It's consumed by weapons.
     * @param user The character using the item.
     * @return false, as ammunition is not "used" in this way.
     */
    @Override
    public boolean use(Character user) {
        Message.displayOnScreen(TerminalStyler.info(getName() + " is ammunition, load it into a compatible weapon."));
        return false; // Not consumed by direct use
    }

    /**
     * Decreases the quantity of ammunition in this stack.
     * @param amount The amount to decrease by.
     * @return true if the stack is depleted (quantity becomes 0 or less).
     */
    public boolean decreaseQuantity(int amount) {
        if (amount <= 0) return false;
        this.quantity -= amount;
        if (this.quantity <= 0) {
            this.quantity = 0;
            return true;
        }

        return false;
    }

    /**
     * Increases the quantity of ammunition in this stack.
     * Typically used when picking up more of the same ammo.
     * @param amount The amount to increase by.
     */
    public void increaseQuantity(int amount) {
        if (amount <= 0) return;
        this.quantity += amount;
    }


    @Override
    public String toString() {
        return String.format("%s (Type: %s, Qty: %d, Wt: %.2f total)",
                getName(), ammunitionType.getDisplayName(), quantity, getWeight());
    }


}
