package io.github.com.ranie_borges.thejungle.view.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Handles lighting effects for the game world
 */
public class LightingManager {
    private FrameBuffer lightBuffer;

    public LightingManager() {
        initializeBuffer();
    }

    /**
     * Initialize or re-initialize the lighting buffer
     */
    public void initializeBuffer() {
        if (lightBuffer != null) {
            lightBuffer.dispose();
        }
        lightBuffer = new FrameBuffer(Pixmap.Format.RGBA8888,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight(),
                false);
    }

    /**
     * Begin rendering to the light buffer
     */
    public void beginLightBuffer() {
        lightBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 1); // Clear light buffer with black
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    /**
     * End rendering to the light buffer and render it to the screen
     */
    public void endLightBufferAndRender(SpriteBatch batch) {
        lightBuffer.end();

        batch.begin();
        Texture bufferTexture = lightBuffer.getColorBufferTexture();
        bufferTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        bufferTexture.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);

        batch.draw(bufferTexture,
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                0, 0, 1, 1);
        batch.end();
    }

    /**
     * Render a dark overlay with a visible circle around the player position
     * Used for tall grass effect
     */
    public void renderTallGrassEffect(ShapeRenderer shapeRenderer, float playerScreenX, float playerScreenY) {
        Gdx.gl.glEnable(GL20.GL_BLEND);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.65f); // shadow transparency
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        Gdx.gl.glBlendFunc(GL20.GL_DST_COLOR, GL20.GL_ZERO);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 1, 1); // white = reveals
        shapeRenderer.circle(playerScreenX, playerScreenY, 150);
        shapeRenderer.end();

        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * Clean up resources
     */
    public void dispose() {
        if (lightBuffer != null) {
            lightBuffer.dispose();
            lightBuffer = null;
        }
    }
}
