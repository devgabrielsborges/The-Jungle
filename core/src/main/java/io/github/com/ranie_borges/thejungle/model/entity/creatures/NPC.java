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
import io.github.com.ranie_borges.thejungle.model.world.ambients.Ruins;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NPC extends Creature {
    private boolean hasBeenInteractedWith = false;

    protected NPC(
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

    public NPC() {
        super(
            "NPC",
            "A non-playable character.",
            0.6f,
            0.1f,
            0.0f,
            Clime.DESERT,
            createDrops(),
            createSprites()
        );
    }

    private static Map<String, Sprite> createSprites() {
        Map<String, Sprite> sprites = new HashMap<>();
        try {
            sprites.put("idle", new Sprite(new Texture(Gdx.files.internal("sprites/npcs/npc3.png"))));
        } catch (Exception e) {
            System.err.println("Error loading fish sprite: " + e.getMessage());
        }
        return sprites;
    }

    public static Set<Item> createDrops() {
        Set<Item> drops = new HashSet<>();
        return drops;
    }

    public static boolean canSpawnIn(Ambient ambient) {
        return ambient instanceof Ruins;
    }

    @Override
    public void reloadSprites() {
        if (super.getSprites() == null || super.getSprites().isEmpty()) {
            super.setSpritesAfterLoad(NPC.createSprites());
        }
    }
    public String getDialogue() {
        String dialogue;
        if (!hasBeenInteractedWith) {
            dialogue = "hum survivor!!! I feel so alone...";
            hasBeenInteractedWith = true;
        } else {
            dialogue = "Hello again!";
        }
        return dialogue;
    }
    @Override
    public void attack() {

    }
    public static void talk() {
        System.out.println("NPC: Hello, traveler! How can I assist you today?");
    }
    @Override
    public String getName() {
        return "Jack";
    }
}
