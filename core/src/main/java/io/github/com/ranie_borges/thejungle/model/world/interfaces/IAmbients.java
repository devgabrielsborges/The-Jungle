package io.github.com.ranie_borges.thejungle.model.world.interfaces;

import java.util.Random;

public interface IAmbients {
    /**
     * Explores the ambient environment to discover resources and potentially trigger events.
     */
    default void explore() {
        // Trigger event generation during exploration
        generateEvent();

        // Chance to experience climate changes
        if (new Random().nextDouble() < 0.3) {
            modifiesClime();
        }
    }

    /**
     * Generates a random event based on the ambient's possible events and their probabilities.
     */
    default void generateEvent() {
        // Implementation should select an event based on defined probabilities
        // and apply its effects to the current game state
        Random random = new Random();
        // Logic to select and trigger an event would go here
    }

    /**
     * Changes the climate of the ambient based on available climate types.
     */
    default void modifiesClime() {
        // Implementation should randomly select from available climate options
        // and apply any relevant effects from the climate change
        Random random = new Random();
        // Logic to change climate would go here
    }

    /**
     * Disables an active event in the ambient.
     * Must be implemented by concrete classes.
     */
    void disableEvent();

    int[][] generateMap(int mapWidth, int mapHeight);

}
