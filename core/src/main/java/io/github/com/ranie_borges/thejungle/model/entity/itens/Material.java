package io.github.com.ranie_borges.thejungle.model.entity.itens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.com.ranie_borges.thejungle.model.entity.Item;

import java.util.*;


/**
 * Represents a basic material used for crafting.
 * Materials have a type, resistance, sprite, and a position in the world.
 */
public class Material extends Item {
    private String type;
    private float resistance;
    private final Vector2 position;
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

    public static Material createSmallRock() {
        Material smallRock = new Material("rock", 0.2f, 1.0f, "Rock", 0.5f); // ← nome padronizado
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
        Material stick = new Material("stick", 0.1f, 1.0f, "Wood", 0.3f); // ← nome padronizado
        Map<String, Sprite> sprites = new HashMap<>();
        sprites.put("idle", new Sprite(new Texture("sprites/itens/graveto.png")));
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
    public static List<Material> spawnTrees(int quantidade, int[][] mapa, int mapWidth, int mapHeight, int tileGrass, int tileSize) {
        List<Material> materiais = new ArrayList<>();
        int materiaisGerados = 0;
        int tentativas = 0;

        while (materiaisGerados < quantidade && tentativas < 1000) {
            int x = (int) (Math.random() * mapWidth);
            int y = (int) (Math.random() * mapHeight);

            if (mapa[y][x] == tileGrass) {
                Material tree = new Material("Tree", 5.0f, 10.0f, "Wood", 2.0f);
                Map<String, Sprite> sprites = new HashMap<>();
                Sprite treeSprite = new Sprite(new Texture("Gameplay/tree.png"));
                treeSprite.setSize(128, 128); // Define o tamanho apenas para a árvore
                sprites.put("idle", treeSprite);
                tree.setSprites(sprites);
                tree.setPosition(x * tileSize, y * tileSize);
                materiais.add(tree);
                materiaisGerados++;
            }
            tentativas++;
        }
        return materiais;
    }
    public static List<Material> spawnMedicinalPlants(int quantidade, int[][] mapa, int mapWidth, int mapHeight, int tileGrass, int tileSize) {
        List<Material> materiais = new ArrayList<>();
        int gerados = 0;
        int tentativas = 0;

        while (gerados < quantidade && tentativas < 1000) {
            int x = (int) (Math.random() * mapWidth);
            int y = (int) (Math.random() * mapHeight);

            if (mapa[y][x] == tileGrass) {
                Material planta = createMedicinalPlant();
                planta.setPosition(x * tileSize, y * tileSize);
                materiais.add(planta);
                gerados++;
            }

            tentativas++;
        }

        return materiais;
    }
    private static Map<String, Sprite> loadSprites(String path) {
        Map<String, Sprite> sprites = new HashMap<>();
        Texture texture = new Texture(Gdx.files.internal(path));
        Sprite sprite = new Sprite(texture);
        sprites.put("idle", sprite);
        return sprites;
    }

    public static Material createKnife() {
        Material knife = new Material("Knife", 1.2f, 100, "utility", 5f);
        knife.setSprites(loadSprites("icons/knife.png"));
        return knife;
    }

    public static Material createAxe() {
        Material axe = new Material("Axe", 2.5f, 100, "utility", 5f);
        axe.setSprites(loadSprites("icons/axe.png"));
        return axe;
    }

    public static Material createSpear() {
        Material spear = new Material("Spear", 2.0f, 100, "weapon", 5f);
        spear.setSprites(loadSprites("icons/spear.png"));
        return spear;
    }
    public static Material createMedicinalPlant() {
        Material plant = new Material("Medicinal", 0.2f, 1.0f, "Plant", 0.9f);
        Map<String, Sprite> sprites = new HashMap<>();
        Texture texture = new Texture(Gdx.files.internal("scenarios/jungle/medicinal.png"));
        sprites.put("idle", new Sprite(texture));
        plant.setSprites(sprites);
        return plant;
    }
    public static List<Material> spawnBerryBushes(int quantidade, int[][] mapa, int mapWidth, int mapHeight, int tileGrass, int tileSize) {
        List<Material> materiais = new ArrayList<>();
        int gerados = 0;
        int tentativas = 0;

        while (gerados < quantidade && tentativas < 1000) {
            int x = (int) (Math.random() * mapWidth);
            int y = (int) (Math.random() * mapHeight);

            if (mapa[y][x] == tileGrass) {
                Material bush = createBerryBush();
                bush.setPosition(x * tileSize, y * tileSize);
                materiais.add(bush);
                gerados++;
            }

            tentativas++;
        }

        return materiais;
    }
    public static Material createBerryBush() {
        Material berryBush = new Material("Berry", 0.2f, 1.0f, "Berry", 0.2f);
        Map<String, Sprite> sprites = new HashMap<>();
        Texture texture = new Texture(Gdx.files.internal("scenarios/jungle/berry.png"));
        sprites.put("idle", new Sprite(texture));
        berryBush.setSprites(sprites);
        return berryBush;
    }



}

