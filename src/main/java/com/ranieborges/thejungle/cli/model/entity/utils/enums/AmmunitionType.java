package com.ranieborges.thejungle.cli.model.entity.utils.enums;

import lombok.Getter;

/**
 * Enum representing different types of food.
 * This enum is currently empty and can be expanded in the future.
 */
@Getter
public enum AmmunitionType {
    ARROW("Flecha"),
    BOLT("Virote de Besta"),
    BULLET_HANDGUN("Munição de Pistola"),
    BULLET_RIFLE("Munição de Rifle"),
    NONE("Nenhuma");

    private final String displayName;

    AmmunitionType(String displayName) {
        this.displayName = displayName;
    }
}
