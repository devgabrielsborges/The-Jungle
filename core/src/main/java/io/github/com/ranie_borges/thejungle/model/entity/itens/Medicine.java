package io.github.com.ranie_borges.thejungle.model.entity.itens;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.google.gson.annotations.Expose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a medicine item used for healing.
 */
public class Medicine extends Item {

    private static final Logger logger = LoggerFactory.getLogger(Medicine.class);
    private static Texture bgHud = null; // Initialize lazily or ensure loaded before use

    @Expose
    private double healRatio;

    public Medicine(String name, float weight, float durability, double healRatio) {
        super(name, weight, durability);
        setHealRatio(healRatio);
        if (bgHud == null) { // Lazy initialization
            try {
                bgHud = new Texture(Gdx.files.internal("GameScreen/boxhud.png"));
            } catch (Exception e) {
                logger.error("Failed to load boxhud.png for Medicine class", e);
                // Handle error, maybe use a placeholder or log that UI will be affected
            }
        }
    }

    public static Medicine fromMedicinalPlant(Material plant) {
        if (plant == null || !"Plant".equalsIgnoreCase(plant.getType())
            || !"Medicinal".equalsIgnoreCase(plant.getName())) {
            throw new IllegalArgumentException("O material não é uma planta medicinal válida.");
        }
        return new Medicine("Herbal Medicine", plant.getWeight(), 1.0f, 25.0);
    }

    @Override
    public void useItem() {
        setDurability(getDurability() - 1);
    }

    @Override
    public void dropItem() {
        logger.info("Você deixou cair o medicamento: {}.", getName());
    }

    public double getHealRatio() {
        return healRatio;
    }

    public void setHealRatio(double healRatio) {
        this.healRatio = Math.max(0, healRatio);
    }

    /**
     * Creates a standard Bandage.
     */
    public static Medicine createBandage() {
        return new Medicine("Bandage", 0.3f, 1.0f, 20.0);
    }

    /**
     * Creates a standard Medicinal Ointment.
     */
    public static Medicine createMedicinalOintment() {
        return new Medicine("Medicinal Ointment", 0.5f, 1.0f, 30.0);
    }

    /**
     * Creates a standard Antibiotic.
     */
    public static Medicine createAntibiotic() {
        return new Medicine("Antibiotic", 0.4f, 1.0f, 50.0);
    }

    public static void renderUseOption(SpriteBatch batch, Material plant, Character character, float offsetX,
                                       float offsetY) {
        if (plant == null || !"Plant".equalsIgnoreCase(plant.getType())
            || !"Medicinal".equalsIgnoreCase(plant.getName()))
            return;

        if (bgHud == null) { // If texture failed to load, can't render this prompt
            logger.warn("bgHud texture is null in Medicine.renderUseOption, skipping render.");
            return;
        }

        Vector2 pos = plant.getPosition();
        float boxX = pos.x + offsetX;
        float boxY = pos.y + offsetY + 40; // Position above the plant

        batch.begin(); // Added batch.begin()
        batch.setColor(1, 1, 1, 0.7f);
        batch.draw(bgHud, boxX, boxY, 160, 30);
        batch.setColor(1, 1, 1, 1);

        BitmapFont font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.2f);
        font.draw(batch, "[E] Coletar Planta Medicinal", boxX + 10, boxY + 20);
        font.dispose();
        batch.end(); // Added batch.end()
    }
}
