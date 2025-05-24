package io.github.com.ranie_borges.thejungle.model.entity.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Food;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.LakeRiver;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Fish extends Creature {

    protected Fish(
        String name,
        String description,
        float probability,
        float lifeRatio,
        float damage,
        Clime climeSpawn,
        Set<Item> drops,
        Map<String, Sprite> sprites
    ) {
        super(name, description, probability, lifeRatio, damage, climeSpawn, drops, sprites);
    }

    public Fish() {
        super(
            "Fish",
            "A common river fish.",
            0.6f,
            0.1f,
            0.0f,
            Clime.LAKERIVER,
            createDrops(),
            createSprites()
        );
    }

    private static Map<String, Sprite> createSprites() {
        Map<String, Sprite> sprites = new HashMap<>();
        try {
            sprites.put("idle", new Sprite(new Texture(Gdx.files.internal("sprites/criaturas/fish.png"))));
        } catch (Exception e) {
            System.err.println("Error loading fish sprite: " + e.getMessage());
        }
        return sprites;
    }

    public static Set<Item> createDrops() {
        Set<Item> drops = new HashSet<>();
        drops.add(new Food("Raw Fish", 0.5f, 100f, 20, "Meat", 2));
        return drops;
    }

    public static boolean canSpawnIn(Ambient ambient) {
        return ambient instanceof LakeRiver;
    }

    @Override
    public void reloadSprites() {
        if (super.getSprites() == null || super.getSprites().isEmpty()) {
            super.setSpritesAfterLoad(Fish.createSprites());
        }
    }

    @Override
    public void attack() {
        System.out.println(getName() + " splashes aimlessly.");
    }
}
