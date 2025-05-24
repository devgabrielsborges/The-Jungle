package io.github.com.ranie_borges.thejungle.model.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.google.gson.annotations.Expose;
import io.github.com.ranie_borges.thejungle.model.entity.characters.Doctor;
import io.github.com.ranie_borges.thejungle.model.entity.characters.Lumberjack;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.Fish; // Import Fish
import io.github.com.ranie_borges.thejungle.model.entity.itens.*;
import io.github.com.ranie_borges.thejungle.model.enums.Trait;
import io.github.com.ranie_borges.thejungle.model.entity.interfaces.ICharacter;
import io.github.com.ranie_borges.thejungle.model.entity.interfaces.IInventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Jungle;
import io.github.com.ranie_borges.thejungle.view.interfaces.UI; // For TILE_SIZE

import java.util.*;

public abstract class Character implements ICharacter, IInventory, UI { // Implement UI for TILE_SIZE
    private static final Logger logger = LoggerFactory.getLogger(Character.class);
    private static final Random random = new Random(); // For random chances

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
    private final float maxCarryWeight = 30f;

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
    private float speed = 100f;

    @Expose
    private Vector2 position;
    private transient Texture texture;
    private transient Animation<TextureRegion> playerIdleUp;
    private transient Animation<TextureRegion> playerIdleDown;
    private transient Animation<TextureRegion> playerIdleLeft;
    private transient Animation<TextureRegion> playerIdleRight;
    private transient Animation<TextureRegion> playerWalkUp;
    private transient Animation<TextureRegion> playerWalkDown;
    private transient Animation<TextureRegion> playerWalkLeft;
    private transient Animation<TextureRegion> playerWalkRight;

    private enum PlayerState {
        IDLE_UP, IDLE_DOWN, IDLE_LEFT, IDLE_RIGHT, WALK_UP, WALK_DOWN, WALK_LEFT, WALK_RIGHT
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private transient Direction lastDirection = Direction.DOWN;
    private transient PlayerState currentState = PlayerState.IDLE_DOWN;
    private transient boolean isMoving = false;
    private transient float stateTime = 0;

    private boolean inTallGrass = false;

    protected Character(
        String name,
        float life,
        float energy,
        float sanity,
        float attackDamage,
        float xPosition,
        float yPosition) {
        try {
            this.name = name != null ? name : "Unknown";
            this.life = Math.max(0, life);
            this.hunger = Math.max(0, (float) 100);
            this.thirsty = Math.max(0, (float) 100);
            this.energy = Math.max(0, energy);
            this.sanity = Math.max(0, sanity);
            this.attackDamage = Math.max(0, attackDamage);
            this.inventory = new Array<>(inventoryInitialCapacity);
            this.traits = new ArrayList<>();
            this.position = new Vector2(xPosition, yPosition);
            this.characterType = this.getClass().getSimpleName();
        } catch (Exception e) {
            logger.error("Error creating character {}: {}", name, e.getMessage());
            throw e;
        }
    }

    public float getMaxCarryWeight() {
        return this.maxCarryWeight;
    }

    public float getHungerDepletionModifier() { return 1.0f; }
    public float getThirstDepletionModifier() { return 1.0f; }
    public float getHealingEffectivenessModifier() { return 1.0f; }
    public int getFoodWaterGatheringBonusQuantity() { return 0; }
    public float getWoodCuttingYieldModifier() { return 1.0f; }


    public void loadPlayerAnimations() {
        playerIdleDown = loadAnimation("personagem_parado_frente.png", 0.5f, 2);
        playerIdleUp = loadAnimation("personagem_parado_costas.png", 0.5f, 2);
        playerIdleLeft = loadAnimation("personagem_parado_esquerda.png", 0.5f, 2);
        playerIdleRight = loadAnimation("personagem_parado_direita.png", 0.5f, 2);
        playerWalkDown = loadAnimation("personagem_andando_frente.png", 0.1f, 4);
        playerWalkUp = loadAnimation("personagem_andando_costas.png", 0.1f, 4);
        playerWalkLeft = loadAnimation("personagem_andando_esquerda.png", 0.1f, 4);
        playerWalkRight = loadAnimation("personagem_andando_direita.png", 0.1f, 4);

        if (playerIdleDown == null) {
            logger.warn("playerIdleDown animation is null. Creating fallback texture for Character.");
            Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
            pixmap.setColor(1, 0, 1, 0.5f);
            pixmap.fill();
            this.texture = new Texture(pixmap);
            pixmap.dispose();
        }
    }

