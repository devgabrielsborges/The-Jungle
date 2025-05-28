package com.ranieborges.thejungle.cli.model.factions.utils;

import lombok.Getter;

/**
 * Represents the general disposition of a faction towards outsiders or the player by default.
 */
@Getter
public enum FactionDisposition {
    HOSTILE("Hostile", "Will likely attack on sight or be uncooperative."),
    NEUTRAL("Neutral", "Will not attack unless provoked; may be open to interaction."),
    FRIENDLY("Friendly", "Generally peaceful and may offer aid or trade easily."),
    GUARDED("Guarded", "Cautious and suspicious, but not immediately hostile.");

    private final String displayName;
    private final String description;

    FactionDisposition(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}
