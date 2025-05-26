package com.ranieborges.thejungle.cli.model.entity.utils.enums;

import lombok.Getter;

/**
 * Enum representing different types of food.
 * This enum is currently empty and can be expanded in the future.
 */
@Getter
public enum AmmunitionType {
    ARROW("Flecha"),
    BOLT("Virote de Besta"), // Crossbow bolt
    BULLET_HANDGUN("Munição de Pistola"),
    BULLET_RIFLE("Munição de Rifle"),
    NONE("Nenhuma"); // For melee or thrown weapons not requiring separate ammo items

    private final String displayName;

    AmmunitionType(String displayName) {
        this.displayName = displayName;
    }
}
