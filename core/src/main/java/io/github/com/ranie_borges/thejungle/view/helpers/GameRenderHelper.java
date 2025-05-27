package io.github.com.ranie_borges.thejungle.view.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.creatures.*;
import io.github.com.ranie_borges.thejungle.model.entity.itens.Material;
import io.github.com.ranie_borges.thejungle.model.events.events.SnakeEventManager;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import io.github.com.ranie_borges.thejungle.model.world.ambients.Jungle;
import io.github.com.ranie_borges.thejungle.model.world.ambients.LakeRiver;
import io.github.com.ranie_borges.thejungle.view.interfaces.UI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map; // Import Map

public class GameRenderHelper implements UI {
    private static final Logger logger = LoggerFactory.getLogger(GameRenderHelper.class);

    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final GlyphLayout layout;

    private static Texture bgHudShared;
    private final BitmapFont promptFont;
    private final GlyphLayout promptLayoutInstance;

    private float offsetX = 0;
    private float offsetY = 0;

    public GameRenderHelper() {
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.getData().setScale(2f);
        font.setUseIntegerPositions(true);
        layout = new GlyphLayout();

        if (bgHudShared == null) {
            try {
                bgHudShared = new Texture(Gdx.files.internal("GameScreen/boxhud.png"));
            } catch (Exception e) {
                logger.error("Failed to load boxhud.png for GameRenderHelper", e);
            }
        }
        promptFont = new BitmapFont();
        promptFont.setColor(Color.WHITE);
        promptFont.getData().setScale(1.2f);
        promptLayoutInstance = new GlyphLayout();
    }

    public void beginRender() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public void updateCameraOffset(float playerX, float playerY, int screenWidth, int screenHeight) {
        float mapPixelWidth = MAP_WIDTH * TILE_SIZE;
        float mapPixelHeight = MAP_HEIGHT * TILE_SIZE;

        if (mapPixelWidth < screenWidth) {
            offsetX = (screenWidth - mapPixelWidth) / 2f;
        } else {
            offsetX = screenWidth / 2f - playerX - TILE_SIZE / 2f;
            offsetX = Math.max(offsetX, screenWidth - mapPixelWidth);
            offsetX = Math.min(offsetX, 0);
        }

        if (mapPixelHeight < screenHeight) {
            offsetY = (screenHeight - mapPixelHeight) / 2f;
        } else {
            offsetY = screenHeight / 2f - playerY - TILE_SIZE / 2f;
            offsetY = Math.max(offsetY, screenHeight - mapPixelHeight);
            offsetY = Math.min(offsetY, 0);
        }
    }

