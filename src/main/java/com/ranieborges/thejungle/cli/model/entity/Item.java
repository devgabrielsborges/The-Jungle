package com.ranieborges.thejungle.cli.model.entity;

import lombok.Getter;
import com.ranieborges.thejungle.cli.view.Message;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;

@Getter
public abstract class Item {

    private final String name;
    private final float weight;
    private int durability;
    private final int maxDurability;
    private final String description;

    public Item(String name, String description, float weight, int durability) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Item name cannot be null or empty.");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("Item weight cannot be negative.");
        }
        if (durability < 0) {
            throw new IllegalArgumentException("Item durability cannot be negative.");
        }
        this.name = name;
        this.description = description;
        this.weight = weight;
        this.maxDurability = durability;
        this.durability = durability;
    }

    public Item(String name, String description, float weight) {
        this(name, description, weight, 0);
    }

    public abstract boolean use(Character user);
    public void setDurability(int durability) {
        if (this.maxDurability > 0) {
            this.durability = Math.max(0, Math.min(durability, this.maxDurability));
        } else {
            this.durability = 0;
        }
    }

    public boolean decreaseDurability(int amount) {
        if (this.maxDurability > 0) {
            this.setDurability(this.durability - amount);
            if (this.durability == 0) {
                Message.displayOnScreen(TerminalStyler.error(this.name + " broke!"));
                return true;
            }
        }
        return false;
    }
    public boolean decreaseDurability() {
        return decreaseDurability(1);
    }

    @Override
    public String toString() {
        String durabilityInfo = "";
        if (maxDurability > 0) {
            durabilityInfo = String.format(", Dur: %d/%d", durability, maxDurability);
        }
        return String.format("%s (Desc: %s, Wt: %.1f%s)", name, description, weight, durabilityInfo);
    }

}
