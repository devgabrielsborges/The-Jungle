package io.github.com.ranie_borges.thejungle.model.entity;

import io.github.com.ranie_borges.thejungle.model.entity.interfaces.IItem;

public abstract class Item implements IItem {
    private String name;
    private float weight;
    private float durability;

    protected Item(String name, String weight, float durability) {
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
