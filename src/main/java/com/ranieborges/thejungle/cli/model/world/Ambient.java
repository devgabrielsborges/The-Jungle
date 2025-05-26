package com.ranieborges.thejungle.cli.model.world;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Creature;
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Abstract base class for all environments (ambients) in the game "Última Fronteira".
 * Corresponds to "Superclasse: Ambiente" from the PDF.
 */
@Getter
public abstract class Ambient {
    private final String name; // "Nome: Identificação do ambiente."
    private final String description; // "Descrição: Texto explicativo sobre as características gerais do local."
    private final float explorationDifficulty; // "Dificuldade de exploração: Define se o ambiente consome mais energia" (e.g., energy cost multiplier or flat cost)

    @Setter
    private String currentWeather; // "Condições climáticas predominantes" - can be dynamic

    protected static final Random random = new Random();

    /**
     * Constructor for Ambient.
     * @param name Name of the ambient.
     * @param description Description of the ambient.
     * @param explorationDifficulty A factor or cost associated with exploring this ambient.
     * @param defaultWeather The typical weather in this ambient.
     */
    public Ambient(String name, String description, float explorationDifficulty, String defaultWeather) {
        this.name = name;
        this.description = description;
        this.explorationDifficulty = explorationDifficulty;
        this.currentWeather = defaultWeather;
    }

    /**
     * Simulates the character exploring the current ambient.
     * This method should handle resource finding, potential minor events, or encounters.
     * The PDF's "explorar (Personagem jogador)" method.
     *
     * @param character The character exploring.
     * @return A summary string of what happened during exploration.
     */
    public abstract String explore(Character character);

    /**
     * Gets a list of potential resources that can be found in this ambient.
     * The actual items found during exploration would be determined by chance and other factors.
     * Corresponds to "Recursos disponíveis: Lista de itens que podem ser coletados na área."
     * The map could be Item class vs. probability (0.0-1.0) or Item class vs. max quantity.
     * For simplicity, let's return a list of item *prototypes* or names.
     * @return A list of item types or names that can be found here.
     */
    public abstract List<String> getAvailableResourceTypes();


    /**
     * Gets a list of creatures typically found in this ambient.
     * This can be used by the exploration logic or an event system.
     * @return A list of creature types (class names or identifiers).
     */
    public abstract List<Class<? extends Creature>> getTypicalCreatures();


    /**
     * Modifies the current weather in this ambient.
     * Corresponds to "modificarClima()" from the PDF.
     * This might be called by a global weather system or specific events.
     * @return A description of the weather change.
     */
    public abstract String changeWeather();

    /**
     * Gets the probability of different event types occurring in this ambient.
     * This would be used by an EventManager.
     * Example: Map<EventType, Double> where EventType is an enum and Double is probability.
     * For now, this can be a conceptual method.
     * @return A representation of event probabilities.
     */
    public abstract Map<String, Double> getEventProbabilities();


    /**
     * Placeholder for updating resources in the ambient (e.g., regeneration over time).
     * Called during the maintenance phase.
     */
    public void updateResources() {
        // Default implementation: do nothing. Subclasses can override.
        // System.out.println("Resources in " + getName() + " are being updated (conceptual).");
    }

    @Override
    public String toString() {
        return TerminalStyler.style(String.format("%s: %s (Difficulty: %.1f, Weather: %s)", name, description, explorationDifficulty, currentWeather), TerminalStyler.BRIGHT_CYAN);
    }
}
