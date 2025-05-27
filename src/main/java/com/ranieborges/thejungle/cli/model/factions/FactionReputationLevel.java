package com.ranieborges.thejungle.cli.model.factions;

import lombok.Getter;

/**
 * Represents the player's reputation level with a specific faction.
 */
@Getter
public enum FactionReputationLevel {
    HATED(-100, -51, "Hated", "Actively hunted or attacked."),
    DISLIKED(-50, -11, "Disliked", "Unwelcome, interactions are difficult."),
    NEUTRAL(-10, 10, "Neutral", "Indifferent, standard interactions."),
    ACCEPTED(11, 50, "Accepted", "Tolerated, minor benefits or better trades."),
    TRUSTED(51, 99, "Trusted", "Friendly, offered better opportunities or aid."),
    ALLIED(100, Integer.MAX_VALUE, "Allied", "Considered a close friend, significant benefits.");

    private final int minPoints;
    private final int maxPoints;
    private final String displayName;
    private final String description;

    FactionReputationLevel(int minPoints, int maxPoints, String displayName, String description) {
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
        this.displayName = displayName;
        this.description = description;
    }

    public static FactionReputationLevel fromPoints(int points) {
        for (FactionReputationLevel level : values()) {
            if (points >= level.minPoints && points <= level.maxPoints) {
                return level;
            }
        }
        if (points < HATED.minPoints) return HATED;
        if (points > ALLIED.maxPoints) return ALLIED; // Should be covered by ALLIED.maxPoints = Integer.MAX_VALUE
        return NEUTRAL; // Default fallback
    }
}
