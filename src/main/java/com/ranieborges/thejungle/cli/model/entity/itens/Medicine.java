package com.ranieborges.thejungle.cli.model.entity.itens;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MedicineEffect;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.MedicineType;
import lombok.Getter;

/**
 * Represents a medicine item in the game.
 * Medicines can have various effects on characters, such as healing, curing infections, or relieving pain.
 * They can be single-use or multi-dose items.
 */
@Getter
public class Medicine extends Item {
    private final MedicineType medicineType;
    private final MedicineEffect primaryEffect;
    private final float effectPotency;
    private final int doses;
    private int remainingDoses;

    /**
     * Constructor for a Medicine item.
     *
     * @param name           The name of the medicine (e.g., "Clean Bandage", "Herbal Antibiotic").
     * @param description    A brief description.
     * @param weight         The weight of the item.
     * @param medicineType   The type of medicine.
     * @param primaryEffect  The primary medicinal effect.
     * @param effectPotency  The strength/magnitude of the effect.
     * @param doses          The number of doses this medicine contains (1 for single-use).
     */
    public Medicine(String name, String description, float weight, MedicineType medicineType,
                    MedicineEffect primaryEffect, float effectPotency, int doses) {
        super(name, description, weight);
        if (effectPotency < 0) {
            throw new IllegalArgumentException("Effect potency cannot be negative.");
        }
        if (doses <= 0) {
            throw new IllegalArgumentException("Doses must be a positive integer.");
        }
        this.medicineType = medicineType;
        this.primaryEffect = primaryEffect;
        this.effectPotency = effectPotency;
        this.doses = doses;
        this.remainingDoses = doses;
    }

    /**
     * Simplified constructor for single-dose medicine.
     */
    public Medicine(String name, String description, float weight, MedicineType medicineType,
                    MedicineEffect primaryEffect, float effectPotency) {
        this(name, description, weight, medicineType, primaryEffect, effectPotency, 1);
    }


    /**
     * The character consumes/applies the medicine.
     * The defined effect is applied to the character.
     *
     * @param user The character using the medicine.
     * @return true if the medicine is fully consumed (all doses used), false otherwise.
     */
    @Override
    public boolean use(Character user) {
        if (remainingDoses <= 0) {
            System.out.println(getName() + " has no doses left.");
            return false; // Cannot be used
        }

        System.out.println(user.getName() + " uses " + getName() + " (" + medicineType.getDisplayName() + ").");
        applyEffect(user);
        this.remainingDoses--;

        if (this.remainingDoses > 0) {
            System.out.println(getName() + " has " + this.remainingDoses + "/" + this.doses + " doses remaining.");
            return false;
        } else {
            System.out.println(getName() + " has been fully used.");
            return true;
        }
    }

    /**
     * Helper method to apply the specific medicinal effect to the character.
     * This would be expanded with more complex status effect systems.
     * @param user The character receiving the effect.
     */
    private void applyEffect(Character user) {
        switch (this.primaryEffect) {
            case HEAL_WOUNDS:
                user.changeHealth(this.effectPotency);
                System.out.println(user.getName() + " heals for " + this.effectPotency + " health. Current health: " + String.format("%.1f", user.getHealth()));
                break;
            case CURE_INFECTION:
                System.out.println(user.getName() + "'s infection is treated. (Conceptual: remove 'infected' status)");
                // user.removeStatusEffect(StatusEffect.INFECTION);
                break;
            case RELIEVE_PAIN:
                user.changeSanity(this.effectPotency); // Example: Pain relief boosts sanity
                System.out.println(user.getName() + " feels a wave of relief. Sanity +" + this.effectPotency);
                break;
            case CURE_POISON:
                System.out.println(user.getName() + "'s poisoning is counteracted. (Conceptual: remove 'poisoned' status)");
                // user.removeStatusEffect(StatusEffect.POISON);
                break;
            case BOOST_ENERGY:
                user.changeEnergy(this.effectPotency);
                System.out.println(user.getName() + " feels a surge of energy. Energy +" + this.effectPotency);
                break;
            case BOOST_SANITY:
                user.changeSanity(this.effectPotency);
                System.out.println(user.getName() + " feels their mind clear. Sanity +" + this.effectPotency);
                break;
            case REDUCE_SICKNESS:
                // Could improve a general "sick" status or slightly boost health/hunger if sickness was affecting them
                System.out.println(user.getName() + " feels less sick.");
                user.changeHealth(this.effectPotency / 2);
                break;
            case SOOTHE_BURNS:
            case STABILIZE_FRACTURE:
            case PURIFY_WATER: // This effect would target an Item (Water) not the Character directly.
                // The use() method might need to be more flexible or have variants.
                System.out.println("Effect '" + this.primaryEffect + "' applied (specific logic to be implemented).");
                break;
            default:
                System.out.println("The medicine has an unknown effect.");
                break;
        }
    }

    @Override
    public String toString() {
        String doseInfo = (doses > 1) ? String.format(", Doses: %d/%d", remainingDoses, doses) : "";
        return String.format("%s (Type: %s, Effect: %s, Potency: %.1f, Wt: %.1f%s)",
                getName(), medicineType.getDisplayName(), primaryEffect.name(), effectPotency, getWeight(), doseInfo);
    }
}
