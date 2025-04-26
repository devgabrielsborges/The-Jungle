package io.github.com.ranie_borges.thejungle.model.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.google.gson.annotations.Expose;
import io.github.com.ranie_borges.thejungle.model.enums.Trait;
import io.github.com.ranie_borges.thejungle.model.entity.interfaces.ICharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class Character implements ICharacter {
    private static final Logger logger = LoggerFactory.getLogger(Character.class);

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
        try {
            this.name = name != null ? name : "Unknown";
            this.life = Math.max(0, life);
            this.hunger = Math.max(0, hunger);
            this.thirsty = Math.max(0, thirsty);
            this.energy = Math.max(0, energy);
            this.sanity = Math.max(0, sanity);
            this.attackDamage = Math.max(0, attackDamage);
            this.inventory = new Array<>(inventoryInitialCapacity);
            this.traits = new ArrayList<>();

            // Load texture with exception handling
            Texture tempTexture;
            try {
                if (spritePath == null || spritePath.isEmpty()) {
                    throw new IllegalArgumentException("Sprite path cannot be null or empty");
                }
                tempTexture = new Texture(Gdx.files.internal(spritePath));
                logger.debug("Successfully loaded texture: {}", spritePath);
            } catch (Exception e) {
                logger.error("Failed to load texture {}: {}", spritePath, e.getMessage());
                // Create a fallback 1x1 texture
                Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
                pixmap.setColor(1, 0, 1, 0.5f); // Semi-transparent magenta for visual debugging
                pixmap.fill();
                tempTexture = new Texture(pixmap);
                pixmap.dispose();
            }
            this.texture = tempTexture;

            this.position = new Vector2(xPosition, yPosition);
            this.characterType = this.getClass().getSimpleName();

        } catch (Exception e) {
            logger.error("Error creating character {}: {}", name, e.getMessage());
            throw e; // Re-throw after logging as this is a critical initialization error
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        try {
            this.name = name != null ? name : "Unknown";
        } catch (Exception e) {
            logger.error("Error setting name: {}", e.getMessage());
            this.name = "Error";
        }
    }

    public float getLife() {
        return life;
    }

    public void setLife(float life) {
        try {
            if (life < 0) {
                logger.warn("{}: Tried to set negative life value ({}), clamping to 0", name, life);
                this.life = 0;
            } else {
                this.life = life;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting life: {}", name, e.getMessage());
        }
    }

    public float getHunger() {
        return hunger;
    }

    public void setHunger(float hunger) {
        try {
            if (hunger < 0) {
                logger.warn("{}: Tried to set negative hunger value ({}), clamping to 0", name, hunger);
                this.hunger = 0;
            } else {
                this.hunger = hunger;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting hunger: {}", name, e.getMessage());
        }
    }

    public float getThirsty() {
        return thirsty;
    }

    public void setThirsty(float thirsty) {
        try {
            if (thirsty < 0) {
                logger.warn("{}: Tried to set negative thirsty value ({}), clamping to 0", name, thirsty);
                this.thirsty = 0;
            } else {
                this.thirsty = thirsty;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting thirsty: {}", name, e.getMessage());
        }
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        try {
            if (energy < 0) {
                logger.warn("{}: Tried to set negative energy value ({}), clamping to 0", name, energy);
                this.energy = 0;
            } else {
                this.energy = energy;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting energy: {}", name, e.getMessage());
        }
    }

    public float getSanity() {
        return sanity;
    }

    public void setSanity(float sanity) {
        try {
            if (sanity < 0) {
                logger.warn("{}: Tried to set negative sanity value ({}), clamping to 0", name, sanity);
                this.sanity = 0;
            } else {
                this.sanity = sanity;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting sanity: {}", name, e.getMessage());
        }
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        try {
            if (speed < 0) {
                logger.warn("{}: Tried to set negative speed value ({}), clamping to 0", name, speed);
                this.speed = 0;
            } else {
                this.speed = speed;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting speed: {}", name, e.getMessage());
        }
    }

    public Array<Item> getInventory() {
        return inventory;
    }

    public void setInventory(Array<Item> inventory) {
        try {
            if (inventory == null) {
                logger.warn("{}: Tried to set null inventory, creating empty inventory instead", name);
                this.inventory = new Array<>(inventoryInitialCapacity);
            } else {
                this.inventory = inventory;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting inventory: {}", name, e.getMessage());
            this.inventory = new Array<>(inventoryInitialCapacity);
        }
    }

    public int getMaxInventoryCapacity() {
        return maxInventoryCapacity;
    }

    public void setMaxInventoryCapacity(int maxInventoryCapacity) {
        try {
            if (maxInventoryCapacity <= 0) {
                logger.warn("{}: Invalid max inventory capacity: {}, must be positive", name, maxInventoryCapacity);
                return;
            }
            this.maxInventoryCapacity = maxInventoryCapacity;
        } catch (Exception e) {
            logger.error("{}: Error setting max inventory capacity: {}", name, e.getMessage());
        }
    }

    public int getInventoryInitialCapacity() {
        return inventoryInitialCapacity;
    }

    public void setInventoryInitialCapacity(int inventoryInitialCapacity) {
        try {
            if (inventoryInitialCapacity <= 0) {
                logger.warn("{}: Invalid inventory initial capacity: {}, must be positive", name, inventoryInitialCapacity);
                return;
            }

            if (inventoryInitialCapacity > maxInventoryCapacity) {
                logger.warn("{}: Initial capacity {} exceeds max capacity {}, capping at max",
                    name, inventoryInitialCapacity, maxInventoryCapacity);
                this.inventoryInitialCapacity = maxInventoryCapacity;
            } else {
                this.inventoryInitialCapacity = inventoryInitialCapacity;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting initial inventory capacity: {}", name, e.getMessage());
        }
    }

    public void insertItemInInventory(Item item) {
        try {
            if (item == null) {
                logger.warn("{}: Attempted to add null item to inventory", name);
                return;
            }

            if (isInventoryFull()) {
                logger.warn("{}: Cannot add item '{}', inventory is full", name, item.getName());
                return;
            }

            inventory.add(item);
            logger.debug("{}: Added item '{}' to inventory", name, item.getName());
        } catch (Exception e) {
            logger.error("{}: Error adding item to inventory: {}", name, e.getMessage());
        }
    }

    public void insertItemInInventory(Item item, int index) {
        try {
            if (item == null) {
                logger.warn("{}: Attempted to add null item at index {}", name, index);
                return;
            }

            if (index < 0 || index >= inventoryInitialCapacity) {
                logger.warn("{}: Invalid inventory index: {}", name, index);
                return;
            }

            // Ensure array is large enough for the index
            while (inventory.size <= index) {
                inventory.add(null);
            }

            inventory.set(index, item);
            logger.debug("{}: Set item '{}' at index {}", name, item.getName(), index);
        } catch (Exception e) {
            logger.error("{}: Error setting item at index {}: {}", name, index, e.getMessage());
        }
    }

    public Item getItem(int index) {
        try {
            if (index < 0 || index >= inventory.size) {
                logger.warn("{}: Attempt to access invalid inventory index: {}", name, index);
                return null;
            }
            return inventory.get(index);
        } catch (Exception e) {
            logger.error("{}: Error accessing inventory at index {}: {}", name, index, e.getMessage());
            return null;
        }
    }

    public void dropItem(int index) {
        try {
            if (index < 0 || index >= inventory.size) {
                logger.warn("{}: Cannot drop item, invalid index: {}", name, index);
                return;
            }

            Item item = inventory.get(index);
            if (item == null) {
                logger.warn("{}: No item to drop at index {}", name, index);
                return;
            }

            inventory.set(index, null);
            logger.debug("{}: Dropped item '{}' from index {}", name, item.getName(), index);
        } catch (Exception e) {
            logger.error("{}: Error dropping item at index {}: {}", name, index, e.getMessage());
        }
    }

    public void emptyInventory() {
        try {
            inventory.clear();
            logger.debug("{}: Inventory emptied", name);
        } catch (Exception e) {
            logger.error("{}: Error emptying inventory: {}", name, e.getMessage());
        }
    }

    public void increaseInventoryCapacity(int newCapacity) {
        try {
            if (!isNewInventoryCapacityOk(newCapacity)) {
                logger.warn("{}: Invalid new inventory capacity: {}", name, newCapacity);
                return;
            }

            this.inventoryInitialCapacity = newCapacity;
            if (inventory.size < newCapacity) {
                inventory.ensureCapacity(newCapacity);
                logger.debug("{}: Inventory capacity increased to {}", name, newCapacity);
            }
        } catch (Exception e) {
            logger.error("{}: Error increasing inventory capacity: {}", name, e.getMessage());
        }
    }

    public boolean isNewInventoryCapacityOk(int newCapacity) {
        try {
            return newCapacity > 0 && newCapacity <= maxInventoryCapacity;
        } catch (Exception e) {
            logger.error("{}: Error checking inventory capacity: {}", name, e.getMessage());
            return false;
        }
    }

    public boolean isInventoryFull() {
        try {
            return inventory.size >= inventoryInitialCapacity;
        } catch (Exception e) {
            logger.error("{}: Error checking if inventory is full: {}", name, e.getMessage());
            return true; // Safer to assume it's full if we can't check
        }
    }

    public boolean isInventoryIndexOk(int index) {
        try {
            return index >= 0 && index < inventory.size;
        } catch (Exception e) {
            logger.error("{}: Error checking inventory index: {}", name, e.getMessage());
            return false;
        }
    }

    public boolean isInventoryIndexFree(int index) {
        try {
            return isInventoryIndexOk(index) && inventory.get(index) == null;
        } catch (Exception e) {
            logger.error("{}: Error checking if inventory index is free: {}", name, e.getMessage());
            return false;
        }
    }

    public boolean isInventoryEmpty() {
        try {
            // Check if size is 0 or all elements are null
            if (inventory.size == 0) return true;

            for (Item item : inventory) {
                if (item != null) return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("{}: Error checking if inventory is empty: {}", name, e.getMessage());
            return true; // Safer to assume it's empty
        }
    }

    public int getInventorySize() {
        try {
            return inventory.size;
        } catch (Exception e) {
            logger.error("{}: Error getting inventory size: {}", name, e.getMessage());
            return 0;
        }
    }

    public double getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(double attackDamage) {
        try {
            if (attackDamage < 0) {
                logger.warn("{}: Tried to set negative attack damage ({}), clamping to 0", name, attackDamage);
                this.attackDamage = 0;
            } else {
                this.attackDamage = attackDamage;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting attack damage: {}", name, e.getMessage());
        }
    }

    public List<Trait> getTraits() {
        return traits;
    }

    public void setTraits(List<Trait> traits) {
        try {
            if (traits == null) {
                logger.warn("{}: Tried to set null traits list, creating empty list instead", name);
                this.traits = new ArrayList<>();
            } else {
                this.traits = traits;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting traits: {}", name, e.getMessage());
            this.traits = new ArrayList<>();
        }
    }

    public String getCharacterType() {
        return characterType;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setCharacterType(String characterType) {
        try {
            if (characterType == null || characterType.isEmpty()) {
                logger.warn("{}: Invalid character type, using class name instead", name);
                this.characterType = this.getClass().getSimpleName();
            } else {
                this.characterType = characterType;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting character type: {}", name, e.getMessage());
            this.characterType = "Unknown";
        }
    }

    public abstract void dropItem(Item item);

    public void updatePosition(float delta) {
        try {
            if (delta <= 0) {
                logger.warn("{}: Invalid delta time: {}, skipping position update", name, delta);
                return;
            }

            float newX = position.x;
            float newY = position.y;

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                newY += speed * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                newY -= speed * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                newX -= speed * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                newX += speed * delta;
            }

            position.set(newX, newY);
        } catch (Exception e) {
            logger.error("{}: Error updating position: {}", name, e.getMessage());
        }
    }

    public void render(Batch batch) {
        try {
            if (batch == null) {
                logger.warn("{}: Cannot render with null batch", name);
                return;
            }

            if (texture == null) {
                logger.warn("{}: Cannot render with null or disposed texture", name);
                return;
            }

            batch.draw(texture, position.x, position.y);
        } catch (Exception e) {
            logger.error("{}: Error rendering character: {}", name, e.getMessage());
        }
    }

    public void dispose() {
        try {
            if (texture != null) {
                texture.dispose();
                logger.debug("{}: Texture disposed successfully", name);
            }
        } catch (Exception e) {
            logger.error("{}: Error disposing texture: {}", name, e.getMessage());
        }
    }
}
