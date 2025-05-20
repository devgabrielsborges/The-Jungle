package io.github.com.ranie_borges.thejungle.model.entity.itens;

import io.github.com.ranie_borges.thejungle.model.entity.Character;
import io.github.com.ranie_borges.thejungle.model.entity.Item;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a medicine item used for healing.
 */
public class Medicine extends Item {

    private static final Logger logger = LoggerFactory.getLogger(Medicine.class);
    private static final Texture bgHud = new Texture(Gdx.files.internal("GameScreen/boxhud.png"));

    private double healRatio;

    public Medicine(String name, float weight, float durability, double healRatio) {
        super(name, weight, durability);
        setHealRatio(healRatio);
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

        Vector2 pos = plant.getPosition();
        float boxX = pos.x + offsetX;
        float boxY = pos.y + offsetY + 40; // Position above the plant

        batch.setColor(1, 1, 1, 0.7f);
        batch.draw(bgHud, boxX, boxY, 160, 30);
        batch.setColor(1, 1, 1, 1);

        BitmapFont font = new BitmapFont(); // Consider passing font as a parameter or using a shared instance
        font.setColor(Color.WHITE);
        font.getData().setScale(1.2f);
        // Changed prompt text
        font.draw(batch, "[E] Coletar Planta Medicinal", boxX + 10, boxY + 20);
        font.dispose();
    }

}
