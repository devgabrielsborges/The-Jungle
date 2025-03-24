package io.github.com.ranie_borges.thejungle.model.entity.interfaces;

import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.entity.NonPlayerCharacter;

public interface ICharacter {
    /**
     * Attacks a non-player character with the specified damage
     *
     * @param attackDamage The amount of damage to inflict
     * @param npc The non-player character target
     * @return true if attack was successful, false otherwise
     */
    boolean attack(double attackDamage, NonPlayerCharacter npc);    // returns if the attack was succeeded of failed
    /**
     * Attempts to avoid a fight based on luck and other factors
     *
     * @param hasTraitLucky Whether the character has the lucky trait
     * @return true if fight was successfully avoided, false otherwise
     */
    boolean avoidFight(boolean hasTraitLucky);   // if the player has Trait.LUCKY, he can avoid the fight
    /**
     * Collects an item from the environment if conditions allow
     *
     * @param hasItemNear Whether an item is available nearby
     * @param isInventoryFull Whether the character's inventory is full
     */
    void collectItem(boolean hasItemNear, boolean isInventoryFull);
    /**
     * Consumes a drinkable item to replenish thirst
     *
     * @param hasDrinkableItem Whether the character has a drinkable item
     */
    void drink(boolean hasDrinkableItem);
    /**
     * Uses an item from the inventory
     *
     * @param item The item to use
     */
    void useItem(Item item);
}
