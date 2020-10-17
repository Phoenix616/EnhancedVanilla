package de.themoep.enhancedvanilla.mechanics;

/*
 * EnhancedVanilla
 * Copyright (c) 2020 Max Lee aka Phoenix616 (max@themoep.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import de.themoep.enhancedvanilla.EnhancedUtils;
import de.themoep.enhancedvanilla.EnhancedVanilla;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Iterator;

public class ChickenShearing extends AdvancedEnhancedMechanic implements Listener {

    private double damagePerShear;
    private boolean shearWithoutShears;

    public ChickenShearing(EnhancedVanilla plugin) {
        super(plugin);
    }

    @Override
    public void loadConfig() {
        super.loadConfig();
        damagePerShear = getConfig().getDouble("damage-per-shear");
        shearWithoutShears = getConfig().getBoolean("shear-without-shears");
    }

    @EventHandler(ignoreCancelled = true)
    public void onChickenShear(PlayerInteractAtEntityEvent event) {
        if (!isEnabled())
            return;

        if (event.getHand() != EquipmentSlot.HAND)
            return;

        if (event.getRightClicked().getType() != EntityType.CHICKEN)
            return;

        if (!shearWithoutShears) {
            ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
            if (tool != null && tool.getType() != Material.SHEARS)
                return;
        }

        Chicken chicken = (Chicken) event.getRightClicked();
        if (!chicken.isAdult())
            return;

        if (!event.getPlayer().hasPermission(getPermissionNode()))
            return;

        chicken.setMetadata("ehSheared", new FixedMetadataValue(plugin, true));
        chicken.damage(damagePerShear, event.getPlayer());
        EnhancedUtils.damageTool(event.getPlayer());
        if (chicken.getHealth() > 0) {
            chicken.getLocation().getWorld().dropItemNaturally(chicken.getLocation(), new ItemStack(Material.FEATHER));
        }
        chicken.getLocation().getWorld().playSound(chicken.getLocation(), Sound.ENTITY_SHEEP_SHEAR, 1.0F, 1.6F);
    }

    @EventHandler(ignoreCancelled = true)
    public void onChickenDeath(EntityDeathEvent event) {
        if (!isEnabled())
            return;

        if (event.getEntity().getType() != EntityType.CHICKEN)
            return;

        if (event.getEntity().hasMetadata("ehSheared")) {
            Iterator<ItemStack> drops = event.getDrops().iterator();
            while (drops.hasNext()) {
                if (drops.next().getType() == Material.FEATHER) {
                    drops.remove();
                }
            }
        }
    }
}
