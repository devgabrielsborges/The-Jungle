package io.github.com.ranie_borges.thejungle.model.entity.itens;

import io.github.com.ranie_borges.thejungle.model.entity.Item;

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
        System.out.println("Você deixou cair a arma: " + getName() + ".");
    }

    public float getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(float attackSpeed) {
        this.attackSpeed = Math.max(0, attackSpeed);
    }

    // ======= ADICIONADO: Factory Methods para criar armas padrão =======

    /**
     * Creates a basic Wooden Spear
     */
    public static Weapon createWoodenSpear() {
        return new Weapon("Wooden Spear", 1.5f, 1.0f, 8.0f, 1.0f);
    }

    /**
     * Creates a Stone Spear (improved spear with more damage)
     */
    public static Weapon createStoneSpear() {
        return new Weapon("Stone Spear", 2.0f, 1.2f, 12.0f, 0.9f);
    }

    /**
     * Creates a basic Knife (usable as weapon too)
     */
    public static Weapon createKnife() {
        return new Weapon("Knife", 0.8f, 0.9f, 5.0f, 1.5f);
    }
}
