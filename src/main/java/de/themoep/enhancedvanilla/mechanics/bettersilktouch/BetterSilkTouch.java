package de.themoep.enhancedvanilla.mechanics.bettersilktouch;

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

import de.themoep.enhancedvanilla.EnhancedVanilla;
import de.themoep.enhancedvanilla.mechanics.AdvancedEnhancedMechanic;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class BetterSilkTouch extends AdvancedEnhancedMechanic implements Listener {

    private Set<MaterialData> blocks = new HashSet<>();

    public BetterSilkTouch(EnhancedVanilla plugin) {
        super(plugin);
    }

    @Override
    public void loadConfig() {
        super.loadConfig();

        for (String matStr : getConfig().getStringList("blocks")) {
            try {
                String[] parts = matStr.split(":");
                Material mat = Material.valueOf(parts[0].toUpperCase());
                if (parts.length > 1) {
                    blocks.add(new MaterialData(mat, Byte.parseByte(parts[1])));
                } else {
                    blocks.add(new MaterialData(mat));
                }
            } catch (NumberFormatException e) {
                log(Level.SEVERE, matStr + " is not a valid Bukkit MaterialData!");
            } catch (IllegalArgumentException e) {
                log(Level.SEVERE, matStr + " is not a valid Bukkit Material name!");
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isEnabled() || event instanceof BetterSilkTouchBlockBreakEvent)
            return;

        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        if (tool == null || !tool.containsEnchantment(Enchantment.SILK_TOUCH))
            return;

        if (!event.getPlayer().hasPermission(getPermissionNode()))
            return;

        if (!blocks.contains(event.getBlock().getState().getData()))
            return;

        ItemStack drop = event.getBlock().getState().getData().toItemStack(1);
        if (event.getBlock().getDrops().size() > 0) {
            event.setCancelled(true);
            BetterSilkTouchBlockBreakEvent breakEvent = new BetterSilkTouchBlockBreakEvent(event);
            plugin.getServer().getPluginManager().callEvent(breakEvent);
            if (!breakEvent.isCancelled()) {
                event.getBlock().setType(Material.AIR);
            } else {
                return;
            }
        }
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), drop);
    }
}
