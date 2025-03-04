package io.github.com.ranie_borges.thejungle.model;

import io.github.com.ranie_borges.thejungle.model.enums.ItemType;
import io.github.com.ranie_borges.thejungle.model.interfaces.IItem;

public abstract class Item implements IItem {
    private ItemType type;
    private String name;
    private float weight;
    private float durability;

    protected Item(ItemType type, String name, float weight, float durability) {
        this.type = type;
        this.name = name;
        this.weight = Math.max(0, weight);
        this.durability = Math.max(0, Math.min(100, durability));
    }

    public float getDurability() {
        return this.durability;
    }

    public void setDurability(float durability) {
        this.durability = Math.max(0, Math.min(100, durability));
    }

    public ItemType getType() {
        return this.type;
    }

    public void setType(ItemType type) {
        if (type != null) {
            this.type = type;
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
    }

    public float getWeight() {
        return this.weight;
    }

    public void setWeight(float weight) {
        this.weight = Math.max(0, weight);
    }
}
