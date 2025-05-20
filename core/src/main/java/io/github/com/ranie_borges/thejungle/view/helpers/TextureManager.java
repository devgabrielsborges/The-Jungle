package io.github.com.ranie_borges.thejungle.view.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages textures for the game, allowing for better resource handling
 */
public class TextureManager {
    private final Map<String, Texture> textureCache;
    private Texture floorTexture;
    private Texture wallTexture;
    private Texture sidebarTexture;

    public TextureManager() {
        textureCache = new HashMap<>();
    }

    /**
     * Load textures for the given ambient
     *
     * @param ambient The ambient to load textures for
     */
    public void loadAmbientTextures(Ambient ambient) {
        disposeAmbientTextures();

        floorTexture = ambient.getFloorTexture() != null ? ambient.getFloorTexture()
                : getOrLoadTexture("GameScreen/chao.png");

        wallTexture = ambient.getWallTexture() != null ? ambient.getWallTexture()
                : getOrLoadTexture("GameScreen/parede.png");

        sidebarTexture = ambient.getSidebarTexture() != null ? ambient.getSidebarTexture()
                : getOrLoadTexture("Gameplay/sidebar.jpg");
    }

    /**
     * Load a texture for a character class icon
     *
     * @param characterType The character class type
     * @return The class icon texture
     */
    public Texture loadClassIcon(String characterType) {
        switch (characterType) {
            case "Hunter":
                return getOrLoadTexture("StatsScreen/cacadorFundo.png");
            case "Lumberjack":
                return getOrLoadTexture("StatsScreen/lenhadorFundo.png");
            case "Doctor":
                return getOrLoadTexture("StatsScreen/medicoFundo.png");
            default:
                return getOrLoadTexture("StatsScreen/desempregadoFundo.png");
        }
    }

    /**
     * Get a texture from the cache or load it from disk
     *
     * @param path Path to the texture
     * @return The loaded texture
     */
    public Texture getOrLoadTexture(String path) {
        if (textureCache.containsKey(path)) {
            return textureCache.get(path);
        } else {
            Texture texture = new Texture(Gdx.files.internal(path));
            textureCache.put(path, texture);
            return texture;
        }
    }

    public Texture getFloorTexture() {
        return floorTexture;
    }

    public Texture getWallTexture() {
        return wallTexture;
    }

    public Texture getSidebarTexture() {
        return sidebarTexture;
    }

    /**
     * Dispose specific ambient textures
     */
    private void disposeAmbientTextures() {
        // We don't actually dispose the textures here
        // since they might be cached and used elsewhere
        floorTexture = null;
        wallTexture = null;
        sidebarTexture = null;
    }

    /**
     * Dispose all textures when no longer needed
     */
    public void dispose() {
        // Clear texture references
        floorTexture = null;
        wallTexture = null;
        sidebarTexture = null;

        // Dispose all textures in cache
        for (Texture texture : textureCache.values()) {
            if (texture != null) {
                texture.dispose();
            }
        }
        textureCache.clear();
    }
}
