package io.github.com.ranie_borges.thejungle.game.characters;

import io.github.com.ranie_borges.thejungle.model.Character;
import io.github.com.ranie_borges.thejungle.model.Item;
import io.github.com.ranie_borges.thejungle.model.NonPlayerCharacter;
import io.github.com.ranie_borges.thejungle.model.enums.Trait;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
//TODO
public class Doctor extends Character {

    protected Doctor(String name) {
        super(name);
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
