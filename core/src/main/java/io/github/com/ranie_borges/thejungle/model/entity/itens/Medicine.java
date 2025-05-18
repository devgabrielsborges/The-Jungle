package io.github.com.ranie_borges.thejungle.model.entity.itens;

import io.github.com.ranie_borges.thejungle.model.entity.Item;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import io.github.com.ranie_borges.thejungle.model.entity.Character;

/**
 * Represents a medicine item used for healing.
 */
public class Medicine extends Item {
    private double healRatio;

    public Medicine(String name, float weight, float durability, double healRatio) {
        super(name, weight, durability);
        setHealRatio(healRatio);
    }
    public static Medicine fromMedicinalPlant(Material plant) {
        if (plant == null || !"Plant".equalsIgnoreCase(plant.getType()) || !"Medicinal".equalsIgnoreCase(plant.getName())) {
            throw new IllegalArgumentException("O material não é uma planta medicinal válida.");
        }
        return new Medicine("Herbal Medicine", plant.getWeight(), 1.0f, 25.0);
    }

    @Override
    public void useItem() {
        setDurability(getDurability() - 1); // Usar o remédio reduz a durabilidade (pode ser removido depois)
    }

    @Override
    public void dropItem() {
        System.out.println("Você deixou cair o medicamento: " + getName() + ".");
    }

    public double getHealRatio() {
        return healRatio;
    }

    public void setHealRatio(double healRatio) {
        this.healRatio = Math.max(0, healRatio);
    }

    // ====== ADICIONADO: Factory Methods para criar remédios padrão ======

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
    public static void renderUseOption(SpriteBatch batch, Material plant, Character player) {
        if (plant == null || !"Plant".equalsIgnoreCase(plant.getType()) || !"Medicinal".equalsIgnoreCase(plant.getName()))
            return;

        Vector2 pos = plant.getPosition();
        float boxX = pos.x;
        float boxY = pos.y + 40;

        // Desenha fundo da caixa (você pode substituir com NinePatch ou ShapeRenderer se quiser)
        Texture bg = new Texture("GameScreen/boxhud.png");
        batch.setColor(1, 1, 1, 0.7f);
        batch.draw(bg, boxX, boxY, 160, 30);
        batch.setColor(1, 1, 1, 1); // reset

        BitmapFont font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.draw(batch, "[E] Usar planta medicinal", boxX + 10, boxY + 20);

        // Se o jogador pressionar E, converte e usa
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            try {
                Medicine med = fromMedicinalPlant(plant);
                player.heal(med);
                player.getInventory().removeValue(plant, true);
                System.out.println(player.getName() + " usou uma planta medicinal!");
            } catch (Exception e) {
                System.out.println("Erro ao tentar usar planta medicinal: " + e.getMessage());
            }
        }

        font.dispose();
        bg.dispose(); // descarrega o fundo da UI se não for manter ele carregado
    }

}
