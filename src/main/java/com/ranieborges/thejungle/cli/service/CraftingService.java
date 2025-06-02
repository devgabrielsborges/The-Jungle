package com.ranieborges.thejungle.cli.service;

import com.ranieborges.thejungle.cli.model.Inventory;
import com.ranieborges.thejungle.cli.model.Recipe;
import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.itens.*;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MaterialType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.ToolType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.WeaponType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MedicineType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MedicineEffect;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.AmmunitionType; // For Ammunition recipes


import com.ranieborges.thejungle.cli.view.Message;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;


import java.util.*;
import java.util.stream.Collectors;

public class CraftingService {

    private final List<Recipe> allRecipes;

    public CraftingService() {
        this.allRecipes = new ArrayList<>();
        initializeRecipes();
    }

    private void initializeRecipes() {
        Map<MaterialType, Integer> stoneAxeMaterials = new EnumMap<>(MaterialType.class);
        stoneAxeMaterials.put(MaterialType.WOOD, 1);
        stoneAxeMaterials.put(MaterialType.STONE, 1);
        stoneAxeMaterials.put(MaterialType.FIBER, 1);


        allRecipes.add(new Recipe(
                "Stone Axe",
                new Tool("Stone Axe", "A crude but functional axe made of stone and wood.", 1.5f, ToolType.AXE, 30, 0.8f, 5f),
                stoneAxeMaterials,
                Collections.emptyMap(),
                null,
                Collections.emptySet(),
                10
        ));

        Map<MaterialType, Integer> spearMaterials = new EnumMap<>(MaterialType.class);
        spearMaterials.put(MaterialType.WOOD, 1);
        spearMaterials.put(MaterialType.STONE, 1);
        allRecipes.add(new Recipe(
                "Sharpened Spear",
                new Weapon("Sharpened Spear", "A wooden spear with a sharpened stone tip.", 1.0f, 25, WeaponType.MELEE_PIERCING, 12f),
                spearMaterials,
                Collections.emptyMap(),
                ToolType.KNIFE,
                Collections.emptySet(),
                15
        ));

        Map<MaterialType, Integer> bandageMaterials = new EnumMap<>(MaterialType.class);
        bandageMaterials.put(MaterialType.FIBER, 2);
        allRecipes.add(new Recipe(
                "Simple Bandage",
                new Medicine("Simple Bandage", "A strip of cloth to cover minor wounds.", 0.1f, MedicineType.BANDAGE, MedicineEffect.HEAL_WOUNDS, 10f, 1),
                bandageMaterials,
                Collections.emptyMap(),
                null,
                Collections.emptySet(),
                5
        ));

        Map<MaterialType, Integer> torchMaterials = new EnumMap<>(MaterialType.class);
        torchMaterials.put(MaterialType.WOOD, 1);
        torchMaterials.put(MaterialType.FIBER, 1);
        allRecipes.add(new Recipe(
                "Basic Torch",
                new Tool("Basic Torch", "A burning stick wrapped with flammable material. Provides light.", 0.5f, ToolType.OTHER, 20, 1.0f),
                torchMaterials,
                Collections.emptyMap(),
                null,
                Collections.emptySet(),
                5
        ));

        Map<MaterialType, Integer> arrowMaterials = new EnumMap<>(MaterialType.class);
        arrowMaterials.put(MaterialType.WOOD, 1);   // For arrow shafts
        arrowMaterials.put(MaterialType.STONE, 1);  // For arrowheads (small sharp stones)
        arrowMaterials.put(MaterialType.FIBER, 1);  // For fletching (e.g. feathers, or plant fibers)
        allRecipes.add(new Recipe(
                "Bundle of Arrows (x5)",
                new Ammunition("Arrows", "A bundle of sharpened arrows.", 0.05f, AmmunitionType.ARROW, 5), // Weight is per arrow, constructor handles total
                arrowMaterials,
                Collections.emptyMap(),
                ToolType.KNIFE, // For shaping wood and stone
                Collections.emptySet(),
                15 // Energy cost
        ));
    }

    public List<Recipe> getCraftableRecipes(Inventory inventory, Character character) {
        if (inventory == null || character == null) {
            return Collections.emptyList();
        }
        return allRecipes.stream()
                .filter(recipe -> recipe.canCraft(inventory, character))
                .collect(Collectors.toList());
    }

