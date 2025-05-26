package com.ranieborges.thejungle.cli.model.entity.utils.enums;

public enum Purity {
    POTABLE("Potável"),
    CONTAMINATED("Contaminada"),
    UNKNOWN("Desconhecida");

    private final String displayName;

    Purity(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