    public boolean setInitialSpawn(
        int[][] map, int mapWidth, int mapHeight, int tileSize,
        int tileGrass, int tileCave, String ambientName,
        io.github.com.ranie_borges.thejungle.model.world.Ambient ambient) {
        try {
            int x, y;
            int attempts = 0;
            int maxAttempts = 1000;
            boolean positionFound = false;
            do {
                x = (int) (Math.random() * mapWidth);
                y = (int) (Math.random() * mapHeight);
                attempts++;
                if (y < 0 || y >= mapHeight || x < 0 || x >= mapWidth) continue;
                int tileType = map[y][x];
                boolean isValidTile = ((tileType == tileGrass
                    || (ambientName.equalsIgnoreCase("Cave") && tileType == tileCave))
                    && !(ambient instanceof Jungle && ((Jungle) ambient).isTallGrass(x, y)));
                if (isValidTile) {
                    getPosition().set(x * tileSize, y * tileSize);
                    positionFound = true;
                }
            } while (!positionFound && attempts < maxAttempts);
            if (!positionFound) {
                float fallbackX = ((float) mapWidth / 2) * tileSize;
                float fallbackY = ((float) mapHeight / 2) * tileSize;
                getPosition().set(fallbackX, fallbackY);
                logger.warn("{}: Could not find valid spawn position after {} attempts, using fallback at ({}, {})",
                    getName(), maxAttempts, fallbackX / tileSize, fallbackY / tileSize);
            } else {
                logger.info("{}: Player spawned at ({}, {})", getName(), (int) (getPosition().x / tileSize),
                    (int) (getPosition().y / tileSize));
            }
            return true;
        } catch (Exception e) {
            logger.error("{}: Error during spawn positioning: {}", getName(), e.getMessage(), e);
            float fallbackX = ((float) mapWidth / 2) * tileSize;
            float fallbackY = ((float) mapHeight / 2) * tileSize;
            getPosition().set(fallbackX, fallbackY);
            return false;
        }
    }

    private Animation<TextureRegion> loadAnimation(String filename, float frameDuration, int framesCount) {
        try {
            Texture spriteSheet = new Texture(Gdx.files.internal("sprites/character/" + filename));
            int frameWidth = spriteSheet.getWidth() / framesCount;
            int frameHeight = spriteSheet.getHeight();
            TextureRegion[][] tmp = TextureRegion.split(spriteSheet, frameWidth, frameHeight);
            Array<TextureRegion> frames = new Array<>(framesCount);
            for (int i = 0; i < framesCount; i++) {
                frames.add(tmp[0][i]);
            }
            return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
        } catch (Exception e) {
            logger.error("Failed to load animation sprite sheet: sprites/character/{}", filename, e);
            return null;
        }
    }

    private void updatePlayerState() {
        if (isMoving) {
            switch (lastDirection) {
                case UP: currentState = PlayerState.WALK_UP; break;
                case DOWN: currentState = PlayerState.WALK_DOWN; break;
                case LEFT: currentState = PlayerState.WALK_LEFT; break;
                case RIGHT: currentState = PlayerState.WALK_RIGHT; break;
            }
        } else {
            switch (lastDirection) {
                case UP: currentState = PlayerState.IDLE_UP; break;
                case DOWN: currentState = PlayerState.IDLE_DOWN; break;
                case LEFT: currentState = PlayerState.IDLE_LEFT; break;
                case RIGHT: currentState = PlayerState.IDLE_RIGHT; break;
            }
        }
    }

    private TextureRegion getFrameForCurrentState(float stateTime) {
        Animation<TextureRegion> currentAnimation = playerIdleDown;
        switch (currentState) {
            case IDLE_UP: currentAnimation = playerIdleUp; break;
            case IDLE_LEFT: currentAnimation = playerIdleLeft; break;
            case IDLE_RIGHT: currentAnimation = playerIdleRight; break;
            case WALK_UP: currentAnimation = playerWalkUp; break;
            case WALK_DOWN: currentAnimation = playerWalkDown; break;
            case WALK_LEFT: currentAnimation = playerWalkLeft; break;
            case WALK_RIGHT: currentAnimation = playerWalkRight; break;
        }
        return (currentAnimation != null) ? currentAnimation.getKeyFrame(stateTime, true) : null;
    }

