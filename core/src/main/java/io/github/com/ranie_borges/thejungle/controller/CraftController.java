package io.github.com.ranie_borges.thejungle.controller;

import com.badlogic.gdx.utils.Array;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Recipe;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Tool;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Weapon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CraftController {
    private static final Logger logger = LoggerFactory.getLogger(CraftController.class); // Logger

    private static final List<Recipe> recipes = new ArrayList<>();

    static {
        recipes.add(new Recipe("Knife", Map.of("stick", 1, "rock", 1), Tool::createKnife));
        recipes.add(new Recipe("Axe", Map.of("stick", 2, "rock", 3), Tool::createAxe));
        recipes.add(new Recipe("Spear", Map.of("stick", 3, "rock", 1), Weapon::createWoodenSpear));
    }

    public static Item tryCraft(List<Item> items) {
        for (Recipe recipe : recipes) {
            if (recipe.matches(items)) {
                return recipe.craft();
            }
        }
        return null;
    }

    public static Item craft(String itemName, List<Item> items) {
        for (Recipe recipe : recipes) {
            if (recipe.getResultName().equalsIgnoreCase(itemName) && recipe.matches(items)) {
                // Note: This version doesn't consume items from the passed list.
                // It's more of a check and create.
                return recipe.craft();
            }
        }
        return null;
    }

    public static Item craft(String itemName, Array<Item> inventory) {
        for (Recipe recipe : recipes) {
            if (recipe.getResultName().equalsIgnoreCase(itemName)) {
                List<Item> itemList = new ArrayList<>();
                for (int i = 0; i < inventory.size; i++) { // Use indexed loop for GDX Array
                    Item item = inventory.get(i);
                    if (item != null) {
                        itemList.add(item);
                    }
                }

                if (recipe.matches(itemList)) {
                    boolean consumedSuccessfully = consumeIngredients(recipe, inventory);
                    if (consumedSuccessfully) {
                        logger.info("Successfully crafted {}. Ingredients consumed.", itemName);
                        return recipe.craft();
                    } else {
                        logger.warn("Could not craft {}: failed to consume all ingredients.", itemName);
                        return null;
                    }
                }
            }
        }
        logger.debug("No matching recipe found for {} or ingredients insufficient.", itemName);
        return null;
    }

    public static boolean canCraft(String itemName, List<Item> inventory) {
        for (Recipe recipe : recipes) {
            if (recipe.getResultName().equalsIgnoreCase(itemName) && recipe.matches(inventory)) {
                return true;
            }
        }
        return false;
    }

    public static boolean canCraft(String itemName, Array<Item> inventory) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < inventory.size; i++) { // Use indexed loop
            Item item = inventory.get(i);
            if (item != null) {
                items.add(item);
            }
        }
        return canCraft(itemName, items);
    }

    public static List<Recipe> getAvailableRecipes(List<Item> inventory) {
        List<Recipe> available = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (recipe.matches(inventory)) {
                available.add(recipe);
            }
        }
        return available;
    }

    public static boolean consumeIngredients(Recipe recipe, Array<Item> inventory) {
        Map<String, Integer> required = new HashMap<>(recipe.getRequiredItems());
        Map<String, Integer> initiallyAvailable = new HashMap<>();

        // 1. Check if all ingredients are available in sufficient quantities
        for (int i = 0; i < inventory.size; i++) {
            Item item = inventory.get(i);
            if (item != null) {
                String itemNameLower = item.getName().toLowerCase();
                initiallyAvailable.put(itemNameLower, initiallyAvailable.getOrDefault(itemNameLower, 0) + item.getQuantity());
            }
        }

        for (Map.Entry<String, Integer> reqEntry : required.entrySet()) {
            String requiredItemNameLower = reqEntry.getKey().toLowerCase();
            int amountNeeded = reqEntry.getValue();
            if (initiallyAvailable.getOrDefault(requiredItemNameLower, 0) < amountNeeded) {
                logger.warn("Cannot consume ingredients for {}: Not enough {} (need {}, have {}).",
                    recipe.getResultName(), requiredItemNameLower, amountNeeded, initiallyAvailable.getOrDefault(requiredItemNameLower, 0));
                return false; // Not enough of an ingredient
            }
        }

        for (Map.Entry<String, Integer> reqEntry : recipe.getRequiredItems().entrySet()) {
            String requiredItemNameLower = reqEntry.getKey().toLowerCase();
            int amountToConsume = reqEntry.getValue(); // Total amount needed for this type

            for (int i = 0; i < inventory.size; i++) {
                if (amountToConsume <= 0) break; // Done with this ingredient type

                Item currentItem = inventory.get(i);
                if (currentItem != null && currentItem.getName().toLowerCase().equals(requiredItemNameLower)) {
                    int consumeFromThisStack = Math.min(amountToConsume, currentItem.getQuantity());

                    currentItem.setQuantity(currentItem.getQuantity() - consumeFromThisStack);
                    amountToConsume -= consumeFromThisStack;

                    if (currentItem.getQuantity() <= 0) {
                        inventory.set(i, null); // Mark slot for removal
                    }
                }
            }
        }

        for (int i = inventory.size - 1; i >= 0; i--) {
            if (inventory.get(i) == null) {
                inventory.removeIndex(i);
            }
        }
        logger.debug("Ingredients consumed for recipe: {}. Inventory size after consumption: {}", recipe.getResultName(), inventory.size);
        return true;
    }
}
