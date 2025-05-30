package io.github.com.ranie_borges.thejungle.model.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import io.github.com.ranie_borges.thejungle.model.enums.Clime;
import io.github.com.ranie_borges.thejungle.model.entity.interfaces.ICreature;
import com.badlogic.gdx.math.Vector2;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;


import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Creature implements ICreature {
    private String name;
    private String description;
    private float probability;
    private float lifeRatio;
    private float damage;
    private Clime climeSpawn;
    private Set<Item> drops;

    protected transient Map<String, Sprite> sprites; // Changed to protected and kept transient

    private final Vector2 position = new Vector2();

    protected Creature(
        String name,
        String description,
        float probability,
        float lifeRatio,
        float damage,
        Clime climeSpawn,
        Set<Item> drops,
        Map<String, Sprite> sprites
    ) {
        setName(name);
        setDescription(description);
        setProbability(probability);
        setLifeRatio(lifeRatio);
        setDamage(damage);
        setClimeSpawn(climeSpawn);
        setDrops(drops != null ? new HashSet<>(drops) : new HashSet<>());
        // Initialize sprites directly. reloadSprites() will be for post-deserialization.
        this.sprites = sprites != null ? new HashMap<>(sprites) : new HashMap<>();
        if (this.sprites.isEmpty() && getClass() != Creature.class) {
            // This initial call to reloadSprites() might be redundant if subclasses'
            // createSprites() is comprehensive and always called by their constructors.
            // However, it ensures sprites are loaded if the passed map was empty.
            reloadSprites();
        }
    }

    // Method to be called after a Creature object (and its subclasses) is deserialized
    // or if sprites need to be re-initialized for any other reason.
    public abstract void reloadSprites();

    /**
     * Protected method to allow subclasses to set their sprites,
     * typically used within their reloadSprites() implementation.
     * @param reloadedSprites The map of sprites to set.
     */
    protected void setSpritesAfterLoad(Map<String, Sprite> reloadedSprites) {
        this.sprites = reloadedSprites;
    }


    public static <T extends Creature> List<T> regenerateCreatures(
        int count,
        int[][] map,
        int mapWidth,
        int mapHeight,
        int validTileType,
        int tileSize,
        Supplier<T> constructor,
        Ambient ambient,
        Predicate<Ambient> canSpawnPredicate
    ) {
        List<T> creatures = new ArrayList<>();
        int tries = 0;
        Random rand = new Random();

        if (!canSpawnPredicate.test(ambient)) {
            return creatures;
        }

        while (creatures.size() < count && tries < 1000) {
            int x = rand.nextInt(mapWidth);
            int y = rand.nextInt(mapHeight);
            tries++;

            if (y < 0 || y >= mapHeight || x < 0 || x >= mapWidth) continue;

            if (map[y][x] == validTileType) {
                T creature = constructor.get();
                creature.getPosition().set(x * tileSize, y * tileSize);
                creatures.add(creature);
            }
        }
        return creatures;
    }

    public Vector2 getPosition() { return position; }
    public void setPosition(float x, float y) { this.position.set(x,y); }
    public Clime getClimeSpawn() { return climeSpawn; }
    public void setClimeSpawn(Clime climeSpawn) { this.climeSpawn = climeSpawn; }
    public float getLifeRatio() { return lifeRatio; }
    public void setLifeRatio(float lifeRatio) { this.lifeRatio = Math.max(0, Math.min(1, lifeRatio)); }
    public float getDamage() { return damage; }
    public void setDamage(float damage) { this.damage = Math.max(0, damage); }
    public Set<Item> getDrops() { return Collections.unmodifiableSet(drops); }
    public void setDrops(Set<Item> drops) { this.drops = drops != null ? new HashSet<>(drops) : new HashSet<>(); }

    public Map<String, Sprite> getSprites() {
        if (this.sprites == null) {
            reloadSprites(); // Attempt to reload if null
        }
        if (this.sprites == null) { // If still null after attempt, create empty to prevent NPE downstream
            this.sprites = new HashMap<>();
            System.err.println("Error: Sprites for " + getName() + " are still null after reload attempt. Visuals will be missing.");
        }
        return Collections.unmodifiableMap(this.sprites);
    }

    public void setSprites(Map<String, Sprite> sprites) {
        this.sprites = sprites != null ? new HashMap<>(sprites) : new HashMap<>();
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public float getProbability() { return probability; }
    public void setProbability(float probability) { this.probability = probability; }

    public abstract void attack();
}
