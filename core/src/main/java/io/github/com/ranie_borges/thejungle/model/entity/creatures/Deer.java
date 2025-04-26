package io.github.com.ranie_borges.thejungle.model.entity.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2; // <-- Import adicionado
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Deer extends Creature {

    private Vector2 position = new Vector2(); // <-- Adicionado

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
            0.5f,
            3.0f,
            Clime.FOREST,
            createDrops(),
            createSprites()
        );
    }

    private static Map<String, Sprite> createSprites() {
        Map<String, Sprite> sprites = new HashMap<>();
        sprites.put("idle", new Sprite(new Texture(Gdx.files.internal("sprites/criaturas/DeerIdle.png"))));
        return sprites;
    }

    private static Set<Item> createDrops() {
        Set<Item> drops = new HashSet<>();
        drops.add(new Item("Carne Crua de Veado", 15, 25) {
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
        System.out.println(getName() + " ataca com seus chifres afiados!");
    }
}
