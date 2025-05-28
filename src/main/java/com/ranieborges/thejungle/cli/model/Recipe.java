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


public record Recipe(String recipeName, Item resultItemPrototype, Map<MaterialType, Integer> requiredMaterials,
                     Map<String, Integer> requiredSpecificItems, ToolType toolRequired, Set<Trait> traitsRequired,
                     int energyCost) {

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
