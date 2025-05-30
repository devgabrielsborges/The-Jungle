package io.github.com.ranie_borges.thejungle.model.entity.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Food; // Assuming Raw Deer Meat is Food
import io.github.com.ranie_borges.thejungle.model.enums.Clime;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Deer extends Creature {


    protected Deer(
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

    public Deer() {
        super(
            "Veado",
            "Um veado selvagem que habita a floresta",
            0.4f,
            0.5f, // Life Ratio
            3.0f, // Damage
            Clime.FOREST,
            createDrops(),
            createSprites() // Sprites are initialized by constructor
        );
    }

    private static Map<String, Sprite> createSprites() {
        Map<String, Sprite> sprites = new HashMap<>();
        try {
            sprites.put("idle", new Sprite(new Texture(Gdx.files.internal("sprites/criaturas/DeerIdle.png"))));
        } catch (Exception e) {
            System.err.println("Error loading Deer sprite: " + e.getMessage());
        }
        return sprites;
    }

    private static Set<Item> createDrops() {
        Set<Item> drops = new HashSet<>();
        drops.add(new Food("Raw Deer Meat", 1.0f, 100f, 30, "Meat", 3));
        return drops;
    }

    public static boolean canSpawnIn(Ambient ambient) {
        String name = ambient.getName().toLowerCase();
        return name.contains("jungle") || name.contains("mountain") || name.contains("forest"); // Expanded spawn
    }

    @Override
    public void reloadSprites() {
        if (super.getSprites() == null || super.getSprites().isEmpty()) {
            super.setSpritesAfterLoad(Deer.createSprites());
        }
    }

    @Override
    public void attack() {
        System.out.println(getName() + " ataca com seus chifres afiados!");
    }

}