    public void renderMap(SpriteBatch batch, int[][] map, Texture floorTexture, Texture wallTexture, Ambient ambient) {
        if (batch == null || !batch.isDrawing()) {
            logger.warn("renderMap called while batch is null or not drawing!");
            return;
        }
        for (int y = 0; y < MAP_HEIGHT; y++) {
            for (int x = 0; x < MAP_WIDTH; x++) {
                float tileX = x * TILE_SIZE + offsetX;
                float tileY = y * TILE_SIZE + offsetY;

                if (tileX < -TILE_SIZE || tileX > Gdx.graphics.getWidth() ||
                    tileY < -TILE_SIZE || tileY > Gdx.graphics.getHeight()) {
                    continue;
                }

                int tileType = map[y][x];
                Texture currentAmbientFloorTexture = (ambient != null && ambient.getFloorTexture() != null) ? ambient.getFloorTexture() : floorTexture;
                Texture currentAmbientWallTexture = (ambient != null && ambient.getWallTexture() != null) ? ambient.getWallTexture() : wallTexture;

                switch (tileType) {
                    case TILE_GRASS:
                        if(currentAmbientFloorTexture != null) batch.draw(currentAmbientFloorTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);
                        break;
                    case TILE_WALL:
                        if(currentAmbientWallTexture != null) batch.draw(currentAmbientWallTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);
                        break;
                    case TILE_DOOR:
                        if(currentAmbientFloorTexture != null) batch.draw(currentAmbientFloorTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);
                        if(currentAmbientWallTexture != null) {
                            batch.setColor(1, 0.8f, 0, 0.85f);
                            batch.draw(currentAmbientWallTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);
                            batch.setColor(1, 0.6f, 0, 1);
                            batch.draw(currentAmbientWallTexture, tileX, tileY, TILE_SIZE, TILE_SIZE * 0.15f);
                            batch.draw(currentAmbientWallTexture, tileX, tileY + TILE_SIZE * 0.85f, TILE_SIZE, TILE_SIZE * 0.15f);
                            batch.draw(currentAmbientWallTexture, tileX, tileY, TILE_SIZE * 0.15f, TILE_SIZE);
                            batch.draw(currentAmbientWallTexture, tileX + TILE_SIZE * 0.85f, tileY, TILE_SIZE * 0.15f, TILE_SIZE);
                        }
                        batch.setColor(1, 1, 1, 1);
                        break;
                    case TILE_CAVE:
                        if(currentAmbientFloorTexture != null) {
                            if (ambient instanceof Jungle) {
                                Jungle jungle = (Jungle) ambient;
                                batch.draw(currentAmbientFloorTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);
                                if (jungle.isTallGrass(x, y) && jungle.getTallGrassTexture() != null) {
                                    batch.draw(jungle.getTallGrassTexture(), tileX, tileY, TILE_SIZE, TILE_SIZE);
                                }
                            } else {
                                batch.setColor(0.5f, 0.5f, 0.5f, 1f);
                                batch.draw(currentAmbientFloorTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);
                                batch.setColor(1, 1, 1, 1);
                            }
                        }
                        break;
                    case TILE_WATER:
                        if (ambient instanceof LakeRiver && ((LakeRiver) ambient).getWaterTexture() != null) {
                            batch.draw(((LakeRiver) ambient).getWaterTexture(), tileX, tileY, TILE_SIZE, TILE_SIZE);
                        } else if (currentAmbientFloorTexture != null) {
                            batch.setColor(0.2f, 0.6f, 1f, 0.8f);
                            batch.draw(currentAmbientFloorTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);
                            batch.setColor(1, 1, 1, 1);
                        }
                        break;
                    default:
                        if(currentAmbientFloorTexture != null) batch.draw(currentAmbientFloorTexture, tileX, tileY, TILE_SIZE, TILE_SIZE);
                        break;
                }
            }
        }
    }

    public void renderMaterials(SpriteBatch batch, List<Material> materials) {
        if (materials == null || batch == null || !batch.isDrawing()) {
            if (materials == null) logger.warn("renderMaterials called with null materials list.");
            if (batch == null) logger.warn("renderMaterials called with null batch.");
            if (batch != null && !batch.isDrawing()) logger.warn("renderMaterials called while batch is not drawing!");
            return;
        }

         logger.debug("Attempting to render {} materials.", materials.size());
        for (Material material : materials) {
            if (material == null) {
                 logger.warn("Skipping null material in renderMaterials.");
                continue;
            }

            Map<String, Sprite> materialSprites = material.getSprites();
            if (materialSprites != null && materialSprites.containsKey("idle")) {
                Sprite sprite = materialSprites.get("idle");
                if (sprite != null && sprite.getTexture() != null) {
                    float x = material.getPosition().x + offsetX;
                    float y = material.getPosition().y + offsetY;

                    if (x < -TILE_SIZE*2 || x > Gdx.graphics.getWidth() + TILE_SIZE || // Adjusted culling slightly
                        y < -TILE_SIZE*2 || y > Gdx.graphics.getHeight() + TILE_SIZE) {
                        continue;
                    }

                    float originalWidth = sprite.getWidth();
                    float originalHeight = sprite.getHeight();

                    if ("Tree".equals(material.getName())) {
                        sprite.setSize(128, 128);
                        sprite.setPosition(x + (TILE_SIZE - sprite.getWidth()) / 2f, y);
                    } else {
                        sprite.setSize(TILE_SIZE, TILE_SIZE);
                        sprite.setPosition(x, y);
                    }

                    if (material.getName().equalsIgnoreCase("Berry")) {
                        logger.info("RENDERING BERRY: Name: {}, Pos: ({},{}), ScreenPos: ({},{}), Size: ({},{}), Texture: {}, Region: ({},{},{},{})",
                            material.getName(), material.getPosition().x, material.getPosition().y, x, y,
                            sprite.getWidth(), sprite.getHeight(),
                            sprite.getTexture().getTextureObjectHandle(), // Check if texture is valid
                            sprite.getRegionX(), sprite.getRegionY(), sprite.getRegionWidth(), sprite.getRegionHeight());
                    } else if (material.getName().equalsIgnoreCase("Tree")) {
                        logger.info("RENDERING TREE: Name: {}, Pos: ({},{}), ScreenPos: ({},{}), Size: ({},{}), Texture: {}",
                            material.getName(), material.getPosition().x, material.getPosition().y, x, y,
                            sprite.getWidth(), sprite.getHeight(),
                            sprite.getTexture().getTextureObjectHandle());
                    }


                    sprite.draw(batch);
                } else {
                    logger.warn("Material '{}' idle sprite is null or its texture is null. Not rendering.", material.getName());
                }
            } else {
                logger.warn("Material '{}' has no 'idle' sprite or sprites map is null. Not rendering.", material.getName());
            }
        }
    }

