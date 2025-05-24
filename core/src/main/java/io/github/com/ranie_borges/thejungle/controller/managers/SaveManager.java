package io.github.com.ranie_borges.thejungle.controller.managers;

import com.google.gson.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.BitmapFont; // Often problematic for serialization
import com.badlogic.gdx.graphics.g2d.GlyphLayout; // Also problematic
import com.badlogic.gdx.graphics.Mesh; // Highly problematic
import java.nio.Buffer;
import java.nio.FloatBuffer; // Explicitly add FloatBuffer

import io.github.com.ranie_borges.thejungle.controller.exceptions.save.SaveManagerException;
import io.github.com.ranie_borges.thejungle.controller.adapters.AmbientAdapter;
import io.github.com.ranie_borges.thejungle.controller.adapters.CharacterAdapter;
import io.github.com.ranie_borges.thejungle.controller.adapters.ItemAdapter;
import io.github.com.ranie_borges.thejungle.controller.adapters.OffsetDateTimeAdapter;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Creature;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import io.github.com.ranie_borges.thejungle.model.stats.AmbientData;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
// import java.lang.reflect.Field; // Not strictly needed for FieldAttributes if using getDeclaredClass
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
// import java.util.List; // Not directly used here but often in initializeTransientData

public class SaveManager {
    private static final Logger logger = LoggerFactory.getLogger(SaveManager.class);
    private static final String SAVE_DIRECTORY = "saves/";
    private static final String JSON_EXTENSION = ".json";
    private final Gson gson; // Create Gson instance once

    public SaveManager() {
        try {
            createSaveDirectory();
        } catch (Exception e) {
            logger.error("Failed to initialize SaveManager: {}", e.getMessage());
            throw new SaveManagerException("Failed to initialize SaveManager", e);
        }
        this.gson = buildGsonInstance(); // Initialize Gson with strategy
    }

