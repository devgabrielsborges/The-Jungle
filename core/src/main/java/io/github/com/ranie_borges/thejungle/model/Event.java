package io.github.com.ranie_borges.thejungle.model;

import io.github.com.ranie_borges.thejungle.model.enums.Attribute;
import io.github.com.ranie_borges.thejungle.model.interfaces.IEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Event implements IEvent {
    private String name;
    private String description;
    private float probability;
    private List<Attribute> impacts;
    private boolean isActivatable;

    protected Event(String name, String description, float probability) {
        this.name = name;
        this.description = description;
        this.probability = Math.max(0, Math.min(1, probability)); // Ensure between 0 and 1
        this.impacts = new ArrayList<>();
        this.isActivatable = true;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getProbability() {
        return this.probability;
    }

    public void setProbability(float probability) {
        this.probability = Math.max(0, Math.min(1, probability)); // Ensure between 0 and 1
    }

    public List<Attribute> getImpacts() {
        return Collections.unmodifiableList(this.impacts);
    }

    public void addImpact(Attribute impact) {
        this.impacts.add(impact);
    }

    public void setImpacts(List<Attribute> impacts) {
        this.impacts = new ArrayList<>(impacts);
    }

    public boolean isPossible() {
        return this.isActivatable;
    }

    public void setPossible(boolean possible) {
        this.isActivatable = possible;
    }
}
