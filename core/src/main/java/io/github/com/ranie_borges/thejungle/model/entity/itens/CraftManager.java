package io.github.com.ranie_borges.thejungle.model.entity.itens;

import com.badlogic.gdx.utils.Array;
import io.github.com.ranie_borges.thejungle.model.entity.Item;

import java.util.*;

public class CraftManager {

    private static final List<Recipe> recipes = new ArrayList<>();

    static {
        recipes.add(new Recipe("Knife", Map.of("stick", 1, "rock", 1), Material::createKnife));
        recipes.add(new Recipe("Axe", Map.of("stick", 2, "rock", 3), Material::createAxe));
        recipes.add(new Recipe("Spear", Map.of("stick", 3, "rock", 1), Material::createSpear));
    }

    /**
     * Tenta craftar com base em uma lista de itens fornecida.
     */
    public static Item tryCraft(List<Item> items) {
        for (Recipe recipe : recipes) {
            if (recipe.matches(items)) {
                return recipe.craft();
            }
        }
        return null;
    }

    /**
     * Tenta craftar com base no nome do item e uma lista de itens.
     */
    public static Item craft(String itemName, List<Item> items) {
        for (Recipe recipe : recipes) {
            if (recipe.getResultName().equalsIgnoreCase(itemName) && recipe.matches(items)) {
                return recipe.craft();
            }
        }
        return null;
    }

    /**
     * Versão alternativa para aceitar Array<Item> (LibGDX).
     */
    public static Item craft(String itemName, Array<Item> inventory) {
        for (Recipe recipe : recipes) {
            if (recipe.getResultName().equalsIgnoreCase(itemName)) {
                // Converte Array<Item> para List<Item>
                List<Item> itemList = new ArrayList<>();
                for (Item i : inventory) {
                    if (i != null) itemList.add(i);
                }

                if (recipe.matches(itemList)) {
                    // Copia requisitos
                    Map<String, Integer> required = new HashMap<>(recipe.getRequiredItems());

                    // Consome os ingredientes do inventário
                    for (int i = 0; i < inventory.size; i++) {
                        Item item = inventory.get(i);
                        if (item == null) continue;

                        String name = item.getName().toLowerCase();
                        if (required.containsKey(name)) {
                            int needed = required.get(name);
                            int available = item.getQuantity();

                            if (available <= needed) {
                                required.put(name, needed - available);
                                inventory.set(i, null); // remove item
                            } else {
                                item.setQuantity(available - needed);
                                required.remove(name);
                            }

                            // Se todos os ingredientes foram consumidos
                            if (required.isEmpty()) break;
                        }
                    }

                    // Cria o item
                    return recipe.craft();
                }
            }
        }

        return null;
    }

    /**
     * Verifica se é possível craftar um item dado o nome e os itens no inventário.
     */
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
        for (Item item : inventory) {
            if (item != null) items.add(item);
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

    public static void consumeIngredients(Recipe recipe, Array<Item> inventory) {
        Map<String, Integer> required = new HashMap<>(recipe.getRequiredItems());

        for (int i = 0; i < inventory.size && !required.isEmpty(); i++) {
            Item item = inventory.get(i);
            if (item == null) continue;

            String name = item.getName().toLowerCase();
            if (required.containsKey(name)) {
                int needed = required.get(name);
                int available = item.getQuantity();

                if (available <= needed) {
                    required.put(name, needed - available);
                    inventory.set(i, null); // remove slot
                } else {
                    item.setQuantity(available - needed);
                    required.remove(name);
                }
            }
        }
    }
}
