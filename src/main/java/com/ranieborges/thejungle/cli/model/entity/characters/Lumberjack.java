package com.ranieborges.thejungle.cli.model.entity.characters;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.Trait;

import java.util.EnumSet;

/**
 * Represents the Lumberjack character class in the game.
 * Lumberjacks are strong and excel at gathering wood.
 */
public class Lumberjack extends Character {

    private static final float LUMBERJACK_INITIAL_SANITY = 60f;
    private static final float LUMBERJACK_ATTACK_DAMAGE = 35f;
    private static final float LUMBERJACK_SPEED = 45f;
    private static final float LUMBERJACK_MAX_CARRY_WEIGHT = 90f;
    private static final float LUMBERJACK_ABILITY_COST = 25f;
    private static final float LUMBERJACK_INITIAL_HEALTH = 80f;

    public Lumberjack(String name) {
        super(
                name,
                LUMBERJACK_INITIAL_HEALTH,
                LUMBERJACK_INITIAL_SANITY,
                LUMBERJACK_ATTACK_DAMAGE,
                LUMBERJACK_SPEED,
                LUMBERJACK_MAX_CARRY_WEIGHT,
                EnumSet.of(Trait.STRONG, Trait.FIRE_MAKER, Trait.MECHANIC) // Mechanic for tool upkeep
        );
        // Note: initial health, hunger, thirst, energy are set by Character's constructor.
    }

    /**
     * Uses the Lumberjack's special ability: "Timber!".
     * Consumes energy to simulate gathering extra wood.
     * (Actual item addition would require Inventory interaction)
     */
    @Override
    public void useSpecialAbility() {
        if (getEnergy() >= LUMBERJACK_ABILITY_COST) {
            setEnergy(getEnergy() - LUMBERJACK_ABILITY_COST);
            System.out.println(getName() + " shouts 'Timber!' and expertly fells a tree.");
            // In a full implementation, this would add wood to the inventory.
            // e.g., getInventory().addItem(new WoodItem(5)); // Assuming WoodItem and quantity
            System.out.println(getName() + " gathered a good amount of wood (conceptual).");
            System.out.println(getName() + " consumed " + LUMBERJACK_ABILITY_COST + " energy. Energy left: " + String.format("%.1f", getEnergy()));

        } else {
            System.out.println(getName() + " doesn't have enough energy to use 'Timber!'. Needs " + LUMBERJACK_ABILITY_COST + " energy.");
        }
    }

    @Override
    public String getDescription() {
        return "The Lumberjack: Strong and hardy, skilled with an axe. " +
                "Excels at gathering wood for construction and fuel, and can carry heavy loads.";
    }

    @Override
    public void displayStatus() {
        super.displayStatus();
        System.out.println("Profession: Lumberjack");
        System.out.println("Special Ability: 'Timber!' - Consumes energy to gather extra wood (conceptual).");
    }
}
