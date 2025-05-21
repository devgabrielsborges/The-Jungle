package io.github.com.ranie_borges.thejungle.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import io.github.com.ranie_borges.thejungle.controller.managers.CharacterManager;
import io.github.com.ranie_borges.thejungle.controller.managers.GameStateManager;
import io.github.com.ranie_borges.thejungle.controller.managers.MapManager;
import io.github.com.ranie_borges.thejungle.controller.ResourceController;
import io.github.com.ranie_borges.thejungle.controller.managers.SaveManager;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.Cannibal;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.Deer;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Material;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Medicine;
import io.github.com.ranie_borges.thejungle.model.events.events.SnakeEventManager;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Jungle;
import io.github.com.ranie_borges.thejungle.view.helpers.GameRenderHelper;
import io.github.com.ranie_borges.thejungle.view.helpers.LightingManager;
import io.github.com.ranie_borges.thejungle.view.helpers.TextureManager;
import io.github.com.ranie_borges.thejungle.view.interfaces.UI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Main gameplay screen showing the procedurally generated map
 */
public class ProceduralMapScreen implements Screen, UI {
    private static final Logger logger = LoggerFactory.getLogger(ProceduralMapScreen.class);

    // Core components
    private final GameState gameState;
    private final MapManager mapManager;
    private final CharacterManager characterManager;
    private final ResourceController resourceController;
    private final GameStateManager gameStateManager;
    private final GameRenderHelper renderHelper;

    // Game state elements
    private final Character character;
    private Ambient ambient;
    private int[][] map;
    private List<Material> materiaisNoMapa = new ArrayList<>();
    private List<Deer> deers = new ArrayList<>();
    private List<Cannibal> cannibals = new ArrayList<>();

    // Visual components
    private Texture playerTexture;
    private Texture classIcon;
    private Texture inventoryBackground, backpackIcon;
    private TextureManager textureManager;
    private LightingManager lightingManager;
    private CraftingBar craftingBar;
    private Hud hud;
    private CharacterUI characterUI;

    // Rendering components
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    // For interaction prompts
    private static Texture bgHudShared;
    private BitmapFont promptFont;
    private GlyphLayout promptLayoutInstance;

    // UI state
    private boolean showInventory = false;
    private float blinkTimer = 0f;
    private boolean blinkVisible = true;

    private boolean playerSpawned = false;

    // Camera offsets
    private float offsetX = 0;
    private float offsetY = 0;

    public ProceduralMapScreen(Character character, Ambient ambient) {
        if (character == null) {
            logger.error("Character is null in ProceduralMapScreen constructor");
            throw new IllegalArgumentException("Character cannot be null");
        }
        if (ambient == null) {
            logger.error("Ambient is null in ProceduralMapScreen constructor");
            throw new IllegalArgumentException("Ambient cannot be null");
        }

        this.character = character;
        this.ambient = ambient;
        Vector2 playerPos = new Vector2();
        SaveManager saveManager = new SaveManager();

        this.gameState = new GameState();
        gameState.setCharacter(character);
        gameState.setCurrentAmbient(ambient);

        this.mapManager = new MapManager(ambient);
        this.characterManager = new CharacterManager(character, ambient);
        this.resourceController = new ResourceController();
        this.gameStateManager = new GameStateManager(gameState);
        this.renderHelper = new GameRenderHelper();
    }

    @Override
    public void show() {
        try {
            textureManager = new TextureManager();
            textureManager.loadAmbientTextures(ambient);

            playerTexture = textureManager.getOrLoadTexture("sprites/character/personagem_luta.png");
            classIcon = textureManager.loadClassIcon(character.getCharacterType());
            inventoryBackground = textureManager.getOrLoadTexture("Gameplay/backpackInside.png");
            backpackIcon = textureManager.getOrLoadTexture("Gameplay/backpack.png");

            if (bgHudShared == null || bgHudShared.toString().contains("unknown")) {
                bgHudShared = new Texture(Gdx.files.internal("GameScreen/boxhud.png"));
            }
            promptFont = new BitmapFont();
            promptFont.setColor(Color.WHITE);
            promptFont.getData().setScale(1.2f);
            promptLayoutInstance = new GlyphLayout();

            batch = new SpriteBatch();
            shapeRenderer = new ShapeRenderer();
            font = new BitmapFont();
            font.getData().setScale(2f);
            font.setUseIntegerPositions(true);


            map = mapManager.generateMap();

            characterManager.setMap(map);

            if (!playerSpawned) {
                playerSpawned = character.setInitialSpawn(
                        map, MAP_WIDTH, MAP_HEIGHT, TILE_SIZE,
                        TILE_GRASS, TILE_CAVE, ambient.getName(), ambient);
            }

            character.loadPlayerAnimations();
            character.updateStateTime(0f);

            if (ambient.getName().toLowerCase().contains("cave")) {
                mapManager.generateCaveDoors();
            }

            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            deers = resourceController.spawnCreatures(ambient, map);
            cannibals = resourceController.spawnCannibals(ambient, map);
            materiaisNoMapa = resourceController.spawnResources(ambient, map);

            lightingManager = new LightingManager();

        } catch (Exception e) {
            logger.error("Error initializing ProceduralMapScreen: {}", e.getMessage());
            throw e;
        }

        // Initialize UI components
        character.updateStateTime(0f);
        this.craftingBar = new CraftingBar();
        hud = new Hud(textureManager.getSidebarTexture(), classIcon, font, backpackIcon);
        characterUI = new CharacterUI(inventoryBackground, font);
    }

