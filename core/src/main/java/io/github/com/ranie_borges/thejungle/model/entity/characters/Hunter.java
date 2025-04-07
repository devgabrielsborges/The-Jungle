package io.github.com.ranie_borges.thejungle.model.entity.characters;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.entity.NonPlayerCharacter;

//TODO implement default attributes for Character
public class Hunter extends Character {

    protected Hunter(String name) {
        super(name);
    }

    /**
     * @param item
     */
    @Override
    public void dropItem(Item item) {

    }

    /**
     *
     */
    @Override
    public void defend() {

    }

    /**
     * @param attackDamage The amount of damage to inflict
     * @param npc          The non-player character target
     * @return
     */
    @Override
    public boolean attack(double attackDamage, NonPlayerCharacter npc) {
        return false;
    }

    /**
     * @param hasTraitLucky Whether the character has the lucky trait
     * @return
     */
    @Override
    public boolean avoidFight(boolean hasTraitLucky) {
        return false;
    }

    /**
     * @param hasItemNear     Whether an item is available nearby
     * @param isInventoryFull Whether the character's inventory is full
     */
    @Override
    public void collectItem(boolean hasItemNear, boolean isInventoryFull) {

    }

    /**
     * @param hasDrinkableItem Whether the character has a drinkable item
     */
    @Override
    public void drink(boolean hasDrinkableItem) {

    }

    /**
     * @param item The item to use
     */
    @Override
    public void useItem(Item item) {

    }
}