    public void renderCreatures(SpriteBatch batch, List<Deer> deers, List<Cannibal> cannibals, Character character, List<Fish> fishes,List<NPC> NPCS,List<Boat> boats, List<RadioGuy> radioGuys) {
        if (batch == null || !batch.isDrawing()) {
            logger.warn("renderCreatures called while batch is null or not drawing!");
            return;
        }
        if (deers != null) {
            for (Deer deer : deers) {
                if (deer == null || deer.getSprites() == null) continue;
                Sprite sprite = deer.getSprites().get("idle");
                if (sprite != null && sprite.getTexture() != null) {
                    float x = deer.getPosition().x + offsetX; float y = deer.getPosition().y + offsetY;
                    if (x < -50 || x > Gdx.graphics.getWidth() || y < -50 || y > Gdx.graphics.getHeight()) continue;
                    sprite.setSize(50, 50);
                    sprite.setPosition(x + (TILE_SIZE - sprite.getWidth()) / 2, y + (TILE_SIZE - sprite.getHeight()) / 2);
                    sprite.draw(batch);
                }
            }
        }
        if (cannibals != null) {
            for (Cannibal cannibal : cannibals) {
                if (cannibal == null || cannibal.getSprites() == null) continue;
                Sprite sprite = cannibal.getSprites().get("idle");
                if (sprite != null && sprite.getTexture() != null) {
                    float x = cannibal.getPosition().x + offsetX; float y = cannibal.getPosition().y + offsetY;
                    if (x < -40 || x > Gdx.graphics.getWidth() || y < -40 || y > Gdx.graphics.getHeight()) continue;
                    sprite.setSize(40, 40);
                    sprite.setPosition(x + (TILE_SIZE - sprite.getWidth()) / 2, y + (TILE_SIZE - sprite.getHeight()) / 2);
                    sprite.draw(batch);
                }
            }
        }
        if (fishes != null) {
            for (Fish fish : fishes) {
                if (fish == null || fish.getSprites() == null) continue;
                Sprite sprite = fish.getSprites().get("idle");
                if (sprite != null && sprite.getTexture() != null) {
                    float x = fish.getPosition().x + offsetX; float y = fish.getPosition().y + offsetY;
                    if (x < -30 || x > Gdx.graphics.getWidth() || y < -30 || y > Gdx.graphics.getHeight()) continue;
                    sprite.setSize(30, 30);
                    sprite.setPosition(x + (TILE_SIZE - sprite.getWidth()) / 2, y + (TILE_SIZE - sprite.getHeight()) / 2);
                    sprite.draw(batch);
                }
            }
        }
        if (NPCS != null) {
            for (NPC npc : NPCS) {
                if (npc == null || npc.getSprites() == null) continue;
                Sprite sprite = npc.getSprites().get("idle");
                if (sprite != null && sprite.getTexture() != null) {
                    float x = npc.getPosition().x + offsetX; float y = npc.getPosition().y + offsetY;
                    if (x < -40 || x > Gdx.graphics.getWidth() || y < -40 || y > Gdx.graphics.getHeight()) continue;
                    sprite.setSize(40, 40);
                    sprite.setPosition(x + (TILE_SIZE - sprite.getWidth()) / 2, y + (TILE_SIZE - sprite.getHeight()) / 2);
                    sprite.draw(batch);
                }
            }
        }
        if (boats != null) {
            for (Boat boat : boats) {
                if (boat == null || boat.getSprites() == null) continue;
                Sprite sprite = boat.getSprites().get("idle");
                if (sprite != null && sprite.getTexture() != null) {
                    float x = boat.getPosition().x + offsetX; float y = boat.getPosition().y + offsetY;
                    if (x < -50 || x > Gdx.graphics.getWidth() || y < -50 || y > Gdx.graphics.getHeight()) continue;
                    sprite.setSize(50, 50);
                    sprite.setPosition(x + (TILE_SIZE - sprite.getWidth()) / 2, y + (TILE_SIZE - sprite.getHeight()) / 2);
                    sprite.draw(batch);
                }
            }
        }
        if (radioGuys != null) {
            for (RadioGuy radioGuy : radioGuys) {
                if (radioGuy == null || radioGuy.getSprites() == null) continue;
                Sprite sprite = radioGuy.getSprites().get("idle");
                if (sprite != null && sprite.getTexture() != null) {
                    float x = radioGuy.getPosition().x + offsetX; float y = radioGuy.getPosition().y + offsetY;
                    if (x < -50 || x > Gdx.graphics.getWidth() || y < -50 || y > Gdx.graphics.getHeight()) continue;
                    sprite.setSize(50, 50);
                    sprite.setPosition(x + (TILE_SIZE - sprite.getWidth()) / 2, y + (TILE_SIZE - sprite.getHeight()) / 2);
                    sprite.draw(batch);
                }
            }
        }

        if (character != null && character.getCurrentFrame() != null) {
            TextureRegion frame = character.getCurrentFrame();
            if (frame.getTexture() != null) { // Ensure character frame also has a valid texture
                float playerX = character.getPosition().x + offsetX;
                float playerY = character.getPosition().y + offsetY;
                batch.draw(frame, playerX, playerY, TILE_SIZE, TILE_SIZE);
            } else {
                logger.warn("Character's current animation frame has a null texture!");
            }
        }
    }

