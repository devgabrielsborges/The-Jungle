package io.github.com.ranie_borges.thejungle.model.world.ambients;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Drinkable;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;

import java.util.Random;
import java.util.Set;

import static io.github.com.ranie_borges.thejungle.model.enums.AmbientAttribute.HUMID_CLIMATE;
import static io.github.com.ranie_borges.thejungle.model.enums.Clime.CAVE;

public class Cave extends Ambient {

    public Cave() {
        super(
            "Cave",
            "A dark and damp cave, echoing with the sounds of dripping water and distant growls.",
            3.5f,
            Set.of(HUMID_CLIMATE),
            new Texture(Gdx.files.internal("scenarios/cave/floor.png")),
            new Texture(Gdx.files.internal("scenarios/cave/wall.png")),
            new Texture(Gdx.files.internal("scenarios/cave/sidebar.jpg")),
            0.3f,  // wallDensity - more walls for caves
            0.2f   // itemDensity - fewer items in caves
        );

        setClimes(Set.of(CAVE));
        setResources(Set.of(new Drinkable("Cave Water", 0.3f, 0.5f)));
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

        // Start with all walls
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                map[y][x] = 1;
            }
        }

        // Carve out cave chambers using cellular automata
        int centerX = mapWidth / 2;
        int centerY = mapHeight / 2;
        int radius = Math.min(mapWidth, mapHeight) / 3;

        // Start with a central chamber
        for (int y = centerY - radius; y <= centerY + radius; y++) {
            for (int x = centerX - radius; x <= centerX + radius; x++) {
                if (y > 0 && y < mapHeight - 1 && x > 0 && x < mapWidth - 1) {
                    int dx = x - centerX;
                    int dy = y - centerY;
                    if (dx*dx + dy*dy <= radius*radius) {
                        map[y][x] = 0;
                    }
                }
            }
        }

        // Add tunnels branching out from center
        int tunnels = 3 + rand.nextInt(3);
        for (int i = 0; i < tunnels; i++) {
            double angle = rand.nextDouble() * Math.PI * 2;
            int length = radius + rand.nextInt(radius);
            int curX = centerX;
            int curY = centerY;

            for (int j = 0; j < length; j++) {
                curX = (int)(centerX + j * Math.cos(angle));
                curY = (int)(centerY + j * Math.sin(angle));

                if (curY > 0 && curY < mapHeight - 1 && curX > 0 && curX < mapWidth - 1) {
                    map[curY][curX] = 0;
                    map[curY][curX+1] = 0;
                    map[curY+1][curX] = 0;
                }
            }
        }

        addDoors(map, mapWidth, mapHeight, rand);
        return map;
    }
}
