package io.github.com.ranie_borges.thejungle.model.entity.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Cave; // Import Cave

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Cannibal extends Creature {


    protected Cannibal(
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

    public Cannibal() {
        super(
            "Canibal",
            "Um selvagem canibal que habita a escuridao.",
            0.2f, // Probability
            0.7f, // Life Ratio
            15.0f, // Damage
            Clime.CAVE, // Spawn Clime
            createDrops(),
            createSprites()
        );
    }

    private static Map<String, Sprite> createSprites() {
        Map<String, Sprite> sprites = new HashMap<>();
        try {
            sprites.put("idle", new Sprite(new Texture(Gdx.files.internal("sprites/criaturas/canibal.png"))));
        } catch (Exception e) {
            System.err.println("Error loading Cannibal sprite: " + e.getMessage());
        }
        return sprites;
    }

    private static Set<Item> createDrops() {
        Set<Item> drops = new HashSet<>();
        return drops; // No specific drops defined in provided snippet, keeping it empty
    }

    public static boolean canSpawnIn(Ambient ambient) {
        // Cannibals primarily spawn in Caves.
        return ambient instanceof Cave;
    }

    @Override
    public void reloadSprites() {
        if (super.getSprites() == null || super.getSprites().isEmpty()) {
            super.setSpritesAfterLoad(Cannibal.createSprites());
        }
    }

    @Override
    public void attack() {
        System.out.println(getName() + " ataca com sua lanca afiada!");
    }

}
