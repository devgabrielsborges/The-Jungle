package io.github.com.ranie_borges.thejungle.view.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.Cannibal;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.Deer;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Material;
import io.github.com.ranie_borges.thejungle.model.events.events.SnakeEventManager;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Jungle;
import io.github.com.ranie_borges.thejungle.view.interfaces.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Helper class for rendering game elements
 */
public class GameRenderHelper implements UI {
    private static final Logger logger = LoggerFactory.getLogger(GameRenderHelper.class);

    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final GlyphLayout layout;

    // For interaction prompts
    private static Texture bgHudShared;
    private final BitmapFont promptFont;
    private final GlyphLayout promptLayoutInstance;

    private float offsetX = 0;
    private float offsetY = 0;

    public GameRenderHelper() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(2f);
        font.setUseIntegerPositions(true);
        layout = new GlyphLayout();

        // Initialize shared resources for prompts
        if (bgHudShared == null) {
            bgHudShared = new Texture(Gdx.files.internal("GameScreen/boxhud.png"));
        }
        promptFont = new BitmapFont();
        promptFont.setColor(Color.WHITE);
        promptFont.getData().setScale(1.2f);
        promptLayoutInstance = new GlyphLayout();
    }

    /**
     * Begin rendering operations
     */
    public void beginRender() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /**
     * Set camera offset based on player position
     *
     * @param playerX      Player X position
     * @param playerY      Player Y position
     * @param screenWidth  Screen width
     * @param screenHeight Screen height
     */
    public void updateCameraOffset(float playerX, float playerY, int screenWidth, int screenHeight) {
        float centerX = (screenWidth - (MAP_WIDTH * TILE_SIZE)) / 2f;
        float centerY = (screenHeight - (MAP_HEIGHT * TILE_SIZE)) / 2f;

        if (centerX > 0 && centerY > 0) {
            offsetX = centerX;
            offsetY = centerY;
        } else {
            offsetX = screenWidth / 2f - playerX - TILE_SIZE / 2f;
            offsetY = screenHeight / 2f - playerY - TILE_SIZE / 2f;

            int mapPixelWidth = MAP_WIDTH * TILE_SIZE;
            int mapPixelHeight = MAP_HEIGHT * TILE_SIZE;

            if (offsetX > 0)
                offsetX = 0;
            if (offsetY > 0)
                offsetY = 0;
            if (offsetX < screenWidth - mapPixelWidth)
                offsetX = screenWidth - mapPixelWidth;
            if (offsetY < screenHeight - mapPixelHeight)
                offsetY = screenHeight - mapPixelHeight;
        }
    }

    /**
     * Render the map tiles
     *
     * @param map          The map array
     * @param floorTexture Texture for floor tiles
     * @param wallTexture  Texture for wall tiles
     * @param ambient      The current ambient
     */
    public void renderMap(int[][] map, Texture floorTexture, Texture wallTexture, Ambient ambient) {
        batch.begin();

        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                float tileX = x * TILE_SIZE + offsetX;
                float tileY = y * TILE_SIZE + offsetY;

                if (tileX < -TILE_SIZE || tileX > Gdx.graphics.getWidth() ||
                        tileY < -TILE_SIZE || tileY > Gdx.graphics.getHeight()) {
                    continue;
                }

                int tileType = map[y][x];

                switch (tileType) {
                    case TILE_GRASS:
                        batch.draw(floorTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);
                        break;
                    case TILE_WALL:
                        batch.draw(wallTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);
                        break;
                    case TILE_DOOR:
                        batch.draw(floorTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);
                        batch.setColor(1, 0.8f, 0, 0.85f); // Brighter golden color for doors
                        batch.draw(wallTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);

                        // Draw a door frame to make it more visible
                        batch.setColor(1, 0.6f, 0, 1); // Darker gold for the frame
                        // Top and bottom of door frame
                        batch.draw(wallTexture, tileX, tileY, TILE_SIZE, TILE_SIZE * 0.15f);
                        batch.draw(wallTexture, tileX, tileY + TILE_SIZE * 0.85f, TILE_SIZE, TILE_SIZE * 0.15f);
                        // Left and right of door frame
                        batch.draw(wallTexture, tileX, tileY, TILE_SIZE * 0.15f, TILE_SIZE);
                        batch.draw(wallTexture, tileX + TILE_SIZE * 0.85f, tileY, TILE_SIZE * 0.15f, TILE_SIZE);

                        batch.setColor(1, 1, 1, 1); // Reset color
                        break;
                    case TILE_CAVE:
                        if (ambient instanceof Jungle) {
                            Jungle jungle = (Jungle) ambient;
                            if (jungle.isTallGrass(x, y)) {
                                batch.draw(floorTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);
                                batch.draw(jungle.getTallGrassTexture(), tileX, tileY, TILE_SIZE, TILE_SIZE);
                            } else {
                                batch.draw(floorTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);
                            }
                        } else {
                            // For actual cave ambient
                            batch.setColor(0.5f, 0.5f, 0.5f, 1f);
                            batch.draw(floorTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);
                            batch.setColor(1, 1, 1, 1);
                        }
                        break;
                    case TILE_WATER:
                        batch.setColor(0.2f, 0.6f, 1f, 0.8f);
                        batch.draw(floorTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);
                        batch.setColor(1, 1, 1, 1);
                        break;
                }
            }
        }

        batch.end();
    }

    /**
     * Render all materials on the map
     *
     * @param materials List of materials to render
     */
    public void renderMaterials(List<Material> materials) {
        batch.begin();
        for (Material material : materials) {
            float x = material.getPosition().x + offsetX;
            float y = material.getPosition().y + offsetY;

            // Skip if outside visible area
            if (x < -TILE_SIZE || x > Gdx.graphics.getWidth() ||
                    y < -TILE_SIZE || y > Gdx.graphics.getHeight()) {
                continue;
            }

            // Get sprite from material's map and draw it
            if (material.getSprites() != null && material.getSprites().containsKey("idle")) {
                // Position the sprite correctly
                Sprite sprite = material.getSprites().get("idle");
                sprite.setPosition(x, y);
                sprite.draw(batch);
            } else {
                // Fallback for materials without sprites
                batch.setColor(0.7f, 0.7f, 0.7f, 1f); // Gray fallback
                batch.draw(new Texture(Gdx.files.internal("sprites/itens/rock.png")), x, y, TILE_SIZE, TILE_SIZE);
                batch.setColor(1f, 1f, 1f, 1f);
            }
        }
        batch.end();
    }

    /**
     * Render all creatures on the map
     */
    public void renderCreatures(List<Deer> deers, List<Cannibal> cannibals, Character character) {
        batch.begin();

        // Render deers
        for (Deer deer : deers) {
            float x = deer.getPosition().x + offsetX;
            float y = deer.getPosition().y + offsetY;

            if (x < -TILE_SIZE || x > Gdx.graphics.getWidth() ||
                    y < -TILE_SIZE || y > Gdx.graphics.getHeight()) {
                continue;
            }

            // Get sprite from creature's map and draw it
            if (deer.getSprites() != null && deer.getSprites().containsKey("idle")) {
                Sprite sprite = deer.getSprites().get("idle");
                // Set deer size to 50x50 pixels
                sprite.setSize(50, 50);
                sprite.setPosition(
                        x + (TILE_SIZE - sprite.getWidth()) / 2,
                        y + (TILE_SIZE - sprite.getHeight()) / 2);
                sprite.draw(batch);
            } else {
                // Fallback in case no sprite is available
                batch.setColor(0.8f, 0.6f, 0.4f, 1f); // Brown deer color
                batch.draw(new Texture(Gdx.files.internal("sprites/criaturas/deer.png")),
                        x + (float) (TILE_SIZE - 50) / 2,
                        y + (float) (TILE_SIZE - 50) / 2,
                        50, 50);
                batch.setColor(1f, 1f, 1f, 1f);
            }
        }

        // Render cannibals
        for (Cannibal cannibal : cannibals) {
            float x = cannibal.getPosition().x + offsetX;
            float y = cannibal.getPosition().y + offsetY;

            if (x < -TILE_SIZE || x > Gdx.graphics.getWidth() ||
                    y < -TILE_SIZE || y > Gdx.graphics.getHeight()) {
                continue;
            }

            // Get sprite from creature's map and draw it
            if (cannibal.getSprites() != null && cannibal.getSprites().containsKey("idle")) {
                Sprite sprite = cannibal.getSprites().get("idle");
                // Set cannibal size to 40x40 pixels
                sprite.setSize(40, 40);
                sprite.setPosition(
                        x + (TILE_SIZE - sprite.getWidth()) / 2,
                        y + (TILE_SIZE - sprite.getHeight()) / 2);
                sprite.draw(batch);
            } else {
                // Fallback in case no sprite is available
                batch.setColor(1f, 0.3f, 0.3f, 1f); // Reddish cannibal color
                batch.draw(new Texture(Gdx.files.internal("sprites/criaturas/cannibal.png")),
                        x + (float) (TILE_SIZE - 40) / 2,
                        y + (float) (TILE_SIZE - 40) / 2,
                        40, 40);
                batch.setColor(1f, 1f, 1f, 1f);
            }
        }

        // Render player character
        float playerX = character.getPosition().x + offsetX;
        float playerY = character.getPosition().y + offsetY;
        batch.draw(character.getCurrentFrame(), playerX, playerY, TILE_SIZE, TILE_SIZE);

        batch.end();
    }

    /**
     * Render an interaction prompt
     */
    public void renderInteractionPrompt(Material material, String text) {
        batch.begin();

        Vector2 pos = material.getPosition();
        float boxWidth = 160;
        float boxHeight = 30;
        // Center the box above the material's tile
        float boxX = pos.x + offsetX + (TILE_SIZE / 2f) - (boxWidth / 2f);
        float boxY = pos.y + offsetY + TILE_SIZE; // Position it above the tile

        batch.setColor(1, 1, 1, 0.7f);
        batch.draw(bgHudShared, boxX, boxY, boxWidth, boxHeight);
        batch.setColor(1, 1, 1, 1);

        promptLayoutInstance.setText(promptFont, text);
        promptFont.draw(batch, promptLayoutInstance, boxX + (boxWidth - promptLayoutInstance.width) / 2f,
                boxY + boxHeight - (boxHeight - promptLayoutInstance.height) / 2f - 2); // Adjusted for vertical
                                                                                        // centering

        batch.end();
    }

    /**
     * Render snake alert screen
     */
    public void renderSnakeAlertScreen() {
        batch.begin();

        Texture image = SnakeEventManager.getSnakeBiteImage();
        float imgX = (Gdx.graphics.getWidth() - image.getWidth()) / 2f;
        float imgY = (Gdx.graphics.getHeight() - image.getHeight()) / 2f;
        batch.draw(image, imgX, imgY);

        font.getData().setScale(2f);
        String msg = "You were bitten by a snake!\nPress [SPACE] to continue.";
        layout.setText(font, msg, Color.WHITE, Gdx.graphics.getWidth(), 1, true);

        float textX = (Gdx.graphics.getWidth() - layout.width) / 2f;
        float textY = imgY - 20; // slightly below image
        font.setColor(Color.RED);
        font.draw(batch, layout, textX, textY);

        batch.end();
    }

    /**
     * Get the current X offset for rendering
     */
    public float getOffsetX() {
        return offsetX;
    }

    /**
     * Get the current Y offset for rendering
     */
    public float getOffsetY() {
        return offsetY;
    }

    /**
     * Clean up resources when no longer needed
     */
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        promptFont.dispose();
        if (bgHudShared != null) {
            bgHudShared.dispose();
            bgHudShared = null;
        }
    }
}
