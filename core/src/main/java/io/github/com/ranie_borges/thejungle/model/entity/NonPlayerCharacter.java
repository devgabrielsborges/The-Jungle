package io.github.com.ranie_borges.thejungle.model.entity;

import com.badlogic.gdx.utils.Array;
import io.github.com.ranie_borges.thejungle.model.entity.interfaces.INonPlayerCharacter;
import io.github.com.ranie_borges.thejungle.model.enums.Trait;

import java.util.ArrayList;
import java.util.List;

public abstract class NonPlayerCharacter<T extends Item> implements INonPlayerCharacter {
    private String name;
    private float life;
    private float hunger;
    private float thirsty;
    private float energy;
    private float sanity;
    private double attackDamage;
    private double defenseStatus;
    private Array<Item> inventory;
    private List<Trait> traits;
    private double[] localization;
    private boolean isHostile;

    protected NonPlayerCharacter(String name) {
        this.name = name;
        this.life = 100.0f;
        this.hunger = 0.0f;
        this.thirsty = 0.0f;
        this.energy = 100.0f;
        this.sanity = 100.0f;
        this.attackDamage = 0.0;
        this.defenseStatus = 0.0;
        this.inventory = new Array<>();
        this.traits = new ArrayList<>();
        this.localization = new double[]{0.0, 0.0};
        this.isHostile = false;
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

    public Array<Item> getInventory() {
        return inventory;
    }

    public void setInventory(Array<Item> inventory) {
        this.inventory = inventory;
    }

    public void insertItemInInventory(Item item) {
        this.inventory.add(item);
    }

    public void dropItem(int index) {
        if (index >= 0 && index < inventory.size) {
            inventory.removeIndex(index);
        }
    }

    public List<Trait> getTraits() {
        return traits;
    }

    public void setTraits(List<Trait> traits) {
        this.traits = traits;
    }

    public double[] getLocalization() {
        return localization;
    }

    public void setLocalization(double[] localization) {
        this.localization = localization;
    }

    public boolean isHostile() {
        return isHostile;
    }

    public void setHostile(boolean hostile) {
        isHostile = hostile;
    }

    public boolean isAlive() {
        return life > 0;
    }

    @Override
    public abstract void roam();
}