    public boolean craftItem(Recipe recipe, Inventory inventory, Character character) {
        if (recipe == null) {
            Message.displayOnScreen(TerminalStyler.error("Invalid recipe selected."));
            return false;
        }
        if (!recipe.canCraft(inventory, character)) {
            Message.displayOnScreen(TerminalStyler.warning("Cannot craft " + recipe.recipeName() + ". Conditions not met (check energy, materials, tools, traits)."));
            if (character.getEnergy() < recipe.energyCost()) {
                Message.displayOnScreen(TerminalStyler.warning(" - Not enough energy. Required: " + recipe.energyCost() + ", Have: " + character.getEnergy()));
            }
            return false;
        }

        Item resultPrototype = recipe.resultItemPrototype();
        if (inventory.getCurrentWeight() + resultPrototype.getWeight() > inventory.getMaxWeightCapacity()) {
            Message.displayOnScreen(TerminalStyler.error("Cannot craft " + resultPrototype.getName() + ". Not enough inventory space for the result."));
            Message.displayOnScreen(TerminalStyler.info(String.format(" (Required: %.1f, Available: %.1f)", resultPrototype.getWeight(), inventory.getMaxWeightCapacity() - inventory.getCurrentWeight())));
            return false;
        }

        List<Item> consumedItemsForRefund = new ArrayList<>();

        character.changeEnergy(-recipe.energyCost());

        for (Map.Entry<MaterialType, Integer> entry : recipe.requiredMaterials().entrySet()) {
            MaterialType typeToConsume = entry.getKey();
            int quantityToConsume = entry.getValue();
            List<Item> itemsOfType = inventory.getItems().stream()
                    .filter(item -> item instanceof Material && ((Material) item).getMaterialType() == typeToConsume)
                    .limit(quantityToConsume)
                    .toList();

            if (itemsOfType.size() < quantityToConsume) {
                Message.displayOnScreen(TerminalStyler.error("Error: Insufficient material " + typeToConsume.getDisplayName() + " during consumption. Crafting aborted."));
                character.changeEnergy(recipe.energyCost());
                return false;
            }
            for(Item itemToRemove : itemsOfType) {
                inventory.removeItem(itemToRemove);
                consumedItemsForRefund.add(itemToRemove);
            }
        }

        for (Map.Entry<String, Integer> entry : recipe.requiredSpecificItems().entrySet()) {
            String itemName = entry.getKey();
            int quantityToConsume = entry.getValue();
            List<Item> specificItems = inventory.getItems().stream()
                    .filter(item -> item.getName().equalsIgnoreCase(itemName))
                    .limit(quantityToConsume)
                    .toList();

            if (specificItems.size() < quantityToConsume) {
                Message.displayOnScreen(TerminalStyler.error("Error: Insufficient specific item " + itemName + " during consumption. Crafting aborted."));
                character.changeEnergy(recipe.energyCost());
                consumedItemsForRefund.forEach(inventory::addItem);
                return false;
            }
            for(Item itemToRemove : specificItems) {
                inventory.removeItem(itemToRemove);
                consumedItemsForRefund.add(itemToRemove);
            }
        }

        if (recipe.toolRequired() != null) {
            Optional<Tool> usedToolOptional = inventory.getItems().stream()
                    .filter(item -> item instanceof Tool && ((Tool)item).getToolType() == recipe.toolRequired() && item.getDurability() > 0)
                    .map(Tool.class::cast)
                    .findFirst();

            if (usedToolOptional.isPresent()) {
                Tool usedTool = usedToolOptional.get();
                if (usedTool.decreaseDurability()) {
                    Message.displayOnScreen(TerminalStyler.warning(usedTool.getName() + " broke while crafting!"));
                    inventory.removeItem(usedTool);
                } else {
                    Message.displayOnScreen(TerminalStyler.info(usedTool.getName() + " durability decreased."));
                }
            } else {
                Message.displayOnScreen(TerminalStyler.error("Error: Required tool " + recipe.toolRequired().getDisplayName() + " not found or broken. Crafting aborted."));
                character.changeEnergy(recipe.energyCost());
                consumedItemsForRefund.forEach(inventory::addItem);
                return false;
            }
        }

        Item craftedItem = createItemFromPrototype(resultPrototype);

        if (inventory.addItem(craftedItem)) {
            Message.displayOnScreen(TerminalStyler.success("Successfully crafted: " + craftedItem.getName() + "!"));
            return true;
        } else {
            Message.displayOnScreen(TerminalStyler.error("Critical Error: Crafted " + craftedItem.getName() + ", but couldn't add it to inventory (unexpected)."));
            character.changeEnergy(recipe.energyCost());
            consumedItemsForRefund.forEach(inventory::addItem);
            Message.displayOnScreen(TerminalStyler.info("Ingredients and energy refunded due to inventory error."));
            return false;
        }
    }

    private Item createItemFromPrototype(Item prototype) {
        if (prototype instanceof Tool p) {
            return new Tool(p.getName(), p.getDescription(), p.getWeight(), p.getToolType(), p.getMaxDurability(), p.getEfficiency(), p.getDamageAsWeapon());
        } else if (prototype instanceof Weapon p) {
            return new Weapon(p.getName(), p.getDescription(), p.getWeight(), p.getMaxDurability(), p.getWeaponType(), p.getDamage(), p.getRange(), p.getAmmoType(), p.getMaxAmmoInClip());
        } else if (prototype instanceof Medicine p) {
            return new Medicine(p.getName(), p.getDescription(), p.getWeight(), p.getMedicineType(), p.getPrimaryEffect(), p.getEffectPotency(), p.getDoses());
        } else if (prototype instanceof Food p) {
            return new Food(p.getName(), p.getDescription(), p.getWeight(), p.getNutritionalValue(), p.getFoodType(), p.getTurnsUntilSpoiled(), p.getSicknessChanceOnSpoil(), p.getSicknessChanceWhenRaw());
        } else if (prototype instanceof Drinkable p) {
            return new Drinkable(p.getName(), p.getDescription(), p.getWeight(), p.getThirstRestored(), p.getPurity(), p.getDiseaseChance());
        } else if (prototype instanceof Material p) {
            return new Material(p.getName(), p.getDescription(), p.getWeight(), p.getMaterialType(), p.getResistance());
        } else if (prototype instanceof Ammunition p) { // <-- Add Ammunition here
            return new Ammunition(p.getName(), p.getDescription(), p.getWeight() / p.getQuantity(), p.getAmmunitionType(), p.getQuantity()); // Assuming weight in prototype is total, and constructor takes unit weight
        }

        Message.displayOnScreen(TerminalStyler.warning("Warning: Crafting a generic Item instance for: " + prototype.getName() + ". Ensure it has no complex state needing deep copy."));
        return new Item(prototype.getName(), prototype.getDescription(), prototype.getWeight(), prototype.getMaxDurability()) {
            @Override
            public boolean use(Character user) {
                Message.displayOnScreen(TerminalStyler.info(getName() + " is a generic crafted item and has no special use."));
                return false;
            }
        };
    }

}
