package com.ranieborges.thejungle.cli.model.entity.characters;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.Trait;

import java.util.EnumSet;

public class Lumberjack extends Character {

    public Lumberjack(String name) {
        super(
                name,
                80f,
                60f,
                35f,
                45f,
                90f,
                EnumSet.of(Trait.STRONG, Trait.FIRE_MAKER, Trait.MECHANIC)
        );
    }

    @Override
    public void useSpecialAbility() {
        float lumberjackAbilityCost = 25f;
        if (getEnergy() >= lumberjackAbilityCost) {
            setEnergy(getEnergy() - lumberjackAbilityCost);
            System.out.println(getName() + " shouts 'Timber!' and expertly fells a tree.");

            System.out.println(getName() + " gathered a good amount of wood (conceptual).");
            System.out.println(getName() + " consumed " + lumberjackAbilityCost + " energy. Energy left: " + String.format("%.1f", getEnergy()));

        } else {
            System.out.println(getName() + " doesn't have enough energy to use 'Timber!'. Needs " + lumberjackAbilityCost + " energy.");
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
