package io.github.com.ranie_borges.thejungle.model.world.ambients;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
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

    public Jungle() {
        super(
            "Jungle",
            "A dense forest with tall trees, lush vegetation, and sounds of wild animals.",
            1f,
            Set.of(DENSE_VEGETATION, ABUNDANT_FAUNA),
            new Texture(Gdx.files.internal("scenarios/jungle/jungleFloor.png")),
            new Texture(Gdx.files.internal("scenarios/jungle/jungleWall.png")),
            new Texture(Gdx.files.internal("scenarios/jungle/sidebar.jpg")),
            0.2f,
            0.3f
        );

        setClimes(Set.of(FOREST));
        setResources(Set.of(
            new Drinkable("Stream Water", 0.1f, 0.8f),
            new Food("Wild Berries", 0.5f, 1.2f),
            new Material("Rope", 1.0f, 0.5f),
            new Material("Stick", 0.2f, 0.5f)
        ));
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

    @Override
    public int[][] generateMap(int mapWidth, int mapHeight) {
        int[][] map = new int[mapHeight][mapWidth];
        Random rand = new Random();
        float wallDensity = getWallDensity();

        // Create a jungle with natural clearings and dense vegetation sections
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                boolean isBorder = x == 0 || y == 0 || x == mapWidth - 1 || y == mapHeight - 1;
                boolean isDenseArea = (x / 3 + y / 3) % 2 == 0;
                float localDensity = isDenseArea ? wallDensity * 1.5f : wallDensity * 0.7f;

                map[y][x] = isBorder ? 1 : (rand.nextFloat() < localDensity ? 1 : 0);
            }
        }

        // Create a winding path
        int pathY = mapHeight / 2;
        for (int x = 1; x < mapWidth - 1; x++) {
            map[pathY][x] = 0;
            if (rand.nextFloat() < 0.3 && pathY > 2 && pathY < mapHeight - 3) {
                pathY += rand.nextBoolean() ? 1 : -1;
                map[pathY][x] = 0;
            }
        }

        addDoors(map, mapWidth, mapHeight, rand);
        return map;
    }


}
