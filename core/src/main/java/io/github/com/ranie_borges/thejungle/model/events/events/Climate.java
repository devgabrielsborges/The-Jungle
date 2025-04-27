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
    private Set<String> effects;
    private final Random rand = new Random();

    protected Climate(String name, String description, float probability) {
        super(name, description, probability);
        this.clime = setRandomClime();
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

    public Clime setRandomClime() {
        return Clime.values()[rand.nextInt() * Clime.values().length];
    }

    /**
     * @param character
     * @param ambient
     */
    @Override
    public void execute(Character character, Ambient ambient) {

    }
}
