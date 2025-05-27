package com.ranieborges.thejungle.cli.model.factions;

import com.ranieborges.thejungle.cli.model.entity.Item; // For potential trade goods
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MaterialType; // For desired items
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a faction within the game world.
 */
@Getter
public class Faction {
    private final String id; // Unique identifier, e.g., "nomads_peaceful"
    private final String name; // Display name, e.g., "Nômades Pacíficos"
    private final String description;
    private FactionDisposition initialDisposition; // Base disposition, can change based on global events or player choices
    private final List<Item> tradeableItems; // Items this faction might offer for trade
    private final List<MaterialType> desiredMaterialTypes; // Material types they value

    // Future additions:
    // - Base location (e.g., an Ambient name)
    // - List of NPC members (if individual NPCs are modeled)
    // - Specific quests or objectives related to this faction

    public Faction(String id, String name, String description, FactionDisposition initialDisposition,
                   List<Item> tradeableItems, List<MaterialType> desiredMaterialTypes) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.initialDisposition = Objects.requireNonNull(initialDisposition);
        this.tradeableItems = (tradeableItems != null) ? new ArrayList<>(tradeableItems) : Collections.emptyList();
        this.desiredMaterialTypes = (desiredMaterialTypes != null) ? new ArrayList<>(desiredMaterialTypes) : Collections.emptyList();
    }

    // Simple constructor
    public Faction(String id, String name, String description, FactionDisposition initialDisposition) {
        this(id, name, description, initialDisposition, null, null);
    }

    // Setter for disposition if it can change dynamically due to game events not tied to player reputation
    public void setInitialDisposition(FactionDisposition disposition) {
        this.initialDisposition = disposition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Faction faction = (Faction) o;
        return id.equals(faction.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + initialDisposition.getDisplayName() + ")";
    }
}
