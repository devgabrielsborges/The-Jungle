package io.github.com.ranie_borges.thejungle.model.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.google.gson.annotations.Expose;
import io.github.com.ranie_borges.thejungle.model.entity.itens.CraftManager;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Material;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Medicine;
import io.github.com.ranie_borges.thejungle.model.enums.Trait;
import io.github.com.ranie_borges.thejungle.model.entity.interfaces.ICharacter;
import org.slf4j.Logger;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Tool;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Character implements ICharacter {
    private static final Logger logger = LoggerFactory.getLogger(Character.class);

    // Fields to be serialized
    @Expose
    private String name;
    @Expose
    private float life;
    @Expose
    private float hunger;
    @Expose
    private float thirsty;
    @Expose
    private float energy;
    @Expose
    private float sanity;
    @Expose
    private Array<Item> inventory;
    @Expose
    private float currentWeight = 0f;

    @Expose
    private float maxCarryWeight = 30f; // peso máximo padrão (pode mudar por profissão depois)


    @Expose
    private int inventoryInitialCapacity = 15;

    @Expose
    private int maxInventoryCapacity = 100;
    @Expose
    private double attackDamage;

    @Expose
    private List<Trait> traits;

    @Expose
    private String characterType;
    @Expose
    private float speed;

    @Expose
    private Vector2 position;
    private Vector2 playerPos;
    private Character character;
    private final int TILE_SIZE = 32;
    private final int MAP_WIDTH = 30;
    private final int MAP_HEIGHT = 20;
    private static final int TILE_GRASS = 0;
    private static final int TILE_WALL = 1;
    private static final int TILE_DOOR = 2;
    private static final int TILE_CAVE = 3;
    private static final int TILE_WATER = 4;
    private int[][] map;
    private float offsetX, offsetY;



    private final Texture texture;

    private Animation<TextureRegion> playerIdleUp;
    private Animation<TextureRegion> playerIdleDown;
    private Animation<TextureRegion> playerIdleLeft;
    private Animation<TextureRegion> playerIdleRight;
    private Animation<TextureRegion> playerWalkUp;
    private Animation<TextureRegion> playerWalkDown;
    private Animation<TextureRegion> playerWalkLeft;
    private Animation<TextureRegion> playerWalkRight;

    private enum PlayerState { IDLE_UP, IDLE_DOWN, IDLE_LEFT, IDLE_RIGHT, WALK_UP, WALK_DOWN, WALK_LEFT, WALK_RIGHT }
    private enum Direction { UP, DOWN, LEFT, RIGHT }

    private Direction lastDirection = Direction.DOWN;
    private PlayerState currentState = PlayerState.IDLE_DOWN;
    private boolean isMoving = false;
    private float stateTime = 0;



    protected Character(
        String name,
        float life,
        float hunger,
        float thirsty,
        float energy,
        float sanity,
        float attackDamage,
        String spritePath,
        float xPosition,
        float yPosition
    ) {
        try {
            this.name = name != null ? name : "Unknown";
            this.life = Math.max(0, life);
            this.hunger = Math.max(0, hunger);
            this.thirsty = Math.max(0, thirsty);
            this.energy = Math.max(0, energy);
            this.sanity = Math.max(0, sanity);
            this.attackDamage = Math.max(0, attackDamage);
            this.inventory = new Array<>(inventoryInitialCapacity);
            this.traits = new ArrayList<>();

            // Load texture with exception handling
            Texture tempTexture;
            try {
                if (spritePath == null || spritePath.isEmpty()) {
                    throw new IllegalArgumentException("Sprite path cannot be null or empty");
                }
                tempTexture = new Texture(Gdx.files.internal(spritePath));
                logger.debug("Successfully loaded texture: {}", spritePath);
            } catch (Exception e) {
                logger.error("Failed to load texture {}: {}", spritePath, e.getMessage());
                // Create a fallback 1x1 texture
                Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
                pixmap.setColor(1, 0, 1, 0.5f); // Semi-transparent magenta for visual debugging
                pixmap.fill();
                tempTexture = new Texture(pixmap);
                pixmap.dispose();
            }
            this.texture = tempTexture;

            this.position = new Vector2(xPosition, yPosition);
            this.characterType = this.getClass().getSimpleName();

        } catch (Exception e) {
            logger.error("Error creating character {}: {}", name, e.getMessage());
            throw e; // Re-throw after logging as this is a critical initialization error
        }
    }
    public void loadPlayerAnimations() {
        // IDLE: 2 frames
        playerIdleDown = loadAnimation("personagem_parado_frente.png", 0.5f, 2);
        playerIdleUp = loadAnimation("personagem_parado_costas.png", 0.5f, 2);
        playerIdleLeft = loadAnimation("personagem_parado_esquerda.png", 0.5f, 2);
        playerIdleRight = loadAnimation("personagem_parado_direita.png", 0.5f, 2);

// WALK: 4 frames
        playerWalkDown = loadAnimation("personagem_andando_frente.png", 0.1f, 4);
        playerWalkUp = loadAnimation("personagem_andando_costas.png", 0.1f, 4);
        playerWalkLeft = loadAnimation("personagem_andando_esquerda.png", 0.1f, 4);
        playerWalkRight = loadAnimation("personagem_andando_direita.png", 0.1f, 4);

    }
    public boolean setInitialSpawn(int[][] map, int mapWidth, int mapHeight, int tileSize, int tileGrass, int tileCave, String ambientName) {
        try {
            int x = 0, y = 0;
            int attempts = 0;
            int maxAttempts = 1000;
            boolean positionFound = false;

            do {
                x = (int)(Math.random() * mapWidth);
                y = (int)(Math.random() * mapHeight);
                attempts++;

                int tileType = map[y][x];
                boolean isValidTile = (
                    tileType == tileGrass ||
                        (ambientName.equalsIgnoreCase("Cave") && tileType == tileCave)
                );

                if (isValidTile) {
                    getPosition().set(x * tileSize, y * tileSize);
                    positionFound = true;
                }
            } while (!positionFound && attempts < maxAttempts);

            if (!positionFound) {
                getPosition().set((mapWidth / 2) * tileSize, (mapHeight / 2) * tileSize);
                logger.warn("{}: Could not find valid spawn position, using fallback", getName());
            }

            logger.info("{}: Player spawned at ({}, {})", getName(), (int)(getPosition().x / tileSize), (int)(getPosition().y / tileSize));
            return true;
        } catch (Exception e) {
            logger.error("{}: Error during spawn positioning: {}", getName(), e.getMessage());
            return false;
        }
    }


    private Animation<TextureRegion> loadAnimation(String filename, float frameDuration, int framesCount) {
        Texture spriteSheet = new Texture(Gdx.files.internal("sprites/character/" + filename.replace(".gif", ".png")));

        int frameWidth = spriteSheet.getWidth() / framesCount;
        int frameHeight = spriteSheet.getHeight();

        TextureRegion[][] tmp = TextureRegion.split(spriteSheet, frameWidth, frameHeight);
        Array<TextureRegion> frames = new Array<>(framesCount);

        for (int i = 0; i < framesCount; i++) {
            frames.add(tmp[0][i]);
        }

        return new Animation<>(frameDuration, frames);
    }
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
    public void move(float deltaX, float deltaY) {
        try {
            // Atualiza a posição do personagem
            position.x += deltaX;
            position.y += deltaY;

            // Atualiza o estado de movimento para animações
            isMoving = (deltaX != 0 || deltaY != 0);

            // Define a direção com base no movimento
            if (deltaY > 0) lastDirection = Direction.UP;
            else if (deltaY < 0) lastDirection = Direction.DOWN;
            else if (deltaX < 0) lastDirection = Direction.LEFT;
            else if (deltaX > 0) lastDirection = Direction.RIGHT;

            // Atualiza o estado da animação do jogador
            updatePlayerState();
        } catch (Exception e) {
            logger.error("{}: Erro ao mover personagem: {}", name, e.getMessage());
        }
    }
    public boolean tryMove(float delta, int[][] map, int tileSize, int tileWall, int tileDoor, int tileCave, int mapWidth, int mapHeight) {
        float speed = getSpeed() > 0 ? getSpeed() : 100f;
        float deltaX = 0, deltaY = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) deltaY = speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) deltaY = -speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) deltaX = -speed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) deltaX = speed * delta;

        float nextX = getPosition().x + deltaX;
        float nextY = getPosition().y + deltaY;

        int tileX = (int) ((nextX + 8) / tileSize);
        int tileY = (int) ((nextY + 8) / tileSize);

        if (tileX >= 0 && tileX < mapWidth && tileY >= 0 && tileY < mapHeight) {
            int tileType = map[tileY][tileX];

            if (tileType != tileWall) {
                move(deltaX, deltaY);
                updateStats(delta);
                updateStateTime(delta);
                return tileType == tileDoor; // true se for uma porta (trigger para Procedural reagir)
            }
        }

        updateStats(delta);
        updateStateTime(delta);
        return false;
    }

    public void updateStats(float delta) {
        try {
            // Depleção de recursos ao longo do tempo
            float hungerDepletion = 0.01f * delta;
            float thirstDepletion = 0.015f * delta;
            float energyDepletion = 0.005f * delta;

            // Atualiza os atributos
            setHunger(Math.max(0, getHunger() - hungerDepletion));
            setThirsty(Math.max(0, getThirsty() - thirstDepletion));
            setEnergy(Math.max(0, getEnergy() - energyDepletion));

            // Se fome ou sede estão baixas, diminui a vida
            if (getHunger() <= 10 || getThirsty() <= 10) {
                setLife(Math.max(0, getLife() - 0.05f * delta));
            }
        } catch (Exception e) {
            logger.error("{}: Erro ao atualizar atributos: {}", name, e.getMessage());
        }
    }
    public void updateStateTime(float delta) {
        stateTime += delta;
    }
    public float getStateTime() {
        return stateTime;
    }
    public PlayerState getCurrentState() {
        return currentState;
    }
    public TextureRegion getCurrentFrame() {
        return getFrameForCurrentState(stateTime);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        try {
            this.name = name != null ? name : "Unknown";
        } catch (Exception e) {
            logger.error("Error setting name: {}", e.getMessage());
            this.name = "Error";
        }
    }

    public float getLife() {
        return life;
    }

    public void setLife(float life) {
        try {
            if (life < 0) {
                logger.warn("{}: Tried to set negative life value ({}), clamping to 0", name, life);
                this.life = 0;
            } else {
                this.life = life;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting life: {}", name, e.getMessage());
        }
    }

    public float getHunger() {
        return hunger;
    }

    public void setHunger(float hunger) {
        try {
            if (hunger < 0) {
                logger.warn("{}: Tried to set negative hunger value ({}), clamping to 0", name, hunger);
                this.hunger = 0;
            } else {
                this.hunger = hunger;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting hunger: {}", name, e.getMessage());
        }
    }

    public float getThirsty() {
        return thirsty;
    }

    public void setThirsty(float thirsty) {
        try {
            if (thirsty < 0) {
                logger.warn("{}: Tried to set negative thirsty value ({}), clamping to 0", name, thirsty);
                this.thirsty = 0;
            } else {
                this.thirsty = thirsty;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting thirsty: {}", name, e.getMessage());
        }
    }

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        try {
            if (energy < 0) {
                logger.warn("{}: Tried to set negative energy value ({}), clamping to 0", name, energy);
                this.energy = 0;
            } else {
                this.energy = energy;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting energy: {}", name, e.getMessage());
        }
    }

    public float getSanity() {
        return sanity;
    }

    public void setSanity(float sanity) {
        try {
            if (sanity < 0) {
                logger.warn("{}: Tried to set negative sanity value ({}), clamping to 0", name, sanity);
                this.sanity = 0;
            } else {
                this.sanity = sanity;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting sanity: {}", name, e.getMessage());
        }
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        try {
            if (speed < 0) {
                logger.warn("{}: Tried to set negative speed value ({}), clamping to 0", name, speed);
                this.speed = 0;
            } else {
                this.speed = speed;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting speed: {}", name, e.getMessage());
        }
    }

    public Array<Item> getInventory() {
        return inventory;
    }

    public void setInventory(Array<Item> inventory) {
        try {
            if (inventory == null) {
                logger.warn("{}: Tried to set null inventory, creating empty inventory instead", name);
                this.inventory = new Array<>(inventoryInitialCapacity);
            } else {
                this.inventory = inventory;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting inventory: {}", name, e.getMessage());
            this.inventory = new Array<>(inventoryInitialCapacity);
        }
    }

    public int getMaxInventoryCapacity() {
        return maxInventoryCapacity;
    }

    public void setMaxInventoryCapacity(int maxInventoryCapacity) {
        try {
            if (maxInventoryCapacity <= 0) {
                logger.warn("{}: Invalid max inventory capacity: {}, must be positive", name, maxInventoryCapacity);
                return;
            }
            this.maxInventoryCapacity = maxInventoryCapacity;
        } catch (Exception e) {
            logger.error("{}: Error setting max inventory capacity: {}", name, e.getMessage());
        }
    }

    public int getInventoryInitialCapacity() {
        return inventoryInitialCapacity;
    }

    public void setInventoryInitialCapacity(int inventoryInitialCapacity) {
        try {
            if (inventoryInitialCapacity <= 0) {
                logger.warn("{}: Invalid inventory initial capacity: {}, must be positive", name, inventoryInitialCapacity);
                return;
            }

            if (inventoryInitialCapacity > maxInventoryCapacity) {
                logger.warn("{}: Initial capacity {} exceeds max capacity {}, capping at max",
                    name, inventoryInitialCapacity, maxInventoryCapacity);
                this.inventoryInitialCapacity = maxInventoryCapacity;
            } else {
                this.inventoryInitialCapacity = inventoryInitialCapacity;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting initial inventory capacity: {}", name, e.getMessage());
        }
    }
    public boolean tryCollectNearbyMaterial(List<Material> materiais) {
        Iterator<Material> iterator = materiais.iterator();
        while (iterator.hasNext()) {
            Material material = iterator.next();
            float dist = getPosition().dst(material.getPosition());

            // Considera coleta se estiver a menos de 24px (ajuste fino)
            if (dist < 24f) {
                if (isInventoryFull()) {
                    System.out.println(getName() + ": inventário cheio!");
                    return false;
                }

                if (!canCarryMore(material.getWeight())) {
                    System.out.println(getName() + ": muito pesado para carregar " + material.getName());
                    return false;
                }

                insertItemInInventory(material);
                iterator.remove();
                return true;
            }
        }
        return false;
    }


    public void insertItemInInventory(Item item) {
        try {
            if (item == null) {
                logger.warn("{}: Attempted to add null item to inventory", name);
                return;
            }

            if (isInventoryFull()) {
                logger.warn("{}: Cannot add item '{}', inventory is full", name, item.getName());
                System.out.println(getName() + " tentou adicionar um item, mas o inventário está cheio!");
                return;
            }

            if (!canCarryMore(item.getWeight())) {
                logger.warn("{}: Cannot carry item '{}', too heavy!", name, item.getName());
                System.out.println(getName() + " está muito pesado para carregar: " + item.getName());
                return;
            }

            inventory.add(item);
            currentWeight += item.getWeight();

            logger.debug("{}: Added item '{}' to inventory (currentWeight = {}/{})", name, item.getName(), currentWeight, maxCarryWeight);
            System.out.println(getName() + " adicionou " + item.getName() + " no inventário. Peso atual: " + currentWeight + "/" + maxCarryWeight);
        } catch (Exception e) {
            logger.error("{}: Error adding item to inventory: {}", name, e.getMessage());
        }
    }


    public Item getItem(int index) {
        try {
            if (index < 0 || index >= inventory.size) {
                logger.warn("{}: Attempt to access invalid inventory index: {}", name, index);
                return null;
            }
            return inventory.get(index);
        } catch (Exception e) {
            logger.error("{}: Error accessing inventory at index {}: {}", name, index, e.getMessage());
            return null;
        }
    }

    public void dropItem(int index) {
        try {
            if (index < 0 || index >= inventory.size) {
                logger.warn("{}: Cannot drop item, invalid index: {}", name, index);
                return;
            }

            Item item = inventory.get(index);
            if (item == null) {
                logger.warn("{}: No item to drop at index {}", name, index);
                return;
            }

            inventory.set(index, null);
            logger.debug("{}: Dropped item '{}' from index {}", name, item.getName(), index);
        } catch (Exception e) {
            logger.error("{}: Error dropping item at index {}: {}", name, index, e.getMessage());
        }
    }
    public void cutTree() {
        try {
            if (isInventoryFull()) {
                logger.warn("{}: Inventory full, cannot collect wood.", getName());
                System.out.println(getName() + " tentou cortar uma árvore, mas o inventário está cheio!");
                return;
            }

            Material woodLog = Material.createWoodLog(); // Usa o método padrão da classe Material
            insertItemInInventory(woodLog);

            logger.info("{} cut down a tree and collected a Wood Log.", getName());
            System.out.println(getName() + " cortou uma árvore e coletou uma tora de madeira!");
        } catch (Exception e) {
            logger.error("{}: Error while cutting tree: {}", getName(), e.getMessage());
        }
    }
    /**
     * Cuts a tree using an Axe, collecting extra wood logs if successful.
     */
    public void cutTreeWithAxe() {
        try {
            Tool axe = null;

            for (Item item : inventory) {
                if (item instanceof Tool) {
                    Tool tool = (Tool) item;
                    if (tool.getName().equalsIgnoreCase("Axe")) {
                        axe = tool;
                        break;
                    }
                }
            }

            if (axe == null) {
                logger.warn("{}: Tried to cut a tree but has no Axe.", getName());
                System.out.println(getName() + " tentou cortar uma árvore, mas não tem um Machado!");
                return;
            }

            if (isInventoryFull()) {
                logger.warn("{}: Inventory full, cannot collect wood.", getName());
                System.out.println(getName() + " tentou cortar uma árvore, mas o inventário está cheio!");
                return;
            }

            insertItemInInventory(Material.createWoodLog());
            insertItemInInventory(Material.createWoodLog());

            logger.info("{} used an Axe to cut a tree and collected 2 Wood Logs.", getName());
            System.out.println(getName() + " cortou uma árvore com o Machado e coletou 2 toras de madeira!");

            axe.useItem();

            if (axe.getDurability() <= 0) {
                inventory.removeValue(axe, true);
                System.out.println("O Machado quebrou após o uso!");
                logger.info("{}: Axe broke after use.", getName());
            }
        } catch (Exception e) {
            logger.error("{}: Error while cutting tree with Axe: {}", getName(), e.getMessage());
        }
    }
    /**
     * Collects a resource using a Knife, improving success and reducing wear.
     *
     * @param resource The item to collect
     */
    public void collectWithKnife(Item resource) {
        try {
            if (resource == null) {
                logger.warn("{}: Tried to collect a null resource.", getName());
                System.out.println(getName() + " tentou coletar algo, mas não havia nada.");
                return;
            }

            Tool knife = null;

            for (Item item : inventory) {
                if (item instanceof Tool) {
                    Tool tool = (Tool) item;
                    if (tool.getName().equalsIgnoreCase("Knife")) {
                        knife = tool;
                        break;
                    }
                }
            }

            if (knife == null) {
                logger.warn("{}: Tried to collect a resource but has no Knife.", getName());
                System.out.println(getName() + " tentou coletar um recurso, mas não tem uma Faca!");
                return;
            }

            if (isInventoryFull()) {
                logger.warn("{}: Inventory full, cannot collect resource.", getName());
                System.out.println(getName() + " tentou coletar, mas o inventário está cheio!");
                return;
            }

            insertItemInInventory(resource);

            logger.info("{} used a Knife to collect resource: {}", getName(), resource.getName());
            System.out.println(getName() + " usou a Faca e coletou com sucesso: " + resource.getName());

            knife.useItem();

            if (knife.getDurability() <= 0) {
                inventory.removeValue(knife, true);
                System.out.println("A Faca quebrou após o uso!");
                logger.info("{}: Knife broke after use.", getName());
            }
        } catch (Exception e) {
            logger.error("{}: Error while collecting with Knife: {}", getName(), e.getMessage());
        }
    }



    public void emptyInventory() {
        try {
            inventory.clear();
            logger.debug("{}: Inventory emptied", name);
        } catch (Exception e) {
            logger.error("{}: Error emptying inventory: {}", name, e.getMessage());
        }
    }
    private boolean canCarryMore(float itemWeight) {
        return (currentWeight + itemWeight) <= maxCarryWeight;
    }

    public void increaseInventoryCapacity(int newCapacity) {
        try {
            if (!isNewInventoryCapacityOk(newCapacity)) {
                logger.warn("{}: Invalid new inventory capacity: {}", name, newCapacity);
                return;
            }

            this.inventoryInitialCapacity = newCapacity;
            if (inventory.size < newCapacity) {
                inventory.ensureCapacity(newCapacity);
                logger.debug("{}: Inventory capacity increased to {}", name, newCapacity);
            }
        } catch (Exception e) {
            logger.error("{}: Error increasing inventory capacity: {}", name, e.getMessage());
        }
    }

    public boolean isNewInventoryCapacityOk(int newCapacity) {
        try {
            return newCapacity > 0 && newCapacity <= maxInventoryCapacity;
        } catch (Exception e) {
            logger.error("{}: Error checking inventory capacity: {}", name, e.getMessage());
            return false;
        }
    }

    public boolean isInventoryFull() {
        try {
            return inventory.size >= inventoryInitialCapacity;
        } catch (Exception e) {
            logger.error("{}: Error checking if inventory is full: {}", name, e.getMessage());
            return true; // Safer to assume it's full if we can't check
        }
    }

    public boolean isInventoryIndexOk(int index) {
        try {
            return index >= 0 && index < inventory.size;
        } catch (Exception e) {
            logger.error("{}: Error checking inventory index: {}", name, e.getMessage());
            return false;
        }
    }

    public boolean isInventoryIndexFree(int index) {
        try {
            return isInventoryIndexOk(index) && inventory.get(index) == null;
        } catch (Exception e) {
            logger.error("{}: Error checking if inventory index is free: {}", name, e.getMessage());
            return false;
        }
    }

    public boolean isInventoryEmpty() {
        try {
            // Check if size is 0 or all elements are null
            if (inventory.size == 0) return true;

            for (Item item : inventory) {
                if (item != null) return false;
            }
            return true;
        } catch (Exception e) {
            logger.error("{}: Error checking if inventory is empty: {}", name, e.getMessage());
            return true; // Safer to assume it's empty
        }
    }

    public int getInventorySize() {
        try {
            return inventory.size;
        } catch (Exception e) {
            logger.error("{}: Error getting inventory size: {}", name, e.getMessage());
            return 0;
        }
    }

    /**
     * Heals the character using a Medicine item.
     *
     * @param medicine The medicine item to use
     */
    public void heal(Medicine medicine) {
        try {
            if (medicine == null) {
                logger.warn("{}: Tried to heal with a null medicine.", getName());
                System.out.println(getName() + " tentou usar um medicamento inexistente!");
                return;
            }

            if (!inventory.contains(medicine, true)) {
                logger.warn("{}: Tried to heal with a medicine not in inventory.", getName());
                System.out.println(getName() + " tentou usar um medicamento que não está no inventário!");
                return;
            }

            // Cura a vida baseado no healRatio do remédio
            float lifeRestored = (float) (getHealRatioPercentage(medicine) * getLifeMax());

            setLife(Math.min(getLife() + lifeRestored, getLifeMax()));

            // Usa o remédio
            medicine.useItem();

            logger.info("{} used {} and restored {} life.", getName(), medicine.getName(), lifeRestored);
            System.out.println(getName() + " usou " + medicine.getName() + " e restaurou " + lifeRestored + " pontos de vida!");

            // Se a durabilidade zerar, remove do inventário
            if (medicine.getDurability() <= 0) {
                inventory.removeValue(medicine, true);
                System.out.println("O medicamento " + medicine.getName() + " acabou após o uso!");
                logger.info("{}: {} finished after usage.", getName(), medicine.getName());
            }

        } catch (Exception e) {
            logger.error("{}: Error while using medicine: {}", getName(), e.getMessage());
        }
    }

    /**
     * Helper method to get how much of life the medicine will restore.
     */
    private float getHealRatioPercentage(Medicine medicine) {
        return (float) (medicine.getHealRatio() / 100.0);
    }

    /**
     * Helper method to define the maximum life of a character (could vary by profession)
     */
    private float getLifeMax() {
        return 100f; // Aqui você pode depois customizar dependendo da classe (Hunter 100, Doctor 80, etc.)
    }

    /**
     * Uses an item intelligently depending on its type (Medicine, Tool, Weapon, etc.).
     *
     * @param item The item to use
     */
    public void useItem(Item item) {
        try {
            if (item == null) {
                logger.warn("{}: Tried to use a null item.", getName());
                System.out.println(getName() + " tentou usar um item inexistente!");
                return;
            }

            if (!inventory.contains(item, true)) {
                logger.warn("{}: Tried to use an item not in inventory: {}", getName(), item.getName());
                System.out.println(getName() + " tentou usar um item que não está no inventário: " + item.getName());
                return;
            }

            if (item instanceof Medicine) {
                heal((Medicine) item);
            } else {
                item.useItem();
                System.out.println(getName() + " usou o item: " + item.getName());
                logger.info("{} used item: {}", getName(), item.getName());
            }

            if (item.getDurability() <= 0) {
                inventory.removeValue(item, true);
                System.out.println("O item " + item.getName() + " quebrou ou foi consumido completamente!");
                logger.info("{}: Item {} removed from inventory after use.", getName(), item.getName());
            }
        } catch (Exception e) {
            logger.error("{}: Error while using item: {}", getName(), e.getMessage());
        }
    }


    public double getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(double attackDamage) {
        try {
            if (attackDamage < 0) {
                logger.warn("{}: Tried to set negative attack damage ({}), clamping to 0", name, attackDamage);
                this.attackDamage = 0;
            } else {
                this.attackDamage = attackDamage;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting attack damage: {}", name, e.getMessage());
        }
    }

    public List<Trait> getTraits() {
        return traits;
    }

    public void setTraits(List<Trait> traits) {
        try {
            if (traits == null) {
                logger.warn("{}: Tried to set null traits list, creating empty list instead", name);
                this.traits = new ArrayList<>();
            } else {
                this.traits = traits;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting traits: {}", name, e.getMessage());
            this.traits = new ArrayList<>();
        }
    }

    public String getCharacterType() {
        return characterType;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setCharacterType(String characterType) {
        try {
            if (characterType == null || characterType.isEmpty()) {
                logger.warn("{}: Invalid character type, using class name instead", name);
                this.characterType = this.getClass().getSimpleName();
            } else {
                this.characterType = characterType;
            }
        } catch (Exception e) {
            logger.error("{}: Error setting character type: {}", name, e.getMessage());
            this.characterType = "Unknown";
        }
    }

    @Override
    public void dropItem(Item item) {
        try {
            if (item == null) {
                logger.warn("{}: Tried to drop a null item.", getName());
                return;
            }

            if (!inventory.contains(item, true)) {
                logger.warn("{}: Tried to drop an item not in inventory: {}", getName(), item.getName());
                System.out.println(getName() + " tentou dropar um item que não possui: " + item.getName());
                return;
            }

            inventory.removeValue(item, true);
            currentWeight -= item.getWeight();
            if (currentWeight < 0) currentWeight = 0;

            System.out.println(getName() + " dropou o item: " + item.getName());
            logger.info("{} dropped item: {}", getName(), item.getName());

        } catch (Exception e) {
            logger.error("{}: Error dropping item: {}", getName(), e.getMessage());
        }
    }


    public void updatePosition(float delta) {
        try {
            if (delta <= 0) {
                logger.warn("{}: Invalid delta time: {}, skipping position update", name, delta);
                return;
            }

            float newX = position.x;
            float newY = position.y;

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                newY += speed * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                newY -= speed * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                newX -= speed * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                newX += speed * delta;
            }

            position.set(newX, newY);
        } catch (Exception e) {
            logger.error("{}: Error updating position: {}", name, e.getMessage());
        }
    }
    private void renderPlayer(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = getFrameForCurrentState(stateTime);

        // Pega o tamanho real do quadro da animação
        float frameWidth = currentFrame.getRegionWidth();
        float frameHeight = currentFrame.getRegionHeight();

        // Desenha o personagem centralizado no TILE_SIZE
        batch.draw(currentFrame,
            playerPos.x + offsetX + (TILE_SIZE - frameWidth) / 2f,
            playerPos.y + offsetY + (TILE_SIZE - frameHeight) / 2f,
            frameWidth, frameHeight);
    }

    public void render(Batch batch) {
        try {
            if (batch == null) {
                logger.warn("{}: Cannot render with null batch", name);
                return;
            }

            if (texture == null) {
                logger.warn("{}: Cannot render with null or disposed texture", name);
                return;
            }

            batch.draw(texture, position.x, position.y);
        } catch (Exception e) {
            logger.error("{}: Error rendering character: {}", name, e.getMessage());
        }
    }

    public void dispose() {
        try {
            if (texture != null) {
                texture.dispose();
                logger.debug("{}: Texture disposed successfully", name);
            }
        } catch (Exception e) {
            logger.error("{}: Error disposing texture: {}", name, e.getMessage());
        }
    }
    public void autoCombineInventory() {
        Array<Item> inventory = getInventory();
        for (int i = 0; i < inventory.size; i++) {
            Item a = inventory.get(i);
            if (a == null) continue;

            for (int j = i + 1; j < inventory.size; j++) {
                Item b = inventory.get(j);
                if (b == null) continue;

                // Combina iguais
                if (a.getName().equalsIgnoreCase(b.getName())) {
                    a.addQuantity(b.getQuantity());
                    inventory.set(j, null);
                } else {
                    List<Item> pair = new ArrayList<>();
                    pair.add(a);
                    pair.add(b);
                    Item crafted = CraftManager.tryCraft(pair);
                    if (crafted != null) {
                        inventory.set(i, crafted);
                        inventory.set(j, null);
                        return; // recomeça após crafting
                    }
                }
            }
        }
    }
}
