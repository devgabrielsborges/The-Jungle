package io.github.com.ranie_borges.thejungle.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.com.ranie_borges.thejungle.model.world.ambients.enums.ScenarioType;

import java.util.Random;

// ... (importações iguais às anteriores)
public class ProceduralMapScreen implements Screen {
    private final int TILE_SIZE = 32;
    private final int MAP_WIDTH = 30;
    private final int MAP_HEIGHT = 20;
    private final int SIDEBAR_WIDTH = 300;

    private int[][] map = new int[MAP_HEIGHT][MAP_WIDTH];
    private Texture floorTexture, wallTexture, playerTexture, sidebarTexture, classIcon;
    private Texture inventoryBackground, backpackIcon;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private GlyphLayout layout;

    private float life = 0.8f, hunger = 0.5f, sanity = 0.65f, thirst = 0.7f, energy = 0.6f;
    private float offsetX, offsetY;
    private Vector2 playerPos;
    private final String playerName, playerClass;
    private boolean showInventory = false;
    private ScenarioType currentScenario = ScenarioType.JUNGLE;

    private float blinkTimer = 0f;
    private boolean blinkVisible = true;
    private final float BLINK_INTERVAL = 0.5f;

    public ProceduralMapScreen(String playerName, String playerClass) {
        this.playerName = playerName;
        this.playerClass = playerClass;
    }

    @Override
    public void show() {
        floorTexture = new Texture("GameScreen/chao.png");
        wallTexture = new Texture("GameScreen/parede.png");
        playerTexture = new Texture("sprites/character/personagem_luta.png");
        sidebarTexture = new Texture("Gameplay/sidebar.jpg");
        classIcon = new Texture(getIconPathForClass(playerClass));
        inventoryBackground = new Texture(Gdx.files.internal("Gameplay/backpackInside.png"));
        backpackIcon = new Texture(Gdx.files.internal("Gameplay/backpack.png"));

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(2f);
        font.setUseIntegerPositions(true);
        layout = new GlyphLayout();

        generateMap();
        setPlayerPosition();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private String getIconPathForClass(String rawClass) {
        switch (rawClass) {
            case "Survivor": return "StatsScreen/desempregadoFundo.png";
            case "Hunter": return "StatsScreen/cacadorFundo.png";
            case "Lumberjack": return "StatsScreen/lenhadorFundo.png";
            case "Doctor": return "StatsScreen/medicoFundo.png";
        }
        return rawClass;
    }

    private void generateMap() {
        Random rand = new Random();
        float wallDensity = currentScenario.wallDensity;
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                map[y][x] = (x == 0 || y == 0 || x == MAP_WIDTH - 1 || y == MAP_HEIGHT - 1) ? 1 : (rand.nextFloat() < wallDensity ? 1 : 0);
            }
        }
        for (int x = 1; x < MAP_WIDTH - 1; x++) map[MAP_HEIGHT / 2][x] = 0;

        int numDoors = 2 + (rand.nextFloat() < currentScenario.itemDensity ? 1 : 0);
        java.util.List<int[]> borderPositions = new java.util.ArrayList<>();
        for (int x = 2; x < MAP_WIDTH - 2; x++) {
            borderPositions.add(new int[]{0, x});
            borderPositions.add(new int[]{MAP_HEIGHT - 1, x});
        }
        for (int y = 2; y < MAP_HEIGHT - 2; y++) {
            borderPositions.add(new int[]{y, 0});
            borderPositions.add(new int[]{y, MAP_WIDTH - 1});
        }

