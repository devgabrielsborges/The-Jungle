package io.github.com.ranie_borges.thejungle.view.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import io.github.com.ranie_borges.thejungle.model.world.Ambient;
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory

import java.util.HashMap;
import java.util.Map;

public class TextureManager {
    private static final Logger logger = LoggerFactory.getLogger(TextureManager.class); // Logger instance
    private final Map<String, Texture> textureCache;
    private Texture floorTexture;
    private Texture wallTexture;
    private Texture sidebarTexture;

    public TextureManager() {
        textureCache = new HashMap<>();
        logger.info("TextureManager initialized. Cache created.");
    }

    public void loadAmbientTextures(Ambient ambient) {
        if (ambient == null) {
            logger.warn("Cannot load ambient textures: ambient is null.");
            // Ensure default textures are set if ambient is null
            this.floorTexture = getOrLoadTexture("scenarios/jungle/jungleFloor.jpg"); // Default fallback
            this.wallTexture = getOrLoadTexture("scenarios/jungle/jungleWall.png");   // Default fallback
            this.sidebarTexture = getOrLoadTexture("Gameplay/sidebar.jpg");            // Default fallback
            return;
        }
        logger.debug("Loading ambient textures for: {}", ambient.getName());
        disposeAmbientTextures();

        floorTexture = (ambient.getFloorTexture() != null)
            ? ambient.getFloorTexture() // This assumes Ambient subclasses load their own Textures
            : getOrLoadTexture("scenarios/jungle/jungleFloor.jpg"); // Fallback if not pre-loaded by Ambient

        wallTexture = (ambient.getWallTexture() != null)
            ? ambient.getWallTexture()
            : getOrLoadTexture("scenarios/jungle/jungleWall.png");  // Fallback

        sidebarTexture = (ambient.getSidebarTexture() != null)
            ? ambient.getSidebarTexture()
            : getOrLoadTexture("Gameplay/sidebar.jpg"); // Fallback

        if(floorTexture == null) logger.error("Failed to load floorTexture for {}", ambient.getName());
        if(wallTexture == null) logger.error("Failed to load wallTexture for {}", ambient.getName());
        if(sidebarTexture == null) logger.error("Failed to load sidebarTexture for {}", ambient.getName());
    }

    public Texture loadClassIcon(String characterType) {
        String path;
        switch (characterType) {
            case "Hunter":
                path = "StatsScreen/cacadorFundo.png";
                break;
            case "Lumberjack":
                path = "StatsScreen/lenhadorFundo.png";
                break;
            case "Doctor":
                path = "StatsScreen/medicoFundo.png";
                break;
            default: // Survivor or unspecified
                path = "StatsScreen/desempregadoFundo.png";
                break;
        }
        return getOrLoadTexture(path);
    }

    public Texture getOrLoadTexture(String path) {
        if (path == null || path.trim().isEmpty()) {
            logger.warn("Attempted to load texture with null or empty path.");
            return null;
        }
        if (textureCache.containsKey(path)) {
            Texture cachedTexture = textureCache.get(path);
            if (cachedTexture != null) {
                // logger.trace("TextureManager: Returning cached texture for path: {}", path);
                return cachedTexture;
            } else {
                logger.warn("TextureManager: Cache contained null for path: {}. Attempting reload.", path);
                // Remove the null entry so it can be reloaded
                textureCache.remove(path);
            }
        }

        Texture texture = null;
        try {
            logger.debug("TextureManager: Attempting to load texture from path: {}", path);
            texture = new Texture(Gdx.files.internal(path));
            textureCache.put(path, texture);
            logger.info("TextureManager: Successfully loaded and cached texture from path: {}", path);
        } catch (Exception e) {
            logger.error("TextureManager: CRITICAL - Failed to load texture from path: '{}'. Error: {}", path, e.getMessage(), e);
            // Do not cache null on failure, so retries are possible if the issue was temporary
            // Or cache a placeholder error texture if desired:
            // texture = getErrorPlaceholderTexture();
            // textureCache.put(path, texture); // Cache placeholder to prevent repeated load errors for same path
        }
        return texture;
    }

    // Optional: Placeholder for missing textures
    // private Texture getErrorPlaceholderTexture() {
    //     if (textureCache.containsKey("error_placeholder")) return textureCache.get("error_placeholder");
    //     Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
    //     pixmap.setColor(Color.RED);
    //     pixmap.fill();
    //     Texture errorTexture = new Texture(pixmap);
    //     pixmap.dispose();
    //     textureCache.put("error_placeholder", errorTexture);
    //     return errorTexture;
    // }

    public Texture getFloorTexture() { return floorTexture; }
    public Texture getWallTexture() { return wallTexture; }
    public Texture getSidebarTexture() { return sidebarTexture; }

    private void disposeAmbientTextures() {
        // Only dispose if TextureManager exclusively owns these.
        // If Textures are passed from Ambient objects that manage their own lifecycle, don't dispose here.
        // Current Ambient class seems to load its own textures, so these might not need disposal by TM.
        // For now, just nullify references. If these were loaded by getOrLoadTexture, they are in cache.
        floorTexture = null;
        wallTexture = null;
        sidebarTexture = null;
    }

    public void dispose() {
        logger.info("Disposing TextureManager. Cached textures: {}", textureCache.size());
        for (Map.Entry<String, Texture> entry : textureCache.entrySet()) {
            if (entry.getValue() != null) {
                logger.debug("Disposing cached texture: {}", entry.getKey());
                entry.getValue().dispose();
            }
        }
        textureCache.clear();
        // Ambient-specific textures (floorTexture, wallTexture, sidebarTexture) are either
        // from the cache (and disposed above) or were direct references from Ambient objects
        // which should manage their own disposal if they created them.
        floorTexture = null;
        wallTexture = null;
        sidebarTexture = null;
        logger.info("TextureManager disposed.");
    }
}
