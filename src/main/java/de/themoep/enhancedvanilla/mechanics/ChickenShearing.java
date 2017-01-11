package de.themoep.enhancedvanilla.mechanics;

/*
 * Copyright 2017 Max Lee (https://github.com/Phoenix616/)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Mozilla Public License as published by
 * the Mozilla Foundation, version 2.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Mozilla Public License v2.0 for more details.
 * 
 * You should have received a copy of the Mozilla Public License v2.0
 * along with this program. If not, see <http://mozilla.org/MPL/2.0/>.
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
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.LazyMetadataValue;

import java.util.Iterator;

public class ChickenShearing extends EnhancedMechanic implements Listener {

    public ChickenShearing(EnhancedVanilla plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onChickenShear(PlayerInteractAtEntityEvent event) {
        if (!isEnabled())
            return;

        if (event.getHand() != EquipmentSlot.HAND)
            return;

        if (event.getRightClicked().getType() != EntityType.CHICKEN)
            return;

        Chicken chicken = (Chicken) event.getRightClicked();
        if (!chicken.isAdult())
            return;

        if (!event.getPlayer().hasPermission(getPermissionNode()))
            return;

        chicken.setMetadata("ehSheared", new FixedMetadataValue(plugin, true));
        chicken.damage(1, event.getPlayer());
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
