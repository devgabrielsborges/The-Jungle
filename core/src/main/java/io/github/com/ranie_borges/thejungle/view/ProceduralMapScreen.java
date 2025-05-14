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
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.entity.itens.CraftManager;
import io.github.com.ranie_borges.thejungle.model.entity.itens.CraftingBar;
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
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Input;


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

    private float stateTime = 0;
    private boolean isMoving = false;
    private CraftingBar craftingBar;






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
            if (!playerSpawned) {
                playerSpawned = character.setInitialSpawn(map, MAP_WIDTH, MAP_HEIGHT, TILE_SIZE, TILE_GRASS, TILE_CAVE, ambient.getName());
            }

            character.loadPlayerAnimations();
            character.updateStateTime(0f);

            if (ambient.getName().toLowerCase().contains("cave")) {
                generateCaveDoors();
            }
            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            deers = Creature.regenerateCreatures(
                5,
                map,
                MAP_WIDTH,
                MAP_HEIGHT,
                TILE_GRASS,
                TILE_SIZE,
                Deer::new,
                ambient,
                Deer::canSpawnIn
            );

            cannibals = Creature.regenerateCreatures(
                3,
                map,
                MAP_WIDTH,
                MAP_HEIGHT,
                TILE_CAVE,
                TILE_SIZE,
                Cannibal::new,
                ambient,
                Cannibal::canSpawnIn
            );

            materiaisNoMapa = Material.spawnSmallRocks(3, map, MAP_WIDTH, MAP_HEIGHT, TILE_CAVE, TILE_SIZE);

        } catch (Exception e) {
            logger.error("Error initializing ProceduralMapScreen: {}", e.getMessage());
            throw e;
        }
        character.updateStateTime(0f);
        this.craftingBar = new CraftingBar();

    }

    public void generateMap() {
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

        // Verifica clique do mouse para arrastar e soltar
        if (Gdx.input.justTouched()) {
            int mouseX = Gdx.input.getX();
            int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    float sx = startX + col * (slotSize + padding);
                    float sy = startY + (rows - row - 1) * (slotSize + padding);
                    int slotIndex = row * cols + col;
                }
            }
        }

        batch.begin();
        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float sx = startX + col * (slotSize + padding);
                float sy = startY + (rows - row - 1) * (slotSize + padding);
                int slotIndex = row * cols + col;

                if (slotIndex < character.getInventory().size && character.getInventory().get(slotIndex) != null) {
                    Item item = character.getInventory().get(slotIndex);
                    Texture icon = item.getIconTexture();
                    if (icon != null) {
                        batch.draw(icon, sx + 8, sy + 8, slotSize - 16, slotSize - 16);
                    }

                    // Mostra quantidade sempre
                    String qtd = "x" + item.getQuantity();
                    layout.setText(font, qtd);
                    font.draw(batch, qtd, sx + slotSize - layout.width - 4, sy + 16);

                    // Mostra nome apenas se o mouse estiver sobre o slot
                    if (mouseX >= sx && mouseX <= sx + slotSize && mouseY >= sy && mouseY <= sy + slotSize) {
                        String itemName = item.getName();
                        layout.setText(font, itemName);
                        font.draw(batch, itemName, sx + (slotSize - layout.width) / 2, sy + slotSize + 12);
                    }
                }
            }
        }

        batch.end();
    }

    @Override
    public void render(float delta) {
        try {
            // 1) Atualiza movimentação do personagem
            boolean passedThroughDoor = character.tryMove(
                delta, map, TILE_SIZE, TILE_WALL, TILE_DOOR, TILE_CAVE, MAP_WIDTH, MAP_HEIGHT
            );

            if (passedThroughDoor) {
                generateMap();
                // Garante spawn seguro do personagem após trocar de mapa
                boolean spawnEncontrado = false;
                int tentativas = 0;
                int maxTentativas = 1000;

                while (!spawnEncontrado && tentativas < maxTentativas) {
                    int x = (int)(Math.random() * MAP_WIDTH);
                    int y = (int)(Math.random() * MAP_HEIGHT);
                    int tile = map[y][x];

                    boolean tileEhValido = tile == TILE_GRASS || (ambient instanceof Cave && tile == TILE_CAVE);

                    if (tileEhValido) {
                        character.getPosition().set(x * TILE_SIZE, y * TILE_SIZE);
                        spawnEncontrado = true;
                    }

                    tentativas++;
                }

                if (!spawnEncontrado) {
                    // Fallback para o meio do mapa
                    character.getPosition().set((MAP_WIDTH / 2) * TILE_SIZE, (MAP_HEIGHT / 2) * TILE_SIZE);
                    logger.warn("Não foi possível encontrar um spawn seguro após {} tentativas. Usando fallback central.", maxTentativas);
                }
                if (ambient.getName().toLowerCase().contains("cave")) {
                    generateCaveDoors();
                }
                deers = Creature.regenerateCreatures(
                    5,
                    map,
                    MAP_WIDTH,
                    MAP_HEIGHT,
                    TILE_GRASS,
                    TILE_SIZE,
                    Deer::new,
                    ambient,
                    Deer::canSpawnIn
                );
                cannibals = Creature.regenerateCreatures(
                    3,
                    map,
                    MAP_WIDTH,
                    MAP_HEIGHT,
                    TILE_CAVE,
                    TILE_SIZE,
                    Cannibal::new,
                    ambient,
                    Cannibal::canSpawnIn
                );
                // Materiais
                if (ambient instanceof Cave) {
                    materiaisNoMapa = Material.spawnSmallRocks(3, map, MAP_WIDTH, MAP_HEIGHT, TILE_CAVE, TILE_SIZE);
                } else if (ambient instanceof Jungle || ambient instanceof LakeRiver || ambient instanceof Ruins) {
                    materiaisNoMapa = Material.spawnSticksAndRocks(5, map, MAP_WIDTH, MAP_HEIGHT, TILE_GRASS, TILE_SIZE);
                } else {
                    materiaisNoMapa = new ArrayList<>();
                }
                autosaveGame();
            }
            character.updateStateTime(delta);

            // 2) Blink do ícone de inventário
            blinkTimer += delta;
            if (blinkTimer >= BLINK_INTERVAL) {
                blinkVisible = !blinkVisible;
                blinkTimer = 0f;
            }

            // 3) Checa se clicou no inventário
            float size = 96;
            float bx   = Gdx.graphics.getWidth()  - SIDEBAR_WIDTH + (SIDEBAR_WIDTH - size) / 2f;
            float by   = 30;
            int mouseX = Gdx.input.getX();
            int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
            boolean mouseOverBackpack = mouseX >= bx && mouseX <= bx + size
                && mouseY >= by && mouseY <= by + size;
            if ((Gdx.input.justTouched() && mouseOverBackpack)
                || Gdx.input.isKeyJustPressed(Input.Keys.I)) {
                showInventory = !showInventory;
            }

            // 4) Clear e begin batch
            ScreenUtils.clear(0, 0, 0, 1);
            batch.begin();

            // 5) Desenha sidebar
            batch.draw(sidebarTexture, 0, 0, SIDEBAR_WIDTH, Gdx.graphics.getHeight());
            batch.draw(sidebarTexture,
                Gdx.graphics.getWidth() - SIDEBAR_WIDTH,
                0,
                SIDEBAR_WIDTH,
                Gdx.graphics.getHeight());

            // 6) Texto do ambiente e personagem
            layout.setText(font, ambient.getName().toUpperCase());
            font.draw(batch,
                ambient.getName().toUpperCase(),
                Gdx.graphics.getWidth() - SIDEBAR_WIDTH + (SIDEBAR_WIDTH - layout.width) / 2f,
                Gdx.graphics.getHeight() - 20);
            batch.draw(classIcon, 20, Gdx.graphics.getHeight() - 360, 260, 300);
            layout.setText(font, character.getName());
            font.draw(batch,
                character.getName(),
                SIDEBAR_WIDTH / 2f - layout.width / 2f,
                Gdx.graphics.getHeight() - 380);

            // 7) Barras de status (labels)
            float barX   = 30;
            float baseY  = Gdx.graphics.getHeight() - 450;
            float spacing= 60;
            font.draw(batch, "Life",   barX, baseY + 45);
            font.draw(batch, "Hunger", barX, baseY - spacing + 45);
            font.draw(batch, "Thirst", barX, baseY - spacing*2 + 45);
            font.draw(batch, "Sanity", barX, baseY - spacing*3 + 45);
            font.draw(batch, "Energy", barX, baseY - spacing*4 + 45);
            font.draw(batch,
                "Days: " + gameState.getDaysSurvived(),
                Gdx.graphics.getWidth() - SIDEBAR_WIDTH + 20,
                Gdx.graphics.getHeight() - 60);

            // 8) Desenha o mapa
            for (int y = 0; y < MAP_HEIGHT; y++) {
                for (int x = 0; x < MAP_WIDTH; x++) {
                    float dx = x * TILE_SIZE + offsetX;
                    float dy = y * TILE_SIZE + offsetY;
                    switch (map[y][x]) {
                        case TILE_GRASS:
                        case TILE_CAVE:
                            batch.draw(floorTexture, dx, dy);
                            break;
                        case TILE_WALL:
                            batch.setColor(0.4f, 0.4f, 0.4f, 1f);
                            batch.draw(floorTexture, dx, dy);
                            batch.setColor(Color.WHITE);
                            break;
                        case TILE_DOOR:
                            batch.setColor(0, 0, 0, 1f);
                            batch.draw(floorTexture, dx, dy);
                            batch.setColor(Color.WHITE);
                            break;
                    }
                }
            }

            // 9) Ícone de mochila
            batch.draw(backpackIcon, bx, by, size, size);

            // 10) Entidades (veados, canibais, materiais)
            for (Deer deer : deers) {
                Sprite s = deer.getSprites().get("idle");
                if (s != null) {
                    s.setSize(50,50);
                    s.setPosition(
                        deer.getPosition().x + offsetX + (TILE_SIZE - s.getWidth())/2,
                        deer.getPosition().y + offsetY + (TILE_SIZE - s.getHeight())/2
                    );
                    s.draw(batch);
                }
            }
            for (Cannibal c : cannibals) {
                Sprite s = c.getSprites().get("idle");
                if (s != null) {
                    s.setSize(40,40);
                    s.setPosition(
                        c.getPosition().x + offsetX + (TILE_SIZE - s.getWidth())/2,
                        c.getPosition().y + offsetY + (TILE_SIZE - s.getHeight())/2
                    );
                    s.draw(batch);
                }
            }
            for (Material m : materiaisNoMapa) {
                Sprite s = m.getSprites().get("idle");
                if (s != null) {
                    s.setSize(32,32);
                    s.setPosition(
                        m.getPosition().x + offsetX + (TILE_SIZE - s.getWidth())/2,
                        m.getPosition().y + offsetY + (TILE_SIZE - s.getHeight())/2
                    );
                    s.draw(batch);
                }
            }

            // 10) Blink do “i”
            if (blinkVisible || mouseOverBackpack) {
                layout.setText(font, "i");
                font.setColor(mouseOverBackpack ? Color.YELLOW : Color.GREEN);
                font.draw(batch,
                    "i",
                    bx + size/2f - layout.width/2f,
                    by + size/2.3f);
                font.setColor(Color.WHITE);
            }

            TextureRegion frame = character.getCurrentFrame();
            batch.draw(
                frame,
                character.getPosition().x + offsetX,
                character.getPosition().y + offsetY,
                frame.getRegionWidth(),
                frame.getRegionHeight()
            );

            batch.end();

            // 11) Barras de vida, fome etc
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            drawBar(Color.RED,    character.getLife()   /100f, barX, baseY);
            drawBar(Color.ORANGE, character.getHunger()/100f, barX, baseY - spacing);
            drawBar(Color.BLUE,   character.getThirsty()/100f,barX, baseY - spacing*2);
            drawBar(Color.CYAN,   character.getSanity()/100f, barX, baseY - spacing*3);
            drawBar(Color.YELLOW, character.getEnergy()/100f, barX, baseY - spacing*4);
            shapeRenderer.end();

            // 12) Inventário e game-over
            if (showInventory) renderInventoryWindow();
            if (showInventory) {
                craftingBar.render(batch, shapeRenderer, character, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }

            if (character.getLife() <= 0) gameOver();

        } catch (Exception e) {
            logger.error("Error in render: {}", e.getMessage());
        }
        // 13) Pega item no mapa
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            character.tryCollectNearbyMaterial(materiaisNoMapa);
        }
        if (showInventory) {
            renderInventoryWindow();
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
        if (craftingBar != null) craftingBar.dispose();

    }
}





