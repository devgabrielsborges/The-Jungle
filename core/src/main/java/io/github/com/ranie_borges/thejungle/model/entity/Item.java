package io.github.com.ranie_borges.thejungle.model.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import io.github.com.ranie_borges.thejungle.model.entity.interfaces.IItem;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;


public abstract class Item implements IItem {
    private String name;
    private float weight;
    private float durability;
    private Map<String, Sprite> sprites;
    private Vector2 position = new Vector2();


    protected Item(String name, float weight, float durability) {
        this.name = name;
        this.weight = Math.max(0, weight);
        this.durability = Math.max(0, Math.min(100, durability));
        this.sprites = new HashMap<>();
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
    private static final Map<String, Texture> ICON_CACHE = new HashMap<>();

    public Texture getIconTexture() {
        String iconKey = getName().toLowerCase();

        if (!ICON_CACHE.containsKey(iconKey)) {
            try {
                Texture texture = new Texture("icons/" + iconKey + ".png");
                ICON_CACHE.put(iconKey, texture);
            } catch (Exception e) {
                // Fallback se imagem não existir
                Texture fallback = new Texture("icons/default.png");
                ICON_CACHE.put(iconKey, fallback);
            }
        }

        return ICON_CACHE.get(iconKey);
    }
    private int quantity = 1;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(1, quantity);
    }

    public void addQuantity(int amount) {
        this.quantity += amount;
    }




}

