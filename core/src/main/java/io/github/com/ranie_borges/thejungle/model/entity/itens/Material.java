package io.github.com.ranie_borges.thejungle.model.entity.itens;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.com.ranie_borges.thejungle.model.entity.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Represents a basic material used for crafting.
 * Materials have a type, resistance, sprite, and a position in the world.
 */
public class Material extends Item {
    private String type;
    private float resistance;
    private Vector2 position;
    private Map<String, Sprite> sprites;

    public Material(String name, float weight, float durability, String type, float resistance) {
        super(name, weight, durability);
        this.type = type;
        this.resistance = Math.max(0, resistance);
        this.position = new Vector2();
        this.sprites = new HashMap<>();
    }

    @Override
    public void useItem() {
        System.out.println("Você está usando o material: " + getName() + " do tipo " + type + ".");
    }

    @Override
    public void dropItem() {
        System.out.println("Você deixou cair o material: " + getName() + ".");
    }

    // --- Posição no mundo ---
    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    // --- Sprites do item ---
    public Map<String, Sprite> getSprites() {
        return Collections.unmodifiableMap(sprites);
    }

    public void setSprites(Map<String, Sprite> sprites) {
        this.sprites = sprites != null ? new HashMap<>(sprites) : new HashMap<>();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getResistance() {
        return resistance;
    }

    public void setResistance(float resistance) {
        this.resistance = Math.max(0, resistance);
    }

    // --- Criadores padrão de Materiais ---

    public static Material createPebble() {
        Material pebble = new Material("Pebble", 0.4f, 1.0f, "Stone", 0.7f);
        Map<String, Sprite> sprites = new HashMap<>();
        sprites.put("idle", new Sprite(new Texture("sprites/itens/rock.png")));
        pebble.setSprites(sprites);
        return pebble;
    }

    public static Material createSmallRock() {
        Material smallRock = new Material("Rock", 0.2f, 1.0f, "Rock", 0.5f);
        Map<String, Sprite> sprites = new HashMap<>();
        sprites.put("idle", new Sprite(new Texture("sprites/itens/rock.png")));
        smallRock.setSprites(sprites);
        return smallRock;
    }

    public static Material createWoodLog() {
        Material woodLog = new Material("Wood Log", 1.2f, 1.0f, "Wood", 0.6f);
        Map<String, Sprite> sprites = new HashMap<>();
        sprites.put("idle", new Sprite(new Texture("sprites/itens/rock.png")));
        woodLog.setSprites(sprites);
        return woodLog;
    }

    public static Material createStick() {
        Material stick = new Material("Stick", 0.1f, 1.0f, "Wood", 0.3f);
        Map<String, Sprite> sprites = new HashMap<>();
        sprites.put("idle", new Sprite(new Texture("sprites/itens/graveto.png"))); // <-- Aqui coloca a imagem do graveto
        stick.setSprites(sprites);
        return stick;
    }

    // --- Método para spawnar materiais aleatoriamente no mapa ---
    public static List<Material> spawnSmallRocks(int quantidade, int[][] mapa, int mapWidth, int mapHeight, int tileCave, int tileSize) {
        List<Material> materiais = new ArrayList<>();
        int materiaisGerados = 0;
        int tentativas = 0;

        while (materiaisGerados < quantidade && tentativas < 1000) {
            int x = (int) (Math.random() * mapWidth);
            int y = (int) (Math.random() * mapHeight);

            if (mapa[y][x] == tileCave) {
                Material material = Material.createSmallRock();
                material.setPosition(x * tileSize, y * tileSize);
                materiais.add(material);
                materiaisGerados++;
            }
            tentativas++;
        }
        return materiais;
    }

    public static List<Material> spawnSticksAndRocks(int quantidade, int[][] mapa, int mapWidth, int mapHeight, int tileGrass, int tileSize) {
        List<Material> materiais = new ArrayList<>();
        int materiaisGerados = 0;
        int tentativas = 0;

        while (materiaisGerados < quantidade && tentativas < 1000) {
            int x = (int) (Math.random() * mapWidth);
            int y = (int) (Math.random() * mapHeight);

            if (mapa[y][x] == tileGrass) {
                Material material = Math.random() < 0.5 ? Material.createStick() : Material.createSmallRock();
                material.setPosition(x * tileSize, y * tileSize);
                materiais.add(material);
                materiaisGerados++;
            }
            tentativas++;
        }
        return materiais;
    }
}

