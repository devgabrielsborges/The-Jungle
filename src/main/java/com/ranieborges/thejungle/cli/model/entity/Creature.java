package com.ranieborges.thejungle.cli.model.entity;

import com.ranieborges.thejungle.cli.model.entity.utils.enums.Hostility;
import lombok.Getter;
import lombok.Setter;
import com.ranieborges.thejungle.cli.view.Message;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;

import java.util.List;
import java.util.Random;

/**
 * Represents a creature in the game, such as an animal or monster.
 * Creatures can have different levels of hostility and can attack characters.
 * They can also drop loot upon death.
 */
@Getter
public abstract class Creature {

    @Setter private String name;
    private float health;
    private final float maxHealth;
    @Setter private float attackDamage;
    @Setter private float speed; // Can influence attack order, chance to hit/dodge
    @Setter private Hostility hostility;
    protected static final Random random = new Random(); // For random behaviors

    /**
     * Constructor for a Creature.
     *
     * @param name         The name or type of the creature (e.g., "Wolf", "Grizzly Bear").
     * @param maxHealth    The maximum health of the creature.
     * @param attackDamage The base attack damage of the creature.
     * @param speed        The speed of the creature.
     * @param hostility    The initial hostility level of the creature.
     */
    public Creature(String name, float maxHealth, float attackDamage, float speed, Hostility hostility) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Creature name cannot be null or empty.");
        }
        if (maxHealth <= 0) {
            throw new IllegalArgumentException("Creature max health must be positive.");
        }
        if (attackDamage < 0) {
            throw new IllegalArgumentException("Creature attack damage cannot be negative.");
        }
        if (speed < 0) {
            throw new IllegalArgumentException("Creature speed cannot be negative.");
        }
        this.name = name;
        this.maxHealth = maxHealth;
        this.health = maxHealth; // Start with full health
        this.attackDamage = attackDamage;
        this.speed = speed;
        this.hostility = hostility;
    }

    /**
     * Sets the creature's current health, clamping it between 0 and maxHealth.
     * @param health The new health value.
     */
    public void setHealth(float health) {
        this.health = Math.max(0, Math.min(health, this.maxHealth));
        if (this.health == 0) {
            Message.displayOnScreen(TerminalStyler.error(this.name + " has been defeated!"));
            // Death logic, like dropping loot, would be triggered here or by the game manager
        }
    }

    /**
     * Reduces the creature's health by a specified amount.
     *
     * @param amount The amount of damage to take.
     */
    public void takeDamage(float amount) {
        if (amount < 0) return; // Cannot take negative damage
        Message.displayOnScreen(TerminalStyler.style(this.name + " takes " + String.format("%.1f", amount) + " damage.", TerminalStyler.RED));
        setHealth(this.health - amount);
    }

    /**
     * Abstract method defining how the creature attacks a character.
     *
     * @param target The character being attacked.
     */
    public abstract void attack(Character target);

    /**
     * Abstract method defining the creature's behavior or action during its turn.
     * This could involve moving, attacking, fleeing, or other special actions.
     * @param player The player character, for context (e.g., to decide whether to attack or flee).
     */
    public abstract void act(Character player);

    /**
     * Abstract method to determine what loot, if any, the creature drops upon death.
     *
     * @return A list of items dropped by the creature. Can be empty.
     */
    public abstract List<Item> dropLoot();

    public boolean isAlive() {
        return this.health > 0;
    }

    /**
     * Displays the creature's current status.
     */
    public void displayStatus() {
        Message.displayOnScreen(TerminalStyler.title("--- Creature: " + getName() + " ---"));
        Message.displayOnScreen(String.format("Health: %s%.1f%s/%.1f", (getHealth() <= getMaxHealth() * 0.25) ? TerminalStyler.BRIGHT_RED : TerminalStyler.GREEN, getHealth(), TerminalStyler.RESET, getMaxHealth()));
        Message.displayOnScreen(String.format("Attack Damage: %s%.1f%s", TerminalStyler.BRIGHT_BLACK, getAttackDamage(), TerminalStyler.RESET));
        Message.displayOnScreen(String.format("Speed: %s%.1f%s", TerminalStyler.BRIGHT_BLACK, getSpeed(), TerminalStyler.RESET));
        Message.displayOnScreen("Hostility: " + TerminalStyler.style(getHostility().getDisplayName(), TerminalStyler.YELLOW));
        Message.displayOnScreen(TerminalStyler.style("-----------------------", TerminalStyler.MAGENTA));
    }

    @Override
    public String toString() {
        return TerminalStyler.style(String.format("%s (HP: %.1f/%.1f, Dmg: %.1f, Hostility: %s)",
                name, health, maxHealth, attackDamage, hostility.getDisplayName()), TerminalStyler.CYAN);
    }
}
