package io.github.com.ranie_borges.thejungle.model.world.ambients;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Drinkable;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Food;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;

import java.util.Random;
import java.util.Set;

import static io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute.DRY_CLIMATE;
import static io.github.com.ranie_borges.thejungle.model.enums.Clime.SNOW;

public class Mountain extends Ambient {

    public Mountain() {
        super(
            "Mountain",
            "Steep rocky peaks with thin air and panoramic views. The harsh terrain offers limited resources but valuable minerals.",
            3.0f,
            Set.of(DRY_CLIMATE),
            new Texture(Gdx.files.internal("scenarios/mountain/mountainFloor.png")),
            new Texture(Gdx.files.internal("scenarios/mountain/mountainWall.png")),
            new Texture(Gdx.files.internal("scenarios/mountain/sidebar.jpg")),
            0.35f,  // wallDensity - more rocky terrain
            0.2f    // itemDensity - scarce resources
        );

        setClimes(Set.of(SNOW));
        setResources(Set.of(
            new Drinkable("Mountain Spring Water", 0.1f, 1.0f),
            new Food("Wild Berries", 0.5f, 1.2f)
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

        // Create mountain terrain with peaks and valleys
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                // Border walls
                if (x == 0 || y == 0 || x == mapWidth - 1 || y == mapHeight - 1) {
                    map[y][x] = 1;
                    continue;
                }

                // Create peaks based on noise
                double noise = (Math.sin(x * 0.4) + Math.cos(y * 0.3)) * 0.5;
                noise += rand.nextDouble() * 0.5;

                map[y][x] = noise > 0.7 ? 1 : 0;
            }
        }

        // Carve a mountain path
        int pathY = mapHeight / 2;
        for (int x = 1; x < mapWidth - 1; x++) {
            map[pathY][x] = 0;
            // Make wider path sections
            map[pathY-1][x] = 0;

            if (rand.nextFloat() < 0.2 && pathY > 3 && pathY < mapHeight - 4) {
                pathY += rand.nextBoolean() ? 1 : -1;
            }
        }

        addDoors(map, mapWidth, mapHeight, rand);
        return map;
    }
}
