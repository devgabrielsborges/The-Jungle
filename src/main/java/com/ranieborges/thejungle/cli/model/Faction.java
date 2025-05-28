package com.ranieborges.thejungle.cli.model;

import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MaterialType;
import com.ranieborges.thejungle.cli.model.factions.utils.FactionDisposition;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
public abstract class Faction {
    private final String id;
    private final String name;
    private final String description;
    @Setter private FactionDisposition disposition;
    private final List<Item> tradeableItems;
    private final List<MaterialType> desiredMaterialTypes;

    public Faction(String id, String name, String description, FactionDisposition initialDisposition,
                   List<Item> tradeableItems, List<MaterialType> desiredMaterialTypes) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.description = Objects.requireNonNull(description);
        this.disposition = Objects.requireNonNull(initialDisposition);
        this.tradeableItems = (tradeableItems != null) ? new ArrayList<>(tradeableItems) : Collections.emptyList();
        this.desiredMaterialTypes = (desiredMaterialTypes != null) ? new ArrayList<>(desiredMaterialTypes) : Collections.emptyList();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false; // Check specific class for subtypes
        Faction faction = (Faction) o;
        return id.equals(faction.id);
    }

    public int hashCode() {
        return Objects.hash(id);
    }

    public String toString() {
        return name + " (" + disposition.getDisplayName() + ")";
    }
}
