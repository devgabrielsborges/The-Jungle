package com.ranieborges.thejungle.cli.model.entity.utils.enums;

import lombok.Getter;

/**
 * Enum representing different types of food.
 * This enum is currently empty and can be expanded in the future.
 */
@Getter
public enum FishType {
    TROUT("Truta"),
    SALMON("Salmão"),
    PIRANHA("Piranha"), // Could be an aggressive variant
    CATFISH("Bagre"),
    SMALL_RIVER_FISH("Peixe Pequeno de Rio");

    private final String displayName;
    FishType(String displayName) { this.displayName = displayName; }
}
