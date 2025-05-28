package com.ranieborges.thejungle.cli.model.entity.utils.enums;

/**
 * Represents special traits or abilities a character might possess.
 * These traits can provide advantages or define a character's specialization.
 */
public enum Trait {
    // Traits Roteiro
    TRACKER("Rastreador", "Finds food and water more easily."),
    MECHANIC("Mecânico", "Can repair tools and craft new weapons more effectively."),
    MEDIC("Médico", "Can treat wounds more effectively, possibly without rare items."),
    NATURAL_SURVIVOR("Sobrevivente Nato", "Less impacted by hunger and thirst."),

    // Additional common survival traits
    STRONG("Força Aprimorada", "Increased carrying capacity and melee damage."),
    AGILE("Agilidade", "Moves faster and is better at dodging threats."),
    STEALTHY("Furtividade", "Better at avoiding detection by creatures or other survivors."),
    RESOURCEFUL("Engenhoso", "Higher chance of finding extra resources or better quality items."),
    KEEN_SENSES("Sentidos Aguçados", "Better at detecting nearby threats or points of interest."),
    IRON_STOMACH("Estômago de Ferro", "Reduced chance of getting sick from questionable food/water."),
    NIGHT_OWL("Notívago", "Less penalty to energy or sanity during the night."),
    FIRE_MAKER("Perito em Fogo", "Easier to start and maintain fires."),
    NEGOTIATOR("Negociador", "Better outcomes when interacting with other survivors or factions."),
    SCAVENGER("Catador", "Finds more items when scavenging locations like ruins."),
    FAST_HEALER("Recuperação Rápida", "Recovers health from injuries faster."),
    ENDURANCE_RUNNER("Corredor de Resistência", "Consumes less energy when moving long distances."),
    ANIMAL_WHISPERER("Amigo dos Animais", "Less likely to be attacked by neutral wildlife; may be able to tame some."),
    BOTANIST("Botanista", "Can identify edible/medicinal plants and gather them more effectively."),
    FISHERMAN("Pescador Habilidoso", "Higher chance of catching fish and larger fish."),
    COOK("Cozinheiro", "Can prepare food that provides more nourishment or positive effects."),
    WEATHER_RESISTANT("Resistente ao Clima", "Less affected by extreme weather conditions (heat/cold)."),
    PATHFINDER("Desbravador", "Moves more efficiently through difficult terrain (e.g., dense forests, mountains)."),
    LUCKY("Sortudo", "Slightly increased chance for positive outcomes in random events."),
    CALM_MIND("Mente Calma", "More resistant to sanity loss from stressful situations.");


    private final String displayName;
    private final String description;

    Trait(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Gets the user-friendly display name of the trait (e.g., in Portuguese).
     * @return The display name.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the description of what the trait does.
     * @return The trait's description.
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName + ": " + description;
    }
}
