package io.github.com.ranie_borges.thejungle.model.entity.itens;

import com.google.gson.annotations.Expose;
import io.github.com.ranie_borges.thejungle.model.entity.Item;

/**
 * Represents a tool that can be used for work or crafting
 */
public class Tool extends Item {
    @Expose
    private float workPower;
    @Expose
    private float usageSpeed;
    private final int durabilityLoss;

    /**
     * Creates a new tool
     *
     * @param name       The name of the tool
     * @param weight     The weight of the tool
     * @param durability The initial durability of the tool
     * @param workPower  The power of work this tool provides
     * @param usageSpeed The speed at which the tool can be used
     */
    public Tool(String name, float weight, float durability,
            float workPower, float usageSpeed) {
        super(name, weight, durability);
        this.workPower = Math.max(0, workPower);
        this.usageSpeed = Math.max(0, usageSpeed);
        this.durabilityLoss = 1;
    }

    /**
     * Gets the work power of this tool
     *
     * @return The work power value
     */
    public float getWorkPower() {
        return workPower;
    }

    /**
     * Sets the work power value of this tool
     *
     * @param workPower The new work power value
     */
    public void setWorkPower(float workPower) {
        this.workPower = Math.max(0, workPower);
    }

    /**
     * Uses the tool, reducing its durability
     */
    @Override
    public void useItem() {
        setDurability(getDurability() - durabilityLoss);
    }

    /**
     * Drops the tool, allowing it to be picked up later
     */
    @Override
    public void dropItem() {
        System.out.println("Você deixou cair a ferramenta: " + getName() + ".");
    }

    public float getUsageSpeed() {
        return usageSpeed;
    }

    public void setUsageSpeed(float usageSpeed) {
        this.usageSpeed = Math.max(0, usageSpeed);
    }

    // ====== ADICIONADO: Métodos para criar ferramentas padrão ======

    /**
     * Creates a standard Axe.
     *
     * @return A Tool representing an Axe
     */
    public static Tool createAxe() {
        return new Tool("Axe", 2.5f, 3f, 1.5f, 1.2f);
    }

    /**
     * Creates a standard Knife.
     *
     * @return A Tool representing a Knife
     */
    public static Tool createKnife() {
        return new Tool("Knife", 0.8f, 0.9f, 1.0f, 1.5f);
    }

    /**
     * Creates a standard Lighter.
     *
     * @return A Tool representing a Lighter
     */
    public static Tool createLighter() {
        return new Tool("Lighter", 0.2f, 0.5f, 0.0f, 2.0f);
    }
}
