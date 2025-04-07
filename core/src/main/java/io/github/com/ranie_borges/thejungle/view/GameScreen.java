package io.github.com.ranie_borges.thejungle.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.com.ranie_borges.thejungle.core.Main;

public class GameScreen implements Screen {

    private final Main game;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private TiledMap map;
    private IsometricTiledMapRenderer mapRenderer;

    private Texture playerTexture;
    private SpriteBatch batch;

    private float playerX;
    private float playerY;

    private float centerX;
    private float centerY;

    private float moveSpeed = 100f;
    private float playerScale = 0.04f;

    private float minX, maxX, minY, maxY;

    private TiledMapTileLayer collisionLayer;

    public GameScreen(Main game) {
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new FitViewport(400, 240, camera);

        map = new TmxMapLoader().load("GameScreen/map/forest.tmx");
        mapRenderer = new IsometricTiledMapRenderer(map);

        collisionLayer = (TiledMapTileLayer) map.getLayers().get(0);

        int mapWidth = collisionLayer.getWidth();
        int mapHeight = collisionLayer.getHeight();
        float tileWidth = collisionLayer.getTileWidth();
        float tileHeight = collisionLayer.getTileHeight();

        centerX = (mapWidth + mapHeight) * tileWidth / 4f;
        centerY = (mapWidth + mapHeight) * tileHeight / 8f;

        float margin = 8f;
        minX = margin;
        maxX = (mapWidth + mapHeight) * tileWidth / 2f - margin;
        minY = margin;
        maxY = (mapWidth + mapHeight) * tileHeight / 4f - margin;

        camera.position.set(centerX, centerY, 0);
        camera.update();

        playerTexture = new Texture(Gdx.files.internal("sprites/character/personagem_luta.png"));
        batch = new SpriteBatch();

        playerX = centerX;
        playerY = centerY;
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float move = moveSpeed * delta;
        float nextX = playerX;
        float nextY = playerY;

        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            nextX -= move;
            nextY += move;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            nextX += move;
            nextY -= move;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            nextX -= move;
            nextY -= move;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            nextX += move;
            nextY += move;
        }

        // 🔒 Aplica os limites de borda
        nextX = Math.max(minX, Math.min(maxX, nextX));
        nextY = Math.max(minY, Math.min(maxY, nextY));

        // ✅ Só move se o tile não for bloqueado
        if (!isBlocked(nextX, nextY)) {
            playerX = nextX;
            playerY = nextY;
        }

        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        float scaledWidth = playerTexture.getWidth() * playerScale;
        float scaledHeight = playerTexture.getHeight() * playerScale;

        batch.draw(playerTexture, playerX - scaledWidth / 2f, playerY - scaledHeight / 4f, scaledWidth, scaledHeight);

        batch.end();
    }

    private boolean isBlocked(float worldX, float worldY) {
        float tileWidth = collisionLayer.getTileWidth();
        float tileHeight = collisionLayer.getTileHeight();

        // 📐 Conversão mundo → tile (isométrico)
        int tileX = (int) (((worldX / tileWidth + worldY / tileHeight) / 2));
        int tileY = (int) (((worldY / tileHeight - (worldX / tileWidth)) / 2));

        TiledMapTileLayer.Cell cell = collisionLayer.getCell(tileX, tileY);
        if (cell == null || cell.getTile() == null) return false;

        Object blocked = cell.getTile().getProperties().get("blocked");
        return blocked != null && Boolean.parseBoolean(blocked.toString());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(centerX, centerY, 0);
        camera.update();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        playerTexture.dispose();
        batch.dispose();
    }
}
