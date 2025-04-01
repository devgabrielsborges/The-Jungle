package io.github.com.ranie_borges.thejungle.model.entity.interfaces;

import io.github.com.ranie_borges.thejungle.model.entity.Character;

public interface INonPlayerCharacter {
    /**
     * Attacks a character with the specified damage.
     *
     * @param attackDamage The amount of damage to inflict
     * @param character The character to attack
     * @return true if attack was successful, false otherwise
     */
    boolean attack(double attackDamage, Character<?> character);

    /**
     * Interacts with a character
     *
     * @param character The character to interact with
     */
    void interact(Character<?> character);

    /**
     * Makes the NPC roam around
     */
    void roam();
}
