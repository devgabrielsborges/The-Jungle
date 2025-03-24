package io.github.com.ranie_borges.thejungle.model.entity;

import io.github.com.ranie_borges.thejungle.model.enums.Trait;
import io.github.com.ranie_borges.thejungle.model.entity.interfaces.ICharacter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class Character implements ICharacter {
    private String name;
    private float life;
    private float hunger;
    private float thirsty;
    private float energy;
    private float sanity;
    private double[] localization;
    private List<Item> inventory;
    private double attackDamage;
    private double defenseStatus;
    private List<Trait> traits;

    protected Character(String name) {
        this.name = name;
        this.life = 100.0f;
        this.hunger = 0.0f;
        this.thirsty = 0.0f;
        this.energy = 100.0f;
        this.sanity = 100.0f;
        this.localization = new double[]{0.0, 0.0};
        this.inventory = new ArrayList<>();
        this.attackDamage = 0.0;
        this.defenseStatus = 0.0;
        this.traits = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLife() {
        return life;
    }

    public void setLife(float life) {
        this.life = Math.max(0, Math.min(100, life));
    }

    public float getHunger() {
        return hunger;
    }

    public void setHunger(float hunger) {
        this.hunger = Math.max(0, Math.min(100, hunger));
    }

    public float getThirsty() {
        return thirsty;
    }

    public void setThirsty(float thirsty) {
        this.thirsty = Math.max(0, Math.min(100, thirsty));
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = Math.max(0, Math.min(100, energy));
    }

    public float getSanity() {
        return sanity;
    }

    public void setSanity(float sanity) {
        this.sanity = Math.max(0, Math.min(100, sanity));
    }

    public double[] getLocalization() {
        return Arrays.copyOf(localization, localization.length);
    }

    public void setLocalization(double[] localization) {
        this.localization = Arrays.copyOf(localization, localization.length);
    }

    public List<Item> getInventory() {
        return Collections.unmodifiableList(inventory);
    }

    public void addItem(Item item) {
        this.inventory.add(item);
    }

    public void removeItem(Item item) {
        this.inventory.remove(item);
    }

    public void setInventory(List<Item> inventory) {
        this.inventory = new ArrayList<>(inventory);
    }

    public double getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(double attackDamage) {
        this.attackDamage = Math.max(0, attackDamage);
    }

    public double getDefenseStatus() {
        return defenseStatus;
    }

    public void setDefenseStatus(double defenseStatus) {
        this.defenseStatus = Math.max(0, defenseStatus);
    }

    public List<Trait> getTraits() {
        return Collections.unmodifiableList(traits);
    }

    public void addTrait(Trait trait) {
        this.traits.add(trait);
    }

    public void removeTrait(Trait trait) {
        this.traits.remove(trait);
    }

    public void setTraits(List<Trait> traits) {
        this.traits = new ArrayList<>(traits);
    }
}