    private void updateTextures(Ambient newAmbient) {
        textureManager.loadAmbientTextures(newAmbient);
    }
    private void renderInteractionPrompt(SpriteBatch batch, Material material, float offsetX,
                                         float offsetY) {
        Vector2 pos = material.getPosition();
        float boxWidth = 160;
        float boxHeight = 30;
        // Center the box above the material's tile
        float boxX = pos.x + offsetX + (TILE_SIZE / 2f) - (boxWidth / 2f);
        float boxY = pos.y + offsetY + TILE_SIZE; // Position it above the tile

        batch.setColor(1, 1, 1, 0.7f);
        batch.draw(bgHudShared, boxX, boxY, boxWidth, boxHeight);
        batch.setColor(1, 1, 1, 1);

        promptLayoutInstance.setText(promptFont, "[E] Collect Berry");
        promptFont.draw(batch, promptLayoutInstance, boxX + (boxWidth - promptLayoutInstance.width) / 2f,
                boxY + boxHeight - (boxHeight - promptLayoutInstance.height) / 2f - 2); // Adjusted for vertical
                                                                                        // centering
    }

    @Override
    public void render(float delta) {
        SnakeEventManager.handleInput();

        if (SnakeEventManager.isWaitingForSpace()) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            renderHelper.renderSnakeAlertScreen();
            return;
        }

        lightingManager.beginLightBuffer();

