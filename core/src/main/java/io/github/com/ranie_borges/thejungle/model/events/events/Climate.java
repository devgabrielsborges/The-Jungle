package io.github.com.ranie_borges.thejungle.model.events.events;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;
import io.github.com.ranie_borges.thejungle.model.events.Event;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;

import java.util.Random;
import java.util.Set;

public class Climate extends Event {
    private Clime clime;
    private double duration;
    private Set<String> effects; // Example: "REDUCE_VISIBILITY", "INCREASE_THIRST_RATE"
    private final transient Random rand = new Random(); // Added transient

    protected Climate(String name, String description, float probability) {
        super(name, description, probability);
        this.clime = setRandomClime(); // Initialize with a random clime type
        // Duration and effects would typically be set based on the clime type
    }

    public Clime getClime() {
        return clime;
    }

    public void setClime(Clime clime) {
        this.clime = clime;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public Set<String> getEffects() { return effects; }

    public void setEffects(Set<String> effects) { this.effects = effects; }

    private Clime setRandomClime() { // Renamed to avoid conflict with setter
        Clime[] climeValues = Clime.values();
        if (climeValues.length == 0) return null; // Should not happen
        return climeValues[rand.nextInt(climeValues.length)];
    }

    @Override
    public void execute(Character character, Ambient ambient) {
        // Implementation for how this climate event affects the character and ambient
        // e.g., if clime is EXTREMELY_HOT, increase character's thirst rate
        // or change ambient properties.
        // For now, it just logs.
        System.out.println("Climate event '" + getName() + "' (Type: " + clime + ") executed in " + ambient.getName());
        // Example: ambient.setCurrentClime(this.clime); character.applyEffect(this.effects);
    }
}
