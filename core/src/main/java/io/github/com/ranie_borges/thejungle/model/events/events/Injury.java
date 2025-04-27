package io.github.com.ranie_borges.thejungle.model.events.events;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.events.Event;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;

import java.util.Set;

public class Injury extends Event {
    private String name;

    private float impact;

    private float healRate;
    private boolean isHealable;
    private Set<Item> healableItems;

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
     * @param ambient
     */
    @Override
    public void execute(Character character, Ambient ambient) {

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public Set<Item> getHealableItems() {
        return healableItems;
    }

    public void setHealableItems(Set<Item> healableItems) {
        this.healableItems = healableItems;
    }
}
