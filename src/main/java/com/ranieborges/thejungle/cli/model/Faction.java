package com.ranieborges.thejungle.cli.model;

import com.ranieborges.thejungle.cli.model.entity.Item; // For potential trade goods
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MaterialType; // For desired items
import com.ranieborges.thejungle.cli.model.factions.FactionDisposition;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a faction within the game world.
 */
@Getter
public class Faction {
    private final String id;
    private final String name;
    private final String description;
    @Setter private FactionDisposition disposition;
    private final List<Item> tradeableItems;
    private final List<MaterialType> desiredMaterialTypes;

    public Faction(String id, String name, String description, FactionDisposition disposition,
                   List<Item> tradeableItems, List<MaterialType> desiredMaterialTypes) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.disposition = Objects.requireNonNull(disposition);
        this.tradeableItems = (tradeableItems != null) ? new ArrayList<>(tradeableItems) : Collections.emptyList();
        this.desiredMaterialTypes = (desiredMaterialTypes != null) ? new ArrayList<>(desiredMaterialTypes) : Collections.emptyList();
    }

    public Faction(String id, String name, String description, FactionDisposition disposition) {
        this(id, name, description, disposition, null, null);
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
        return name + " (" + disposition.getDisplayName() + ")";
    }
}
