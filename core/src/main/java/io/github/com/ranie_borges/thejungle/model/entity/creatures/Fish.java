package io.github.com.ranie_borges.thejungle.model.entity.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2; // <-- Import adicionado
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Fish extends Creature {

    private final Vector2 position = new Vector2();
    private Ambient ambient;


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
    private boolean canSpawnFishInCurrentAmbient() {
        String ambientName = ambient.getName();
        return ambientName.equals("LakeRiver");
    }
    public static boolean canSpawnIn(Ambient ambient) {
        String name = ambient.getName().toLowerCase();
        return name.contains("lake") ;
    }


    public Fish() {
        super(
            "Fish",
            "Just a fish",
            0.4f,
            0.5f,
            3.0f,
            Clime.LAKERIVER,
            createDrops(),
            createSprites()
        );
    }

    private static Map<String, Sprite> createSprites() {
        Map<String, Sprite> sprites = new HashMap<>();
        sprites.put("idle", new Sprite(new Texture(Gdx.files.internal("sprites/criaturas/fish.png"))));
        return sprites;
    }

    private static Set<Item> createDrops() {
        Set<Item> drops = new HashSet<>();
        drops.add(new Item("Raw Fish", 15, 25) {
            @Override
            public void useItem() {
                // comportamento ao usar
            }

            @Override
            public void dropItem() {
                // comportamento ao dropar
            }
        });
        return drops;
    }

    public Vector2 getPosition() { // <-- Adicionado
        return position;
    }

    @Override
    public void attack() {
    }
}
