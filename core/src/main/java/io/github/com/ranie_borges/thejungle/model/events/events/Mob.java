package io.github.com.ranie_borges.thejungle.model.events.events;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.events.Event;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;

public class Mob extends Event {
    private Creature creature;
    private int dangerLevel;
    private boolean isHostile;
    private boolean isAvoidable;

    public Creature getCreature() { return creature; }

    public void setCreature(Creature creature) {
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
        Creature creature,
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
     */
    @Override
    public void execute(Character character, Ambient ambient) {

    }
}
