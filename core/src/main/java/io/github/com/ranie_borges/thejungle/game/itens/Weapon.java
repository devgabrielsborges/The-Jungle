package io.github.com.ranie_borges.thejungle.game.itens;

import io.github.com.ranie_borges.thejungle.model.Item;

/**
 * Represents a weapon that can be used for combat
 */
public class Weapon extends Item {
    private float damage;
    private float attackSpeed;
    private final int durabilityLoss;

    /**
     * Creates a new weapon
     *
     * @param name The name of the weapon
     * @param weight The weight of the weapon
     * @param durability The initial durability of the weapon
     * @param damage The base damage inflicted by this weapon
     * @param attackSpeed The attack speed of this weapon
     */
    public Weapon(String name, float weight, float durability,
                  float damage, float attackSpeed) {
        super(name, weight, durability);
        this.damage = Math.max(0, damage);
        this.attackSpeed = Math.max(0, attackSpeed);
        this.durabilityLoss = 1;
    }

    /**
     * Gets the damage value of this weapon
     *
     * @return The base damage value
     */
    public float getDamage() {
        return damage;
    }

    /**
     * Sets the damage value of this weapon
     *
     * @param damage The new damage value
     */
    public void setDamage(float damage) {
        this.damage = Math.max(0, damage);
    }

    /**
     * Uses the weapon, reducing its durability
     */
    @Override
    public void useItem() {
        setDurability(getDurability() - durabilityLoss);
    }

    /**
     * Drops the weapon, allowing it to be picked up later
     */
    @Override
    public void dropItem() {
        // Implementation for dropping a weapon
    }

    public float getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(float attackSpeed) {
        this.attackSpeed = attackSpeed;
    }
}
