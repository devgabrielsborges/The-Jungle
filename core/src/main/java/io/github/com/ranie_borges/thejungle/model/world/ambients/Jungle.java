package io.github.com.ranie_borges.thejungle.model.world.ambients;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Drinkable;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Food;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Material;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;

import java.util.Random;
import java.util.Set;

import static io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute.ABUNDANT_FAUNA;
import static io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute.DENSE_VEGETATION;
import static io.github.com.ranie_borges.thejungle.model.enums.Clime.FOREST;

public class Jungle extends Ambient {
    private boolean[][] tallGrass;
    private transient final Texture tallGrassTexture; // Made transient if not already
    private final transient Random random = new Random(); // Added transient

    public Jungle() {
        super(
            "Jungle",
            "A dense forest with tall trees, lush vegetation, and sounds of wild animals.",
            1f,
            Set.of(DENSE_VEGETATION, ABUNDANT_FAUNA),
            new Texture(Gdx.files.internal("scenarios/jungle/jungleFloor.jpg")),
            new Texture(Gdx.files.internal("scenarios/jungle/jungleWall.png")),
            new Texture(Gdx.files.internal("scenarios/jungle/sidebar.jpg")),
            0.2f,
            0.3f
        );
        this.tallGrassTexture = new Texture(Gdx.files.internal("scenarios/jungle/tall_grass.png"));
        setClimes(Set.of(FOREST));
        setResources(Set.of(
            new Drinkable("Stream Water", 0.1f, 0.8f, true, 5f),
            new Food("Wild Berries", 0.5f, 1.2f, 15, "Fruit", 3),
            new Material("Pebble", 0.4f, 1.0f, "Stone", 0.7f),
            Material.createMedicinalPlant()
        ));
    }

    public boolean isTallGrass(int x, int y) {
        if (tallGrass == null) return false;
        if (x < 0 || y < 0 || y >= tallGrass.length || x >= tallGrass[0].length) return false;
        return tallGrass[y][x];
    }

    public void checkSnakeBite(io.github.com.ranie_borges.thejungle.model.entity.Character character) {
        Vector2 pos = character.getPosition();
        int tileX = (int)(pos.x / 32f); // Assuming TILE_SIZE is 32
        int tileY = (int)(pos.y / 32f);

        if (isTallGrass(tileX, tileY)) {
            // FIXME
            // Snake bite logic, potentially using this.random
            // float chance = 0.1f; // Example: 10% chance
            // if (this.random.nextFloat() < chance) {
            // SnakeEventManager.triggerSnakeBite();
            // }
        }
    }

    public Texture getTallGrassTexture() {
        return tallGrassTexture;
    }

    @Override
    public int[][] generateMap(int mapWidth, int mapHeight) {
        int[][] map = new int[mapHeight][mapWidth];
        tallGrass = new boolean[mapHeight][mapWidth]; // Initialize tallGrass array

        // Random rand = new Random(); // Use the class field random
        float wallDensity = getWallDensity();

        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                boolean isBorder = x == 0 || y == 0 || x == mapWidth - 1 || y == mapHeight - 1;
                boolean isDenseArea = (x / 3 + y / 3) % 2 == 0; // Example logic for varied density
                float localDensity = isDenseArea ? wallDensity * 1.5f : wallDensity * 0.7f;

                boolean isWall = random.nextFloat() < localDensity;
                map[y][x] = isBorder ? 1 : (isWall ? 1 : 0); // 1 for TILE_WALL, 0 for TILE_GRASS

                // Tall grass can grow on non-wall, non-border tiles in dense areas
                tallGrass[y][x] = !isWall && isDenseArea && !isBorder;
            }
        }

        // Carve a path
        int pathY = mapHeight / 2;
        for (int x = 1; x < mapWidth - 1; x++) { // Iterate within borders
            if (pathY > 0 && pathY < mapHeight -1) { // Ensure pathY is within map bounds for map array access
                map[pathY][x] = 0; // Clear path to grass
                tallGrass[pathY][x] = false; // No tall grass on the main path
            }

            // Meander path slightly
            if (random.nextFloat() < 0.3 && pathY > 2 && pathY < mapHeight - 3) {
                pathY += random.nextBoolean() ? 1 : -1;
                if (pathY > 0 && pathY < mapHeight -1) { // Check new pathY bounds
                    map[pathY][x] = 0; // Clear new path segment
                    tallGrass[pathY][x] = false;
                }
            }
        }
        addDoors(map, mapWidth, mapHeight, random);
        return map;
    }

    @Override
    public void explore() { super.explore(); }
    @Override
    public void generateEvent() { super.generateEvent(); }
    @Override
    public void modifiesClime() { super.modifiesClime(); }
    @Override
    public void disableEvent() { /* Implementation */ }
}
