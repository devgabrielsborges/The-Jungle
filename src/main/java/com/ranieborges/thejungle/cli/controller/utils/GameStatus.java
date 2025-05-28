package com.ranieborges.thejungle.cli.controller.utils;

public enum GameStatus {
    CONTINUE,           // The game should continue to the next turn.
    PLAYER_DEFEATED,    // The player character has been defeated (e.g., health reached 0).
    SURVIVAL_FAILURE,   // The player failed to meet other survival conditions (e.g., critical sanity for too long).
    PLAYER_QUIT,        // The player chose to quit the current game session.
    OBJECTIVE_MET       // Placeholder for a potential victory condition.
}