    public void move(float deltaX, float deltaY) {
        try {
            position.x += deltaX;
            position.y += deltaY;
            isMoving = (deltaX != 0 || deltaY != 0);
            if (deltaY > 0) lastDirection = Direction.UP;
            else if (deltaY < 0) lastDirection = Direction.DOWN;
            else if (deltaX < 0) lastDirection = Direction.LEFT;
            else if (deltaX > 0) lastDirection = Direction.RIGHT;
            updatePlayerState();
        } catch (Exception e) {
            logger.error("{}: Erro ao mover personagem: {}", name, e.getMessage());
        }
    }

    public boolean tryMove(float delta, int[][] map, int tileSize, int tileWall, int tileDoor, int tileCave,
                           int mapWidth, int mapHeight) {
        float currentSpeed = getSpeed() > 0 ? getSpeed() : 100f;
        float speedMultiplier = isInTallGrass() ? 0.5f : 1.0f;
        float finalSpeed = currentSpeed * speedMultiplier;
        float deltaX = 0, deltaY = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) deltaY = finalSpeed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) deltaY = -finalSpeed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) deltaX = -finalSpeed * delta;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) deltaX = finalSpeed * delta;
        isMoving = (deltaX != 0 || deltaY != 0);

        if (isMoving) {
            float nextX = getPosition().x + deltaX;
            float nextY = getPosition().y + deltaY;
            int targetTileX = (int) ((nextX + tileSize / 2f) / tileSize);
            int targetTileY = (int) ((nextY + tileSize / 4f) / tileSize);
            if (targetTileX >= 0 && targetTileX < mapWidth && targetTileY >= 0 && targetTileY < mapHeight) {
                int tileType = map[targetTileY][targetTileX];
                if (tileType != tileWall) {
                    move(deltaX, deltaY);
                    return tileType == tileDoor;
                }
            }
        } else {
            updatePlayerState();
        }
        return false;
    }

    public void updateStateTime(float delta) {
        stateTime += delta;
    }

    public TextureRegion getCurrentFrame() {
        TextureRegion frame = getFrameForCurrentState(stateTime);
        if (frame == null && texture != null) {
            logger.warn("Current animation frame is null for state {}. Using fallback texture.", currentState);
            return new TextureRegion(texture);
        } else if (frame == null && texture == null) {
            logger.error("Cannot get current frame: no animation and no fallback texture for Character.");
            Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
            pixmap.setColor(1, 0, 0, 1);
            pixmap.fill();
            Texture placeholder = new Texture(pixmap);
            pixmap.dispose();
            return new TextureRegion(placeholder);
        }
        return frame;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name != null ? name : "Unknown"; }
    public float getLife() { return life; }
    public void setLife(float life) { this.life = Math.max(0, Math.min(this instanceof Doctor ? 80f : (this instanceof Lumberjack ? 120f : 100f), life)); } // Max life can vary
    public float getHunger() { return hunger; }
    public void setHunger(float hunger) { this.hunger = Math.max(0, Math.min(100, hunger)); }
    public float getThirsty() { return thirsty; }
    public void setThirsty(float thirsty) { this.thirsty = Math.max(0, Math.min(100, thirsty)); }
    public float getEnergy() { return energy; }
    public void setEnergy(float energy) { this.energy = Math.max(0, Math.min(this instanceof Lumberjack ? 100f : (this instanceof Doctor ? 60f : 80f) , energy)); } // Max energy can vary
    public float getSanity() { return sanity; }
    public void setSanity(float sanity) { this.sanity = Math.max(0, Math.min(100, sanity)); }
    public boolean isInTallGrass() { return inTallGrass; }
    public void setInTallGrass(boolean inTallGrass) { this.inTallGrass = inTallGrass; }
    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }

    public Array<Item> getInventory() { return inventory; }
    public void setInventory(Array<Item> inventory) { this.inventory = inventory != null ? inventory : new Array<>(); }
    public int getMaxInventoryCapacity() { return maxInventoryCapacity; }
    public void setMaxInventoryCapacity(int maxInventoryCapacity) { this.maxInventoryCapacity = Math.max(0, maxInventoryCapacity); }
    public int getInventoryInitialCapacity() { return inventoryInitialCapacity; }
    public void setInventoryInitialCapacity(int capacity) {
        this.inventoryInitialCapacity = Math.max(1, capacity);
    }

    public void tryCollectNearbyMaterial(List<Material> materiaisNoMapa) {
        Iterator<Material> iterator = materiaisNoMapa.iterator();
        while (iterator.hasNext()) {
            Material materialOnMap = iterator.next();
            if (materialOnMap == null) continue;
            float dist = getPosition().dst(materialOnMap.getPosition());
            float collectionRadius = TILE_SIZE * 0.8f; // Reduced radius for more precise collection
            if (dist < collectionRadius) {
                if (isInventoryFull()) {
                    logger.info("{}'s inventory is full. Cannot collect {}.", getName(), materialOnMap.getName());
                    return;
                }
                Item itemToCollect = null;
                int baseQuantity = 1;
                int bonusQuantity = 0;
                if ("Berry".equalsIgnoreCase(materialOnMap.getName())) {
                    itemToCollect = Food.createBerry(); // This creates a Food item
                    bonusQuantity = getFoodWaterGatheringBonusQuantity();
                } else if ("Medicinal".equalsIgnoreCase(materialOnMap.getName()) && "Plant".equalsIgnoreCase(materialOnMap.getType())) {
                    itemToCollect = Material.createMedicinalPlant();
                } else if ("rock".equalsIgnoreCase(materialOnMap.getName())) {
                    itemToCollect = Material.createSmallRock();
                } else if ("stick".equalsIgnoreCase(materialOnMap.getName())) {
                    itemToCollect = Material.createStick();
                } else if ("Tree".equalsIgnoreCase(materialOnMap.getName())) {
                    continue;
                }

                if (itemToCollect != null) {
                    itemToCollect.setQuantity(baseQuantity + bonusQuantity);
                    if (!canCarryMore(itemToCollect.getWeight() * itemToCollect.getQuantity())) { // Check total weight of stack
                        logger.info("{} cannot carry more weight for {} ({} units).", getName(), itemToCollect.getName(), itemToCollect.getQuantity());
                        continue;
                    }
                    insertItemInInventory(itemToCollect);
                    iterator.remove();
                    logger.info("{} collected {} of {}.", getName(), itemToCollect.getQuantity(), itemToCollect.getName());
                    if (bonusQuantity > 0 && (itemToCollect instanceof Food || itemToCollect instanceof Drinkable || "Berry".equalsIgnoreCase(materialOnMap.getName()))) {
                        logger.info("Hunter's ability yielded {} extra {}!", bonusQuantity, itemToCollect.getName());
                    }
                    return;
                }
            }
        }
    }

    public void insertItemInInventory(Item item) {
        if (item == null || item.getQuantity() <= 0) return;
        for (Item existingItem : inventory) {
            if (existingItem != null && existingItem.getName().equals(item.getName()) && existingItem.getClass() == item.getClass()) { // Also check class for type safety
                float weightPerUnit = (existingItem.getWeight() > 0 && existingItem.getQuantity() > 0) ? existingItem.getWeight() / existingItem.getQuantity() : (item.getWeight() / item.getQuantity());
                if (canCarryMore(weightPerUnit * item.getQuantity())) {
                    existingItem.addQuantity(item.getQuantity());
                    currentWeight += weightPerUnit * item.getQuantity();
                    logger.debug("Stacked {} to {}. New quantity: {}", item.getName(), existingItem.getName(), existingItem.getQuantity());
                    return;
                } else {
                    logger.info("Cannot stack more {}, not enough carry capacity.", item.getName());
                }
            }
        }
        if (!isInventoryFull()) {
            float itemTotalWeight = item.getWeight();
            if (canCarryMore(itemTotalWeight)) {
                inventory.add(item);
                currentWeight += itemTotalWeight;
                logger.debug("Added new item {} to inventory.", item.getName());
            } else {
                logger.info("Cannot add new item {}, not enough carry capacity.", item.getName());
            }
        } else {
            logger.info("Inventory is full. Cannot add new item {}.", item.getName());
        }
    }

    public Item getItem(int index) {
        if (index >= 0 && index < inventory.size) return inventory.get(index);
        return null;
    }

    public void dropItem(int index) {
        if (isInventoryIndexOk(index)) {
            Item item = inventory.get(index);
            if (item != null) {
                inventory.removeIndex(index);
                currentWeight -= item.getWeight();
                if (currentWeight < 0) currentWeight = 0;
                logger.info("{} dropped: {}", getName(), item.getName());
            }
        } else {
            logger.warn("{} cannot drop item at invalid index: {}", getName(), index);
        }
    }

    public void cutTree() {
        int woodYield = (int) (1 * getWoodCuttingYieldModifier());
        logger.info("{} attempts to cut a tree. Modified yield: {}", getName(), woodYield);
        for (int i = 0; i < woodYield; i++) {
            if (isInventoryFull()) {
                logger.warn("{}'s inventory is full. Cannot collect more wood.", getName());
                break;
            }
            Material woodLog = Material.createWoodLog();
            if (canCarryMore(woodLog.getWeight())) {
                insertItemInInventory(woodLog);
                logger.info("{} collected a Wood Log.", getName());
            } else {
                logger.warn("{} cannot carry more wood.", getName());
                break;
            }
        }
        if (getWoodCuttingYieldModifier() > 1.0f && woodYield > 1) {
            logger.info("Lumberjack's skill yielded extra wood!");
        }
    }

    public void cutTreeWithAxe() {
        Tool axe = null;
        for (Item item : inventory) {
            if (item instanceof Tool && item.getName().equalsIgnoreCase("Axe")) {
                axe = (Tool) item;
                break;
            }
        }
        if (axe == null) {
            logger.warn("{} tried to cut a tree with an axe, but has no Axe. Defaulting to manual cutting.", getName());
            cutTree();
            return;
        }
        int woodYield = (int) (2 * getWoodCuttingYieldModifier());
        logger.info("{} attempts to cut a tree with an Axe. Modified yield: {}", getName(), woodYield);
        for (int i = 0; i < woodYield; i++) {
            if (isInventoryFull()) {
                logger.warn("{}'s inventory is full. Cannot collect more wood.", getName());
                break;
            }
            Material woodLog = Material.createWoodLog();
            if (canCarryMore(woodLog.getWeight())) {
                insertItemInInventory(woodLog);
                logger.info("{} collected a Wood Log using Axe.", getName());
            } else {
                logger.warn("{} cannot carry more wood with Axe.", getName());
                break;
            }
        }
        if (getWoodCuttingYieldModifier() > 1.0f && woodYield > 2) {
            logger.info("Lumberjack's skill with Axe yielded extra wood!");
        }
        axe.useItem();
        if (axe.getDurability() <= 0) {
            dropItem(axe);
            logger.info("The Axe broke after use and was dropped.");
        }
    }

    public void collectWithKnife(Item resource) { /* ... implementation ... */ }
    public void emptyInventory() { inventory.clear(); currentWeight = 0; }
    @Override
    public boolean canCarryMore(float itemWeight) { return (currentWeight + itemWeight) <= maxCarryWeight; }
    public void increaseInventoryCapacity(int newCapacity) {
        if (isNewInventoryCapacityOk(newCapacity)) {
            this.inventoryInitialCapacity = newCapacity;
            logger.info("{} inventory slot capacity target set to {}", getName(), newCapacity);
        } else {
            logger.warn("{} invalid new inventory capacity: {}", getName(), newCapacity);
        }
    }
    public boolean isNewInventoryCapacityOk(int newCapacity) { return newCapacity > 0 && newCapacity <= maxInventoryCapacity; }
    public boolean isInventoryFull() { return inventory.size >= inventoryInitialCapacity; }
    public boolean isInventoryIndexOk(int index) { return index >= 0 && index < inventory.size; }
    public boolean isInventoryIndexFree(int index) { return isInventoryIndexOk(index) && inventory.get(index) == null; }
    public boolean isInventoryEmpty() { return inventory.isEmpty(); }
    public int getInventorySize() { return inventory.size; }

    public void useItem(Item item) {
        if (item == null || !inventory.contains(item, true)) {
            logger.warn("{}: Attempted to use item not in inventory or null item: {}", getName(), item != null ? item.getName() : "null");
            return;
        }
        logger.info("--------------------------------------------------------------------");
        logger.info("{}: PREPARING TO USE ITEM: Name: '{}', Type: {}, Qty: {}, Dur: {}",
            getName(), item.getName(), item.getClass().getSimpleName(), item.getQuantity(), item.getDurability());
        boolean itemFullyConsumedOrBroken = false;
        if (item instanceof Food) {
            Food food = (Food) item;
            food.useItem();
            setHunger(getHunger() + food.getNutritionalValue());
            logger.info("{}: Ate '{}'. Hunger restored by {}. New Hunger: {}", getName(), food.getName(), food.getNutritionalValue(), getHunger());
            itemFullyConsumedOrBroken = true;
        } else if (item instanceof Medicine) {
            Medicine medicine = (Medicine) item;
            float healingModifier = getHealingEffectivenessModifier();
            float lifeToRestore = (float) (medicine.getHealRatio() * healingModifier);
            setLife(getLife() + lifeToRestore);
            logger.info("{}: Used '{}'. Life restored by {} (base: {}, modifier: {}x). New Life: {}", getName(), medicine.getName(), lifeToRestore, medicine.getHealRatio(), healingModifier, getLife());
            if (healingModifier > 1.0f) {
                logger.info("Doctor's medical expertise enhanced healing!");
            }
            medicine.useItem();
            if (medicine.getDurability() <= 0) {
                itemFullyConsumedOrBroken = true;
            }
        } else if (item instanceof Drinkable) {
            Drinkable drinkable = (Drinkable) item;
            drinkable.useItem();
            logger.info("{}: Used Drinkable '{}'. Effects handled by item.", getName(), drinkable.getName());
            if (drinkable.getDurability() <= 0 || drinkable.getVolume() <= 0) {
                itemFullyConsumedOrBroken = true;
            }
        } else if (item instanceof Tool) {
            Tool tool = (Tool) item;
            tool.useItem();
            logger.info("{}: Used tool '{}'. Durability now: {}", getName(), tool.getName(), tool.getDurability());
            if (tool.getDurability() <= 0) {
                itemFullyConsumedOrBroken = true;
            }
        } else if (item instanceof Weapon) {
            Weapon weapon = (Weapon) item;
            weapon.useItem();
            logger.info("{}: 'Used' (interacted with) weapon '{}'. Durability now: {}", getName(), weapon.getName(), weapon.getDurability());
            if (weapon.getDurability() <= 0) {
                itemFullyConsumedOrBroken = true;
            }
        } else if (item instanceof Material) {
            if ("Medicinal".equalsIgnoreCase(item.getName()) && "Plant".equalsIgnoreCase(((Material) item).getType())) {
                Medicine tempMed = Medicine.fromMedicinalPlant((Material) item);
                float lifeToRestore = (float) (tempMed.getHealRatio() * getHealingEffectivenessModifier());
                setLife(getLife() + lifeToRestore);
                logger.info("{} used raw Material '{}' and restored {} life (modifier: {}x). New Life: {}", getName(), item.getName(), lifeToRestore, getHealingEffectivenessModifier(), getLife());
                if (getHealingEffectivenessModifier() > 1.0f) {
                    logger.info("Doctor's medical expertise enhanced healing from raw plant!");
                }
                itemFullyConsumedOrBroken = true;
            } else {
                logger.info("{}: Interacted with Material '{}'. No direct consumption effect here.", getName(), item.getName());
            }
        } else {
            item.useItem();
            logger.info("{}: Used generic item '{}'.", getName(), item.getName());
            if (item.getDurability() <= 0 && item.getQuantity() == 1) itemFullyConsumedOrBroken = true;
        }

        if (itemFullyConsumedOrBroken) {
            float weightOfItemBeingRemoved = item.getWeight(); // This is the total weight of the item/stack instance
            if (item.getQuantity() > 1 && !((item instanceof Medicine || item instanceof Tool || item instanceof Weapon || item instanceof Drinkable) && item.getDurability() <= 0)) {
                // This handles stackable consumables like Food, or a stack of basic materials like Medicinal Plants if they were stackable.
                // If an item from a stack is consumed.
                weightOfItemBeingRemoved = item.getWeight() / item.getQuantity(); // Weight of one unit from the stack
                item.setQuantity(item.getQuantity() - 1);
                currentWeight -= weightOfItemBeingRemoved;
                logger.info("Decremented quantity of {}. New quantity: {}. Item remains.", item.getName(), item.getQuantity());
            } else {
                // Item's last quantity was used OR a non-stackable (or last of stack) durable item broke.
                inventory.removeValue(item, true);
                currentWeight -= weightOfItemBeingRemoved; // Remove the full weight of the item instance
                logger.info("Item {} (Last Qty before removal:{}, Dur:{}) removed from inventory after use.", item.getName(), item.getQuantity(), item.getDurability());
            }
        }
        if (currentWeight < 0) currentWeight = 0;
        logger.info("--------------------------------------------------------------------");
    }

    public double getAttackDamage() { return attackDamage; }
    public void setAttackDamage(double attackDamage) { this.attackDamage = Math.max(0, attackDamage); }
    public String getCharacterType() { return characterType; }
    public void setCharacterType(String characterType) { this.characterType = (characterType == null || characterType.isEmpty()) ? this.getClass().getSimpleName() : characterType; }
    public Vector2 getPosition() { return position; }
    public void setPosition(float x, float y) { this.position.set(x,y); }

    @Override
    public void dropItem(Item item) {
        if (item != null && inventory.removeValue(item, true)) {
            currentWeight -= item.getWeight();
            if (currentWeight < 0) currentWeight = 0;
            logger.info("{} dropped: {}", getName(), item.getName());
        }
    }

    /**
     * Attempts to capture a fish from the provided list.
     * @param fishes The list of Fish objects currently in the ambient.
     * @return true if a fish was successfully captured, false otherwise.
     */
    public boolean tryCaptureFish(List<Fish> fishes) {
        Weapon spear = null;
        for (Item item : inventory) {
            if (item instanceof Weapon && ("Spear".equalsIgnoreCase(item.getName()) || "Wooden Spear".equalsIgnoreCase(item.getName()) || "Stone Spear".equalsIgnoreCase(item.getName()))) {
                spear = (Weapon) item;
                break;
            }
        }

        if (spear == null) {
            logger.info("{} has no spear to fish with.", getName());
            return false;
        }

        if (isInventoryFull()) {
            logger.info("{}'s inventory is full. Cannot capture fish.", getName());
            return false;
        }

        Iterator<Fish> fishIterator = fishes.iterator();
        while (fishIterator.hasNext()) {
            Fish fish = fishIterator.next();
            float distanceToFish = this.getPosition().dst(fish.getPosition());
            float fishingRange = TILE_SIZE * 1.5f;

            if (distanceToFish < fishingRange) {
                if (random.nextFloat() < 0.70f) {
                    logger.info("{} attempts to spear a fish...", getName());
                    fishIterator.remove(); // Remove fish from the ambient list

                    Set<Item> drops = Fish.createDrops(); // Static method to get drops
                    for (Item drop : drops) {
                        if (drop.getName().equalsIgnoreCase("Raw Fish")) {
                            if (canCarryMore(drop.getWeight())) {
                                insertItemInInventory(drop); // Add "Raw Fish" to inventory
                                logger.info("{} successfully speared a {} and obtained {}!", getName(), fish.getName(), drop.getName());

                                spear.useItem(); // Spear durability decreases
                                if (spear.getDurability() <= 0) {
                                    dropItem(spear); // Use character's dropItem to handle weight etc.
                                    logger.info("The {} broke after fishing.", spear.getName());
                                }
                                return true; // Fish captured
                            } else {
                                logger.info("{} caught a fish, but cannot carry more weight for {}.", getName(), drop.getName());
                                return false; // Failed due to inventory, but fish is gone.
                            }
                        }
                    }
                } else {
                    logger.info("{} tried to spear a fish but missed!", getName());
                    spear.useItem(); // Spear durability decreases even on miss
                    if (spear.getDurability() <= 0) {
                        dropItem(spear);
                        logger.info("The {} broke after a failed fishing attempt.", spear.getName());
                    }
                    return false; // Missed
                }
            }
        }
        logger.info("{} found no fish within range.", getName());
        return false; // No fish in range
    }


    public void render(Batch batch) {
        if (batch == null || !batch.isDrawing()) {
            return;
        }
        TextureRegion currentFrame = getCurrentFrame();
        if (currentFrame != null) {
            batch.draw(currentFrame, position.x, position.y, TILE_SIZE, TILE_SIZE);
        } else if (texture != null) {
            batch.draw(texture, position.x, position.y, TILE_SIZE, TILE_SIZE);
        }
    }
}
