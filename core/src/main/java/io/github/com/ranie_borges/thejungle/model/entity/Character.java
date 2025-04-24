package io.github.com.ranie_borges.thejungle.model.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
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
    private Array<Item> inventory;

    @Expose
    private int inventoryInitialCapacity = 15;

    @Expose
    private int maxInventoryCapacity = 100;
    @Expose
    private double attackDamage;

    @Expose
    private List<Trait> traits;

    @Expose
    private String characterType;
    @Expose
    private float speed;

    @Expose
    private Vector2 position;
    private final Texture texture;

    protected Character(
        String name,
        float life,
        float hunger,
        float thirsty,
        float energy,
        float sanity,
        float attackDamage,
        String spritePath,
        float xPosition,
        float yPosition
    ) {
        this.name = name;
        this.life = life;
        this.hunger = hunger;
        this.thirsty = thirsty;
        this.energy = energy;
        this.sanity = sanity;
        this.attackDamage = attackDamage;
        this.inventory = new Array<>(inventoryInitialCapacity);
        this.traits = new ArrayList<>();
        this.texture = new Texture(Gdx.files.internal(spritePath));
        this.position = new Vector2(xPosition, yPosition);
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

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public Array<Item> getInventory() {
        return inventory;
    }

    public void setInventory(Array<Item> inventory) {
        this.inventory = inventory;
    }

    public int getMaxInventoryCapacity() {
        return maxInventoryCapacity;
    }

    public void setMaxInventoryCapacity(int maxInventoryCapacity) {
        this.maxInventoryCapacity = maxInventoryCapacity;
    }

    public int getInventoryInitialCapacity() {
        return inventoryInitialCapacity;
    }

    public void setInventoryInitialCapacity(int inventoryInitialCapacity) {
        this.inventoryInitialCapacity = inventoryInitialCapacity;
    }

    public void insertItemInInventory(Item item) {
        if (item != null && !isInventoryFull()) {
            inventory.add(item);
        }
    }

    public void insertItemInInventory(Item item, int index) {
        if (index >= 0 && index < inventoryInitialCapacity) {
            // Ensure array is large enough for the index
            while (inventory.size <= index) {
                inventory.add(null);
            }
            inventory.set(index, item);
        }
    }

    public Item getItem(int index) {
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

        for (Item item : inventory) {
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

    public void updatePosition(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            position.y += speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            position.y -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            position.x -= speed * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            position.x += speed * delta;
        }
    }

    public void render(Batch batch) {
        batch.draw(this.texture, this.position.x, this.position.y);
    }

    public void dispose() {
        this.texture.dispose();
    }
}
