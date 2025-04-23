package io.github.com.ranie_borges.thejungle.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.com.ranie_borges.thejungle.model.world.ambients.enums.ScenarioType;

import java.util.Random;

public class ProceduralMapScreen implements Screen {

    private final int TILE_SIZE = 32;
    private final int MAP_WIDTH = 30;
    private final int MAP_HEIGHT = 20;

    private int[][] map = new int[MAP_HEIGHT][MAP_WIDTH];

    private Texture floorTexture;
    private Texture wallTexture;
    private Texture playerTexture;
    private SpriteBatch batch;
    private Vector2 playerPos;

    private ScenarioType currentScenario = ScenarioType.JUNGLE;   //FIXME load the scenario based on the save

    @Override
    public void show() {
        floorTexture = new Texture("GameScreen/chao.png");
        wallTexture = new Texture("GameScreen/parede.png");
        playerTexture = new Texture("sprites/character/personagem_luta.png");

        batch = new SpriteBatch();

        generateMap();
        setPlayerPosition();
    }

    private void generateMap() {
        Random rand = new Random();
        map = new int[MAP_HEIGHT][MAP_WIDTH];

        float wallDensity = currentScenario.wallDensity;   // use current scenario density for map generation

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                if (x == 0 || y == 0 || x == MAP_WIDTH - 1 || y == MAP_HEIGHT - 1) {
                    map[y][x] = 1; // wall
                } else {
                    map[y][x] = rand.nextFloat() < wallDensity ? 1 : 0;
                }
            }
        }

        for (int x = 1; x < MAP_WIDTH - 1; x++) {
            map[MAP_HEIGHT / 2][x] = 0;
        }

        int numDoors = 2 + (rand.nextFloat() < currentScenario.itemDensity ? 1 : 0);

        java.util.List<int[]> borderPositions = new java.util.ArrayList<>();   // possible corners

        // top border (without corners)
        for (int x = 2; x < MAP_WIDTH - 2; x++) {
            borderPositions.add(new int[]{0, x});
        }

        // bottom border (without corners)
        for (int x = 2; x < MAP_WIDTH - 2; x++) {
            borderPositions.add(new int[]{MAP_HEIGHT - 1, x});
        }

        // left border (without corners)
        for (int y = 2; y < MAP_HEIGHT - 2; y++) {
            borderPositions.add(new int[]{y, 0});
        }

        // right border (without corners)
        for (int y = 2; y < MAP_HEIGHT - 2; y++) {
            borderPositions.add(new int[]{y, MAP_WIDTH - 1});
        }

        // shuffle and select positions
        java.util.Collections.shuffle(borderPositions);

        for (int i = 0; i < Math.min(numDoors, borderPositions.size()); i++) {
            int[] pos = borderPositions.get(i);
            int y = pos[0];
            int x = pos[1];
            map[y][x] = 2;   // door

            // create a free path near the door
            if (x == 0) map[y][1] = 0;   // left border door
            else if (x == MAP_WIDTH - 1) map[y][MAP_WIDTH - 2] = 0; // Right border door
            else if (y == 0) map[1][x] = 0;   // top border door
            else if (y == MAP_HEIGHT - 1) map[MAP_HEIGHT - 2][x] = 0; // Bottom border door
        }
    }

    private void setPlayerPosition() {
        // First try to place player in a safe, connected area
        for (int y = 1; y < MAP_HEIGHT - 1; y++) {
            for (int x = 1; x < MAP_WIDTH - 1; x++) {
                if (map[y][x] == 0 && hasAdjacentFloor(y, x)) {
                    playerPos = new Vector2(x * TILE_SIZE, y * TILE_SIZE);
                    return;
                }
            }
        }

        // If no ideal spot, just find any floor
        for (int y = 1; y < MAP_HEIGHT - 1; y++) {
            for (int x = 1; x < MAP_WIDTH - 1; x++) {
                if (map[y][x] == 0) {
                    // Ensure at least one adjacent tile is clear
                    map[y-1][x] = 0;
                    playerPos = new Vector2(x * TILE_SIZE, y * TILE_SIZE);
                    return;
                }
            }
        }

        // Last resort - place in center and clear area
        int centerY = MAP_HEIGHT / 2;
        int centerX = MAP_WIDTH / 2;
        map[centerY][centerX] = 0;
        map[centerY+1][centerX] = 0;
        map[centerY-1][centerX] = 0;
        map[centerY][centerX+1] = 0;
        map[centerY][centerX-1] = 0;
        playerPos = new Vector2(centerX * TILE_SIZE, centerY * TILE_SIZE);
    }

    private boolean hasAdjacentFloor(int y, int x) {
        // Check if there's at least one adjacent floor tile
        if (y > 0 && map[y-1][x] == 0) return true;
        if (y < MAP_HEIGHT-1 && map[y+1][x] == 0) return true;
        if (x > 0 && map[y][x-1] == 0) return true;
        return x < MAP_WIDTH - 1 && map[y][x + 1] == 0;
    }

    private void movePlayer(float delta) {
        float speed = 100f;
        float nextX = playerPos.x;
        float nextY = playerPos.y;

        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W)) nextY += speed * delta;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S)) nextY -= speed * delta;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A)) nextX -= speed * delta;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) nextX += speed * delta;

        int tileX = (int) ((nextX + 8) / TILE_SIZE);
        int tileY = (int) ((nextY + 8) / TILE_SIZE);

        if (tileX >= 0 && tileX < MAP_WIDTH && tileY >= 0 && tileY < MAP_HEIGHT) {
            int tileType = map[tileY][tileX];

            if (tileType != 1) { // if it is not a wall
                playerPos.set(nextX, nextY);
            }

            if (tileType == 2) { // door -> generate new scenario
                generateMap();
                setPlayerPosition();
            }
        }
    }

    @Override
    public void render(float delta) {
        movePlayer(delta);

        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                batch.draw(floorTexture, x * TILE_SIZE, y * TILE_SIZE);

                if (map[y][x] == 1) {
                    batch.draw(wallTexture, x * TILE_SIZE, y * TILE_SIZE);
                }
                // visually distinguish doors
                else if (map[y][x] == 2) {
                    // use a tinted floor texture to represent doors
                    batch.setColor(0.8f, 0.5f, 0.2f, 1f); // Brown tint
                    batch.draw(floorTexture, x * TILE_SIZE, y * TILE_SIZE);
                    batch.setColor(1, 1, 1, 1); // Reset color
                }
            }
        }

        batch.draw(playerTexture, playerPos.x, playerPos.y, 20, 20);

        batch.end();
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        floorTexture.dispose();
        wallTexture.dispose();
        playerTexture.dispose();
        batch.dispose();
    }
}
