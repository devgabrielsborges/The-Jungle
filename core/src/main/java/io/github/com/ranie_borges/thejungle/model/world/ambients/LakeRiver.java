package io.github.com.ranie_borges.thejungle.model.world.ambients;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Drinkable;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Food;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Material;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import java.util.Random;
import java.util.Set;
import static io.github.com.ranie_borges.thejungle.view.ProceduralMapScreen.TILE_GRASS;
import static io.github.com.ranie_borges.thejungle.view.ProceduralMapScreen.TILE_WATER;
import static io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute.HUMID_CLIMATE;

public class LakeRiver extends Ambient {

    private final Texture waterTexture;

    public LakeRiver() {
        super(
            "Lake River",
            "A tranquil body of water with clear surface, surrounded by vibrant vegetation and teeming with aquatic life.",
            1.5f,
            Set.of(HUMID_CLIMATE),
            new Texture(Gdx.files.internal("scenarios/lakeriver/lakeriverFloor.png")), // floor (grama)
            null,
            new Texture(Gdx.files.internal("scenarios/lakeriver/sidebar.jpg")),
            0.15f,
            0.4f
        );
        this.waterTexture = new Texture(Gdx.files.internal("scenarios/lakeriver/lakeriverWater.png"));
        setResources(Set.of(
            new Drinkable("Fresh Water", 0.1f, 1.0f, true, 8f),
            new Food("Wild Berries", 0.5f, 1.2f, 12, "Fruit", 3),
            new Material("Pebble", 0.4f, 1.0f, "Stone", 0.7f)
        ));
    }

    public Texture getWaterTexture() {
        return waterTexture;
    }

    @Override
    public int[][] generateMap(int mapWidth, int mapHeight) {
        int[][] map = new int[mapHeight][mapWidth];
        Random rand = new Random();
        // Geração do mapa com terreno básico e rio ou lago
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                map[y][x] = (x == 0 || y == 0 || x == mapWidth - 1 || y == mapHeight - 1) ? 1 : 0;
            }
        }
        boolean isLake = rand.nextBoolean();
        if (isLake) {
            int lakeX = mapWidth / 2;
            int lakeY = mapHeight / 2;
            int lakeRadius = Math.min(mapWidth, mapHeight) / 4;
            for (int y = 0; y < mapHeight; y++) {
                for (int x = 0; x < mapWidth; x++) {
                    int dx = x - lakeX;
                    int dy = y - lakeY;
                    if (dx * dx + dy * dy < lakeRadius * lakeRadius) {
                        map[y][x] = TILE_WATER;
                    }
                }
            }
        } else {
            int riverY = mapHeight / 2;
            int width = 2 + rand.nextInt(2);
            for (int x = 0; x < mapWidth; x++) {
                for (int w = -width / 2; w <= width / 2; w++) {
                    int y = riverY + w;
                    if (y > 0 && y < mapHeight - 1) {
                        map[y][x] = TILE_WATER;
                    }
                }
                if (rand.nextFloat() < 0.3 && riverY > 5 && riverY < mapHeight - 6) {
                    riverY += rand.nextBoolean() ? 1 : -1;
                }
            }
        }
        addDoors(map, mapWidth, mapHeight, rand);
        for (int y = 1; y < mapHeight - 1; y++) {
            for (int x = 1; x < mapWidth - 1; x++) {
                if (map[y][x] == TILE_GRASS) {
                    boolean nextToWater = false;
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            if (map[y + dy][x + dx] == TILE_WATER) {
                                nextToWater = true;
                                break;
                            }
                        }
                        if (nextToWater) break;
                    }
                    if (nextToWater) {
                        map[y][x] = TILE_GRASS; // ou outro valor para grama molhada, se necessário
                    }
                }
            }
        }
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
        // Sem eventos especiais
    }
}
