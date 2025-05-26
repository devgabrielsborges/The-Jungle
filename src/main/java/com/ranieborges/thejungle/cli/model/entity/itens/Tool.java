package com.ranieborges.thejungle.cli.model.entity.itens;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.ToolType;
import lombok.Getter;

/**
 * Represents a tool item in the game.
 * Tools are durable and their primary purpose is to perform actions.
 * They can also be used as makeshift weapons.
 */
@Getter
public class Tool extends Item {
    private final ToolType toolType;      // "Tipo (machado, faca, isqueiro, lanterna)"
    private final float efficiency;       // "Eficiência (impacta a rapidez ao coletar recursos)"
    private final float damageAsWeapon;   // Some tools can also be used as makeshift weapons

    /**
     * Constructor for a Tool item.
     * Tools are durable and their primary purpose is to perform actions.
     *
     * @param name           The name of the tool (e.g., "Stone Axe", "Hunting Knife").
     * @param description    A brief description.
     * @param weight         The weight of the item.
     * @param toolType       The type of tool.
     * @param durability     The initial and maximum durability of the tool.
     * @param efficiency     The efficiency of the tool (e.g., 1.0 for standard, higher for better).
     * @param damageAsWeapon The damage this tool inflicts if used as a weapon (0 if not applicable).
     */
    public Tool(String name, String description, float weight, ToolType toolType,
                int durability, float efficiency, float damageAsWeapon) {
        super(name, description, weight, durability);
        if (efficiency <= 0) {
            throw new IllegalArgumentException("Tool efficiency must be positive.");
        }
        if (damageAsWeapon < 0) {
            throw new IllegalArgumentException("Tool damage as weapon cannot be negative.");
        }
        this.toolType = toolType;
        this.efficiency = efficiency;
        this.damageAsWeapon = damageAsWeapon;
    }

    /**
     * Simplified constructor for tools not primarily intended as weapons.
     */
    public Tool(String name, String description, float weight, ToolType toolType,
                int durability, float efficiency) {
        this(name, description, weight, toolType, durability, efficiency, 0f);
    }


    /**
     * The character uses the tool. The specific action depends on the toolType
     * and often the context (e.g., using an Axe on a tree).
     * This method reduces durability.
     *
     * @param user The character using the tool.
     * @return true if the tool broke after use, false otherwise.
     * Note: This is different from consumable items. A tool isn't "consumed" in one go,
     * but it can break. The inventory system might remove it if it breaks.
     */
    @Override
    public boolean use(Character user) {
        if (getDurability() <= 0) {
            System.out.println(getName() + " is broken and cannot be used.");
            return true; // Already broken, should ideally be handled before calling use or be removed
        }

        System.out.println(user.getName() + " uses " + getName() + " (" + toolType.getDisplayName() + ").");

        // Specific tool logic would go here, often triggered by player actions in the game loop
        // rather than just "using" the item from inventory without context.
        // For example, if the player chooses "Chop Wood" and has an Axe equipped:
        switch (this.toolType) {
            case AXE:
                System.out.println("Swinging the axe... (Conceptual: gather wood, efficiency: " + this.efficiency + ")");
                // gameLogic.gatherResource(ResourceType.WOOD, this.efficiency);
                break;
            case KNIFE:
                System.out.println("Using the knife... (Conceptual: skin animal, carve wood, efficiency: " + this.efficiency + ")");
                // gameLogic.performAction(ActionType.CARVE, this.efficiency);
                break;
            case PICKAXE:
                System.out.println("Swinging the pickaxe... (Conceptual: mine ore, efficiency: " + this.efficiency + ")");
                break;
            case LIGHTER:
                System.out.println("Flicking the lighter... (Conceptual: start fire)");
                // gameLogic.startFire();
                break;
            case FLASHLIGHT:
                System.out.println("Turning on the flashlight... (Conceptual: provide light)");
                // gameLogic.toggleLightSource(this);
                break;
            // Add cases for other tool types
            default:
                System.out.println("This tool doesn't have a generic 'use' action in this context.");
                break;
        }

        return decreaseDurability(); // Returns true if the tool broke
    }

    /**
     * Simulates attacking with the tool if it has a damage value.
     * @param user The character using the tool to attack.
     * @param target The character being attacked (conceptual).
     */
    public void attackWithTool(Character user, Character target) {
        if (this.damageAsWeapon > 0 && getDurability() > 0) {
            System.out.println(user.getName() + " attacks " + (target != null ? target.getName() : "something") + " with " + getName() + "!");
            float actualDamage = this.damageAsWeapon + user.getAttackDamage(); // Base tool damage + character's own attack
            if(target != null) target.changeHealth(-actualDamage);
            System.out.println(getName() + " dealt " + this.damageAsWeapon + " damage (base).");
            if (decreaseDurability()) {
                System.out.println(getName() + " broke during the attack!");
            }
        } else if (getDurability() <= 0) {
            System.out.println(getName() + " is broken and cannot be used to attack.");
        }
        else {
            System.out.println(getName() + " is not effective as a weapon.");
        }
    }


    @Override
    public String toString() {
        String efficiencyInfo = String.format(", Eff: %.1f", efficiency);
        String damageInfo = (damageAsWeapon > 0) ? String.format(", Dmg: %.1f", damageAsWeapon) : "";
        return String.format("%s (Type: %s%s%s, Dur: %d/%d, Wt: %.1f)",
                getName(), toolType.getDisplayName(), efficiencyInfo, damageInfo,
                getDurability(), getMaxDurability(), getWeight());
    }
}
