// core/src/main/java/io/github/com/ranie_borges/thejungle/view/ProceduralMapScreen.java
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import io.github.com.ranie_borges.thejungle.controller.*;
import io.github.com.ranie_borges.thejungle.controller.managers.CharacterManager;
import io.github.com.ranie_borges.thejungle.controller.managers.GameStateManager;
import io.github.com.ranie_borges.thejungle.controller.managers.MapManager;
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
    private TurnController turnController;

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
    private Stage stage;

    // Rendering components
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private Skin skin;

    // For interaction prompts
    private static Texture bgHudShared;
    private BitmapFont promptFont;
    private GlyphLayout promptLayoutInstance;

    // UI state
    private boolean showInventory = false;
    private float blinkTimer = 0f;
    private boolean blinkVisible = true;
    private boolean playerSpawned = false;
    private boolean mapTransitionTriggered = false;

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

        if (gameState.getEventController() == null) {
            gameState.setEventController(new EventController(gameState));
        }

        this.mapManager = new MapManager(ambient);
        gameState.setMapManager(this.mapManager);
        this.characterManager = new CharacterManager(character, ambient);
        this.resourceController = new ResourceController();
        this.gameStateManager = new GameStateManager(gameState);
        this.renderHelper = new GameRenderHelper();
        this.turnController = new TurnController(gameState, new AmbientController(null));
    }

    // New getter for MapManager
    public MapManager getMapManager() {
        return this.mapManager;
    }

    @Override
    public void show() {
        try {
            stage = new Stage();
            Gdx.input.setInputProcessor(stage);

            skin = new Skin(Gdx.files.internal("mainMenu/uiskin.json"));

            // Pass 'this' (the ProceduralMapScreen instance) to TurnController
            turnController.setUI(stage, skin, this);

            textureManager = new TextureManager();
            lightingManager = new LightingManager();

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

            if (gameState.getCurrentMap() != null && gameState.getCurrentAmbient() != null) {
                this.map = gameState.getCurrentMap();
                this.ambient = gameState.getCurrentAmbient();
                mapManager.setCurrentAmbient(this.ambient);
                logger.info("Loaded map and ambient from GameState.");
            } else {
                this.ambient = new Jungle();
                mapManager.setCurrentAmbient(this.ambient);
                mapManager.generateMapForCurrentAmbient();
                this.map = mapManager.getMap();
                gameState.setCurrentMap(this.map);
                gameState.setCurrentAmbient(this.ambient);
                logger.info("Generated initial map for new game.");
            }
            updateScreenMapAndEntities();


            character.loadPlayerAnimations();
            character.updateStateTime(0f);

            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        } catch (Exception e) {
            logger.error("Error initializing ProceduralMapScreen: {}", e.getMessage());
            throw e;
        }

        character.updateStateTime(0f);
        // Pass the ChatController from GameState to Hud
        hud = new Hud(textureManager.getSidebarTexture(), classIcon, font, gameState.getChatController());
        characterUI = new CharacterUI(inventoryBackground, font);
    }

    /**
     * Centralized method to update the screen's map data and entities after any map change.
     * This is called after initial load, door traversal, or ambient selection.
     */
    public void updateScreenMapAndEntities() {
        this.map = mapManager.getMap();
        this.ambient = mapManager.getCurrentAmbient();

        updateTextures(this.ambient);
        lightingManager.initializeBuffer();

        characterManager.setMap(this.map);
        characterManager.setCurrentAmbient(this.ambient);

        if (!playerSpawned) {
            playerSpawned = character.setInitialSpawn(
                map, MAP_WIDTH, MAP_HEIGHT, TILE_SIZE,
                TILE_GRASS, TILE_CAVE, ambient.getName(), ambient);
        } else {
            characterManager.safeSpawnCharacter();
        }

        if (this.ambient.getName().toLowerCase().contains("cave")) {
            mapManager.generateCaveDoors();
        }

        deers = resourceController.spawnCreatures(this.ambient, this.map);
        cannibals = resourceController.spawnCannibals(this.ambient, this.map);
        materiaisNoMapa = resourceController.spawnResources(this.ambient, this.map);

        gameStateManager.autosave(character, ambient, map);

        // Add message for new map load
        gameState.getChatController().addMessage("Entered " + ambient.getName() + ".");
    }

    /**
     * Handles the logic when a character passes through a door.
     * This method decides whether to generate a new map within the same ambient, or trigger a turn end.
     * @param ambientOnDoorTile The ambient the character is currently in when hitting the door.
     */
    private void handleDoorTraversal(Ambient ambientOnDoorTile) {
        logger.info("Character passed through a door. Checking ambient cycle.");
        boolean ambientRotated = mapManager.checkAndRotateAmbient();

        if (ambientRotated) {
            logger.info("Ambient cycle complete. Prompting user for next action.");
            gameState.getChatController().addMessage("Ambient cycle complete! Choose your next path.", Color.GOLD);
            turnController.advanceTurn();
        } else {
            logger.info("Generating next map within the same ambient: {}", ambientOnDoorTile.getName());
            mapManager.generateMapForCurrentAmbient();
            updateScreenMapAndEntities();
            mapTransitionTriggered = false;
            gameState.getChatController().addMessage("Moved to a new area in " + ambientOnDoorTile.getName() + ".");
        }
    }


    private void updateTextures(Ambient newAmbient) {
        textureManager.loadAmbientTextures(newAmbient);
    }

    @Override
    public void render(float delta) {
        SnakeEventManager.handleInput();

        if (SnakeEventManager.isWaitingForSpace()) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            renderHelper.renderSnakeAlertScreen(batch);
            return;
        }

        if (!mapTransitionTriggered && stage.getActors().size <= 1) {
            try {
                boolean passedThroughDoor = characterManager.updateCharacterMovement(delta);

                if (passedThroughDoor) {
                    if (!mapTransitionTriggered) {
                        mapTransitionTriggered = true;
                        handleDoorTraversal(this.ambient);
                    }
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
            } catch (Exception e) {
                logger.error("Error in game logic update: {}", e.getMessage(), e); // Log the full stack trace for better debugging
            }
        }


        lightingManager.beginLightBuffer();

        batch.begin();
        renderHelper.updateCameraOffset(character.getPosition().x, character.getPosition().y,
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        offsetX = renderHelper.getOffsetX();
        offsetY = renderHelper.getOffsetY();

        renderHelper.renderMap(batch, map, textureManager.getFloorTexture(), textureManager.getWallTexture(), ambient);
        renderHelper.renderCreatures(batch, deers, cannibals, character);

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
        batch.end();

        lightingManager.endLightBufferAndRender(batch);

        if (character.isInTallGrass()) {
            float playerScreenX = character.getPosition().x + offsetX + TILE_SIZE / 2f;
            float playerScreenY = character.getPosition().y + offsetY + TILE_SIZE / 2f;
            lightingManager.renderTallGrassEffect(shapeRenderer, playerScreenX, playerScreenY);
        }

        for (Material m : materiaisNoMapa) {
            float playerDistToMaterial = character.getPosition().dst(m.getPosition());
            boolean playerIsNear = playerDistToMaterial < TILE_SIZE * 1.5f;

            if (playerIsNear) {
                float materialScreenX = m.getPosition().x + offsetX;
                float materialScreenY = m.getPosition().y + offsetY;
                boolean mouseIsOverMaterial = Gdx.input.getX() >= materialScreenX && Gdx.input.getX() <= materialScreenX + TILE_SIZE &&
                    (Gdx.graphics.getHeight() - Gdx.input.getY()) >= materialScreenY && (Gdx.graphics.getHeight() - Gdx.input.getY()) <= materialScreenY + TILE_SIZE;

                if (mouseIsOverMaterial) {
                    if ("Medicinal".equalsIgnoreCase(m.getName()) && "Plant".equalsIgnoreCase(m.getType())) {
                        Medicine.renderUseOption(batch, m, character, offsetX, offsetY);
                    } else if ("Berry".equalsIgnoreCase(m.getName())) {
                        renderHelper.renderInteractionPrompt(batch, m, "Collect Berry");
                    }
                }
            }
        }

        if (showInventory) {
            characterUI.renderInventory(batch, shapeRenderer, character);
            craftingBar.render(batch, shapeRenderer, character, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        if (character.getLife() <= 0)
            gameOver();

        hud.render(batch, shapeRenderer, character, gameState,
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        stage.act(delta);
        stage.draw();

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
        stage.getViewport().update(width, height, true);
        offsetX = (width - (MAP_WIDTH * TILE_SIZE)) / 2f;
        offsetY = (height - (MAP_HEIGHT * TILE_SIZE)) / 2f;
    }

    @Override
    public void pause() {
        gameStateManager.autosave(character, ambient, map);
    }

    @Override
    public void resume() {}
    @Override
    public void hide() {}

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
            if (stage != null) {
                stage.dispose();
            }
            if (skin != null) {
                skin.dispose();
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
