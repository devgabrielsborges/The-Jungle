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
import io.github.com.ranie_borges.thejungle.model.entity.itens.Material;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.controller.systems.SaveManager;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.Deer;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.Cannibal;
import io.github.com.ranie_borges.thejungle.model.world.ambients.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

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

    // Ambient rotation fields
    private int currentAmbientUseCount = 0;
    private final int MAX_AMBIENT_USES = 2 + (int)(Math.random()); // Random 2-3 uses before switching
    private boolean playerSpawned = false;

    private int[][] map;
    private Texture floorTexture, wallTexture, playerTexture, sidebarTexture, classIcon;
    private Texture inventoryBackground, backpackIcon;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private GlyphLayout layout;

    private float offsetX, offsetY;
    private Vector2 playerPos;
    private Character character;
    private Ambient ambient;
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
    private List<Material> materiaisNoMapa;

    // Add these fields to your ProceduralMapScreen class
    private Animation<TextureRegion> playerIdleUp;
    private Animation<TextureRegion> playerIdleDown;
    private Animation<TextureRegion> playerIdleLeft;
    private Animation<TextureRegion> playerIdleRight;
    private Animation<TextureRegion> playerWalkUp;
    private Animation<TextureRegion> playerWalkDown;
    private Animation<TextureRegion> playerWalkLeft;
    private Animation<TextureRegion> playerWalkRight;
    private float stateTime = 0;
    private PlayerState currentState = PlayerState.IDLE_DOWN;
    private boolean isMoving = false;
    private Direction lastDirection = Direction.DOWN;

    // Enums for player state
    private enum PlayerState { IDLE_UP, IDLE_DOWN, IDLE_LEFT, IDLE_RIGHT, WALK_UP, WALK_DOWN, WALK_LEFT, WALK_RIGHT }
    private enum Direction { UP, DOWN, LEFT, RIGHT }

    // In your initialization method/constructor, load all animations
    private void loadPlayerAnimations() {
        // Load idle animations
        playerIdleDown = loadAnimation("personagem_parado_frente.png", 0.01f);
        playerIdleUp = loadAnimation("personagem_parado_costas.gif", 0.01f);
        playerIdleLeft = loadAnimation("personagem_parado_esquerda.gif", 0.01f);
        playerIdleRight = loadAnimation("personagem_parado_direita.gif", 0.01f);

        // Load walking animations
        playerWalkDown = loadAnimation("personagem_andando_frente.gif", 0.04f);
        playerWalkUp = loadAnimation("personagem_andando_costas.gif", 0.04f);
        playerWalkLeft = loadAnimation("personagem_andando_esquerda.gif", 0.04f);
        playerWalkRight = loadAnimation("personagem_andando_direita.gif", 0.04f);
    }

    private Animation<TextureRegion> loadAnimation(String filename, float frameDuration) {
        try {
            // Load a sprite sheet instead of GIF
            Texture spriteSheet = new Texture(Gdx.files.internal("sprites/character/" + filename.replace(".gif", ".png")));

            // For a 4-frame animation in a horizontal strip
            int frameWidth = spriteSheet.getWidth() / 4;
            int frameHeight = spriteSheet.getHeight();

            TextureRegion[][] tmp = TextureRegion.split(spriteSheet, frameWidth, frameHeight);
            Array<TextureRegion> frames = new Array<>(4);

            // Extract frames from the sprite sheet
            for (int i = 0; i < 4; i++) {
                frames.add(tmp[0][i]);
            }

            return new Animation<>(frameDuration, frames);
        } catch (Exception e) {
            logger.error("Failed to load animation: {}", filename, e);
            // Create a fallback animation with the player texture
            TextureRegion[] fallbackFrame = { new TextureRegion(playerTexture) };
            return new Animation<>(frameDuration, new Array<>(fallbackFrame));
        }
    }

    // Update this in your input processing method
    private void handleInput() {
        boolean isMovingNow = false;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            playerPos.y += character.getSpeed() * Gdx.graphics.getDeltaTime();
            lastDirection = Direction.UP;
            isMovingNow = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            playerPos.y -= character.getSpeed() * Gdx.graphics.getDeltaTime();
            lastDirection = Direction.DOWN;
            isMovingNow = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerPos.x -= character.getSpeed() * Gdx.graphics.getDeltaTime();
            lastDirection = Direction.LEFT;
            isMovingNow = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerPos.x += character.getSpeed() * Gdx.graphics.getDeltaTime();
            lastDirection = Direction.RIGHT;
            isMovingNow = true;
        }

        // Update player state based on movement and direction
        isMoving = isMovingNow;
        updatePlayerState();
    }

    // Helper method to update the player state
    private void updatePlayerState() {
        if (isMoving) {
            switch (lastDirection) {
                case UP:    currentState = PlayerState.WALK_UP; break;
                case DOWN:  currentState = PlayerState.WALK_DOWN; break;
                case LEFT:  currentState = PlayerState.WALK_LEFT; break;
                case RIGHT: currentState = PlayerState.WALK_RIGHT; break;
            }
        } else {
            switch (lastDirection) {
                case UP:    currentState = PlayerState.IDLE_UP; break;
                case DOWN:  currentState = PlayerState.IDLE_DOWN; break;
                case LEFT:  currentState = PlayerState.IDLE_LEFT; break;
                case RIGHT: currentState = PlayerState.IDLE_RIGHT; break;
            }
        }
    }

    // In your render method, replace the player texture drawing with this:
    private void renderPlayer(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = getFrameForCurrentState(stateTime);

        // Draw the player with increased size (40x40)
        batch.draw(currentFrame,
            playerPos.x + offsetX - (40 - TILE_SIZE)/2,
            playerPos.y + offsetY - (40 - TILE_SIZE)/2,
            40, 40);
    }

    // Helper method to get the current animation frame
    private TextureRegion getFrameForCurrentState(float stateTime) {
        Animation<TextureRegion> currentAnimation;

        switch (currentState) {
            case IDLE_UP:    currentAnimation = playerIdleUp; break;
            case IDLE_DOWN:  currentAnimation = playerIdleDown; break;
            case IDLE_LEFT:  currentAnimation = playerIdleLeft; break;
            case IDLE_RIGHT: currentAnimation = playerIdleRight; break;
            case WALK_UP:    currentAnimation = playerWalkUp; break;
            case WALK_DOWN:  currentAnimation = playerWalkDown; break;
            case WALK_LEFT:  currentAnimation = playerWalkLeft; break;
            case WALK_RIGHT: currentAnimation = playerWalkRight; break;
            default:         currentAnimation = playerIdleDown; break;
        }

        return currentAnimation.getKeyFrame(stateTime, true);
    }

    public ProceduralMapScreen(Character character, Ambient ambient) {
        this.character = character;
        this.ambient = ambient;
        this.saveManager = new SaveManager();
        this.playerPos = new Vector2();

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
            loadPlayerAnimations();
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
            if (ambient.getName().toLowerCase().contains("cave")) {
                generateCaveDoors();
            }
            setPlayerPosition();
            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            regenerateDeers();
            regenerateCannibals();
            materiaisNoMapa = Material.spawnSmallRocks(3, map, MAP_WIDTH, MAP_HEIGHT, TILE_CAVE, TILE_SIZE);

        } catch (Exception e) {
            logger.error("Error initializing ProceduralMapScreen: {}", e.getMessage());
            throw e;
        }
    }

    private void generateMap() {
        try {
            // Increment ambient use counter
            currentAmbientUseCount++;
            playerSpawned = false; // Reset player spawning flag

            // Check if we need to rotate ambient
            if (currentAmbientUseCount > MAX_AMBIENT_USES) {
                // Switch to a new ambient
                Ambient newAmbient = getNextAmbient();

                // Update reference to current ambient
                ambient = newAmbient;
                gameState.setCurrentAmbient(newAmbient);

                // Update textures for the new ambient
                updateTextures(newAmbient);

                logger.info("Rotating ambient to: {}", newAmbient.getName());
                currentAmbientUseCount = 1; // Reset counter for new ambient
            }

            // Generate the map for the current ambient
            map = ambient.generateMap(MAP_WIDTH, MAP_HEIGHT);
            gameState.setCurrentMap(map);

            logger.info("Map generated for ambient: {} (use {}/{})",
                ambient.getName(),
                currentAmbientUseCount,
                MAX_AMBIENT_USES);

        } catch (Exception e) {
            logger.error("Error generating map: {}", e.getMessage());
            // Fallback map generation
            map = new int[MAP_HEIGHT][MAP_WIDTH];
            for (int y = 0; y < MAP_HEIGHT; y++) {
                for (int x = 0; x < MAP_WIDTH; x++) {
                    map[y][x] = (x == 0 || y == 0 || x == MAP_WIDTH - 1 || y == MAP_HEIGHT - 1) ? TILE_WALL : TILE_GRASS;
                }
            }
        }
    }

    // Update textures when changing ambient
    private void updateTextures(Ambient newAmbient) {
        if (floorTexture != null) floorTexture.dispose();
        if (wallTexture != null) wallTexture.dispose();
        if (sidebarTexture != null) sidebarTexture.dispose();

        floorTexture = newAmbient.getFloorTexture();
        wallTexture = newAmbient.getWallTexture();
        sidebarTexture = newAmbient.getSidebarTexture();
    }

    // Get next ambient that is different from current
    private Ambient getNextAmbient() {
        // Get current ambient name
        String currentName = ambient.getName();

        // Define all available ambient types
        Ambient[] ambients = {
            new Jungle(),
            new Cave(),
            new LakeRiver(),
            new Mountain(),
            new Ruins()
        };

        // Filter out current ambient
        List<Ambient> availableAmbients = new ArrayList<>();
        for (Ambient a : ambients) {
            if (!a.getName().equals(currentName)) {
                availableAmbients.add(a);
            }
        }

        // Pick a random ambient from available options
        return availableAmbients.get(new Random().nextInt(availableAmbients.size()));
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

    // Fix deer spawning to only occur in appropriate ambients
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

    // Only allow deer in specific ambients
    private boolean canSpawnDeerInCurrentAmbient() {
        String ambientName = ambient.getName();
        return ambientName.equals("Jungle") || ambientName.equals("Mountain");
    }

    // Fix cannibal spawning to only occur in caves
    private void regenerateCannibals() {
        cannibals = new ArrayList<>();
        String ambientName = ambient.getName();

        if (ambientName.equals("Cave")) {
            for (int i = 0; i < 3; i++) {
                int x, y;
                do {
                    x = (int)(Math.random() * MAP_WIDTH);
                    y = (int)(Math.random() * MAP_HEIGHT);
                } while (!isValidCaveSpawnTile(x, y));

                Cannibal cannibal = new Cannibal();
                cannibal.getPosition().set(x * TILE_SIZE, y * TILE_SIZE);
                cannibals.add(cannibal);
            }
        }
    }

    // Gera 2 portas em locais acessíveis
    private void generateCaveDoors() {
        int doorsPlaced = 0;
        int attempts = 0;
        while (doorsPlaced < 2 && attempts < 1000) {
            int x = (int) (Math.random() * MAP_WIDTH);
            int y = (int) (Math.random() * MAP_HEIGHT);

            if (map[y][x] == TILE_WALL && hasAdjacentCave(y, x)) {
                map[y][x] = TILE_DOOR;
                doorsPlaced++;
            }
            attempts++;
        }

        // Remove isolated doors
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                if (map[y][x] == TILE_DOOR) {
                    boolean hasCaveNearby = false;
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            if (dx == 0 && dy == 0) continue;
                            int nx = x + dx;
                            int ny = y + dy;
                            if (nx >= 0 && nx < MAP_WIDTH && ny >= 0 && ny < MAP_HEIGHT) {
                                if (map[ny][nx] == TILE_CAVE) {
                                    hasCaveNearby = true;
                                    break;
                                }
                            }
                        }
                        if (hasCaveNearby) break;
                    }
                    if (!hasCaveNearby) {
                        map[y][x] = TILE_WALL;
                    }
                }
            }
        }
    }

    // Verifica se o tile ao lado é CAVE (chão jogável)
    private boolean hasAdjacentCave(int y, int x) {
        return (x > 0 && map[y][x - 1] == TILE_CAVE) ||
            (x < MAP_WIDTH - 1 && map[y][x + 1] == TILE_CAVE) ||
            (y > 0 && map[y - 1][x] == TILE_CAVE) ||
            (y < MAP_HEIGHT - 1 && map[y + 1][x] == TILE_CAVE);
    }

    private void setPlayerPosition() {
        try {
            // Only set player position if not already spawned for this map
            if (!playerSpawned) {
                int x, y;
                int attempts = 0;
                int maxAttempts = 1000;
                boolean positionFound = false;

                do {
                    x = (int)(Math.random() * MAP_WIDTH);
                    y = (int)(Math.random() * MAP_HEIGHT);
                    attempts++;

                    int tileType = map[y][x];
                    boolean isValidTile = (tileType == TILE_GRASS || (ambient.getName().equals("Cave") && tileType == TILE_CAVE));

                    if (isValidTile) {
                        playerPos.set(x * TILE_SIZE, y * TILE_SIZE);
                        positionFound = true;
                        playerSpawned = true;
                    }
                } while (!positionFound && attempts < maxAttempts);

                if (!positionFound) {
                    // Fallback to center of map
                    playerPos.set(MAP_WIDTH / 2 * TILE_SIZE, MAP_HEIGHT / 2 * TILE_SIZE);
                    playerSpawned = true;
                    logger.warn("Could not find valid player spawn position, using fallback");
                }

                character.getPosition().x = x;
                character.getPosition().y = y;
                logger.info("Player spawned at ({}, {})", playerPos.x/TILE_SIZE, playerPos.y/TILE_SIZE);
            }
        } catch (Exception e) {
            logger.error("Error setting player position: {}", e.getMessage());
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
        return x > 0 && x < MAP_WIDTH - 1 && y > 0 && y < MAP_HEIGHT - 1 &&
            (map[y][x] == TILE_GRASS || (ambient.getName().equals("Cave") && map[y][x] == TILE_CAVE));
    }

    private boolean hasAdjacentFloor(int y, int x) {
        return (y > 0 && map[y - 1][x] == TILE_GRASS) ||
            (y < MAP_HEIGHT - 1 && map[y + 1][x] == TILE_GRASS) ||
            (x > 0 && map[y][x - 1] == TILE_GRASS) ||
            (x < MAP_WIDTH - 1 && map[y][x + 1] == TILE_GRASS);
    }

    // Método para contar quantas portas existem
    private int countDoors() {
        int count = 0;
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                if (map[y][x] == TILE_DOOR) {
                    count++;
                }
            }
        }
        return count;
    }

    // Método para adicionar portas em locais válidos
    private void addDoors(int doorsToAdd) {
        int attempts = 0;
        while (doorsToAdd > 0 && attempts < 1000) { // Limite de tentativas
            int x = (int)(Math.random() * MAP_WIDTH);
            int y = (int)(Math.random() * MAP_HEIGHT);

            // Condições para colocar porta:
            if (map[y][x] == TILE_WALL && isAdjacentToCave(x, y)) {
                map[y][x] = TILE_DOOR;
                doorsToAdd--;
            }
            attempts++;
        }
    }

    // Verificar se a parede está encostada na área acessível (TILE_CAVE)
    private boolean isAdjacentToCave(int x, int y) {
        if (x > 0 && map[y][x - 1] == TILE_CAVE) return true;
        if (x < MAP_WIDTH - 1 && map[y][x + 1] == TILE_CAVE) return true;
        if (y > 0 && map[y - 1][x] == TILE_CAVE) return true;
        if (y < MAP_HEIGHT - 1 && map[y + 1][x] == TILE_CAVE) return true;
        return false;
    }


    private void movePlayer(float delta) {
        try {
            float speed = character.getSpeed() > 0 ? character.getSpeed() : 100f;
            float nextX = playerPos.x, nextY = playerPos.y;
            boolean isMovingNow = false;

            // Track movement and direction
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                nextY += speed * delta;
                lastDirection = Direction.UP;
                isMovingNow = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                nextY -= speed * delta;
                lastDirection = Direction.DOWN;
                isMovingNow = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                nextX -= speed * delta;
                lastDirection = Direction.LEFT;
                isMovingNow = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                nextX += speed * delta;
                lastDirection = Direction.RIGHT;
                isMovingNow = true;
            }

            // Update animation state based on movement
            isMoving = isMovingNow;
            updatePlayerState();

            // Continue with collision detection and movement
            int tileX = (int) ((nextX + 8) / TILE_SIZE), tileY = (int) ((nextY + 8) / TILE_SIZE);
            if (tileX >= 0 && tileX < MAP_WIDTH && tileY >= 0 && tileY < MAP_HEIGHT) {
                int tileType = map[tileY][tileX];
                if (tileType != TILE_WALL) {
                    playerPos.set(nextX, nextY);
                    character.getPosition().set(nextX, nextY);
                }
                if (tileType == TILE_DOOR) {
                    generateMap();
                    if (ambient.getName().toLowerCase().contains("cave")) {
                        generateCaveDoors();
                    }
                    setPlayerPosition();
                    regenerateDeers();
                    regenerateCannibals();

                    if (ambient.getName().toLowerCase().contains("cave")) {
                        materiaisNoMapa = Material.spawnSmallRocks(3, map, MAP_WIDTH, MAP_HEIGHT, TILE_CAVE, TILE_SIZE);
                    } else if (ambient.getName().toLowerCase().contains("forest") || ambient.getName().toLowerCase().contains("plains")) {
                        materiaisNoMapa = Material.spawnSticksAndRocks(5, map, MAP_WIDTH, MAP_HEIGHT, TILE_GRASS, TILE_SIZE);
                    } else {
                        materiaisNoMapa = new ArrayList<>();
                    }

                    autosaveGame();
                }
            }

            updateCharacterStats(delta);

            // Handle material collection
            Iterator<Material> materialIter = materiaisNoMapa.iterator();
            while (materialIter.hasNext()) {
                Material material = materialIter.next();
                if (character.getPosition().dst(material.getPosition()) < TILE_SIZE) {
                    if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                        character.getInventory().add(material);
                        materialIter.remove();
                        System.out.println("Você coletou: " + material.getName());
                    }
                }
            }
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
                    } else if (map[y][x] == TILE_CAVE) {
                        batch.draw(floorTexture, dx, dy);
                    } else if (map[y][x] == TILE_WALL) {
                        batch.setColor(0.4f, 0.4f, 0.4f, 1f); // Paredes mais escuras (cinza escuro)
                        batch.draw(floorTexture, dx, dy);
                        batch.setColor(1, 1, 1, 1); // Resetar cor para não afetar o resto
                    } else if (map[y][x] == TILE_DOOR) {
                        batch.setColor(0, 0, 0, 1f); // Porta fica preta
                        batch.draw(floorTexture, dx, dy);
                        batch.setColor(1, 1, 1, 1);
                    }
                }
            }




            renderPlayer(batch);
            batch.draw(backpackIcon, bx, by, size, size);



            for (Deer deer : deers) {
                Sprite deerSprite = deer.getSprites().get("idle");
                if (deerSprite != null) {
                    deerSprite.setSize(50, 50);
                    deerSprite.setPosition(
                        deer.getPosition().x + offsetX + (TILE_SIZE - deerSprite.getWidth()) / 2,
                        deer.getPosition().y + offsetY + (TILE_SIZE - deerSprite.getHeight()) / 2
                    );
                    deerSprite.draw(batch);
                }
            }


// Renderizar os canibais
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

            for (Material material : materiaisNoMapa) {
                Sprite sprite = material.getSprites().get("idle");
                if (sprite != null) {
                    sprite.setSize(32, 32);
                    sprite.setPosition(
                        material.getPosition().x + offsetX + (TILE_SIZE - sprite.getWidth()) / 2,
                        material.getPosition().y + offsetY + (TILE_SIZE - sprite.getHeight()) / 2
                    );
                    sprite.draw(batch);
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





