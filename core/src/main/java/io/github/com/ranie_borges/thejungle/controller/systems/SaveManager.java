package io.github.com.ranie_borges.thejungle.controller.systems;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.com.ranie_borges.thejungle.controller.exceptions.save.SaveManagerException;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.characters.Doctor;
import io.github.com.ranie_borges.thejungle.model.entity.characters.Hunter;
import io.github.com.ranie_borges.thejungle.model.entity.characters.Lumberjack;
import io.github.com.ranie_borges.thejungle.model.entity.characters.Survivor;
import io.github.com.ranie_borges.thejungle.model.stats.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class SaveManager {
    private static final Logger logger = LoggerFactory.getLogger(SaveManager.class);
    private static final String SAVE_DIRECTORY = "saves/";
    private static final String JSON = ".json";
    private final Gson gson;

    public SaveManager() {
        try {
            // Create a custom TypeAdapter for OffsetDateTime
            TypeAdapter<OffsetDateTime> offsetDateTimeAdapter = new TypeAdapter<OffsetDateTime>() {
                @Override
                public void write(JsonWriter out, OffsetDateTime value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                    } else {
                        out.value(value.toString());
                    }
                }

                @Override
                public OffsetDateTime read(JsonReader in) throws IOException {
                    try {
                        String dateStr = in.nextString();
                        return dateStr == null ? null : OffsetDateTime.parse(dateStr);
                    } catch (DateTimeParseException e) {
                        logger.error("Failed to parse OffsetDateTime: {}", e.getMessage());
                        return OffsetDateTime.now();
                    }
                }
            };

            // Configure Gson with runtime type information
            this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(OffsetDateTime.class, offsetDateTimeAdapter)
                .registerTypeHierarchyAdapter(Character.class, new CharacterAdapter())
                .serializeSpecialFloatingPointValues()
                .create();

            // Create save directory if it doesn't exist
            createSaveDirectory();
        } catch (Exception e) {
            logger.error("Failed to initialize SaveManager: {}", e.getMessage());
            throw new SaveManagerException("Failed to initialize SaveManager", e);
        }
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

    public boolean saveGame(GameState gameState, String saveName) {
        if (gameState == null) {
            logger.error("Cannot save game: game state is null");
            return false;
        }

        if (saveName == null || saveName.trim().isEmpty()) {
            logger.warn("Empty save name provided, using timestamp instead");
            saveName = "save_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        }

        String filename = saveName + (saveName.endsWith(JSON) ? "" : JSON);
        Writer writer = null;

        try {
            File saveFile = new File(SAVE_DIRECTORY + filename);
            if (!saveFile.getParentFile().exists()) {
                createSaveDirectory();
            }

            writer = new FileWriter(saveFile);
            gson.toJson(gameState, writer);
            logger.info("Game saved successfully to: {}", filename);
            return true;
        } catch (JsonIOException e) {
            logger.error("JSON writing error while saving game: {}", e.getMessage());
            return false;
        } catch (IOException e) {
            logger.error("I/O error while saving game: {}", e.getMessage());
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.error("Failed to close file writer: {}", e.getMessage());
                }
            }
        }
    }

    public GameState loadGame(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            logger.error("Cannot load game: filename is null or empty");
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }

        String filePath = SAVE_DIRECTORY + (filename.endsWith(JSON) ? filename : filename + JSON);
        Reader reader = null;

        try {
            File saveFile = new File(filePath);
            if (!saveFile.exists() || !saveFile.isFile()) {
                logger.error("Save file not found: {}", filePath);
                return null;
            }

            reader = new FileReader(saveFile);
            GameState gameState = gson.fromJson(reader, GameState.class);

            if (gameState == null) {
                logger.error("Failed to deserialize game state from: {}", filePath);
                return null;
            }

            logger.info("Game loaded successfully from: {}", filename);
            return gameState;
        } catch (JsonSyntaxException e) {
            logger.error("Invalid JSON format in save file: {}", e.getMessage());
            return null;
        } catch (IOException e) {
            logger.error("I/O error while loading game: {}", e.getMessage());
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error("Failed to close file reader: {}", e.getMessage());
                }
            }
        }
    }

    public String[] getSaveFiles() {
        try {
            File saveDir = new File(SAVE_DIRECTORY);
            if (!saveDir.exists() || !saveDir.isDirectory()) {
                logger.warn("Save directory does not exist or is not a directory");
                return new String[0];
            }

            return saveDir.list((dir, name) -> name.toLowerCase().endsWith(JSON));
        } catch (SecurityException e) {
            logger.error("Security exception accessing save directory: {}", e.getMessage());
            return new String[0];
        }
    }

    public boolean deleteSave(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            logger.error("Cannot delete save: filename is null or empty");
            return false;
        }

        String filePath = SAVE_DIRECTORY + (filename.endsWith(JSON) ? filename : filename + JSON);
        try {
            File saveFile = new File(filePath);
            if (!saveFile.exists()) {
                logger.warn("Save file does not exist: {}", filePath);
                return false;
            }

            boolean deleted = saveFile.delete();
            if (deleted) {
                logger.info("Save file deleted: {}", filePath);
            } else {
                logger.error("Failed to delete save file: {}", filePath);
            }

            return deleted;
        } catch (SecurityException e) {
            logger.error("Security exception deleting save file: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Custom adapter to handle Character subclasses properly
     */
    private static class CharacterAdapter extends TypeAdapter<Character> {
        private final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

        @Override
        public void write(JsonWriter out, Character character) throws IOException {
            if (character == null) {
                out.nullValue();
                return;
            }

            out.beginObject();
            out.name("type").value(character.getCharacterType());
            out.name("data").value(gson.toJson(character));
            out.endObject();
        }

        @Override
        public Character read(JsonReader in) throws IOException {
            in.beginObject();
            String type = null;
            String data = null;

            while (in.hasNext()) {
                String name = in.nextName();
                if (name.equals("type")) {
                    type = in.nextString();
                } else if (name.equals("data")) {
                    data = in.nextString();
                } else {
                    in.skipValue();
                }
            }

            in.endObject();

            if (type == null || data == null) {
                return null;
            }

            try {
                switch (type) {
                    case "Survivor":
                        return gson.fromJson(data, Survivor.class);
                    case "Hunter":
                        return gson.fromJson(data, Hunter.class);
                    case "Lumberjack":
                        return gson.fromJson(data, Lumberjack.class);
                    case "Doctor":
                        return gson.fromJson(data, Doctor.class);
                    default:
                        logger.error("Unknown character type: {}", type);
                        return null;
                }
            } catch (JsonSyntaxException e) {
                logger.error("Failed to deserialize character of type {}: {}", type, e.getMessage());
                return null;
            }
        }
    }
}
