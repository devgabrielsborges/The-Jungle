package io.github.com.ranie_borges.thejungle.model.world.ambients;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Drinkable;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;

import java.util.Random;
import java.util.Set;

import static io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute.DRY_CLIMATE;
import static io.github.com.ranie_borges.thejungle.model.enums.Clime.DESERT;

public class Ruins extends Ambient {

    public Ruins() {
        super(
            "Ruins",
            "Ancient stone structures overtaken by time, with crumbling walls and hidden passages. Artifacts of a forgotten civilization may be found here.",
            2.5f,
            Set.of(DRY_CLIMATE),
            new Texture(Gdx.files.internal("scenarios/ruins/ruinsFloor.png")),
            new Texture(Gdx.files.internal("scenarios/ruins/ruinsWall.png")),
            new Texture(Gdx.files.internal("scenarios/ruins/sidebar.jpg")),
            0.4f,   // wallDensity - more structural remains
            0.3f    // itemDensity - moderate artifact presence
        );

        setClimes(Set.of(DESERT));
        setResources(Set.of(
            new Drinkable("Stagnant Water", 0.2f, 0.3f)
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

        // Start with empty space
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                map[y][x] = (x == 0 || y == 0 || x == mapWidth - 1 || y == mapHeight - 1) ? 1 : 0;
            }
        }

        // Generate ruins with room structures
        int numRooms = 3 + rand.nextInt(3);

        for (int i = 0; i < numRooms; i++) {
            int roomWidth = 5 + rand.nextInt(4);
            int roomHeight = 5 + rand.nextInt(4);
            int roomX = 1 + rand.nextInt(mapWidth - roomWidth - 2);
            int roomY = 1 + rand.nextInt(mapHeight - roomHeight - 2);

            // Draw room walls
            for (int y = roomY; y < roomY + roomHeight; y++) {
                for (int x = roomX; x < roomX + roomWidth; x++) {
                    // Only walls around perimeter
                    if (y == roomY || y == roomY + roomHeight - 1 ||
                        x == roomX || x == roomX + roomWidth - 1) {
                        map[y][x] = 1;

                        // Add some broken walls to represent ruins
                        if (rand.nextFloat() < 0.3 && x != 0 && y != 0 &&
                            x != mapWidth-1 && y != mapHeight-1) {
                            map[y][x] = 0;
                        }
                    }
                }
            }

            // Add doorway
            int doorPos;
            switch (rand.nextInt(4)) {
                case 0: // North
                    doorPos = roomX + rand.nextInt(roomWidth);
                    map[roomY][doorPos] = 0;
                    break;
                case 1: // South
                    doorPos = roomX + rand.nextInt(roomWidth);
                    map[roomY + roomHeight - 1][doorPos] = 0;
                    break;
                case 2: // West
                    doorPos = roomY + rand.nextInt(roomHeight);
                    map[doorPos][roomX] = 0;
                    break;
                case 3: // East
                    doorPos = roomY + rand.nextInt(roomHeight);
                    map[doorPos][roomX + roomWidth - 1] = 0;
                    break;
            }
        }

        addDoors(map, mapWidth, mapHeight, rand);
        return map;
    }
}
