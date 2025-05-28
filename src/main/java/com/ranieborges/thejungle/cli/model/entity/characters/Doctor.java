package com.ranieborges.thejungle.cli.model.entity.characters;

import com.ranieborges.thejungle.cli.model.entity.utils.enums.Trait;
import com.ranieborges.thejungle.cli.model.entity.Character;


import java.util.EnumSet;

/**
 * Represents the Doctor character class in the game.
 * Doctors specialize in healing and medical knowledge.
 */
public class Doctor extends Character {

    private final float doctorHealAmount = 25f;
    private final float doctorAbilityCost = 20f;

    public Doctor(String name) {
        super(
            name,
            60f,
            70f,
            15f,
            50f,
            60f,
                EnumSet.of(Trait.MEDIC, Trait.BOTANIST, Trait.FAST_HEALER)
        );
    }

    @Override
    public void useSpecialAbility() {
        if (getEnergy() >= doctorAbilityCost) {
            System.out.println(getName() + " uses their medical expertise to tend to their wounds.");
            if (getHealth() < getCharacterDefaultMaxHealth()) {
                float healthBefore = getHealth();
                setHealth(Math.min(getHealth() + doctorHealAmount, getCharacterDefaultMaxHealth()));

                System.out.println(getName() + " healed for " + (getHealth() - healthBefore) + " health. Current health: " + getHealth());
                setEnergy(getEnergy() - doctorAbilityCost);

            } else {
                System.out.println(getName() + " is already at full health.");
            }
        }
    }

    @Override
    public String getDescription() {
        return "The Doctor: A skilled medic, adept at treating injuries and knowledgeable about medicinal plants. " +
                "They prioritize survival through careful health management and resourcefulness in healing.";
    }

    @Override
    public void displayStatus() {
        super.displayStatus();
        System.out.println("Profession: Doctor");
        System.out.println("Special Ability: Can perform self-healing (" + doctorHealAmount + " HP).");
    }
}
