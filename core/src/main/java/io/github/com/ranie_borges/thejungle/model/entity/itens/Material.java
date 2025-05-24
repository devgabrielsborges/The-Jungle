package io.github.com.ranie_borges.thejungle.model.entity.itens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
// Removed Vector2 import as position is inherited from Item
import com.google.gson.annotations.Expose;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.view.helpers.TextureManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Material extends Item {
    private static final Logger logger = LoggerFactory.getLogger(Material.class);
    @Expose
    private String type;
    @Expose
    private float resistance;

    public Material(String name, float weight, float durability, String type, float resistance) {
        super(name, weight, durability);
        this.type = type;
        this.resistance = Math.max(0, resistance);
    }

    @Override
    public void initializeTransientGraphics() {
        // This method is for items loaded from save.
        // For newly spawned materials, ProceduralMapScreen directly calls initializeSprites(TextureManager).
        // If a global TextureManager is available (e.g., via a singleton or service locator), use it here.
        // Otherwise, this specific item might not have its sprites reloaded correctly from save
        // unless the loading mechanism provides a TextureManager.
        logger.warn("Material.initializeTransientGraphics() called for '{}'. If loaded from save, " +
            "it needs a TextureManager instance to properly reload sprites. " +
            "Newly spawned items use initializeSprites(TextureManager).", getName());
        if (getSprites() == null || getSprites().isEmpty()) {
            // Attempt to create a dummy sprite or leave it empty if no TM available here.
            // This highlights a design point: saved items need a way to re-link to runtime assets.
        }
    }


    public void initializeSprites(TextureManager textureManager) {
        if (textureManager == null) {
            logger.error("TextureManager is null for Material '{}', cannot initialize sprites.", getName());
            if (getSprites() == null) setSprites(new HashMap<>()); // Ensure sprites map is not null
            return;
        }
        Map<String, Sprite> newSprites = new HashMap<>();
        String texturePath = null;
        String materialNameLower = getName().toLowerCase();

        logger.debug("Initializing sprites for Material: '{}', Type: '{}'", getName(), this.type);

        switch (materialNameLower) {
            case "rock": // Covers "rock" and "small rock" if name is normalized
                texturePath = "sprites/itens/rock.png";
                break;
            case "wood log":
                texturePath = "sprites/itens/wood_log.png"; // Ensure this asset exists
                break;
            case "stick":
                texturePath = "sprites/itens/graveto.png";
                break;
            case "tree": // This is for the large tree object on the map
                texturePath = "Gameplay/tree.png";
                break;
            case "medicinal":
                if ("Plant".equalsIgnoreCase(this.type)) {
                    texturePath = "scenarios/jungle/medicinal.png";
                }
                break;
            case "berry": // This is for the berry bush material on the map
                if ("Berry".equalsIgnoreCase(this.type)) {
                    texturePath = "scenarios/jungle/berry.png";
                }
                break;
            default:
                logger.warn("No specific sprite path defined for material name: '{}', type: '{}'. Will attempt fallback icon.", getName(), this.type);
                break;
        }

        if (texturePath != null) {
            Texture texture = textureManager.getOrLoadTexture(texturePath);
            if (texture != null) {
                newSprites.put("idle", new Sprite(texture));
                logger.info("Successfully initialized sprite for '{}' using path '{}'", getName(), texturePath);
            } else {
                logger.error("TextureManager returned null for path '{}' for material '{}'. Sprite not created.", texturePath, getName());
            }
        }

        if (newSprites.isEmpty()) {
            logger.warn("No specific texture loaded for '{}'. Attempting to use generic icon texture as fallback.", getName());
            Texture iconTex = getIconTexture(); // This method tries "icons/" + name + ".png" then "icons/default.png"
            if (iconTex != null) {
                newSprites.put("idle", new Sprite(iconTex));
                logger.info("Using icon texture as fallback sprite for material: {}", getName());
            } else {
                logger.error("CRITICAL: No visual (specific or icon) could be loaded for material: {}", getName());
            }
        }
        setSprites(newSprites);
        logger.debug("Finished initializeSprites for Material: {}. Sprite count: {}", getName(), getSprites() != null ? getSprites().size() : "null map");
    }

    @Override
    public void useItem() { System.out.println("Você está usando o material: " + getName() + " do tipo " + type + "."); }
    @Override
    public void dropItem() { System.out.println("Você deixou cair o material: " + getName() + "."); }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public float getResistance() { return resistance; }
    public void setResistance(float resistance) { this.resistance = Math.max(0, resistance); }

    public static Material createSmallRock() { return new Material("rock", 0.2f, 1.0f, "Rock", 0.5f); }
    public static Material createWoodLog() { return new Material("Wood Log", 1.2f, 1.0f, "Wood", 0.6f); }
    public static Material createStick() { return new Material("stick", 0.1f, 1.0f, "Wood", 0.3f); }
    public static Material createTree() { return new Material("Tree", 5.0f, 10.0f, "Wood", 2.0f); }
    public static Material createMedicinalPlant() { return new Material("Medicinal", 0.2f, 1.0f, "Plant", 0.9f); }
    public static Material createBerryBush() { return new Material("Berry", 0.2f, 1.0f, "Berry", 0.2f); }


    public static List<Material> spawnSmallRocks(int q, int[][] m, int mW, int mH, int tC, int tS) { return spawnGeneric(q,m,mW,mH,tC,tS, Material::createSmallRock); }
    public static List<Material> spawnSticksAndRocks(int q, int[][] m, int mW, int mH, int tG, int tS) {
        List<Material> mats = new ArrayList<>();
        Random rand = new Random();
        for(int i=0; i<q/2; i++) mats.addAll(spawnGeneric(1,m,mW,mH,tG,tS, Material::createStick));
        for(int i=0; i<q/2 + q%2; i++) mats.addAll(spawnGeneric(1,m,mW,mH,tG,tS, Material::createSmallRock));
        return mats;
    }
    public static List<Material> spawnTrees(int q, int[][] m, int mW, int mH, int tG, int tS) { return spawnGeneric(q,m,mW,mH,tG,tS, Material::createTree); }
    public static List<Material> spawnMedicinalPlants(int q, int[][] m, int mW, int mH, int tG, int tS) { return spawnGeneric(q,m,mW,mH,tG,tS, Material::createMedicinalPlant); }
    public static List<Material> spawnBerryBushes(int q, int[][] m, int mW, int mH, int tG, int tS) { return spawnGeneric(q,m,mW,mH,tG,tS, Material::createBerryBush); }

    private static List<Material> spawnGeneric(int quantidade, int[][] mapa, int mapWidth, int mapHeight,
                                               int validTile, int tileSize, java.util.function.Supplier<Material> constructor) {
        List<Material> materiais = new ArrayList<>();
        Random rand = new Random();
        int gerados = 0;
        int tentativas = 0;
        while (gerados < quantidade && tentativas < (quantidade * 100)) { // More reasonable attempt limit
            int x = rand.nextInt(mapWidth);
            int y = rand.nextInt(mapHeight);
            if (y >= 0 && y < mapHeight && x >= 0 && x < mapWidth && mapa[y][x] == validTile) {
                Material material = constructor.get();
                material.setPosition(x * tileSize, y * tileSize);
                materiais.add(material);
                gerados++;
            }
            tentativas++;
        }
        if (gerados < quantidade) logger.warn("Could only spawn {}/{} of {} due to attempts limit or no valid tiles.", gerados, quantidade, "generic material");
        return materiais;
    }
}
