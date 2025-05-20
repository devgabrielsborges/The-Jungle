// Jungle.java
package io.github.com.ranie_borges.thejungle.model.world.ambients;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Drinkable;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Food;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Material;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.events.events.SnakeEventManager;

import java.util.Random;
import java.util.Set;

import static io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute.ABUNDANT_FAUNA;
import static io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute.DENSE_VEGETATION;
import static io.github.com.ranie_borges.thejungle.model.enums.Clime.FOREST;

public class Jungle extends Ambient {
    private boolean[][] tallGrass;
    private final Texture tallGrassTexture = new Texture(Gdx.files.internal("scenarios/jungle/tall_grass.png"));

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
    private final Random random = new Random();

    public void checkSnakeBite(io.github.com.ranie_borges.thejungle.model.entity.Character character) {
        Vector2 pos = character.getPosition();
        int tileX = (int)(pos.x / 32f);
        int tileY = (int)(pos.y / 32f);

        if (isTallGrass(tileX, tileY)) {
            float chance = 0; // probabilidade de picada
            random.nextFloat();
        }
    }

    public Texture getTallGrassTexture() {
        return tallGrassTexture;
    }

    @Override
    public int[][] generateMap(int mapWidth, int mapHeight) {
        int[][] map = new int[mapHeight][mapWidth];
        tallGrass = new boolean[mapHeight][mapWidth];

        Random rand = new Random();
        float wallDensity = getWallDensity();

        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                boolean isBorder = x == 0 || y == 0 || x == mapWidth - 1 || y == mapHeight - 1;
                boolean isDenseArea = (x / 3 + y / 3) % 2 == 0;
                float localDensity = isDenseArea ? wallDensity * 1.5f : wallDensity * 0.7f;

                boolean isWall = rand.nextFloat() < localDensity;
                map[y][x] = isBorder ? 1 : (isWall ? 1 : 0);

                tallGrass[y][x] = !isWall && isDenseArea && !isBorder;
            }
        }

        int pathY = mapHeight / 2;
        for (int x = 1; x < mapWidth - 1; x++) {
            map[pathY][x] = 0;
            tallGrass[pathY][x] = false;

            if (rand.nextFloat() < 0.3 && pathY > 2 && pathY < mapHeight - 3) {
                pathY += rand.nextBoolean() ? 1 : -1;
                map[pathY][x] = 0;
                tallGrass[pathY][x] = false;
            }
        }

        addDoors(map, mapWidth, mapHeight, rand);
        return map;
    }


    @Override
    public void explore() {
        super.explore();
    }

    @Override
    public void generateEvent() {
        super.generateEvent();
    }

    @Override
    public void modifiesClime() {
        super.modifiesClime();
    }

    @Override
    public void disableEvent() {
        // Implementation
    }

}