    public void renderInteractionPrompt(SpriteBatch batch, Material material, String text) {
        // This method now expects to be called within an active batch session
        if (batch == null || !batch.isDrawing()) {
            logger.warn("renderInteractionPrompt called while batch is null or not drawing!");
            return;
        }
        if (bgHudShared == null) {
            logger.warn("bgHudShared is null in renderInteractionPrompt. Prompt will not have background.");
        }

        Vector2 pos = material.getPosition();
        float boxWidth = Math.max(160, text.length() * 8f); // Adjusted width for text
        float boxHeight = 30;
        float boxX = pos.x + offsetX + (TILE_SIZE / 2f) - (boxWidth / 2f);
        float boxY = pos.y + offsetY + TILE_SIZE;

        if (bgHudShared != null) {
            batch.setColor(1, 1, 1, 0.7f);
            batch.draw(bgHudShared, boxX, boxY, boxWidth, boxHeight);
            batch.setColor(1, 1, 1, 1);
        }

        promptLayoutInstance.setText(promptFont, text);
        promptFont.draw(batch, promptLayoutInstance, boxX + (boxWidth - promptLayoutInstance.width) / 2f,
            boxY + boxHeight - (boxHeight - promptLayoutInstance.height) / 2f - 2);
    }

    public void renderSnakeAlertScreen(SpriteBatch batch) {
        if (batch == null) return; // Safety
        batch.begin(); // Ensure batch is active for this specific screen overlay
        Texture image = SnakeEventManager.getSnakeBiteImage();
        if (image != null) {
            float imgX = (Gdx.graphics.getWidth() - image.getWidth()) / 2f;
            float imgY = (Gdx.graphics.getHeight() - image.getHeight()) / 2f;
            batch.draw(image, imgX, imgY);

            if (font != null && layout != null) { // Check for null font/layout
                font.getData().setScale(2f);
                String msg = "You were bitten by a snake!\nPress [SPACE] to continue.";
                layout.setText(font, msg, Color.WHITE, Gdx.graphics.getWidth() * 0.8f, Align.center, true); // Added wrap width and center align

                float textX = (Gdx.graphics.getWidth() - layout.width) / 2f;
                float textY = imgY - 20; // Position below image
                font.setColor(Color.RED);
                font.draw(batch, layout, textX, textY);
            }
        }
        batch.end();
    }

    public float getOffsetX() { return offsetX; }
    public float getOffsetY() { return offsetY; }

    public void dispose() {
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
        if (promptFont != null) promptFont.dispose();
    }
}
