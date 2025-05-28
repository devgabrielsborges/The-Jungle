package com.ranieborges.thejungle.cli.model.entity.characters;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.Trait;

import java.util.EnumSet;

/**
 * Represents the Hunter character class in the game.
 * Hunters are skilled at tracking and procuring food from wildlife.
 */
public class Hunter extends Character {

    public Hunter(String name) {
        super(
                name,
                90f,
                60f,
                30f,
                65f,
                65f,
                EnumSet.of(Trait.TRACKER, Trait.AGILE, Trait.KEEN_SENSES, Trait.ANIMAL_WHISPERER)
        );
    }

    @Override
    public void useSpecialAbility() {
        float hunterAbilityCost = 20f;
        if (getEnergy() >= hunterAbilityCost) {
            setEnergy(getEnergy() - hunterAbilityCost);
            System.out.println(getName() + " uses 'Track Prey', keenly observing the surroundings...");

            System.out.println(getName() + " has a better chance of finding food now (conceptual).");

            System.out.println(getName() + " consumed " + hunterAbilityCost + " energy. Energy left: " + String.format("%.1f", getEnergy()));
        } else {
            System.out.println(getName() + " doesn't have enough energy to use 'Track Prey'. Needs " + hunterAbilityCost + " energy.");
        }
    }

    @Override
    public String getDescription() {
        return "The Hunter: Agile and perceptive, a master of the wild. " +
                "Specializes in tracking animals and procuring food through hunting and foraging.";
    }

    @Override
    public void displayStatus() {
        super.displayStatus();
        System.out.println("Profession: Hunter");
        System.out.println("Special Ability: 'Track Prey' - Consumes energy to increase chances of finding food (conceptual).");
    }
}
