package io.github.com.ranie_borges.thejungle.model.entity.characters;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.entity.NonPlayerCharacter;

import java.util.ArrayList;
import java.util.List;

//TODO implement default attributes for Character
public class Hunter extends Character {
    private int health;
    private int maxInventorySize;
    private double attackPower;
    private List<Item> inventory;

    protected Hunter(String name) {
        super(name);
        this.health = 100;
        this.attackPower = 12.0	;
        this.maxInventorySize = 5;
        this.inventory = new ArrayList<>();    }

    /**
     * @param item
     */
    @Override
    public void dropItem(Item item) {
        inventory.remove(item);
        System.out.println(getName() + " dropped item: " + item.getName());
    }

    /**
     *
     */


    /**
     * @param attackDamage The amount of damage to inflict
     * @param npc          The non-player character target
     * @return
     */
    @Override
    public boolean attack(double attackDamage, NonPlayerCharacter npc) {
        if (npc == null) return false;
        double totalDamage = attackDamage + this.attackPower;
        System.out.println(getName() + " strikes " + npc.getName() + " with " + totalDamage + " damage.");
        return npc.takeDamage(totalDamage);
    }

    /**
     * @param hasTraitLucky Whether the character has the lucky trait
     * @return
     */
    @Override
    public boolean avoidFight(boolean hasTraitLucky) {
        boolean avoided = hasTraitLucky && Math.random() > 0.6; //mudar para evitar luta
        System.out.println(getName() + (avoided ? " avoided" : " couldn't avoid") + " the fight.");
        return avoided;
    }

    /**
     * @param nearbyItem     Whether an item is available nearby
     * @param isInventoryFull Whether the character's inventory is full
     */
    @Override
    public void collectItem(Item nearbyItem, boolean isInventoryFull) {
        if (nearbyItem != null && !isInventoryFull && inventory.size() < maxInventorySize) {
            inventory.add(nearbyItem);
            System.out.println(getName() + " collected: " + nearbyItem.getName());
        } else {
            System.out.println(getName() + " couldn't collect the item.");
        }
    }

    /**
     * @param hasDrinkableItem Whether the character has a drinkable item
     */
    @Override
    public void drink(boolean hasDrinkableItem) {
        if (hasDrinkableItem) {
            health = Math.min(health + 5, 100); // alterar a taxa de quanto cura
            System.out.println(getName() + " drank and recovered health. Current health: " + health);
        } else {
            System.out.println(getName() + " has nothing to drink.");
        }
    }

    /**
     * @param item The item to use
     */
    @Override
    public void useItem(Item item) {
        if (item != null && inventory.contains(item)) {
            System.out.println(getName() + " used: " + item.getName());
            inventory.remove(item);
            // aplicar efeito do item se necessário
        } else {
            System.out.println(getName() + " doesn't have the item: " + (item != null ? item.getName() : "null"));
        }
    }
}
