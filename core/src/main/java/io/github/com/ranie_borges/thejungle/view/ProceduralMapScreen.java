package io.github.com.ranie_borges.thejungle.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.controller.systems.SaveManager;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.Deer;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.Cannibal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ProceduralMapScreen implements Screen {
    private static final Logger logger = LoggerFactory.getLogger(ProceduralMapScreen.class);
    private final int TILE_SIZE = 32;
    private final int MAP_WIDTH = 30;
    private final int MAP_HEIGHT = 20;
    private final int SIDEBAR_WIDTH = 300;

    private static final int TILE_GRASS = 0;
    private static final int TILE_WALL = 1;
    private static final int TILE_DOOR = 2;
    private static final int TILE_CAVE = 3;
    private static final int TILE_WATER = 4;

    private int[][] map;
    private Texture floorTexture, wallTexture, playerTexture, sidebarTexture, classIcon;
    private Texture inventoryBackground, backpackIcon;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private GlyphLayout layout;

    private float offsetX, offsetY;
    private Vector2 playerPos;
    private final Character character;
    private final Ambient ambient;
    private boolean showInventory = false;

    private float blinkTimer = 0f;
    private boolean blinkVisible = true;
    private final float BLINK_INTERVAL = 0.5f;

    private GameState gameState;
    private SaveManager saveManager;
    private float autosaveTimer = 0f;
    private final float AUTOSAVE_INTERVAL = 60f;

    private List<Deer> deers;
    private List<Cannibal> cannibals;



    public ProceduralMapScreen(Character character, Ambient ambient) {
        this.character = character;
        this.ambient = ambient;
        this.saveManager = new SaveManager();

        if (character == null) {
            logger.error("Character is null in ProceduralMapScreen constructor");
            throw new IllegalArgumentException("Character cannot be null");
        }
        if (ambient == null) {
            logger.error("Ambient is null in ProceduralMapScreen constructor");
            throw new IllegalArgumentException("Ambient cannot be null");
        }

        this.gameState = new GameState();
        gameState.setCharacter(character);
        gameState.setCurrentAmbient(ambient);
    }

    @Override
    public void show() {
        try {
            floorTexture = ambient.getFloorTexture() != null ? ambient.getFloorTexture() : new Texture("GameScreen/chao.png");
            wallTexture = ambient.getWallTexture() != null ? ambient.getWallTexture() : new Texture("GameScreen/parede.png");
            playerTexture = new Texture("sprites/character/personagem_luta.png");
            sidebarTexture = ambient.getSidebarTexture() != null ? ambient.getSidebarTexture() : new Texture("Gameplay/sidebar.jpg");
            classIcon = new Texture(getIconPathForClass(character.getCharacterType()));
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


            regenerateDeers();
        } catch (Exception e) {
            logger.error("Error initializing ProceduralMapScreen: {}", e.getMessage());
            throw e;
        }
        cannibals = new ArrayList<>();
        if (ambient.getName().toLowerCase().contains("cave")) {
            for (int i = 0; i < 3; i++) {
                int x, y;
                do {
                    x = (int)(Math.random() * MAP_WIDTH);
                    y = (int)(Math.random() * MAP_HEIGHT);
                } while (!isValidCaveSpawnTile(x, y)); // <- usando a função certa agora!

                Cannibal cannibal = new Cannibal();
                cannibal.getPosition().set(x * TILE_SIZE, y * TILE_SIZE);
                cannibals.add(cannibal);
            }
        }
    }
    private boolean canSpawnDeerInCurrentAmbient() {
        // Só permitir veado em ambientes que são florestais, prados, etc
        return ambient.getName().toLowerCase().contains("forest") ||
            ambient.getName().toLowerCase().contains("jungle") ||
            ambient.getName().toLowerCase().contains("plains") ||
            ambient.getName().toLowerCase().contains("field");
    }


    private void regenerateDeers() {
        deers = new ArrayList<>();
        if (canSpawnDeerInCurrentAmbient()) {
            for (int i = 0; i < 5; i++) {
                int x, y;
                do {
                    x = (int)(Math.random() * MAP_WIDTH);
                    y = (int)(Math.random() * MAP_HEIGHT);
                } while (!isValidSpawnTile(x, y));

                Deer deer = new Deer();
                deer.getPosition().set(x * TILE_SIZE, y * TILE_SIZE);
                deers.add(deer);
            }
        }


    }

    private String getIconPathForClass(String rawClass) {
        switch (rawClass) {
            case "Survivor": return "StatsScreen/desempregadoFundo.png";
            case "Hunter": return "StatsScreen/cacadorFundo.png";
            case "Lumberjack": return "StatsScreen/lenhadorFundo.png";
            case "Doctor": return "StatsScreen/medicoFundo.png";
            default: return "StatsScreen/desempregadoFundo.png";
        }
    }

    private void generateMap() {
        try {
            map = ambient.generateMap(MAP_WIDTH, MAP_HEIGHT);
            gameState.setCurrentMap(map);
            logger.debug("Map generated for ambient: {}", ambient.getName());
        } catch (Exception e) {
            logger.error("Error generating map: {}", e.getMessage());
            map = new int[MAP_HEIGHT][MAP_WIDTH];
            for (int y = 0; y < MAP_HEIGHT; y++) {
                for (int x = 0; x < MAP_WIDTH; x++) {
                    if (x == 0 || y == 0 || x == MAP_WIDTH - 1 || y == MAP_HEIGHT - 1) {
                        map[y][x] = TILE_WALL;
                    } else {
                        map[y][x] = TILE_GRASS;
                    }
                }
            }
        }
    }

    private void setPlayerPosition() {
        try {
            Vector2 charPos = character.getPosition();
            if (charPos != null && isValidPosition((int)(charPos.x / TILE_SIZE), (int)(charPos.y / TILE_SIZE))) {
                playerPos = new Vector2(charPos.x, charPos.y);
                return;
            }

            for (int y = 1; y < MAP_HEIGHT - 1; y++) {
                for (int x = 1; x < MAP_WIDTH - 1; x++) {
                    if (map[y][x] == TILE_GRASS && hasAdjacentFloor(y, x)) {
                        playerPos = new Vector2(x * TILE_SIZE, y * TILE_SIZE);
                        character.getPosition().set(playerPos);
                        return;
                    }
                }
            }

            playerPos = new Vector2((MAP_WIDTH / 2) * TILE_SIZE, (MAP_HEIGHT / 2) * TILE_SIZE);
            character.getPosition().set(playerPos);
        } catch (Exception e) {
            logger.error("Error setting player position: {}", e.getMessage());
            playerPos = new Vector2((MAP_WIDTH / 2) * TILE_SIZE, (MAP_HEIGHT / 2) * TILE_SIZE);
            character.getPosition().set(playerPos);
        }
    }
    private boolean isValidSpawnTile(int x, int y) {
        if (x < 0 || x >= MAP_WIDTH || y < 0 || y >= MAP_HEIGHT) return false;
        return map[y][x] == TILE_GRASS;
    }
    private boolean isValidCaveSpawnTile(int x, int y) {
        if (x < 0 || x >= MAP_WIDTH || y < 0 || y >= MAP_HEIGHT) return false;
        return map[y][x] == TILE_CAVE;
    }

    private boolean isValidPosition(int x, int y) {
        return x > 0 && x < MAP_WIDTH - 1 && y > 0 && y < MAP_HEIGHT - 1 && map[y][x] == TILE_GRASS;
    }

    private boolean hasAdjacentFloor(int y, int x) {
        return (y > 0 && map[y - 1][x] == TILE_GRASS) ||
            (y < MAP_HEIGHT - 1 && map[y + 1][x] == TILE_GRASS) ||
            (x > 0 && map[y][x - 1] == TILE_GRASS) ||
            (x < MAP_WIDTH - 1 && map[y][x + 1] == TILE_GRASS);
    }

    private void movePlayer(float delta) {
        try {
            float speed = character.getSpeed() > 0 ? character.getSpeed() : 100f;
            float nextX = playerPos.x, nextY = playerPos.y;

            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.W)) nextY += speed * delta;
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.S)) nextY -= speed * delta;
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A)) nextX -= speed * delta;
            if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) nextX += speed * delta;

            int tileX = (int) ((nextX + 8) / TILE_SIZE), tileY = (int) ((nextY + 8) / TILE_SIZE);
            if (tileX >= 0 && tileX < MAP_WIDTH && tileY >= 0 && tileY < MAP_HEIGHT) {
                int tileType = map[tileY][tileX];
                if (tileType != TILE_WALL) {
                    playerPos.set(nextX, nextY);
                    character.getPosition().set(nextX, nextY);
                }
                if (tileType == TILE_DOOR) {
                    generateMap();
                    setPlayerPosition();
                    regenerateDeers();
                    autosaveGame();
                }
            }

            updateCharacterStats(delta);

        } catch (Exception e) {
            logger.error("Error moving player: {}", e.getMessage());
        }
    }

    private void updateCharacterStats(float delta) {
        float hungerDepletion = 0.01f * delta;
        float thirstDepletion = 0.015f * delta;
        float energyDepletion = 0.005f * delta;

        character.setHunger(Math.max(0, character.getHunger() - hungerDepletion));
        character.setThirsty(Math.max(0, character.getThirsty() - thirstDepletion));
        character.setEnergy(Math.max(0, character.getEnergy() - energyDepletion));

        if (character.getHunger() <= 10 || character.getThirsty() <= 10) {
            character.setLife(Math.max(0, character.getLife() - 0.05f * delta));
        }

        autosaveTimer += delta;
        if (autosaveTimer >= AUTOSAVE_INTERVAL) {
            autosaveTimer = 0;
            autosaveGame();
        }
    }

    private void autosaveGame() {
        try {
            gameState.setCharacter(character);
            gameState.setCurrentAmbient(ambient);
            gameState.setCurrentMap(map);

            boolean success = saveManager.saveGame(gameState, "autosave");
            if (success) {
                logger.info("Game autosaved successfully");
            } else {
                logger.warn("Autosave failed");
            }
        } catch (Exception e) {
            logger.error("Error during autosave: {}", e.getMessage());
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

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.LIGHT_GRAY);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float sx = startX + col * (slotSize + padding);
                float sy = startY + (rows - row - 1) * (slotSize + padding);
                int slotIndex = row * cols + col;

                shapeRenderer.rect(sx, sy, slotSize, slotSize);

                if (slotIndex < character.getInventory().size && character.getInventory().get(slotIndex) != null) {
                    shapeRenderer.setColor(Color.YELLOW);
                    shapeRenderer.rect(sx + 4, sy + 4, slotSize - 8, slotSize - 8);
                    shapeRenderer.setColor(Color.LIGHT_GRAY);
                }
            }
        }
        shapeRenderer.end();

        batch.begin();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float sx = startX + col * (slotSize + padding);
                float sy = startY + (rows - row - 1) * (slotSize + padding);
                int slotIndex = row * cols + col;

                if (slotIndex < character.getInventory().size && character.getInventory().get(slotIndex) != null) {
                    String itemName = character.getInventory().get(slotIndex).getName();
                    layout.setText(font, itemName);
                    if (layout.width > slotSize) {
                        itemName = itemName.substring(0, 3) + "...";
                        layout.setText(font, itemName);
                    }
                    font.draw(batch, itemName, sx + (slotSize - layout.width) / 2, sy + slotSize / 2);
                }
            }
        }
        batch.end();
    }
    @Override
    public void render(float delta) {
        try {
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

            layout.setText(font, ambient.getName().toUpperCase());
            font.draw(batch, ambient.getName().toUpperCase(),
                Gdx.graphics.getWidth() - SIDEBAR_WIDTH + (SIDEBAR_WIDTH - layout.width) / 2f,
                Gdx.graphics.getHeight() - 20);

            batch.draw(classIcon, 20, Gdx.graphics.getHeight() - 360, 260, 300);
            layout.setText(font, character.getName());
            font.draw(batch, character.getName(), SIDEBAR_WIDTH / 2f - layout.width / 2f, Gdx.graphics.getHeight() - 380);

            float barX = 30, baseY = Gdx.graphics.getHeight() - 450, spacing = 60;
            font.draw(batch, "Life", barX, baseY + 45);
            font.draw(batch, "Hunger", barX, baseY - spacing + 45);
            font.draw(batch, "Thirst", barX, baseY - spacing * 2 + 45);
            font.draw(batch, "Sanity", barX, baseY - spacing * 3 + 45);
            font.draw(batch, "Energy", barX, baseY - spacing * 4 + 45);

            font.draw(batch, "Days: " + gameState.getDaysSurvived(),
                Gdx.graphics.getWidth() - SIDEBAR_WIDTH + 20,
                Gdx.graphics.getHeight() - 60);

            // Renderizar o mapa
            for (int y = 0; y < MAP_HEIGHT; y++) {
                for (int x = 0; x < MAP_WIDTH; x++) {
                    float dx = x * TILE_SIZE + offsetX;
                    float dy = y * TILE_SIZE + offsetY;
                    if (map[y][x] == TILE_GRASS) {
                        batch.draw(floorTexture, dx, dy);
                    } else if (map[y][x] == TILE_WALL) {
                        batch.draw(wallTexture, dx, dy);
                    } else if (map[y][x] == TILE_DOOR) {
                        batch.setColor(0.8f, 0.5f, 0.2f, 1f);
                        batch.draw(floorTexture, dx, dy);
                        batch.setColor(1, 1, 1, 1);
                    } else if (map[y][x] == TILE_CAVE) {
                        batch.setColor(0.5f, 0.3f, 0.2f, 1f);
                        batch.draw(floorTexture, dx, dy);
                        batch.setColor(1, 1, 1, 1);
                    }
                }
            }

            batch.draw(playerTexture, playerPos.x + offsetX, playerPos.y + offsetY, 20, 20);
            batch.draw(backpackIcon, bx, by, size, size);

            // Renderizar os veados
            for (Deer deer : deers) {
                Sprite deerSprite = deer.getSprites().get("idle");
                if (deerSprite != null) {
                    deerSprite.setSize(50, 50); // Definir tamanho primeiro
                    deerSprite.setPosition(
                        deer.getPosition().x + offsetX + (TILE_SIZE - deerSprite.getWidth()) / 2,
                        deer.getPosition().y + offsetY + (TILE_SIZE - deerSprite.getHeight()) / 2
                    );
                    deerSprite.draw(batch);

                }
            }
            for (Cannibal cannibal : cannibals) {
                Sprite cannibalSprite = cannibal.getSprites().get("idle");
                if (cannibalSprite != null) {
                    cannibalSprite.setSize(40, 40);
                    cannibalSprite.setPosition(
                        cannibal.getPosition().x + offsetX + (TILE_SIZE - cannibalSprite.getWidth()) / 2,
                        cannibal.getPosition().y + offsetY + (TILE_SIZE - cannibalSprite.getHeight()) / 2
                    );
                    cannibalSprite.draw(batch);
                }
            }


            if (blinkVisible || mouseOverBackpack) {
                layout.setText(font, "i");
                font.setColor(mouseOverBackpack ? Color.YELLOW : Color.GREEN);
                font.draw(batch, "i", bx + size / 2f - layout.width / 2f, by + size / 2.3f);
                font.setColor(Color.WHITE);
            }

            batch.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            drawBar(Color.RED, character.getLife() / 100f, barX, baseY);
            drawBar(Color.ORANGE, character.getHunger() / 100f, barX, baseY - spacing);
            drawBar(Color.BLUE, character.getThirsty() / 100f, barX, baseY - spacing * 2);
            drawBar(Color.CYAN, character.getSanity() / 100f, barX, baseY - spacing * 3);
            drawBar(Color.YELLOW, character.getEnergy() / 100f, barX, baseY - spacing * 4);
            shapeRenderer.end();

            if (showInventory) renderInventoryWindow();

            if (character.getLife() <= 0) {
                gameOver();
            }

        } catch (Exception e) {
            logger.error("Error in render: {}", e.getMessage());
        }
    }

    private void gameOver() {
        saveManager.saveGame(gameState, "final_save_day_" + gameState.getDaysSurvived());
        logger.info("Game over - character died after {} days", gameState.getDaysSurvived());
    }

    private void drawBar(Color color, float percent, float x, float y) {
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(x, y, 240, 20);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(x, y, 240 * Math.min(1, Math.max(0, percent)), 20);
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
        try {
            autosaveGame();
            if (floorTexture != null) floorTexture.dispose();
            if (wallTexture != null) wallTexture.dispose();
            if (playerTexture != null) playerTexture.dispose();
            if (sidebarTexture != null) sidebarTexture.dispose();
            if (classIcon != null) classIcon.dispose();
            if (inventoryBackground != null) inventoryBackground.dispose();
            if (backpackIcon != null) backpackIcon.dispose();
            if (batch != null) batch.dispose();
            if (font != null) font.dispose();
            if (shapeRenderer != null) shapeRenderer.dispose();
        } catch (Exception e) {
            logger.error("Error disposing resources: {}", e.getMessage());
        }
    }
}