    private void createSaveDirectory() {
        try {
            Path path = Paths.get(SAVE_DIRECTORY);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                logger.info("Created save directory at: {}", SAVE_DIRECTORY);
            }
        } catch (IOException e) {
            logger.error("Failed to create save directory: {}", e.getMessage());
            throw new SaveManagerException("Failed to create save directory", e);
        }
    }

    private Gson buildGsonInstance() { // Renamed from getGsonInstance to indicate it builds
        return new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setPrettyPrinting()
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
            .registerTypeAdapter(Item.class, new ItemAdapter())
            .registerTypeAdapter(Character.class, new CharacterAdapter())
            .registerTypeAdapter(Ambient.class, new AmbientAdapter())
            .setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    Class<?> fieldType = f.getDeclaredClass();
                    // More comprehensive list of types to skip
                    return fieldType == Sprite.class ||
                        fieldType == Texture.class ||
                        fieldType == TextureRegion.class ||
                        fieldType == BitmapFont.class ||
                        fieldType == GlyphLayout.class ||
                        fieldType == Mesh.class ||
                        fieldType == FloatBuffer.class || // Explicitly skip FloatBuffer
                        Buffer.class.isAssignableFrom(fieldType); // Skip any NIO Buffer
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    // Skip these classes if Gson tries to serialize them directly
                    return clazz == Sprite.class ||
                        clazz == Texture.class ||
                        clazz == TextureRegion.class ||
                        clazz == BitmapFont.class ||
                        clazz == GlyphLayout.class ||
                        clazz == Mesh.class ||
                        clazz == FloatBuffer.class || // Explicitly skip FloatBuffer
                        Buffer.class.isAssignableFrom(clazz);
                }
            })
            .enableComplexMapKeySerialization()
            .create();
    }

    public boolean saveGame(GameState gameState, String saveName) {
        if (gameState == null) {
            logger.error("Cannot save game: game state is null");
            return false;
        }

        String effectiveSaveName = saveName;
        if (effectiveSaveName == null || effectiveSaveName.trim().isEmpty()) {
            logger.warn("Empty save name provided, using default 'autosave'");
            effectiveSaveName = "autosave";
        }
        if (!effectiveSaveName.toLowerCase().endsWith(JSON_EXTENSION)) {
            effectiveSaveName += JSON_EXTENSION;
        }

        File saveFile = new File(SAVE_DIRECTORY + effectiveSaveName);
        try (Writer writer = new FileWriter(saveFile)) {
            if (gameState.getCurrentAmbient() != null && gameState.getCurrentMap() != null) {
                String ambientName = gameState.getCurrentAmbient().getName();
                AmbientData currentAmbientData = gameState.getVisitedAmbients().computeIfAbsent(ambientName, k -> new AmbientData());
                currentAmbientData.setMap(gameState.getCurrentMap());
            }
            // Use the pre-configured Gson instance
            this.gson.toJson(gameState, writer);
            logger.info("Game saved successfully to: {}", saveFile.getAbsolutePath());
            return true;
        } catch (Exception e) {
            logger.error("Error saving game to {}: {}", effectiveSaveName, e.getMessage(), e);
            return false;
        }
    }

    public GameState loadGame(String filePathOrName) {
        if (filePathOrName == null || filePathOrName.trim().isEmpty()) {
            logger.error("Cannot load game: filename is null or empty");
            return null;
        }

        File saveFile;
        if (filePathOrName.contains(File.separator) || filePathOrName.startsWith("saves" + File.separator)) {
            saveFile = new File(filePathOrName);
        } else {
            String fileName = filePathOrName.toLowerCase().endsWith(JSON_EXTENSION) ? filePathOrName : filePathOrName + JSON_EXTENSION;
            saveFile = new File(SAVE_DIRECTORY + fileName);
        }

        if (!saveFile.exists()) {
            logger.warn("Save file does not exist: {}", saveFile.getAbsolutePath());
            return null;
        }

        try (Reader reader = new FileReader(saveFile)) {
            // Use the pre-configured Gson instance
            GameState gameState = this.gson.fromJson(reader, GameState.class);

            if (gameState != null) {
                logger.info("Game loaded successfully from: {}", saveFile.getAbsolutePath());
                initializeTransientData(gameState); // Call post-load initialization
            } else {
                logger.error("Failed to deserialize GameState from: {}. Gson returned null.", saveFile.getAbsolutePath());
            }
            return gameState;

        } catch (JsonSyntaxException e) {
            logger.error("JSON syntax error while loading game from {}: {}", saveFile.getAbsolutePath(), e.getMessage(), e);
        } catch (JsonIOException e) {
            logger.error("JSON I/O error while loading game from {}: {}", saveFile.getAbsolutePath(), e.getMessage(), e);
        } catch (IOException e) {
            logger.error("I/O error while loading game from {}: {}", saveFile.getAbsolutePath(), e.getMessage(), e);
        }
        return null;
    }

    private void initializeTransientData(GameState gameState) {
        if (gameState == null) return;
        logger.debug("Performing post-load initialization of transient data...");

        if (gameState.getPlayerCharacter() != null) {
            gameState.getPlayerCharacter().loadPlayerAnimations();
            if (gameState.getPlayerCharacter().getInventory() != null) {
                for (Item item : gameState.getPlayerCharacter().getInventory()) {
                    if (item != null) {
                        // Assuming TextureManager is not available here, item needs to self-initialize
                        // or this method needs a TextureManager parameter.
                        // For now, relying on item.initializeTransientGraphics() if it can work stand-alone
                        // or if it's designed to be called again with a TextureManager by another part of the code.
                        item.initializeTransientGraphics();
                    }
                }
            }
        }

        // Initialize sprites for creatures:
        // This depends on how creatures are persisted. If they are just types/counts and respawned,
        // their constructors + reloadSprites called by ResourceController will handle it.
        // If specific creature instances are saved in GameState/AmbientData and need sprite reloading:
        // for (Creature creature : gameState.getAllCreaturesThatWereSaved()) {
        //     creature.reloadSprites();
        // }


        if (gameState.getVisitedAmbients() != null) {
            for (AmbientData ad : gameState.getVisitedAmbients().values()) {
                if (ad.getRemainingResources() != null) {
                    for (Item item : ad.getRemainingResources()) {
                        if (item != null) {
                            item.initializeTransientGraphics();
                        }
                    }
                }
            }
        }
        logger.debug("Post-load initialization complete (basic). Sprites requiring TextureManager might need further init.");
    }

    public boolean deleteSave(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            logger.error("Cannot delete save: filename is null or empty");
            return false;
        }
        String effectiveFilename = filename;
        if (!effectiveFilename.toLowerCase().endsWith(JSON_EXTENSION)) {
            effectiveFilename += JSON_EXTENSION;
        }
        File saveFile = new File(SAVE_DIRECTORY + effectiveFilename);
        try {
            if (!saveFile.exists()) {
                logger.warn("Save file to delete does not exist: {}", saveFile.getAbsolutePath());
                return false;
            }
            boolean deleted = Files.deleteIfExists(saveFile.toPath());
            if (deleted) {
                logger.info("Save file deleted: {}", saveFile.getAbsolutePath());
            } else {
                logger.error("Failed to delete save file (Files.deleteIfExists returned false): {}", saveFile.getAbsolutePath());
            }
            return deleted;
        } catch (SecurityException e) {
            logger.error("Security exception deleting save file {}: {}", saveFile.getAbsolutePath(), e.getMessage(), e);
        } catch (IOException e) {
            logger.error("IOException deleting save file {}: {}", saveFile.getAbsolutePath(), e.getMessage(), e);
        }
        return false;
    }

    public File[] getSaveFiles() {
        File directory = new File(SAVE_DIRECTORY);
        if (!directory.exists() || !directory.isDirectory()) {
            createSaveDirectory();
            return new File[0];
        }
        return directory.listFiles((dir, name) -> name.toLowerCase().endsWith(JSON_EXTENSION));
    }
}
