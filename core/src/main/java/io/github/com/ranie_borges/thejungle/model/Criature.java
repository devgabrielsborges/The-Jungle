package io.github.com.ranie_borges.thejungle.model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Criature {
    private String name;
    private String description;
    private float probability;
    private float lifeRatio;
    private float damage;
    private Clime climeSpawn;
    private Set<Item> drops;
    private Map<String, Sprite> sprites;

    protected Criature(
        String name,
        String description,
        float probability,
        float lifeRatio,
        float damage,
        Clime climeSpawn,
        Set<Item> drops,
        Map<String, Sprite> sprites
    ) {
        setName(name);
        setDescription(description);
        setProbability(probability);
        setLifeRatio(lifeRatio);
        setDamage(damage);
        setClimeSpawn(climeSpawn);
        setDrops(drops != null ? new HashSet<>(drops) : new HashSet<>());
        setSprites(sprites != null ? new HashMap<>(sprites) : new HashMap<>());
    }

    public Clime getClimeSpawn() {
        return climeSpawn;
    }

    public void setClimeSpawn(Clime climeSpawn) {
        this.climeSpawn = climeSpawn;
    }

    public float getLifeRatio() {
        return lifeRatio;
    }

    public void setLifeRatio(float lifeRatio) {
        this.lifeRatio = lifeRatio;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public Set<Item> getDrops() {
        return Collections.unmodifiableSet(drops);
    }

    public void setDrops(Set<Item> drops) {
        this.drops = drops != null ? new HashSet<>(drops) : new HashSet<>();
    }

    public Map<String, Sprite> getSprites() {
        return Collections.unmodifiableMap(sprites);
    }

    public void setSprites(Map<String, Sprite> sprites) {
        this.sprites = sprites != null ? new HashMap<>(sprites) : new HashMap<>();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getProbability() {
        return probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }
}
