package com.ranieborges.thejungle.cli.model.entity.itens;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MaterialType;
import com.ranieborges.thejungle.cli.view.Message; // For output
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler; // For styling
import lombok.Getter;

import java.util.Optional;

/**
 * Represents a basic material item in the game.
 * Materials can be used for crafting or combining with other materials.
 * This class extends Item and provides specific functionality for materials.
 */
@Getter
public class Material extends Item {

    private final MaterialType materialType;
    private final int resistance;

    public Material(String name, String description, float weight, MaterialType materialType, int resistance) {
        super(name, description, weight);
        if (resistance < 0) {
            throw new IllegalArgumentException("Material resistance cannot be negative.");
        }
        this.materialType = materialType;
        this.resistance = resistance;
    }

    @Override
    public boolean use(Character user) {
        Message.displayOnScreen(user.getName() + " examines " + getName() + ". It's a " + materialType.getDisplayName() + ".");
        return false; // Not consumed by this action
    }

    /**
     * Attempts to combine this material with another material to create a new item.
     * This method is for very simple, direct combinations as suggested by the PDF's
     * "combinar(Material outroMaterial)" signature.
     * For more complex crafting involving multiple ingredients or tools,
     * the Recipe-based system in CraftingService should be used.
     *
     * @param otherMaterial The other material to combine with.
     * @param crafter       The character attempting the combination.
     * @return An Optional containing the new Item if the combination is successful,
     * otherwise an empty Optional. The calling code is responsible for
     * consuming the original materials and adding the new item to inventory.
     */
    public Optional<Item> combine(Material otherMaterial, Character crafter) {
        Message.displayOnScreen(TerminalStyler.info(crafter.getName() + " tries to combine " + this.getName() + " with " + otherMaterial.getName() + "..."));

        if (this.getMaterialType() == MaterialType.STONE && this.getName().equalsIgnoreCase("Rough Stone") &&
                otherMaterial.getMaterialType() == MaterialType.STONE && otherMaterial.getName().equalsIgnoreCase("Rough Stone")) {

            Message.displayOnScreen(TerminalStyler.success("The stones are struck together, creating a Sharp Stone!"));
            return Optional.of(new Material("Sharp Stone", "A stone with a crudely sharpened edge.", 0.4f, MaterialType.STONE, this.getResistance() + otherMaterial.getResistance() + 5));
        }

        if (this.getMaterialType() == MaterialType.WOOD && this.getName().equalsIgnoreCase("Stick") &&
                otherMaterial.getMaterialType() == MaterialType.WOOD && otherMaterial.getName().equalsIgnoreCase("Stick")) {

            Message.displayOnScreen(TerminalStyler.success("Two sticks are bound together to form a Long Staff!"));
            return Optional.of(new Material("Long Staff", "Two sticks bound together, sturdier than one.", 0.8f, MaterialType.WOOD, Math.max(this.getResistance(), otherMaterial.getResistance()) + 10));
        }

        if (this.getMaterialType() == MaterialType.FIBER && this.getName().toLowerCase().contains("fiber") &&
                otherMaterial.getMaterialType() == MaterialType.FIBER && otherMaterial.getName().toLowerCase().contains("fiber")) {
            Message.displayOnScreen(TerminalStyler.success("The plant fibers are twisted into a Simple Rope!"));
            return Optional.of(new Material("Simple Rope", "Basic rope made from plant fibers.", 0.3f, MaterialType.FIBER, 10));
        }


        Message.displayOnScreen(TerminalStyler.info("The combination of " + this.getName() + " and " + otherMaterial.getName() + " didn't yield anything specific with this basic method."));
        Message.displayOnScreen(TerminalStyler.info("For more complex items, try the main Crafting menu."));
        return Optional.empty();
    }

    @Override
    public String toString() {
        return String.format("%s (Type: %s, Resistance: %d, Wt: %.1f)",
                getName(), materialType.getDisplayName(), resistance, getWeight());
    }
}
