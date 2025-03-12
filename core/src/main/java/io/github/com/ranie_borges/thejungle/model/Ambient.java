package io.github.com.ranie_borges.thejungle.model;

import io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;
import io.github.com.ranie_borges.thejungle.model.interfaces.IAmbients;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Ambient implements IAmbients {
    private String name;
    private String description;
    private float difficult;

    private Set<AmbientAttribute> attributes;
    private Set<Item> resources;
    private Map<Event, Double> possibleEvents;
    private Set<Clime> climes;

    protected Ambient(String name, String description, float difficult, Set<AmbientAttribute> attributes) {
        setName(name);
        setDescription(description);
        setDifficult(difficult);
        setAttributes(attributes != null ? new HashSet<>(attributes) : new HashSet<>());
        this.resources = new HashSet<>();
        this.possibleEvents = new HashMap<>();
        this.climes = new HashSet<>();
    }

    protected Ambient(String name, String description, float difficult) {
        this(name, description, difficult, new HashSet<>());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getDifficult() {
        return difficult;
    }

    public void setDifficult(float difficult) {
        this.difficult = difficult;
    }

    public Set<Item> getResources() {
        return Collections.unmodifiableSet(resources);
    }

    public void addResource(Item resource) {
        this.resources.add(resource);
    }

    public void setResources(Set<Item> resources) {
        this.resources = new HashSet<>(resources);
    }

    public Map<Event, Double> getPossibleEvents() {
        return Collections.unmodifiableMap(possibleEvents);
    }

    public void setPossibleEvents(Map<Event, Double> possibleEvents) {
        this.possibleEvents = possibleEvents;
    }

    public Set<Clime> getClimes() {
        return Collections.unmodifiableSet(climes);
    }

    public void addClime(Clime clime) {
        this.climes.add(clime);
    }

    public void setClimes(Set<Clime> climes) {
        this.climes = new HashSet<>(climes);
    }

    public Set<AmbientAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<AmbientAttribute> attributes) {
        this.attributes = attributes;
    }
}
