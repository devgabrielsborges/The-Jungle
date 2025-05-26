package com.ranieborges.thejungle.cli.model.entity.utils.enums;

import lombok.Getter;

/**
 * Enum representing different types of weapons.
 * This enum is currently empty and can be expanded in the future.
 */
@Getter
public enum WeaponType {
    MELEE_SLASHING("Corpo a Corpo - Cortante"), // e.g., Machete, Sword
    MELEE_PIERCING("Corpo a Corpo - Perfurante"), // e.g., Spear, Dagger
    RANGED_BOW("À Distância - Arco"),
    RANGED_CROSSBOW("À Distância - Besta"),
    RANGED_FIREARM_PISTOL("À Distância - Arma de Fogo (Pistola)"),
    RANGED_FIREARM_RIFLE("À Distância - Arma de Fogo (Rifle)"),
    RANGED_THROWN("À Distância - Arremessável"), // e.g., Throwing Knives, Rocks
    OTHER("Outra Arma");

    private final String displayName;

    WeaponType(String displayName) {
        this.displayName = displayName;
    }
}
