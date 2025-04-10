package io.github.com.ranie_borges.thejungle.model.entity;

import com.badlogic.gdx.utils.Array;
import com.google.gson.annotations.Expose;
import io.github.com.ranie_borges.thejungle.model.enums.Trait;
import io.github.com.ranie_borges.thejungle.model.entity.interfaces.ICharacter;

import java.util.ArrayList;
import java.util.List;

public abstract class Character <T extends Item> implements ICharacter {
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
    private Array<T> inventory;

    @Expose
    private int inventoryInitialCapacity = 15;

    @Expose
    private int maxInventoryCapacity = 100;
    @Expose
    private double attackDamage;
    @Expose
    private double defenseStatus;
    @Expose
    private List<Trait> traits;
    @Expose
    private String characterType; // For proper deserialization of subtypes

    protected Character() {
        this.inventory = new Array<>(inventoryInitialCapacity);
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

    public Array<T> getInventory() {
        return inventory;
    }

    public void setInventory(Array<T> inventory) {
        this.inventory = inventory;
    }

    public int getInventoryInitialCapacity() {
        return inventoryInitialCapacity;
    }

    public void setInventoryInitialCapacity(int inventoryInitialCapacity) {
        this.inventoryInitialCapacity = inventoryInitialCapacity;
    }

    public void insertItemInInventory(Item item) {
        if (item != null && !isInventoryFull()) {
            inventory.add((T) item);
        }
    }

    public void insertItemInInventory(T item, int index) {
        if (index >= 0 && index < inventoryInitialCapacity) {
            // Ensure array is large enough for the index
            while (inventory.size <= index) {
                inventory.add(null);
            }
            inventory.set(index, item);
        }
    }

    public T getItem(int index) {
        if (index >= 0 && index < inventory.size) {
            return inventory.get(index);
        }
        return null;
    }

    public void dropItem(int index) {
        if (isInventoryIndexOk(index) && inventory.get(index) != null) {
            inventory.set(index, null);
        }
    }

    public void emptyInventory() {
        inventory.clear();
    }

    public void increaseInventoryCapacity(int newCapacity) {
        if (isNewInventoryCapacityOk(newCapacity)) {
            this.inventoryInitialCapacity = newCapacity;
            // Resize array if needed
            if (inventory.size < newCapacity) {
                inventory.ensureCapacity(newCapacity);
            }
        }
    }

    public boolean isNewInventoryCapacityOk(int newCapacity) {
        return newCapacity > 0 && newCapacity <= maxInventoryCapacity;
    }

    public boolean isInventoryFull() {
        return inventory.size >= inventoryInitialCapacity;
    }

    public boolean isInventoryIndexOk(int index) {
        return index >= 0 && index < inventory.size;
    }

    public boolean isInventoryIndexFree(int index) {
        return isInventoryIndexOk(index) && inventory.get(index) == null;
    }

    public boolean isInventoryEmpty() {
        // Check if size is 0 or all elements are null
        if (inventory.size == 0) return true;

        for (T item : inventory) {
            if (item != null) return false;
        }
        return true;
    }

    public int getInventorySize() {
        return inventory.size;
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

    public abstract void dropItem(Item item);
    public abstract void defend();
}
