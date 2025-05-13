package io.github.com.ranie_borges.thejungle.model.entity.itens;

import io.github.com.ranie_borges.thejungle.model.entity.Item;

import java.util.*;

public class CraftManager {

    private static final List<Recipe> recipes = new ArrayList<>();

    static {
        recipes.add(new Recipe("Knife", Map.of("stick", 1, "rock", 1), Material::createKnife));
        recipes.add(new Recipe("Axe", Map.of("stick", 2, "rock", 3), Material::createAxe));
        recipes.add(new Recipe("Spear", Map.of("stick", 3, "rock", 1), Material::createSpear));
    }

    public static Item tryCraft(List<Item> items) {
        for (Recipe recipe : recipes) {
            if (recipe.matches(items)) {
                return recipe.craft();
            }
        }
        return null;
    }
}
