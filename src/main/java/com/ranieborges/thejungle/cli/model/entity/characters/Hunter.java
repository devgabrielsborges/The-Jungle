package com.ranieborges.thejungle.cli.model.entity.characters;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.Trait;

import java.util.EnumSet;

/**
 * Represents the Hunter character class in the game.
 * Hunters are skilled at tracking and procuring food from wildlife.
 */
public class Hunter extends Character {

    private static final float HUNTER_INITIAL_SANITY = 60f;
    private static final float HUNTER_ATTACK_DAMAGE = 30f;
    private static final float HUNTER_SPEED = 65f;
    private static final float HUNTER_MAX_CARRY_WEIGHT = 65f;
    private static final float HUNTER_ABILITY_COST = 20f;
    private static final float HUNTER_INITIAL_HEALTH = 90f;

    public Hunter(String name) {
        super(
                name,
                HUNTER_INITIAL_HEALTH,
                HUNTER_INITIAL_SANITY,
                HUNTER_ATTACK_DAMAGE,
                HUNTER_SPEED,
                HUNTER_MAX_CARRY_WEIGHT,
                EnumSet.of(Trait.TRACKER, Trait.AGILE, Trait.KEEN_SENSES, Trait.ANIMAL_WHISPERER)
        );
    }

    /**
     * Uses the Hunter's special ability: "Track Prey".
     * Consumes energy to simulate finding a food source.
     * (Actual item addition or event trigger would require more complex game logic)
     */
    @Override
    public void useSpecialAbility() {
        if (getEnergy() >= HUNTER_ABILITY_COST) {
            setEnergy(getEnergy() - HUNTER_ABILITY_COST);
            System.out.println(getName() + " uses 'Track Prey', keenly observing the surroundings...");

            System.out.println(getName() + " has a better chance of finding food now (conceptual).");

            System.out.println(getName() + " consumed " + HUNTER_ABILITY_COST + " energy. Energy left: " + String.format("%.1f", getEnergy()));
        } else {
            System.out.println(getName() + " doesn't have enough energy to use 'Track Prey'. Needs " + HUNTER_ABILITY_COST + " energy.");
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
