package com.ranieborges.thejungle.cli.model.entity.utils.enums;

import lombok.Getter;

/**
 * Enum representing different types of weapons.
 * This enum is currently empty and can be expanded in the future.
 */
@Getter
public enum WeaponType {
    MELEE_SLASHING("Corpo a Corpo - Cortante"),
    MELEE_PIERCING("Corpo a Corpo - Perfurante"),
    RANGED_BOW("À Distância - Arco"),
    RANGED_CROSSBOW("À Distância - Besta"),
    RANGED_FIREARM_PISTOL("À Distância - Arma de Fogo (Pistola)"),
    RANGED_FIREARM_RIFLE("À Distância - Arma de Fogo (Rifle)"),
    RANGED_THROWN("À Distância - Arremessável"),
    OTHER("Outra Arma");

    private final String displayName;

    WeaponType(String displayName) {
        this.displayName = displayName;
    }
}