        try {
            boolean passedThroughDoor = characterManager.updateCharacterMovement(delta);

            if (passedThroughDoor) {
                map = mapManager.generateMap();
                ambient = mapManager.getCurrentAmbient();

                gameState.setCurrentAmbient(ambient);
                gameState.setCurrentMap(map);

                characterManager.setMap(map);
                characterManager.setCurrentAmbient(ambient);

                characterManager.safeSpawnCharacter();

                if (ambient.getName().toLowerCase().contains("cave")) {
                    mapManager.generateCaveDoors();
                }

                deers = resourceController.spawnCreatures(ambient, map);
                cannibals = resourceController.spawnCannibals(ambient, map);
                materiaisNoMapa = resourceController.spawnResources(ambient, map);

                updateTextures(ambient);

                lightingManager.initializeBuffer();

                gameStateManager.autosave(character, ambient, map);
            }

            character.updateStateTime(delta);

            characterManager.updateCharacterStats(delta);

            gameStateManager.update(delta, character, ambient, map);

            blinkTimer += delta;
            float BLINK_INTERVAL = 0.5f;
            if (blinkTimer >= BLINK_INTERVAL) {
                blinkVisible = !blinkVisible;
                blinkTimer = 0f;
            }

            float size = 96;
            float bx = Gdx.graphics.getWidth() - SIDEBAR_WIDTH + (SIDEBAR_WIDTH - size) / 2f;
            float by = 30;
            int mouseX = Gdx.input.getX();
            int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
            boolean mouseOverBackpack = mouseX >= bx && mouseX <= bx + size
                    && mouseY >= by && mouseY <= by + size;
            if ((Gdx.input.justTouched() && mouseOverBackpack)
                    || Gdx.input.isKeyJustPressed(Input.Keys.I)) {
                showInventory = !showInventory;
            }

            renderHelper.updateCameraOffset(character.getPosition().x, character.getPosition().y,
                    Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            offsetX = renderHelper.getOffsetX();
            offsetY = renderHelper.getOffsetY();

            renderHelper.renderMap(map, textureManager.getFloorTexture(), textureManager.getWallTexture(), ambient);

            renderHelper.renderCreatures(deers, cannibals, character);

            batch.begin();

            if (ambient instanceof Jungle) {
                Jungle jungle = (Jungle) ambient;
                Texture grassOverlay = jungle.getTallGrassTexture();
                for (int y = 0; y < MAP_HEIGHT; y++) {
                    for (int x = 0; x < MAP_WIDTH; x++) {
                        if (jungle.isTallGrass(x, y)) {
                            float dx = x * TILE_SIZE + offsetX;
                            float dy = y * TILE_SIZE + offsetY;
                            batch.draw(grassOverlay, dx, dy, TILE_SIZE, TILE_SIZE);
                        }
                    }
                }
            }

            for (Material m : materiaisNoMapa) {
                Sprite s = m.getSprites().get("idle");
                if (s != null) {
                    if ("Tree".equals(m.getName())) {
                        s.setSize(128, 128);
                        s.setPosition(
                                m.getPosition().x + offsetX + (TILE_SIZE - s.getWidth()) / 2f,
                                m.getPosition().y + offsetY);
                    } else {
                        s.setSize(32, 32);
                        s.setPosition(
                                m.getPosition().x + offsetX + (TILE_SIZE - s.getWidth()) / 2f,
                                m.getPosition().y + offsetY + (TILE_SIZE - s.getHeight()) / 2f);
                    }
                    s.draw(batch);
                }
            }

            SnakeEventManager.update(delta);
            if (SnakeEventManager.isAlertActive()) {
                Texture t = SnakeEventManager.getSnakeBiteImage();
                batch.draw(t,
                        (Gdx.graphics.getWidth() - t.getWidth()) / 2f,
                        (Gdx.graphics.getHeight() - t.getHeight()) / 2f);
            }

            for (Material m : materiaisNoMapa) {
                float playerDistToMaterial = character.getPosition().dst(m.getPosition());
                boolean playerIsNear = playerDistToMaterial < TILE_SIZE * 1.5f; // Player needs to be close

                if (playerIsNear) {
                    float materialScreenX = m.getPosition().x + offsetX;
                    float materialScreenY = m.getPosition().y + offsetY;

                    // Check if mouse is hovering over the material's specific tile area
                    boolean mouseIsOverMaterial = mouseX >= materialScreenX && mouseX <= materialScreenX + TILE_SIZE &&
                            mouseY >= materialScreenY && mouseY <= materialScreenY + TILE_SIZE;

                    if (mouseIsOverMaterial) {
                        if ("Medicinal".equalsIgnoreCase(m.getName()) && "Plant".equalsIgnoreCase(m.getType())) {
                            Medicine.renderUseOption(batch, m, character, offsetX, offsetY);
                        } else if ("Berry".equalsIgnoreCase(m.getName())) {
                            renderInteractionPrompt(batch, m, offsetX, offsetY);
                        }
                    }
                }
            }

            batch.end();
            lightingManager.endLightBufferAndRender(batch);

            if (character.isInTallGrass()) {
                float playerScreenX = character.getPosition().x + offsetX + TILE_SIZE / 2f;
                float playerScreenY = character.getPosition().y + offsetY + TILE_SIZE / 2f;
                lightingManager.renderTallGrassEffect(shapeRenderer, playerScreenX, playerScreenY);
            }

            if (showInventory) {
                characterUI.renderInventory(batch, shapeRenderer, character);
                craftingBar.render(batch, shapeRenderer, character, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }

            if (character.getLife() <= 0)
                gameOver();

            batch.begin();
            hud.render(batch, shapeRenderer, character, gameState,
                    Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();

        } catch (Exception e) {
            logger.error("Error in render: {}", e.getMessage());
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            if (!showInventory) {
                character.tryCollectNearbyMaterial(materiaisNoMapa);
            } else {
                Item selectedItem = characterUI.getSelectedItem();
                if (selectedItem != null) {
                    character.useItem(selectedItem);
                }
            }
        }
    }

    private void gameOver() {
        gameStateManager.save(character, ambient, map, "final_save_day_" + gameState.getDaysSurvived());
        logger.info("Game over - character died after {} days", gameState.getDaysSurvived());
    }

    @Override
    public void resize(int width, int height) {
        offsetX = (width - (MAP_WIDTH * TILE_SIZE)) / 2f;
        offsetY = (height - (MAP_HEIGHT * TILE_SIZE)) / 2f;
    }

    @Override
    public void pause() {
        // Save on pause
        gameStateManager.autosave(character, ambient, map);
    }

    @Override
    public void resume() {
        // Nothing to do here
    }

    @Override
    public void hide() {
        // Save on hide
        gameStateManager.autosave(character, ambient, map);
    }

    @Override
    public void dispose() {
        try {
            gameStateManager.autosave(character, ambient, map);

            if (textureManager != null)
                textureManager.dispose();

            playerTexture = null;
            classIcon = null;
            inventoryBackground = null;
            backpackIcon = null;

            if (renderHelper != null)
                renderHelper.dispose();

            if (batch != null)
                batch.dispose();
            if (font != null)
                font.dispose();
            if (shapeRenderer != null)
                shapeRenderer.dispose();

            if (bgHudShared != null) {
                bgHudShared.dispose();
                bgHudShared = null;
            }
            if (promptFont != null) {
                promptFont.dispose();
                promptFont = null;
            }

        } catch (Exception e) {
            logger.error("Error disposing resources: {}", e.getMessage());
        }

        if (craftingBar != null)
            craftingBar.dispose();
        if (lightingManager != null)
            lightingManager.dispose();
    }

}
