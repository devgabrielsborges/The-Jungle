package com.ranieborges.thejungle.cli.model.entity.itens;

import com.ranieborges.thejungle.cli.model.entity.Character;
import com.ranieborges.thejungle.cli.model.entity.Item;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.AmmunitionType;
import com.ranieborges.thejungle.cli.model.entity.utils.enums.WeaponType;
import com.ranieborges.thejungle.cli.view.Message; // For output
import com.ranieborges.thejungle.cli.view.utils.TerminalStyler; // For styling
import lombok.Getter;

@Getter
public class Weapon extends Item {
    private final WeaponType weaponType;
    private final float damage;
    private final float range;
    private final AmmunitionType ammoType;
    private int currentAmmoInClip;
    private final int maxAmmoInClip;

    public Weapon(String name, String description, float weight, int durability,
                  WeaponType weaponType, float damage, float range,
                  AmmunitionType ammoType, int maxAmmoInClip) {
        super(name, description, weight, durability);
        if (damage < 0) throw new IllegalArgumentException("Weapon damage cannot be negative.");
        if (range < 0) throw new IllegalArgumentException("Weapon range cannot be negative.");
        if (maxAmmoInClip < 0) throw new IllegalArgumentException("Max ammo in clip cannot be negative.");

        this.weaponType = weaponType;
        this.damage = damage;
        this.range = range;
        this.ammoType = ammoType;
        this.maxAmmoInClip = (this.ammoType == AmmunitionType.NONE) ? 0 : maxAmmoInClip;
        this.currentAmmoInClip = 0; // Start empty; requires explicit loading for clip-based weapons
        // For single-shot, ammo is drawn from inventory per shot.
    }

    public Weapon(String name, String description, float weight, int durability,
                  WeaponType weaponType, float damage) {
        this(name, description, weight, durability, weaponType, damage, 0f, AmmunitionType.NONE, 0);
        if (!weaponType.name().startsWith("MELEE_")) {
            // System.out.println("Warning: Melee constructor used for a non-melee weapon type: " + weaponType.getDisplayName());
        }
    }

    public boolean use(Character user, Character target) {
        if (getDurability() <= 0) {
            Message.displayOnScreen(TerminalStyler.warning(getName() + " is broken and cannot be used to attack."));
            return true; // Already broken
        }

        // Ammunition Handling
        if (this.ammoType != AmmunitionType.NONE) {
            if (this.maxAmmoInClip > 0) { // Weapon uses a clip/magazine
                if (this.currentAmmoInClip <= 0) {
                    Message.displayOnScreen(TerminalStyler.warning(getName() + " is out of ammunition! Needs to be reloaded."));
                    return false; // Cannot attack, weapon not consumed/broken by this attempt
                }
                this.currentAmmoInClip--;
                Message.displayOnScreen(TerminalStyler.info(getName() + " fired. Ammo left in clip: " + this.currentAmmoInClip + "/" + this.maxAmmoInClip));
            } else { // Single-shot weapon (e.g., bow), consumes ammo directly from inventory
                if (user.getInventory().countAmmunitionByType(this.ammoType) > 0) {
                    if (user.getInventory().removeAmmunitionByType(this.ammoType, 1)) {
                        Message.displayOnScreen(TerminalStyler.info(user.getName() + " used one " + this.ammoType.getDisplayName() + " with " + getName() + "."));
                    } else {
                        // Should not happen if countAmmunitionByType was > 0 and removeAmmunitionByType is correct
                        Message.displayOnScreen(TerminalStyler.error("Error consuming " + this.ammoType.getDisplayName() + ". Attack failed."));
                        return false;
                    }
                } else {
                    Message.displayOnScreen(TerminalStyler.warning(user.getName() + " has no " + this.ammoType.getDisplayName() + " to use with " + getName() + "."));
                    return false; // Cannot attack
                }
            }
        }

        Message.displayOnScreen(user.getName() + " attacks " + (target != null ? target.getName() : "something") + " with " + getName() + "!");

        if (target != null) {
            float totalDamage = this.damage + user.getAttackDamage();
            target.changeHealth(-totalDamage); // changeHealth should handle its own messages
            Message.displayOnScreen(TerminalStyler.info(getName() + " dealt " + String.format("%.1f", this.damage) + " base damage to " + target.getName() + "."));
        } else {
            Message.displayOnScreen(TerminalStyler.info(getName() + " dealt " + String.format("%.1f", this.damage) + " base damage."));
        }

        return decreaseDurability(); // Returns true if the weapon broke
    }

    @Override
    public boolean use(Character user) {
        Message.displayOnScreen(user.getName() + " readies " + getName() + ".");
        // Could implement equipping logic here.
        return false;
    }

    public boolean reload(Character user) {
        if (this.ammoType == AmmunitionType.NONE || this.maxAmmoInClip == 0) {
            Message.displayOnScreen(TerminalStyler.info(getName() + " cannot be reloaded in this way (not clip-based or uses no ammo)."));
            return false;
        }

        if (this.currentAmmoInClip >= this.maxAmmoInClip) {
            Message.displayOnScreen(TerminalStyler.info(getName() + " is already fully loaded."));
            return false;
        }

        int ammoNeeded = this.maxAmmoInClip - this.currentAmmoInClip;
        int ammoAvailableInInventory = user.getInventory().countAmmunitionByType(this.ammoType);

        if (ammoAvailableInInventory == 0) {
            Message.displayOnScreen(TerminalStyler.warning(user.getName() + " has no " + this.ammoType.getDisplayName() + " to reload " + getName() + "."));
            return false;
        }

        int ammoToLoad = Math.min(ammoNeeded, ammoAvailableInInventory);

        if (user.getInventory().removeAmmunitionByType(this.ammoType, ammoToLoad)) {
            this.currentAmmoInClip += ammoToLoad;
            Message.displayOnScreen(TerminalStyler.success("Reloaded " + getName() + " with " + ammoToLoad + " " + this.ammoType.getDisplayName() + "."));
            Message.displayOnScreen(TerminalStyler.info(getName() + " now has " + this.currentAmmoInClip + "/" + this.maxAmmoInClip + " ammo."));
            return true;
        } else {
            Message.displayOnScreen(TerminalStyler.error("Failed to remove ammunition from inventory during reload. Reload failed."));
            return false;
        }
    }

    @Override
    public String toString() {
        String ammoInfo = "";
        if (ammoType != AmmunitionType.NONE) {
            if (maxAmmoInClip > 0) {
                ammoInfo = String.format(", Ammo: %d/%d %s", currentAmmoInClip, maxAmmoInClip, ammoType.getDisplayName());
            } else {
                ammoInfo = String.format(", Uses: %s", ammoType.getDisplayName()); // For single-shot weapons
            }
        }
        return String.format("%s (Type: %s, Dmg: %.1f, Rng: %.1f%s, Dur: %d/%d, Wt: %.1f)",
                getName(), weaponType.getDisplayName(), damage, range, ammoInfo,
                getDurability(), getMaxDurability(), getWeight());
    }
}
