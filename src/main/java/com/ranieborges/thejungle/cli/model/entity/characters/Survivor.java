package com.ranieborges.thejungle.cli.model.entity.characters;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.Trait;

import java.util.EnumSet;
import java.util.Random;

public class Survivor extends Character {

    public Survivor(String name) {
        super(
                name,
                100f,
                65f,
                25f,
                60f,
                70f,
                EnumSet.of(Trait.NATURAL_SURVIVOR, Trait.RESOURCEFUL, Trait.LUCKY)
        );
    }

    @Override
    public void useSpecialAbility() {
        float survivorAbilityCost = 15f;
        if (getEnergy() >= survivorAbilityCost) {
            setEnergy(getEnergy() - survivorAbilityCost);
            System.out.println(getName() + " uses 'Adapt', focusing their will to survive...");

            Random random = new Random();
            int choice = random.nextInt(3);

            float stat_boost_amount = 20f;
            switch (choice) {
                case 0 -> {

                    setHunger(Math.min(getHunger() + stat_boost_amount, 100f));
                    System.out.println(getName() + " feels less hungry! Hunger is now " + String.format("%.1f", getHunger()));
                }
                case 1 -> {

                    setThirst(Math.min(getThirst() + stat_boost_amount, 100f));
                    System.out.println(getName() + " feels less thirsty! Thirst is now " + String.format("%.1f", getThirst()));
                }
                case 2 -> {

                    setEnergy(Math.min(getEnergy() + stat_boost_amount, 100f));
                    System.out.println(getName() + " feels a surge of energy! Energy is now " + String.format("%.1f", getEnergy()));
                }
            }
            System.out.println(getName() + " consumed " + survivorAbilityCost + " energy. Energy left: " + String.format("%.1f", getEnergy()));
        } else {
            System.out.println(getName() + " doesn't have enough energy to use 'Adapt'. Needs " + survivorAbilityCost + " energy.");
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
