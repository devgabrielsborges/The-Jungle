package io.github.com.ranie_borges.thejungle.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import io.github.com.ranie_borges.thejungle.controller.*;
import io.github.com.ranie_borges.thejungle.controller.managers.CharacterManager;
import io.github.com.ranie_borges.thejungle.controller.managers.GameStateManager;
import io.github.com.ranie_borges.thejungle.controller.managers.MapManager;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.Cannibal;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.Deer;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.Fish;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.NPC;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.Boat;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Material;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Medicine;
import io.github.com.ranie_borges.thejungle.model.events.events.SnakeEventManager;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Jungle;
import io.github.com.ranie_borges.thejungle.model.world.ambients.LakeRiver;
import io.github.com.ranie_borges.thejungle.view.helpers.GameRenderHelper;
import io.github.com.ranie_borges.thejungle.view.helpers.LightingManager;
import io.github.com.ranie_borges.thejungle.view.helpers.TextureManager;
import io.github.com.ranie_borges.thejungle.view.interfaces.UI;
import io.github.com.ranie_borges.thejungle.core.Main;
import com.badlogic.gdx.audio.Sound;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Mountain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ProceduralMapScreen implements Screen, UI {
    private static final Logger logger = LoggerFactory.getLogger(ProceduralMapScreen.class);

    private final Main game;
    private final GameState gameState;
    private final MapManager mapManager;
    private final CharacterManager characterManager;
    private final ResourceController resourceController; // Ensure this is initialized
    private final GameStateManager gameStateManager;
    private GameRenderHelper renderHelper;
    private TurnController turnController;

    private final Character character;
    private Ambient ambient;
    private int[][] map;
    private List<Material> materiaisNoMapa = new ArrayList<>();
    private List<Deer> deers = new ArrayList<>();
    private List<Cannibal> cannibals = new ArrayList<>();
    private List<Fish> fishes = new ArrayList<>();
    private List<NPC> NPCS = new ArrayList<>();
    private List<Boat> boats = new ArrayList<>();

    private static final float NPC_INTERACTION_RADIUS = TILE_SIZE * 1.5f;
    private static final float Boat_INTERACTION_RADIUS = TILE_SIZE * 1.5f;


    private Texture classIcon;
    private Texture inventoryBackground, backpackIcon;
    private TextureManager textureManager;
    private LightingManager lightingManager;
    private CraftingBar craftingBar;
    private Hud hud;
    private CharacterUI characterUI;
    private Stage stage;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private Skin skin;

    private static Texture bgHudShared;
    private BitmapFont promptFont;
    private GlyphLayout promptLayoutInstance;

    private boolean showInventory = false;
    private float blinkTimer = 0f;
    private boolean blinkVisible = true;
    private boolean playerSpawned = false;
    private boolean mapTransitionTriggered = false;
    private boolean gameOverTriggered = false;


    private float offsetX = 0;
    private float offsetY = 0;


    private transient Sound eruptionSound;
    private long eruptionSoundId = -1;
    private boolean isEruptionSoundActive = false;
    private float timeSinceLastEruptionSoundCheck = 0f;
    private transient Random eventRandomizer = new Random(); // Um único Random para todos os eventos da tela

    private static final float ERUPTION_SOUND_CHECK_INTERVAL = 20f; // Intervalo em segundos
    private static final float ERUPTION_SOUND_START_CHANCE = 0.20f; // 20% de chance de começar
    private static final float ERUPTION_SOUND_STOP_CHANCE = 0.35f;  // 35% de chance de parar após duração mínima
    private static final float MIN_ERUPTION_SOUND_DURATION = 20f;   // Duração mínima em segundos
    private float currentEruptionSoundActiveTimer = 0f;

    private BattleScreen battleScreen;
    private boolean isBattleActive = false;
    private boolean skipMapRegeneration = false;

    public ProceduralMapScreen(Main game, GameState gameState, Character character, Ambient ambient) {
        this.game = game;

        if (gameState == null) {
            logger.error("GameState is null in ProceduralMapScreen constructor. This is a critical error.");
            throw new IllegalArgumentException("GameState cannot be null");
        }
        this.gameState = gameState;

        if (character == null) {
            character = this.gameState.getPlayerCharacter();
            if (character == null) {
                throw new IllegalArgumentException("Character cannot be null and is not in GameState.");
            }
        }
        this.character = character;
        this.gameState.setCharacter(this.character);

        Ambient initialAmbient = ambient;
        if (initialAmbient == null) initialAmbient = this.gameState.getCurrentAmbient();
        if (initialAmbient == null) {
            logger.warn("Initial ambient is null, defaulting to Jungle for ProceduralMapScreen setup.");
            initialAmbient = new Jungle();
        }
        this.ambient = initialAmbient;
        this.gameState.setCurrentAmbient(this.ambient);

        logger.info("ProceduralMapScreen constructor: Initializing with Ambient: {}", this.ambient.getName());

        if (this.gameState.getMapManager() != null) {
            this.mapManager = this.gameState.getMapManager();
            this.mapManager.externallySetCurrentAmbient(this.ambient);
        } else {
            this.mapManager = new MapManager(this.ambient);
            this.gameState.setMapManager(this.mapManager);
        }

        this.characterManager = new CharacterManager(this.character, this.ambient);
        this.resourceController = new ResourceController(); // Make sure this is initialized
        this.gameStateManager = new GameStateManager(this.gameState);
        this.renderHelper = new GameRenderHelper();

        if (this.game != null && this.game.getScenarioController() != null) {
            this.turnController = new TurnController(this.gameState, this.game.getScenarioController());
        } else {
            logger.error("Main game instance or its ScenarioController is null. TurnController may not function correctly.");
            this.turnController = new TurnController(this.gameState, null); // Fallback
        }
    }


    public MapManager getMapManager() {
        return this.mapManager;
    }

    public void setMapTransitionTriggered(boolean triggered) {
        this.mapTransitionTriggered = triggered;
        logger.debug("ProceduralMapScreen: mapTransitionTriggered set to {}", triggered);
    }

    @Override
    public void show() {
        try {
            stage = new Stage();
            Gdx.input.setInputProcessor(stage);
            skin = new Skin(Gdx.files.internal("mainMenu/uiskin.json"));

            turnController.setUI(stage, skin, this);

            textureManager = new TextureManager();
            logger.info("TextureManager initialized in show().");

            lightingManager = new LightingManager();
            craftingBar = new CraftingBar();

            if (character != null) {
                classIcon = textureManager.loadClassIcon(character.getCharacterType());
                character.loadPlayerAnimations();
                character.updateStateTime(0f);
            } else {
                logger.error("Character is null in show(), cannot load character-specific textures/animations.");
            }

            inventoryBackground = textureManager.getOrLoadTexture("Gameplay/backpackInside.png");
            backpackIcon = textureManager.getOrLoadTexture("Gameplay/backpack.png");

            if (bgHudShared == null || bgHudShared.toString().contains("unknown")) {
                try {
                    bgHudShared = new Texture(Gdx.files.internal("GameScreen/boxhud.png"));
                } catch (Exception e) {
                    logger.error("Failed to load shared boxhud.png", e);
                }
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

            if (eventRandomizer == null) {
                eventRandomizer = new Random();
            }

            // Carregar o som da erupção
            if (eruptionSound == null) {
                try {
                    eruptionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/eruption.ogg"));
                    logger.info("Som de erupção carregado.");
                } catch (Exception e) {
                    logger.error("Falha ao carregar o som de erupção: {}", e.getMessage(), e);
                    eruptionSound = null;
                }
            }

            if (this.gameState.getCurrentMap() != null && this.gameState.getCurrentMap().length > 0 && this.gameState.getCurrentAmbient() != null) {
                mapManager.externallySetCurrentAmbient(this.gameState.getCurrentAmbient());
                mapManager.setCurrentMap(this.gameState.getCurrentMap());
                logger.info("Show(): MapManager synced with GameState. Current Ambient: {}, Map set.",
                    mapManager.getCurrentAmbient().getName());
            } else {
                logger.info("Show(): GameState has no map or ambient. MapManager (for {}) will generate initial map.",
                    mapManager.getCurrentAmbient() != null ? mapManager.getCurrentAmbient().getName() : "a default ambient");
                if (mapManager.getCurrentAmbient() == null) mapManager.externallySetCurrentAmbient(new Jungle());
                mapManager.generateMapForCurrentAmbient();
                this.gameState.setCurrentMap(mapManager.getMap());
                this.gameState.setCurrentAmbient(mapManager.getCurrentAmbient());
            }

            updateScreenMapAndEntities();

            if (character != null) {
                character.updateStateTime(0f);
            }

            if (textureManager != null && classIcon != null && font != null && this.gameState.getChatController() != null) {
                Texture sidebarTex = textureManager.getSidebarTexture();
                if (sidebarTex == null && mapManager.getCurrentAmbient() != null) {
                    textureManager.loadAmbientTextures(mapManager.getCurrentAmbient());
                    sidebarTex = textureManager.getSidebarTexture();
                }
                if (sidebarTex == null) {
                    sidebarTex = textureManager.getOrLoadTexture("Gameplay/sidebar.jpg");
                }
                hud = new Hud(sidebarTex, classIcon, font, this.gameState.getChatController());
            } else {
                logger.error("Cannot initialize HUD due to null components (textureManager, classIcon, font, or chatController).");
            }

            if (inventoryBackground != null && font != null) {
                characterUI = new CharacterUI(inventoryBackground, font);
            } else {
                logger.error("Cannot initialize CharacterUI due to null components.");
            }
            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } catch (Exception e) {
            logger.error("CRITICAL Error initializing ProceduralMapScreen: {}", e.getMessage(), e);
            if (game != null) {
                if (game.getScreen() != null) game.getScreen().dispose();
                game.setScreen(new MainMenuScreen(game));
            }
        }
    }
    private void updateEruptionSoundEvent(float delta) {
        // Se o som não foi carregado, não faz nada
        if (eruptionSound == null) return;

        // Parar o som imediatamente se não estiver na montanha ou se o jogo acabou/transição
        if (isEruptionSoundActive && eruptionSoundId != -1 &&
            (!(this.ambient instanceof Mountain) || gameOverTriggered || mapTransitionTriggered)) {
            eruptionSound.stop(eruptionSoundId);
            isEruptionSoundActive = false;
            eruptionSoundId = -1;
            currentEruptionSoundActiveTimer = 0f;
            logger.info("Som de erupção interrompido (ambiente mudou ou estado de jogo mudou).");
            // Opcional: mensagem no chat que o perigo sonoro passou ao sair da montanha
            if (this.gameState != null && this.gameState.getChatController() != null && !(this.ambient instanceof Mountain)) {
                this.gameState.getChatController().addMessage("Os ruídos ameaçadores da montanha ficaram para trás.", Color.GRAY);
            }
            return; // Retorna cedo se o som foi parado por sair da montanha
        }

        // Não processa nova lógica de som se não estiver na montanha ou se o jogo não estiver ativo
        if (!(this.ambient instanceof Mountain) || gameOverTriggered || mapTransitionTriggered) {
            return;
        }

        timeSinceLastEruptionSoundCheck += delta;

        if (isEruptionSoundActive) {
            currentEruptionSoundActiveTimer -= delta;
            if (currentEruptionSoundActiveTimer <= 0) {
                if (eventRandomizer.nextFloat() < ERUPTION_SOUND_STOP_CHANCE) {
                    if (eruptionSoundId != -1) eruptionSound.stop(eruptionSoundId);
                    isEruptionSoundActive = false;
                    eruptionSoundId = -1;
                    logger.info("Som de erupção (aleatoriamente) parado após duração.");
                    if (this.gameState != null && this.gameState.getChatController() != null) {
                        this.gameState.getChatController().addMessage("Os temíveis estrondos da montanha diminuíram...", Color.SLATE);
                    }
                } else {
                    // O som continua por mais um tempo, reseta o timer para uma nova checagem de parada
                    currentEruptionSoundActiveTimer = MIN_ERUPTION_SOUND_DURATION * 0.5f; // Verifica novamente em menos tempo
                }
            }
        } else {
            // Se não estiver tocando e estiver na montanha, verifica se deve começar
            if (timeSinceLastEruptionSoundCheck >= ERUPTION_SOUND_CHECK_INTERVAL) {
                timeSinceLastEruptionSoundCheck = 0f;
                if (eventRandomizer.nextFloat() < ERUPTION_SOUND_START_CHANCE) {
                    eruptionSoundId = eruptionSound.loop(1f); // Volume (0.0 a 1.0)
                    if (eruptionSoundId == -1) {
                        logger.warn("Falha ao iniciar o som de erupção em loop (ID -1). Verifique limites de som.");
                        return;
                    }
                    isEruptionSoundActive = true;
                    currentEruptionSoundActiveTimer = MIN_ERUPTION_SOUND_DURATION;
                    logger.info("Som de erupção iniciado na Montanha (ID: {}).", eruptionSoundId);
                    if (this.gameState != null && this.gameState.getChatController() != null) {
                        this.gameState.getChatController().addMessage("Um estrondo profundo ecoa... A montanha parece instável.", Color.FIREBRICK);
                    }
                }
            }
        }
    }
    public void updateScreenMapAndEntities() {
        this.map = mapManager.getMap();
        this.ambient = mapManager.getCurrentAmbient();

        if (this.map != null) this.gameState.setCurrentMap(this.map);
        if (this.ambient != null) this.gameState.setCurrentAmbient(this.ambient);

        logger.info("Updating screen entities for ambient: {}", this.ambient != null ? this.ambient.getName() : "UNKNOWN AMBIENT");

        updateTextures(this.ambient);

        if (lightingManager != null) lightingManager.initializeBuffer();

        if (characterManager != null) {
            characterManager.setMap(this.map);
            characterManager.setCurrentAmbient(this.ambient);
        }

        if (character != null) {
            if (!playerSpawned) {
                playerSpawned = character.setInitialSpawn(map, MAP_WIDTH, MAP_HEIGHT, TILE_SIZE, TILE_GRASS, TILE_CAVE, ambient != null ? ambient.getName() : "DefaultAmbient", ambient);
            } else {
                if (characterManager != null) characterManager.safeSpawnCharacter();
            }
        } else {
            logger.error("Character is null during updateScreenMapAndEntities, cannot spawn.");
        }

        if (this.ambient != null && this.ambient.getName().toLowerCase().contains("cave")) {
            mapManager.generateCaveDoors();
            this.map = mapManager.getMap();
            this.gameState.setCurrentMap(this.map);
        }

        // Ensure resourceController is not null before using it
        if (resourceController != null && this.ambient != null && this.map != null) {
            logger.info("Spawning resources, creatures, and fish for ambient: {}", this.ambient.getName());
            deers = resourceController.spawnCreatures(this.ambient, this.map);
            cannibals = resourceController.spawnCannibals(this.ambient, this.map);
            materiaisNoMapa = resourceController.spawnResources(this.ambient, this.map); // This should return a list of Material objects
            fishes = resourceController.spawnFish(this.ambient, this.map);
            NPCS = resourceController.spawnNPC(this.ambient, this.map);
            boats = resourceController.spawnBoat(this.ambient, this.map);



            // Initialize sprites for all spawned materials
            if (materiaisNoMapa != null && textureManager != null) {
                logger.info("Initializing sprites for {} materials on map.", materiaisNoMapa.size());
                for (Material material : materiaisNoMapa) {
                    if (material != null) {
                        logger.debug("Initializing sprites for material: {}", material.getName());
                        material.initializeSprites(this.textureManager);
                    } else {
                        logger.warn("Encountered a null material in materiaisNoMapa during sprite initialization.");
                    }
                }
                logger.info("Finished initializing material sprites. Count: {}", materiaisNoMapa.size());
            } else {
                if (materiaisNoMapa == null) logger.warn("materiaisNoMapa list is null after spawning, cannot initialize sprites.");
                if (textureManager == null) logger.warn("textureManager is null, cannot initialize material sprites.");
            }

            // Reload sprites for creatures if necessary (though their constructors should handle initial sprite setup)
            if (deers != null) for (Deer deer : deers) if (deer != null) deer.reloadSprites(); else logger.warn("Null deer in list.");
            if (cannibals != null) for (Cannibal cannibal : cannibals) if (cannibal != null) cannibal.reloadSprites(); else logger.warn("Null cannibal in list.");
            if (fishes != null) for (Fish fish : fishes) if (fish != null) fish.reloadSprites(); else logger.warn("Null fish in list.");
            if (NPCS != null) for (NPC npc : NPCS) if (npc != null) npc.reloadSprites(); else logger.warn("Null NPC in list.");
            if (boats != null) for (Boat boat : boats) if (boat != null) boat.reloadSprites(); else logger.warn("Null Boat in list.");

        } else {
            logger.warn("Cannot spawn resources/creatures: resourceController, ambient, or map is null.");
            if (resourceController == null) logger.error("ResourceController is NULL in updateScreenMapAndEntities!");
            if (this.ambient == null) logger.error("Ambient is NULL in updateScreenMapAndEntities!");
            if (this.map == null) logger.error("Map is NULL in updateScreenMapAndEntities!");
        }

        if (gameStateManager != null && character != null && this.ambient != null && this.map != null && !gameOverTriggered) {
            gameStateManager.autosave(character, this.ambient, this.map);
        }
        if (this.gameState.getChatController() != null && !gameOverTriggered && this.ambient != null) {
            this.gameState.getChatController().addMessage("Entered " + this.ambient.getName() + ".");
        }
    }

    private void handleDoorTraversal(Ambient ambientAtDoor) {
        // ... (implementation as before)
        logger.info("Character passed through a door. Ambient at door was: {}", ambientAtDoor != null ? ambientAtDoor.getName() : "UNKNOWN");
        boolean ambientTypeRotated = mapManager.checkAndRotateAmbient();
        Ambient ambientForTurnPrompt = mapManager.getAmbientBeforeRotation();

        if (ambientForTurnPrompt == null) {
            logger.error("Critical: ambientBeforeRotation in MapManager is null during handleDoorTraversal. Defaulting.");
            ambientForTurnPrompt = this.ambient != null ? this.ambient : new Jungle();
        }
        this.gameState.setCurrentAmbient(ambientForTurnPrompt);
        logger.info("handleDoorTraversal: GameState.currentAmbient set to {} for turn decision.", ambientForTurnPrompt.getName());

        if (mapManager.getCurrentAmbient() != ambientForTurnPrompt) {
            logger.info("MapManager has internally rotated its currentAmbient to: {}", mapManager.getCurrentAmbient().getName());
        }

        if (ambientTypeRotated) {
            logger.info("Ambient cycle complete for {}. Prompting user for next action.", ambientForTurnPrompt.getName());
            if (this.gameState.getChatController() != null) {
                this.gameState.getChatController().addMessage("You feel a shift in the environment around " + ambientForTurnPrompt.getName() + ". Choose your next path.", Color.GOLD);
            }
            turnController.advanceTurn();
        } else {
            logger.info("Continuing in ambient type: {}. Generating new map area.", ambientForTurnPrompt.getName());
            mapManager.forceSetCurrentAmbient(ambientForTurnPrompt, false);
            mapManager.generateMapForCurrentAmbient();
            updateScreenMapAndEntities();
            this.mapTransitionTriggered = false;
            if (this.gameState.getChatController() != null) {
                this.gameState.getChatController().addMessage("Moved to a new area in " + ambientForTurnPrompt.getName() + ".");
            }
        }
    }

    private void updateTextures(Ambient newAmbient) {
        // ... (implementation as before)
        if (textureManager != null && newAmbient != null) {
            textureManager.loadAmbientTextures(newAmbient);
            if (hud != null ) {
                Texture sidebarTex = textureManager.getSidebarTexture();
                if (sidebarTex == null) {
                    sidebarTex = textureManager.getOrLoadTexture("Gameplay/sidebar.jpg");
                }
            }
        } else {
            logger.warn("TextureManager or newAmbient is null in updateTextures. Cannot update ambient-specific textures.");
        }
    }

    @Override
    public void render(float delta) {
        if (gameOverTriggered) {
            if (stage != null) { stage.act(delta); stage.draw(); }
            return;
        }
        SnakeEventManager.handleInput();
        if (SnakeEventManager.isWaitingForSpace()) {
            Gdx.gl.glClearColor(0, 0, 0, 1); Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            if (batch != null && renderHelper != null) renderHelper.renderSnakeAlertScreen(batch);
            return;
        }
        boolean shouldUpdateGameLogic = !mapTransitionTriggered && (stage == null || stage.getActors().size == 0);
        if (shouldUpdateGameLogic) {
            try {
                if (characterManager != null && character != null) {
                    boolean passedThroughDoor = characterManager.updateCharacterMovement(delta);
                    if (passedThroughDoor) {
                        if (!this.mapTransitionTriggered) {
                            this.mapTransitionTriggered = true;
                            handleDoorTraversal(this.ambient);
                        }
                    }
                    if (!this.mapTransitionTriggered) {
                        character.updateStateTime(delta);
                        characterManager.updateCharacterStats(delta);
                        if (gameStateManager != null) gameStateManager.update(delta, character, this.ambient, this.map);
                        blinkTimer += delta;
                        if (blinkTimer >= 0.5f) { blinkVisible = !blinkVisible; blinkTimer = 0f; }
                        float size = 96;
                        float backpackX = Gdx.graphics.getWidth() - SIDEBAR_WIDTH + (SIDEBAR_WIDTH - size) / 2f;
                        float backpackY = 30;
                        int mouseX = Gdx.input.getX(); int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
                        boolean mouseOverBackpack = mouseX >= backpackX && mouseX <= backpackX + size && mouseY >= backpackY && mouseY <= backpackY + size;
                        if (Gdx.input.isKeyJustPressed(Input.Keys.I) || (Gdx.input.justTouched() && mouseOverBackpack)) showInventory = !showInventory;
                    }
                    if (character.getLife() <= 0 && !gameOverTriggered) handleGameOver();
                }
            } catch (Exception e) { logger.error("Error in game logic update: {}", e.getMessage(), e); }
        }
        if (batch == null || lightingManager == null || renderHelper == null || textureManager == null || character == null || this.ambient == null || this.map == null) {
            if (stage != null) { stage.act(delta); stage.draw(); }
            return;
        }

        if (gameOverTriggered) {
            if (stage != null) {
                stage.act(delta);
                stage.draw();
            }
            return;
        }

        // Lógica de renderização da tela de batalha
        if (isBattleActive) {
            battleScreen.render(batch, shapeRenderer, character, gameState, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                isBattleActive = false;
            }
            return;
        }

        // Lógica de renderização normal da ProceduralMapScreen
        SnakeEventManager.handleInput();
        if (SnakeEventManager.isWaitingForSpace()) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            if (batch != null && renderHelper != null) {
                renderHelper.renderSnakeAlertScreen(batch);
            }
            return;
        }

        // Verifica se o jogador inicia uma batalha ao pressionar E
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            Creature enemy = null;
            // Verifica se há canibal próximo
            for (Cannibal cannibal : cannibals) {
                if (cannibal != null && character.getPosition().dst(cannibal.getPosition()) < TILE_SIZE * 1.5f) {
                    enemy = cannibal;
                    break;
                }
            }
            // Se não encontrou canibal, procura por cervos
            if (enemy == null) {
                for (Deer deer : deers) {
                    if (deer != null && character.getPosition().dst(deer.getPosition()) < TILE_SIZE * 1.5f) {
                        enemy = deer;
                        break;
                    }
                }
            }
            // Se um inimigo for encontrado, inicia a batalha
            if (enemy != null) {
                if (battleScreen == null) {
                    battleScreen = new BattleScreen(game, this);
                }
                battleScreen.resetEnemyHealth();
                battleScreen.setCurrentEnemy(enemy);
                isBattleActive = true;
                return;
            }
        }

        lightingManager.beginLightBuffer();
        batch.begin();
        renderHelper.updateCameraOffset(character.getPosition().x, character.getPosition().y, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        offsetX = renderHelper.getOffsetX();
        offsetY = renderHelper.getOffsetY();

        renderHelper.renderMap(batch, this.map, textureManager.getFloorTexture(), textureManager.getWallTexture(), this.ambient);
        renderHelper.renderMaterials(batch, materiaisNoMapa);
        renderHelper.renderCreatures(batch, deers, cannibals, character, fishes,NPCS,boats);

        if (this.ambient instanceof Jungle) {
            Jungle jungle = (Jungle) this.ambient;
            Texture grassOverlay = jungle.getTallGrassTexture();
            if (grassOverlay != null) {
                for (int y_coord = 0; y_coord < MAP_HEIGHT; y_coord++) {
                    for (int x_coord = 0; x_coord < MAP_WIDTH; x_coord++) {
                        if (jungle.isTallGrass(x_coord, y_coord)) {
                            float dx = x_coord * TILE_SIZE + offsetX;
                            float dy = y_coord * TILE_SIZE + offsetY;
                            batch.draw(grassOverlay, dx, dy, TILE_SIZE, TILE_SIZE);
                        }
                    }
                }
            }
        }
        batch.end();

        lightingManager.endLightBufferAndRender(batch);

        if (character.isInTallGrass()) {
            float playerScreenX = character.getPosition().x + offsetX + TILE_SIZE / 2f;
            float playerScreenY = character.getPosition().y + offsetY + TILE_SIZE / 2f;
            if(shapeRenderer != null) lightingManager.renderTallGrassEffect(shapeRenderer, playerScreenX, playerScreenY);
        }

        for (Material m : materiaisNoMapa) {
            float playerDistToMaterial = character.getPosition().dst(m.getPosition());
            if (playerDistToMaterial < TILE_SIZE * 1.5f) {
                float materialScreenX = m.getPosition().x + offsetX; float materialScreenY = m.getPosition().y + offsetY;
                boolean mouseIsOverMaterial = Gdx.input.getX() >= materialScreenX && Gdx.input.getX() <= materialScreenX + TILE_SIZE && (Gdx.graphics.getHeight() - Gdx.input.getY()) >= materialScreenY && (Gdx.graphics.getHeight() - Gdx.input.getY()) <= materialScreenY + TILE_SIZE;
                if (mouseIsOverMaterial || playerDistToMaterial < TILE_SIZE * 0.8f) {
                    if ("Medicinal".equalsIgnoreCase(m.getName()) && "Plant".equalsIgnoreCase(m.getType())) Medicine.renderUseOption(batch, m, character, offsetX, offsetY);
                    else if ("Berry".equalsIgnoreCase(m.getName())) renderHelper.renderInteractionPrompt(batch, m, "Collect Berry");
                    else if ("rock".equalsIgnoreCase(m.getName()) || "stick".equalsIgnoreCase(m.getName())) renderHelper.renderInteractionPrompt(batch, m, "Collect " + m.getName());
                }
            }
        }

        if (showInventory) {
            if (characterUI != null) characterUI.renderInventory(batch, shapeRenderer, character);
            if (craftingBar != null) craftingBar.render(batch, shapeRenderer, character, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        if (hud != null) hud.render(batch, shapeRenderer, character, gameState, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (stage != null) { stage.act(delta); stage.draw(); }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) { //
            if (!showInventory) { //
                boolean actionTaken = false;
                // Verifica se o jogador tentou interagir com um NPC
                if (this.NPCS != null && !this.NPCS.isEmpty() && character != null) {
                    NPC closestNpc = null;
                    float minDistance = NPC_INTERACTION_RADIUS;

                    for (NPC npc : this.NPCS) {
                        if (npc == null) continue;
                        float distanceToNpc = character.getPosition().dst(npc.getPosition());
                        if (distanceToNpc < minDistance) {
                            minDistance = distanceToNpc;
                            closestNpc = npc;
                        }
                    }

                    if (closestNpc != null) {
                        // Interage com o NPC mais próximo encontrado dentro do raio
                        String dialogue = closestNpc.getDialogue();
                        if (this.gameState.getChatController() != null) {
                            // Usar o nome do NPC (que pode ser "Jack" ou o nome do construtor se você mudar getName())
                            this.gameState.getChatController().addMessage(closestNpc.getName() + ": " + dialogue, Color.CYAN); // Cor para diálogo
                        } else {
                            System.out.println(closestNpc.getName() + ": " + dialogue); // Fallback se ChatController não estiver disponível
                        }
                        actionTaken = true; // Marca que uma ação (interação com NPC) foi tomada
                    }
                }
                // Verifica se o jogador tentou interagir com um barco
                if (this.boats != null && !this.boats.isEmpty() && character != null) {
                    Boat closestBoat = null;
                    float minDistance = Boat_INTERACTION_RADIUS;

                    for (Boat boat : this.boats) {
                        if (boat == null) continue;
                        float distanceToBoat = character.getPosition().dst(boat.getPosition());
                        if (distanceToBoat < minDistance) {
                            minDistance = distanceToBoat;
                            closestBoat = boat;
                        }
                    }

                    if (closestBoat != null) {
                        // Interage com o barco mais próximo encontrado dentro do raio
                        String dialogue = closestBoat.getDialogue();
                        if (this.gameState.getChatController() != null) {
                            // Usar o nome do barco
                            this.gameState.getChatController().addMessage(closestBoat.getName() + ": " + dialogue, Color.CYAN); // Cor para diálogo
                        } else {
                            System.out.println(closestBoat.getName() + ": " + dialogue); // Fallback se ChatController não estiver disponível
                        }
                        actionTaken = true; // Marca que uma ação (interação com barco) foi tomada
                    }
                }

                // Lógica existente de coleta de material, se nenhuma interação com NPC ocorreu
                if (!actionTaken && character != null) { //
                    // A lógica de beber água estava aqui, mantenha se necessário ou ajuste
                    int playerTileX = (int) ((character.getPosition().x + TILE_SIZE / 2f) / TILE_SIZE); //
                    int playerTileY = (int) ((character.getPosition().y + TILE_SIZE / 4f) / TILE_SIZE); //
                    if (playerTileX >= 0 && playerTileX < MAP_WIDTH && playerTileY >= 0 && playerTileY < MAP_HEIGHT) { //
                        int tileTypeAtPlayer = map[playerTileY][playerTileX]; //
                        if (ambient instanceof LakeRiver && tileTypeAtPlayer == TILE_WATER) { //
                            logger.info("{} tries to drink water from {}.", character.getName(), ambient.getName()); //
                            // Adicione a lógica de beber água aqui se ela foi removida ou estava implícita
                            actionTaken = true; //
                        }
                    }
                    if (!actionTaken) { //
                        character.tryCollectNearbyMaterial(materiaisNoMapa); //
                    }
                }

            } else { // Se o inventário estiver aberto
                if (characterUI != null && character != null) { //
                    Item selectedItem = characterUI.getSelectedItem(); //
                    if (selectedItem != null) { //
                        character.useItem(selectedItem); //
                        characterUI.clearSelection(); //
                    }
                }
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            if (character != null && ambient instanceof LakeRiver) {
                boolean captured = character.tryCaptureFish(fishes);
                if (captured) {
                    logger.info("{} captured a fish!", character.getName());
                } else {
                    logger.info("{} tried to fish but failed.", character.getName());
                }
            }
        }

    }
    public void removeEnemyFromMap(Creature enemy) {
        if (enemy instanceof Deer) {
            deers.remove(enemy);
        } else if (enemy instanceof Cannibal) {
            cannibals.remove(enemy);
        }
        logger.info("Inimigo {} removido do mapa.", enemy.getName());
    }
    private void handleGameOver() {
        if (gameOverTriggered) return;
        gameOverTriggered = true;
        logger.info("Game over condition reached for character {} after {} days.", character.getName(), gameState.getDaysSurvived());
        String saveNameToDelete = null;

        if (this.game != null && this.game.getScenarioController() != null) {
            saveNameToDelete = this.game.getScenarioController().getCurrentSaveFileName();
        }
        if (saveNameToDelete == null) saveNameToDelete = "autosave.json";

        if (this.game != null && this.game.getScenarioController() != null) {
            this.game.getScenarioController().triggerGameOver(saveNameToDelete);
        } else {
            logger.error("Main game instance or ScenarioController is null. Cannot trigger GameOverScreen properly.");
            if (Gdx.app.getApplicationListener() instanceof Main) {
                ((Main)Gdx.app.getApplicationListener()).getScenarioController().triggerGameOver(saveNameToDelete);
            }
        }
    }
    public void setSkipMapRegeneration(boolean skip) {
        this.skipMapRegeneration = skip;
    }

    public boolean isSkipMapRegeneration() {
        return skipMapRegeneration;
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) stage.getViewport().update(width, height, true);
        offsetX = (width - (MAP_WIDTH * TILE_SIZE)) / 2f;
        offsetY = (height - (MAP_HEIGHT * TILE_SIZE)) / 2f;
        if (lightingManager != null) lightingManager.initializeBuffer();
    }

    @Override
    public void pause() {
        if (gameStateManager != null && character != null && this.ambient != null && this.map != null && !gameOverTriggered) {
            gameStateManager.autosave(character, this.ambient, this.map);
        }
        if (eruptionSound != null && eruptionSoundId != -1 && isEruptionSoundActive) {
            eruptionSound.stop(eruptionSoundId);
            isEruptionSoundActive = false; // Para que não tente continuar de onde parou no resume
            eruptionSoundId = -1;
            logger.debug("Som de erupção parado devido à pausa da tela.");
        }
    }
    @Override
    public void resume() {}
    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (eruptionSound != null) {
            if (eruptionSoundId != -1) {
                eruptionSound.stop(eruptionSoundId); // Garante que pare o som antes de descartar
            }
            eruptionSound.dispose();
            eruptionSound = null;
            logger.info("Som de erupção descartado.");
        }
        try {
            if (gameStateManager != null && character != null && this.ambient != null && this.map != null && !gameOverTriggered) {
                gameStateManager.autosave(character, this.ambient, this.map);
            }
            if (batch != null) batch.dispose(); batch = null;
            if (shapeRenderer != null) shapeRenderer.dispose(); shapeRenderer = null;
            if (font != null) font.dispose(); font = null;
            if (promptFont != null) promptFont.dispose(); promptFont = null;
            if (textureManager != null) textureManager.dispose(); textureManager = null;
            if (lightingManager != null) lightingManager.dispose(); lightingManager = null;
            if (renderHelper != null) renderHelper.dispose(); renderHelper = null;
            if (craftingBar != null) craftingBar.dispose(); craftingBar = null;

            // Static/shared textures like bgHudShared or Medicine.bgHud should ideally be managed by an AssetManager
            // For now, not re-disposing them here to avoid issues if they are used elsewhere or already handled.
            // if (bgHudShared != null && !bgHudShared.isDisposed()) { /* bgHudShared.dispose(); */ bgHudShared = null; }

            if (stage != null) stage.dispose(); stage = null;
            // Skin is loaded in show() and might be shared or used by other screens if not careful.
            // If it's exclusively for this screen and loaded by it, dispose it.
            // if (skin != null) skin.dispose(); skin = null;
            SnakeEventManager.dispose();
        } catch (Exception e) {
            logger.error("Error disposing resources in ProceduralMapScreen: {}", e.getMessage(), e);
        }
    }
}