        java.util.Collections.shuffle(borderPositions);
        for (int i = 0; i < Math.min(numDoors, borderPositions.size()); i++) {
            int[] pos = borderPositions.get(i);
            int y = pos[0], x = pos[1];
            map[y][x] = 2;
            if (x == 0) map[y][1] = 0;
            else if (x == MAP_WIDTH - 1) map[y][MAP_WIDTH - 2] = 0;
            else if (y == 0) map[1][x] = 0;
            else if (y == MAP_HEIGHT - 1) map[MAP_HEIGHT - 2][x] = 0;
        }
    }

    private void setPlayerPosition() {
        for (int y = 1; y < MAP_HEIGHT - 1; y++) {
            for (int x = 1; x < MAP_WIDTH - 1; x++) {
                if (map[y][x] == 0 && hasAdjacentFloor(y, x)) {
                    playerPos = new Vector2(x * TILE_SIZE, y * TILE_SIZE);
                    return;
                }
            }
        }
        playerPos = new Vector2((MAP_WIDTH / 2) * TILE_SIZE, (MAP_HEIGHT / 2) * TILE_SIZE);
    }

    private boolean hasAdjacentFloor(int y, int x) {
        return (y > 0 && map[y - 1][x] == 0) || (y < MAP_HEIGHT - 1 && map[y + 1][x] == 0)
            || (x > 0 && map[y][x - 1] == 0) || (x < MAP_WIDTH - 1 && map[y][x + 1] == 0);
    }

    private void movePlayer(float delta) {
        float speed = 100f;
        float nextX = playerPos.x, nextY = playerPos.y;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W)) nextY += speed * delta;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S)) nextY -= speed * delta;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A)) nextX -= speed * delta;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) nextX += speed * delta;

        int tileX = (int) ((nextX + 8) / TILE_SIZE), tileY = (int) ((nextY + 8) / TILE_SIZE);
        if (tileX >= 0 && tileX < MAP_WIDTH && tileY >= 0 && tileY < MAP_HEIGHT) {
            int tileType = map[tileY][tileX];
            if (tileType != 1) playerPos.set(nextX, nextY);
            if (tileType == 2) {
                generateMap();
                setPlayerPosition();
            }
        }
    }

    private void renderInventoryWindow() {
        float w = 400, h = 300;
        float x = (Gdx.graphics.getWidth() - w) / 2f, y = (Gdx.graphics.getHeight() - h) / 2f;
        float slotSize = 48, padding = 12;
        int cols = 5, rows = 3;

        batch.begin();
        batch.draw(inventoryBackground, x, y, w, h);
        layout.setText(font, "Inventory");
        font.draw(batch, "Inventory", x + (w - layout.width) / 2f, y + h - 20);
        batch.end();

        float gridW = cols * slotSize + (cols - 1) * padding;
        float gridH = rows * slotSize + (rows - 1) * padding;
        float startX = x + (w - gridW) / 2f;
        float startY = y + (h - gridH) / 2f;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float sx = startX + col * (slotSize + padding);
                float sy = startY + (rows - row - 1) * (slotSize + padding);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(Color.LIGHT_GRAY);
                shapeRenderer.rect(sx, sy, slotSize, slotSize);
                shapeRenderer.end();
            }
        }
    }

    @Override
    public void render(float delta) {
        movePlayer(delta);
        blinkTimer += delta;
        if (blinkTimer >= BLINK_INTERVAL) {
            blinkVisible = !blinkVisible;
            blinkTimer = 0f;
        }

        float size = 96;
        float bx = Gdx.graphics.getWidth() - SIDEBAR_WIDTH + (SIDEBAR_WIDTH - size) / 2f;
        float by = 30;

        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        boolean mouseOverBackpack = mouseX >= bx && mouseX <= bx + size && mouseY >= by && mouseY <= by + size;

        if (Gdx.input.justTouched() && mouseOverBackpack || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.I)) {
            showInventory = !showInventory;
        }

        ScreenUtils.clear(0, 0, 0, 1);
        batch.begin();

        batch.draw(sidebarTexture, 0, 0, SIDEBAR_WIDTH, Gdx.graphics.getHeight());
        batch.draw(sidebarTexture, Gdx.graphics.getWidth() - SIDEBAR_WIDTH, 0, SIDEBAR_WIDTH, Gdx.graphics.getHeight());

        layout.setText(font, "AMBIENTE");
        font.draw(batch, "AMBIENTE", Gdx.graphics.getWidth() - SIDEBAR_WIDTH + (SIDEBAR_WIDTH - layout.width) / 2f, Gdx.graphics.getHeight() - 20);

        batch.draw(classIcon, 20, Gdx.graphics.getHeight() - 360, 260, 300);
        layout.setText(font, playerName);
        font.draw(batch, playerName, SIDEBAR_WIDTH / 2f - layout.width / 2f, Gdx.graphics.getHeight() - 380);

        float barX = 30, baseY = Gdx.graphics.getHeight() - 450, spacing = 60;
        font.draw(batch, "Life", barX, baseY + 45);
        font.draw(batch, "Hunger", barX, baseY - spacing + 45);
        font.draw(batch, "Thirst", barX, baseY - spacing * 2 + 45);
        font.draw(batch, "Sanity", barX, baseY - spacing * 3 + 45);
        font.draw(batch, "Energy", barX, baseY - spacing * 4 + 45);

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                float dx = x * TILE_SIZE + offsetX;
                float dy = y * TILE_SIZE + offsetY;
                batch.draw(floorTexture, dx, dy);
                if (map[y][x] == 1) batch.draw(wallTexture, dx, dy);
                else if (map[y][x] == 2) {
                    batch.setColor(0.8f, 0.5f, 0.2f, 1f);
                    batch.draw(floorTexture, dx, dy);
                    batch.setColor(1, 1, 1, 1);
                }
            }
        }

        batch.draw(playerTexture, playerPos.x + offsetX, playerPos.y + offsetY, 20, 20);
        batch.draw(backpackIcon, bx, by, size, size);

        if (blinkVisible || mouseOverBackpack) {
            layout.setText(font, "i");
            font.setColor(mouseOverBackpack ? Color.YELLOW : Color.GREEN);
            font.draw(batch, "i", bx + size / 2f - layout.width / 2f, by + size / 2.3f);
            font.setColor(Color.WHITE);
        }

        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawBar(Color.RED, life, barX, baseY);
        drawBar(Color.ORANGE, hunger, barX, baseY - spacing);
        drawBar(Color.BLUE, thirst, barX, baseY - spacing * 2);
        drawBar(Color.CYAN, sanity, barX, baseY - spacing * 3);
        drawBar(Color.YELLOW, energy, barX, baseY - spacing * 4);
        shapeRenderer.end();

        if (showInventory) renderInventoryWindow();
    }

    private void drawBar(Color color, float percent, float x, float y) {
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(x, y, 240, 20);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(x, y, 240 * percent, 20);
    }

    @Override
    public void resize(int width, int height) {
        offsetX = (width - (MAP_WIDTH * TILE_SIZE)) / 2f;
        offsetY = (height - (MAP_HEIGHT * TILE_SIZE)) / 2f;
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        floorTexture.dispose();
        wallTexture.dispose();
        playerTexture.dispose();
        sidebarTexture.dispose();
        classIcon.dispose();
        inventoryBackground.dispose();
        backpackIcon.dispose();
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
    }
}
