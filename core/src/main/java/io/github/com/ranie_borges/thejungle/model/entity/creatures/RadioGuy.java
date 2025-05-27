package io.github.com.ranie_borges.thejungle.model.entity.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import io.github.com.ranie_borges.thejungle.core.Main;
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Food;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.LakeRiver;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Mountain;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Ruins;
import io.github.com.ranie_borges.thejungle.view.GameWinScreen;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RadioGuy extends Creature {
    private boolean hasBeenInteractedWith = false;

    protected RadioGuy(
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

    public RadioGuy() {
        super(
            "Luc",
            "Crazy man",
            0.6f,
            0.1f,
            0.0f,
            Clime.SNOW,
            createDrops(),
            createSprites()
        );
    }

    private static Map<String, Sprite> createSprites() {
        Map<String, Sprite> sprites = new HashMap<>();
        try {
            sprites.put("idle", new Sprite(new Texture(Gdx.files.internal("sprites/win/radio.png"))));
        } catch (Exception e) {
            System.err.println("Error loading radioguy sprite: " + e.getMessage());
        }
        return sprites;
    }

    public static Set<Item> createDrops() {
        Set<Item> drops = new HashSet<>();
        return drops;
    }
    public void interact(io.github.com.ranie_borges.thejungle.model.entity.Character player, Main game) {
        int coinsNeeded = 10;
        int totalCoins = 0;
        for (Item item : player.getInventory()) {
            if (item.getName().equalsIgnoreCase("Coin")) {
                totalCoins += item.getQuantity();
            }
        }
        if (totalCoins >= coinsNeeded) {
            int toRemove = coinsNeeded;
            for (Item item : player.getInventory()) {
                if (item.getName().equalsIgnoreCase("Coin")) {
                    int qty = item.getQuantity();
                    if (qty >= toRemove) {
                        item.setQuantity(qty - toRemove);
                        break;
                    } else {
                        toRemove -= qty;
                        item.setQuantity(0);
                    }
                }
            }
            game.setScreen(new GameWinScreen(game, ""));
        } else {
            System.out.println("Moedas insuficientes para usar a rádio!");
        }
    }
    public static boolean canSpawnIn(Ambient ambient) {
        return ambient instanceof Mountain;
    }

    @Override
    public void reloadSprites() {
        if (super.getSprites() == null || super.getSprites().isEmpty()) {
            super.setSpritesAfterLoad(RadioGuy.createSprites());
        }
    }
    public String getDialogue() {
        String dialogue;
        if (!hasBeenInteractedWith) {
            dialogue = "Hello, traveler! If you want use my radio, you need pay me 10 coins.";
            hasBeenInteractedWith = true;
        } else {
            dialogue = "Hello again! Where are my coins?";
        }
        return dialogue;
    }
    @Override
    public void attack() {

    }
    @Override
    public String getName() {
        return "Jack";
    }
}
