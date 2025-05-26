package com.ranieborges.thejungle.cli.model;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MaterialType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.ToolType; // If tools are needed for crafting
import com.ranieborges.thejungle.cli.model.entity.utils.enums.Trait; // If traits are needed
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Represents a crafting recipe in the game.
 * Defines the required ingredients (materials and specific items),
 * the resulting item, and any conditions like required tools or traits.
 *
 * @param recipeName            e.g., "Craft Stone Axe", "Cook Rabbit Meat"
 * @param resultItemPrototype   A prototype of the item to be crafted
 * @param requiredMaterials     e.g., WOOD: 2, STONE: 1
 * @param requiredSpecificItems e.g., "Rope": 1, "Wolf Pelt": 1
 * @param toolRequired          e.g., ToolType.HAMMER (can be null if no tool needed)
 * @param traitsRequired        e.g., Trait.MECHANIC (can be empty if no specific traits needed)
 * @param energyCost            Energy consumed to perform the craft
 */
public record Recipe(String recipeName, Item resultItemPrototype, Map<MaterialType, Integer> requiredMaterials,
                     Map<String, Integer> requiredSpecificItems, ToolType toolRequired, Set<Trait> traitsRequired,
                     int energyCost) {
    /**
     * Constructor for a crafting recipe.
     *
     * @param recipeName            A descriptive name for the recipe.
     * @param resultItemPrototype   A prototype (instance) of the item that will be created.
     *                              The crafting service will likely clone this or create a new instance based on it.
     * @param requiredMaterials     A map of MaterialType to quantity needed. Can be empty.
     * @param requiredSpecificItems A map of specific item names (String) to quantity needed. Can be empty.
     * @param toolRequired          The ToolType required for crafting. Can be null.
     * @param traitsRequired        A set of Traits the character must possess. Can be empty.
     * @param energyCost            The amount of energy the character consumes to craft this item.
     */
    public Recipe(String recipeName, Item resultItemPrototype,
                  Map<MaterialType, Integer> requiredMaterials,
                  Map<String, Integer> requiredSpecificItems,
                  ToolType toolRequired, Set<Trait> traitsRequired, int energyCost) {
        if (recipeName == null || recipeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipe name cannot be null or empty.");
        }
        if (resultItemPrototype == null) {
            throw new IllegalArgumentException("Result item prototype cannot be null.");
        }
        this.recipeName = recipeName;
        this.resultItemPrototype = resultItemPrototype;
        this.requiredMaterials = (requiredMaterials != null) ? Collections.unmodifiableMap(requiredMaterials) : Collections.emptyMap();
        this.requiredSpecificItems = (requiredSpecificItems != null) ? Collections.unmodifiableMap(requiredSpecificItems) : Collections.emptyMap();
        this.toolRequired = toolRequired;
        this.traitsRequired = (traitsRequired != null) ? Collections.unmodifiableSet(traitsRequired) : Collections.emptySet();
        this.energyCost = Math.max(0, energyCost); // Energy cost cannot be negative
    }

    /**
     * Checks if the character has the necessary ingredients, tools, and traits to craft this recipe.
     *
     * @param inventory The character's inventory.
     * @param character The character attempting to craft.
     * @return true if the character can craft this recipe, false otherwise.
     */
    public boolean canCraft(Inventory inventory, Character character) {
        if (character == null || inventory == null) {
            return false;
        }

        if (character.getEnergy() < this.energyCost) {
            return false;
        }

        if (this.traitsRequired != null && !this.traitsRequired.isEmpty() && (character.getTraits() == null || !character.getTraits().containsAll(this.traitsRequired))) {
            return false; // Character doesn't have all required traits
        }


        if (this.toolRequired != null && !inventory.hasToolType(this.toolRequired)) {
            return false; // Required tool not found in inventory
        }


        if (this.requiredMaterials != null && !this.requiredMaterials.isEmpty()) {
            for (Map.Entry<MaterialType, Integer> entry : this.requiredMaterials.entrySet()) {
                if (inventory.countItemsByMaterialType(entry.getKey()) < entry.getValue()) {
                    return false; // Not enough of a specific material type
                }
            }
        }

        if (this.requiredSpecificItems != null && !this.requiredSpecificItems.isEmpty()) {
            for (Map.Entry<String, Integer> entry : this.requiredSpecificItems.entrySet()) {
                if (inventory.countSpecificItemByName(entry.getKey()) < entry.getValue()) {
                    return false; // Not enough of a specific named item
                }
            }
        }

        return true; // All conditions met
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("'%s' -> %s (Energy: %d)", recipeName, resultItemPrototype.getName(), energyCost));
        sb.append("\n  Requires:");
        if (requiredMaterials.isEmpty() && requiredSpecificItems.isEmpty()) {
            sb.append(" Nothing specific.");
        } else {
            requiredMaterials.forEach((type, count) -> sb.append(String.format("\n    - %d x %s (Material)", count, type.getDisplayName())));
            requiredSpecificItems.forEach((name, count) -> sb.append(String.format("\n    - %d x %s", count, name)));
        }
        if (toolRequired != null) {
            sb.append(String.format("\n  Tool: %s", toolRequired.getDisplayName()));
        }
        if (traitsRequired != null && !traitsRequired.isEmpty()) {
            sb.append("\n  Traits: ");
            traitsRequired.forEach(trait -> sb.append(trait.getDisplayName()).append(" "));
        }
        return sb.toString();
    }

}
