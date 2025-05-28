package com.ranieborges.thejungle.cli.model.entity.characters;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.Trait;

import java.util.EnumSet;
import java.util.Random;

/**
 * Represents the Survivor character class in the game.
 * Survivors are adaptable and resourceful.
 */
public class Survivor extends Character {
    private static final float SURVIVOR_INITIAL_SANITY = 65f;
    private static final float SURVIVOR_ATTACK_DAMAGE = 25f;
    private static final float SURVIVOR_SPEED = 60f;
    private static final float SURVIVOR_MAX_CARRY_WEIGHT = 70f;
    private static final float SURVIVOR_ABILITY_COST = 15f;
    private static final float STAT_BOOST_AMOUNT = 20f;
    private static final float SURVIVOR_INITIAL_HEALTH = 100f;

    public Survivor(String name) {
        super(
                name,
                SURVIVOR_INITIAL_HEALTH,
                SURVIVOR_INITIAL_SANITY,
                SURVIVOR_ATTACK_DAMAGE,
                SURVIVOR_SPEED,
                SURVIVOR_MAX_CARRY_WEIGHT,
                EnumSet.of(Trait.NATURAL_SURVIVOR, Trait.RESOURCEFUL, Trait.LUCKY)
        );
    }

    /**
     * Uses the Survivor's special ability: "Adapt".
     * Consumes some energy to randomly boost hunger, thirst, or energy,
     * or has a small chance to find a generic item (conceptual).
     */
    @Override
    public void useSpecialAbility() {
        if (getEnergy() >= SURVIVOR_ABILITY_COST) {
            setEnergy(getEnergy() - SURVIVOR_ABILITY_COST);
            System.out.println(getName() + " uses 'Adapt', focusing their will to survive...");

            Random random = new Random();
            int choice = random.nextInt(3); // 0 for hunger, 1 for thirst, 2 for energy

            switch (choice) {
                case 0:

                    setHunger(Math.min(getHunger() + STAT_BOOST_AMOUNT, 100f));
                    System.out.println(getName() + " feels less hungry! Hunger is now " + String.format("%.1f", getHunger()));
                    break;
                case 1:

                    setThirst(Math.min(getThirst() + STAT_BOOST_AMOUNT, 100f));
                    System.out.println(getName() + " feels less thirsty! Thirst is now " + String.format("%.1f", getThirst()));
                    break;
                case 2:

                    setEnergy(Math.min(getEnergy() + STAT_BOOST_AMOUNT, 100f));
                    System.out.println(getName() + " feels a surge of energy! Energy is now " + String.format("%.1f", getEnergy()));
                    break;
            }
            System.out.println(getName() + " consumed " + SURVIVOR_ABILITY_COST + " energy. Energy left: " + String.format("%.1f", getEnergy()));
        } else {
            System.out.println(getName() + " doesn't have enough energy to use 'Adapt'. Needs " + SURVIVOR_ABILITY_COST + " energy.");
        }
    }

    @Override
    public String getDescription() {
        return "The Survivor: A jack-of-all-trades, resilient and resourceful. " +
                "They excel at making the most out of any situation and have a knack for finding what they need.";
    }

    @Override
    public void displayStatus() {
        super.displayStatus();
        System.out.println("Profession: Survivor");
        System.out.println("Special Ability: 'Adapt' - Consumes energy to randomly recover hunger, thirst, or energy.");
    }
}
