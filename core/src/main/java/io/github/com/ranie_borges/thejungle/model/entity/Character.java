package io.github.com.ranie_borges.thejungle.model.entity;

import com.google.gson.annotations.Expose;
import io.github.com.ranie_borges.thejungle.model.enums.Trait;
import io.github.com.ranie_borges.thejungle.model.entity.interfaces.ICharacter;

import java.util.ArrayList;
import java.util.List;

public abstract class Character implements ICharacter {
    // Fields to be serialized
    @Expose
    private String name;
    @Expose
    private float life;
    @Expose
    private float hunger;
    @Expose
    private float thirsty;
    @Expose
    private float energy;
    @Expose
    private float sanity;
    @Expose
    private double[] localization;
    @Expose
    private List<Item> inventory;
    @Expose
    private double attackDamage;
    @Expose
    private double defenseStatus;
    @Expose
    private List<Trait> traits;
    @Expose
    private String characterType; // For proper deserialization of subtypes

    protected Character() {
        this.inventory = new ArrayList<>();
        this.traits = new ArrayList<>();
        this.localization = new double[]{0.0, 0.0};
    }

    protected Character(String name) {
        this();
        this.name = name;
        this.life = 100.0f;
        this.hunger = 0.0f;
        this.thirsty = 0.0f;
        this.energy = 100.0f;
        this.sanity = 100.0f;
        this.attackDamage = 0.0;
        this.defenseStatus = 0.0;
        this.characterType = this.getClass().getSimpleName();
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
        this.life = life;
    }

    public float getHunger() {
        return hunger;
    }

    public void setHunger(float hunger) {
        this.hunger = hunger;
    }

    public float getThirsty() {
        return thirsty;
    }

    public void setThirsty(float thirsty) {
        this.thirsty = thirsty;
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public float getSanity() {
        return sanity;
    }

    public void setSanity(float sanity) {
        this.sanity = sanity;
    }

    public double[] getLocalization() {
        return localization;
    }

    public void setLocalization(double[] localization) {
        this.localization = localization;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public void setInventory(List<Item> inventory) {
        this.inventory = inventory;
    }

    public double getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(double attackDamage) {
        this.attackDamage = attackDamage;
    }

    public double getDefenseStatus() {
        return defenseStatus;
    }

    public void setDefenseStatus(double defenseStatus) {
        this.defenseStatus = defenseStatus;
    }

    public List<Trait> getTraits() {
        return traits;
    }

    public void setTraits(List<Trait> traits) {
        this.traits = traits;
    }

    public String getCharacterType() {
        return characterType;
    }

    public void setCharacterType(String characterType) {
        this.characterType = characterType;
    }
}
