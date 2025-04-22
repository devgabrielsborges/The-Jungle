package io.github.com.ranie_borges.thejungle.model.events.events;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.events.Event;

public class Mob<T extends Creature> extends Event {
    private T creature;
    private int dangerLevel;
    private boolean isHostile;
    private boolean isAvoidable;

    public T getCreature() { return creature; }

    public void setCreature(T creature) {
        this.creature = creature;
    }

    public int getDangerLevel() {
        return dangerLevel;
    }

    public void setDangerLevel(int dangerLevel) {
        this.dangerLevel = dangerLevel;
    }

    public boolean isHostile() {
        return isHostile;
    }

    public void setHostile(boolean hostile) {
        isHostile = hostile;
    }

    public boolean isAvoidable() {
        return isAvoidable;
    }

    public void setAvoidable(boolean avoidable) {
        isAvoidable = avoidable;
    }

    protected Mob(
        String name,
        String description,
        float probability,
        T creature,
        int dangerLevel,
        boolean isAvoidable,
        boolean isHostile
    ) {
        super(name, description, probability);
        setCreature(creature);
        setDangerLevel(dangerLevel);
        setAvoidable(isAvoidable);
        setHostile(isHostile);

    }

    /**
     * @param character
     * @param <U>
     */
    @Override
    public <U extends Character> void execute(U character) {
    }

}
