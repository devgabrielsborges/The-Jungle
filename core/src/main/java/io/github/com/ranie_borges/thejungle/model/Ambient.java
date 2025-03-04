package io.github.com.ranie_borges.thejungle.model;

import io.github.com.ranie_borges.thejungle.model.enums.Clime;
import io.github.com.ranie_borges.thejungle.model.interfaces.IAmbients;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Ambient implements IAmbients {
    private String name;
    private String description;
    private float difficult;
    private List<Item> resources;
    private List<Event> possibleEvents;
    private float eventChance;

    public List<Event> getPossibleEvents() {
        return possibleEvents;
    }

    public void setPossibleEvents(List<Event> possibleEvents) {
        this.possibleEvents = possibleEvents;
    }

    private List<Clime> climes;

    protected Ambient(String name, String description, float difficult) {
        this.name = name;
        this.description = description;
        this.difficult = difficult;
        this.resources = new ArrayList<>();
        this.climes = new ArrayList<>();
        this.eventChance = 0.0f;
    }

    // Getters and setters
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

    public List<Item> getResources() {
        return Collections.unmodifiableList(resources);
    }

    public void addResource(Item resource) {
        this.resources.add(resource);
    }

    public void setResources(List<Item> resources) {
        this.resources = new ArrayList<>(resources);
    }

    public float getEventChance() {
        return this.eventChance;
    }

    public void setEventChance(float eventChance) {
        this.eventChance = Math.max(0, Math.min(1, eventChance)); // Ensure between 0 and 1
    }

    public List<Clime> getClimes() {
        return Collections.unmodifiableList(climes);
    }

    public void addClime(Clime clime) {
        this.climes.add(clime);
    }

    public void setClimes(List<Clime> climes) {
        this.climes = new ArrayList<>(climes);
    }
}
