package io.github.com.ranie_borges.thejungle.model.world.ambients;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Drinkable;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Food;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Material;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.view.interfaces.UI;

import java.util.Random;
import java.util.Set;

import static io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute.HUMID_CLIMATE;

public class LakeRiver extends Ambient implements UI {

    private transient final Texture waterTexture;
    private final transient Random rand = new Random(); // Added transient

    public LakeRiver() {
        super(
            "Lake River",
            "A tranquil body of water with clear surface, surrounded by vibrant vegetation and teeming with aquatic life.",
            1.5f,
            Set.of(HUMID_CLIMATE),
            new Texture(Gdx.files.internal("scenarios/lakeriver/lakeriverFloor.png")),
            new Texture(Gdx.files.internal("scenarios/lakeriver/lakeriverWall.png")),
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

        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                if (x == 0 || y == 0 || x == mapWidth - 1 || y == mapHeight - 1) {
                    map[y][x] = TILE_WALL;
                } else {
                    map[y][x] = TILE_GRASS;
                }
            }
        }

        boolean isLake = rand.nextBoolean();

        if (isLake) {
            int lakeCenterX = mapWidth / 2 + rand.nextInt(mapWidth / 4) - (mapWidth / 8);
            int lakeCenterY = mapHeight / 2 + rand.nextInt(mapHeight / 4) - (mapHeight / 8);
            int lakeRadius = Math.min(mapWidth, mapHeight) / 4 + rand.nextInt(Math.min(mapWidth, mapHeight) / 8);

            for (int y = 1; y < mapHeight - 1; y++) {
                for (int x = 1; x < mapWidth - 1; x++) {
                    int dx = x - lakeCenterX;
                    int dy = y - lakeCenterY;
                    if (dx * dx + dy * dy < lakeRadius * lakeRadius) {
                        map[y][x] = TILE_WATER;
                    }
                }
            }
        } else {
            int riverWidth = 2 + rand.nextInt(3);
                int riverCurrentY = mapHeight / 2 + rand.nextInt(mapHeight / 3) - (mapHeight / 6);
                for (int x = 1; x < mapWidth - 1; x++) {
                    for (int wOffset = -riverWidth / 2; wOffset <= riverWidth / 2; wOffset++) {
                        int y = riverCurrentY + wOffset;
                        if (y > 0 && y < mapHeight - 1) {
                            map[y][x] = TILE_WATER;
                        }
                    }
                    if (x % (5 + rand.nextInt(5)) == 0 && rand.nextFloat() < 0.6) {
                        int yChange = rand.nextBoolean() ? 1 : -1;
                        if (riverCurrentY + yChange > riverWidth && riverCurrentY + yChange < mapHeight - 1 - riverWidth) {
                            riverCurrentY += yChange;
                        }
                    }
                }
        }
        for(int i=0; i< (mapWidth*mapHeight)/10; i++){
            int gx = 1 + rand.nextInt(mapWidth-2);
            int gy = 1 + rand.nextInt(mapHeight-2);
            if(map[gy][gx] == TILE_WATER) map[gy][gx] = TILE_GRASS;
        }
        addDoors(map, mapWidth, mapHeight, rand);
        return map;
    }

    @Override
    public void explore() { super.explore(); }
    @Override
    public void generateEvent() { super.generateEvent(); }
    @Override
    public void modifiesClime() { super.modifiesClime(); }
    @Override
    public void disableEvent() { /* Implementation if needed */ }
}
