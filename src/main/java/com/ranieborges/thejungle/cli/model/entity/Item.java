package com.ranieborges.thejungle.cli.model.entity;

import lombok.Getter;
import com.ranieborges.thejungle.cli.view.Message;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;

/**
 * Abstract base class for all items in the game "Última Fronteira".
 * Corresponds to "Superclasse: Item" from the PDF.
 */
@Getter // Lombok will generate getters for all fields
public abstract class Item {

    private final String name; // "Nome: Identificação do item."
    private final float weight;  // "Peso: Influencia a quantidade de itens que o personagem pode carregar."
    private int durability;    // "Durabilidade: Alguns itens se desgastam com o uso e podem quebrar."
    private final int maxDurability; // To know the original durability
    private final String description; // A short description for the item

    /**
     * Constructor for items that have durability.
     *
     * @param name        The name of the item.
     * @param description A brief description of the item.
     * @param weight      The weight of the item.
     * @param durability  The initial and maximum durability of the item.
     */
    public Item(String name, String description, float weight, int durability) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be null or empty.");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("Item weight cannot be negative.");
        }
        if (durability < 0) {
            throw new IllegalArgumentException("Item durability cannot be negative.");
        }
        this.name = name;
        this.description = description;
        this.weight = weight;
        this.maxDurability = durability;
        this.durability = durability;
    }

    /**
     * Constructor for items that do not have durability (or infinite durability).
     *
     * @param name        The name of the item.
     * @param description A brief description of the item.
     * @param weight      The weight of the item.
     */
    public Item(String name, String description, float weight) {
        this(name, description, weight, 0); // Durability 0 means not applicable or infinite
    }

    /**
     * Abstract method to define the action of using the item.
     * Subclasses will implement the specific behavior.
     * Corresponds to "métodos como 'usar()' são definidos na superclasse" from the PDF.
     *
     * @param user The character using the item.
     * @return true if the item should be removed from inventory after use (e.g., consumed or broken), false otherwise.
     */
    public abstract boolean use(Character user);

    /**
     * Sets the current durability of the item.
     * Ensures durability does not go below 0 or exceed maxDurability.
     *
     * @param durability The new durability value.
     */
    public void setDurability(int durability) {
        if (this.maxDurability > 0) { // Only for items that are meant to have durability
            this.durability = Math.max(0, Math.min(durability, this.maxDurability));
        } else {
            // For items with no maxDurability (e.g., constructed with durability 0),
            // durability remains unchanged or is considered not applicable.
            this.durability = 0;
        }
    }

    /**
     * Decreases the item's durability by a specified amount.
     * Typically called within the use() method of durable items.
     *
     * @param amount The amount to decrease durability by.
     * @return true if the item broke (durability reached 0) as a result, false otherwise.
     */
    protected boolean decreaseDurability(int amount) {
        if (this.maxDurability > 0) { // Only if it's a durable item
            this.setDurability(this.durability - amount);
            if (this.durability == 0) {
                Message.displayOnScreen(TerminalStyler.error(this.name + " broke!"));
                return true; // Item broke
            }
        }
        return false; // Item not broken or not durable
    }

    /**
     * Decreases the item's durability by 1.
     * @return true if the item broke, false otherwise.
     */
    public boolean decreaseDurability() {
        return decreaseDurability(1);
    }


    @Override
    public String toString() {
        String durabilityInfo = "";
        if (maxDurability > 0) {
            durabilityInfo = String.format(", Dur: %d/%d", durability, maxDurability);
        }
        return String.format("%s (Desc: %s, Wt: %.1f%s)", name, description, weight, durabilityInfo);
    }
    
}
