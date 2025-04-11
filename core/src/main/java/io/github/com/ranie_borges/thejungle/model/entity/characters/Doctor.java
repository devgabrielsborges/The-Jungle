package io.github.com.ranie_borges.thejungle.model.entity.characters;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.entity.NonPlayerCharacter;

import java.util.ArrayList;
import java.util.List;

public class Doctor extends Character {

    private int health;
    private int maxInventorySize;
    private double attackPower;
    private List<Item> inventory;

    protected Doctor(String name) {
        super(name);
        this.health = 80;
        this.attackPower = 7.0;
        this.maxInventorySize = 5;
        this.inventory = new ArrayList<>();
    }

    @Override
    public void dropItem(Item item) {
        inventory.remove(item);
        System.out.println(getName() + " dropped item: " + item.getName());
    }


    @Override
    public boolean attack(double attackDamage, NonPlayerCharacter npc) {
        if (npc == null) return false;
        System.out.println(getName() + " attacks " + npc.getName() + " with " + attackDamage + " damage.");
        return npc.takeDamage(attackDamage);
    }

    @Override
    public boolean avoidFight(boolean hasTraitLucky) {
        boolean avoided = hasTraitLucky && Math.random() > 0.3;
        System.out.println(getName() + (avoided ? " avoided" : " couldn't avoid") + " the fight.");
        return avoided;
    }


    @Override
    public void collectItem(Item nearbyItem, boolean isInventoryFull) {
        if (nearbyItem != null && !isInventoryFull && inventory.size() < maxInventorySize) {
            inventory.add(nearbyItem);
            System.out.println(getName() + " collected: " + nearbyItem.getName());
        } else {
            System.out.println(getName() + " couldn't collect the item.");
        }
    }

    @Override
    public void drink(boolean hasDrinkableItem) {
        if (hasDrinkableItem) {
            health = Math.min(health + 10, 100);
            System.out.println(getName() + " drank something and recovered health. Current health: " + health);
        } else {
            System.out.println(getName() + " has nothing to drink.");
        }
    }

    @Override
    public void useItem(Item item) {
        if (item != null && inventory.contains(item)) {
            System.out.println(getName() + " used: " + item.getName());
            inventory.remove(item);
            // Aqui você pode aplicar o efeito do item, se necessário
        } else {
            System.out.println(getName() + " doesn't have the item: " + (item != null ? item.getName() : "null"));
        }
    }
}
