package io.github.com.ranie_borges.thejungle.model.events.events;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.events.Event;

public class Injury extends Event {
    private float impact;
    private float healRate;
    private boolean isHealable;

    protected Injury(String name, String description, float probability) {
        super(name, description, probability);
    }

    public float getImpact() {
        return impact;
    }

    public void setImpact(float impact) {
        this.impact = impact;
    }

    public float getHealRate() {
        return healRate;
    }

    public void setHealRate(float healRate) {
        this.healRate = healRate;
    }

    public boolean isHealable() {
        return isHealable;
    }

    public void setHealable(boolean healable) {
        isHealable = healable;
    }

    /**
     * @param character
     * @param <T>
     */
    @Override
    public <T extends Character> void execute(T character) {
    }

}
