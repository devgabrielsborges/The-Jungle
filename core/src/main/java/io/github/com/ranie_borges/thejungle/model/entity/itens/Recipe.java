package io.github.com.ranie_borges.thejungle.model.entity.itens;

import io.github.com.ranie_borges.thejungle.model.entity.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Recipe {

    private final String name;
    private final Map<String, Integer> requiredItems;
    private final Supplier<Item> resultSupplier;

    public Recipe(String name, Map<String, Integer> requiredItems, Supplier<Item> resultSupplier) {
        this.name = name;
        this.requiredItems = requiredItems;
        this.resultSupplier = resultSupplier;
    }

    public boolean matches(List<Item> items) {
        Map<String, Integer> available = new HashMap<>();

        for (Item item : items) {
            String itemName = item.getName().toLowerCase();
            available.put(itemName, available.getOrDefault(itemName, 0) + item.getQuantity());
        }

        for (Map.Entry<String, Integer> req : requiredItems.entrySet()) {
            if (available.getOrDefault(req.getKey(), 0) < req.getValue()) {
                return false;
            }
        }

        return true;
    }

    public Item craft() {
        return resultSupplier.get();
    }

    public String getName() {
        return name;
    }
}
