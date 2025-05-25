package io.github.com.ranie_borges.thejungle.model.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.google.gson.annotations.Expose;
import io.github.com.ranie_borges.thejungle.model.entity.interfaces.IItem;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture; // Keep for getIconTexture
import com.badlogic.gdx.Gdx; // Keep for Gdx.files in getIconTexture

public abstract class Item implements IItem {
    @Expose
    private String name;
    @Expose
    private float weight;
    @Expose
    private float durability;

    private transient Map<String, Sprite> sprites; // Marked as transient

    private final transient Vector2 position = new Vector2(); // Also make position transient if not saved/loaded based on need

    @Expose
    private int quantity = 1;

    protected Item(String name, float weight, float durability) {
        this.name = name;
        this.weight = Math.max(0, weight);
        this.durability = Math.max(0, Math.min(100, durability)); // Durability typically 0-1 or 0-100
        this.sprites = new HashMap<>(); // Initialize even if transient, for runtime use
    }

    public float getDurability() {
        return this.durability;
    }

    public void setDurability(float durability) {
        this.durability = Math.max(0, Math.min(100, durability));
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
    }

    public float getWeight() {
        return this.weight;
    }

    public void setWeight(float weight) {
        this.weight = Math.max(0, weight);
    }

    public Map<String, Sprite> getSprites() {
        // If sprites are null after loading (due to transient), they need to be re-initialized.
        // This might be done via a specific loadAssets() or initSprites() method called post-deserialization.
        if (this.sprites == null) {
            this.sprites = new HashMap<>();
            // Consider calling a method here to re-populate sprites based on item name/type
            // e.g., this.loadMySprites();
        }
        return Collections.unmodifiableMap(sprites);
    }

    public void setSprites(Map<String, Sprite> sprites) {
        this.sprites = sprites != null ? new HashMap<>(sprites) : new HashMap<>();
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    // ICON_CACHE is static and transient by nature of static for instance serialization
    private static final transient Map<String, Texture> ICON_CACHE = new HashMap<>();

    public Texture getIconTexture() {
        if (getName() == null) return null; // Handle cases where item name might be null
        String iconKey = getName().toLowerCase().replace(" ", "_"); // Make key more filesystem-friendly

        if (!ICON_CACHE.containsKey(iconKey)) {
            Texture texture = null;
            try {
                texture = new Texture(Gdx.files.internal("icons/" + iconKey + ".png"));
                ICON_CACHE.put(iconKey, texture);
            } catch (Exception e) {
                // Fallback if specific icon doesn't exist
                try {
                    texture = new Texture(Gdx.files.internal("icons/default.png"));
                    ICON_CACHE.put(iconKey, texture); // Cache fallback for this key too
                } catch (Exception e2) {
                    System.err.println("Failed to load default icon: " + e2.getMessage());
                    // If default is also missing, this will be problematic.
                    // Consider returning a placeholder or handling it. For now, it might remain null.
                }
            }
        }
        return ICON_CACHE.get(iconKey);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity); // Ensure quantity is not negative
    }

    public void addQuantity(int amount) {
        this.quantity += amount;
        if (this.quantity < 0) this.quantity = 0; // Ensure not negative after adding (e.g. if amount was negative)
    }

    // Abstract methods from IItem
    public abstract void useItem();
    public abstract void dropItem();

    // Method to be called after deserialization to re-initialize transient fields
    // This is a placeholder; specific logic will depend on how sprites are managed (e.g., TextureManager)
    public void initializeTransientGraphics() {
        if (this.sprites == null) {
            this.sprites = new HashMap<>();
        }
        // Example: Re-load sprites based on item name or type
        // This needs actual implementation based on your asset loading strategy
        // For Material, its static factories like createSmallRock already set sprites.
        // If this is a Material instance, its type should determine sprite loading.
        // e.g. if (this instanceof Material) { ((Material)this).reloadSprites(); }
        // This is a complex part that depends on your overall asset and object initialization design.
    }
}
