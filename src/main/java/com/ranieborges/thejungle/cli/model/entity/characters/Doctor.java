package com.ranieborges.thejungle.cli.model.entity.characters;

import com.ranieborges.thejungle.cli.model.entity.utils.enums.Trait;
import com.ranieborges.thejungle.cli.model.entity.Character;


import java.util.EnumSet;

/**
 * Represents the Doctor character class in the game.
 * Doctors specialize in healing and medical knowledge.
 */
public class Doctor extends Character {

    private static final float DOCTOR_INITIAL_SANITY = 70f;
    private static final float DOCTOR_ATTACK_DAMAGE = 15f;
    private static final float DOCTOR_SPEED = 50f;
    private static final float DOCTOR_MAX_CARRY_WEIGHT = 60f;
    private static final float DOCTOR_HEAL_AMOUNT = 25f;
    private static final float DOCTOR_INITIAL_HEALTH = 60f;
    private static final float DOCTOR_ABILITY_COST = 20f;

    public Doctor(String name) {
        super(
                name,
                DOCTOR_INITIAL_HEALTH,
                DOCTOR_INITIAL_SANITY,
                DOCTOR_ATTACK_DAMAGE,
                DOCTOR_SPEED,
                DOCTOR_MAX_CARRY_WEIGHT,
                EnumSet.of(Trait.MEDIC, Trait.BOTANIST, Trait.FAST_HEALER)
        );
    }

    @Override
    public void useSpecialAbility() {
        if (getEnergy() >= DOCTOR_ABILITY_COST) {
            System.out.println(getName() + " uses their medical expertise to tend to their wounds.");
            if (getHealth() < Character.characterDefaultMaxHealth) {
                float healthBefore = getHealth();
                setHealth(Math.min(getHealth() + DOCTOR_HEAL_AMOUNT, Character.characterDefaultMaxHealth));

                System.out.println(getName() + " healed for " + (getHealth() - healthBefore) + " health. Current health: " + getHealth());
                setEnergy(getEnergy() - DOCTOR_ABILITY_COST);

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
        System.out.println("Special Ability: Can perform self-healing (" + DOCTOR_HEAL_AMOUNT + " HP).");
    }
}
