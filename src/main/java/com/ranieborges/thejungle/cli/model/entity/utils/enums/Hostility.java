package com.ranieborges.thejungle.cli.model.entity.utils.enums;

import lombok.Getter;

@Getter
public enum Hostility {
    HOSTILE("Hostil", "Will attack on sight."),
    NEUTRAL("Neutro", "Will only attack if provoked."),
    FLEEING("Assustado", "Will try to flee if approached or attacked."),
    PASSIVE("Passivo", "Will not attack, even if provoked.");

    private final String displayName;
    private final String description;

    Hostility(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
