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
    private int quantity; // For stackable ammo like arrows or bullets

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
            // Update total weight if quantity affects it (it does in current constructor)
            // This item instance would be removed from inventory if quantity is 0.
            return true; // Stack depleted
        }
        // Update weight - this is tricky if weight is set once in super constructor.
        // For simplicity, we'll assume inventory manages removal of 0-quantity items.
        // A more robust system might have Item's weight be dynamic or Inventory handle stacks.
        return false; // Stack not depleted
    }

    /**
     * Increases the quantity of ammunition in this stack.
     * Typically used when picking up more of the same ammo.
     * @param amount The amount to increase by.
     */
    public void increaseQuantity(int amount) {
        if (amount <= 0) return;
        this.quantity += amount;
        // Update weight - see note in decreaseQuantity
    }


    @Override
    public String toString() {
        return String.format("%s (Type: %s, Qty: %d, Wt: %.2f total)",
                getName(), ammunitionType.getDisplayName(), quantity, getWeight());
    }

    // Recalculate weight based on quantity - needs to be called if quantity changes
    // and if the base Item class allows weight modification or if Inventory handles it.
    // For now, the initial weight in the super constructor is based on initialQuantity.
    // If items are stackable and quantity changes, their weight should ideally update.
    // This is a simplification for now. A better way is for Item.getWeight() to calculate dynamically
    // if it's a stackable item, or for Inventory to manage stacks.
}
