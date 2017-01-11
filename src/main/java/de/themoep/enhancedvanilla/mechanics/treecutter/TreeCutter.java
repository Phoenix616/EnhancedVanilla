package de.themoep.enhancedvanilla.mechanics.treecutter;

/**
 * EnhancedVanilla
 * Copyright (C) 2016 Max Lee (https://github.com/Phoenix616/)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Mozilla Public License as published by
 * the Mozilla Foundation, version 2.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Mozilla Public License v2.0 for more details.
 * <p/>
 * You should have received a copy of the Mozilla Public License v2.0
 * along with this program. If not, see <http://mozilla.org/MPL/2.0/>.
 */

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.themoep.enhancedvanilla.EnhancedVanilla;
import de.themoep.enhancedvanilla.EnhancedUtils;
import de.themoep.enhancedvanilla.mechanics.EnhancedMechanic;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class TreeCutter extends EnhancedMechanic implements Listener {

    private Cache<UUID, List<Block>> treeCache;

    public TreeCutter(EnhancedVanilla plugin) {
        super(plugin);
        treeCache = CacheBuilder.newBuilder().expireAfterAccess(60, TimeUnit.SECONDS).build();
    }

    @EventHandler(ignoreCancelled = true)
    public void onLogBreak(BlockBreakEvent event) {
        //log(Level.INFO, "Is TreeCutterBlockBreakEvent: " + (event instanceof TreeCutterBlockBreakEvent));
        if (!isEnabled() || event instanceof TreeCutterBlockBreakEvent)
            return;

        //log(Level.INFO, "event.getPlayer().isSneaking(): " + event.getPlayer().isSneaking());
        if (event.getPlayer().isSneaking())
            return;

        //log(Level.INFO, "event.getBlock().getType() != Material.LOG && event.getBlock().getType() != Material.LOG_2: " + (event.getBlock().getType() != Material.LOG && event.getBlock().getType() != Material.LOG_2));
        if (event.getBlock().getType() != Material.LOG && event.getBlock().getType() != Material.LOG_2)
            return;

        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        //log(Level.INFO, "tool == null || !tool.getType().toString().contains(\"AXE\"): " + (tool == null || !tool.getType().toString().contains("AXE")));
        if (tool == null || !tool.getType().toString().contains("AXE"))
            return;

        //log(Level.INFO, "!event.getPlayer().hasPermission(getPermissionNode()): " + (!event.getPlayer().hasPermission(getPermissionNode())));
        if (!event.getPlayer().hasPermission(getPermissionNode()))
            return;

        List<Block> tree = treeCache.getIfPresent(event.getPlayer().getUniqueId());
        //log(Level.INFO, "tree == null || tree.size() <= 1 || !tree.contains(event.getBlock()): " + (tree == null || tree.size() <= 1 || !tree.contains(event.getBlock())));
        if (tree == null || tree.size() <= 1 || !tree.contains(event.getBlock())) {
            // User has no tree cached or the broken block is not in the last cached tree
            tree = EnhancedUtils.getTree(event.getBlock());
        }

        //log(Level.INFO, "tree == null || tree.size() <= 1: " + (tree == null || tree.size() <= 1));
        if (tree == null || tree.size() <= 1) {
            // No tree found or size is 1 or 0
            return;
        }

        event.setCancelled(true);
        Block block = tree.get(tree.size() - 1);

        //log(Level.INFO, "block.getType() != Material.LOG && block.getType() != Material.LOG_2: " + (block.getType() != Material.LOG && block.getType() != Material.LOG_2));
        if (block.getType() != Material.LOG && block.getType() != Material.LOG_2) {
            tree.remove(block);
            treeCache.put(event.getPlayer().getUniqueId(), tree);
            return;
        }

        TreeCutterBlockBreakEvent breakEvent = new TreeCutterBlockBreakEvent(block, event.getPlayer(), tree);
        plugin.getServer().getPluginManager().callEvent(breakEvent);

        //log(Level.INFO, "!breakEvent.isCancelled(): " + (!breakEvent.isCancelled()));
        if (!breakEvent.isCancelled()) {
            tree = breakEvent.getTree();
            tree.remove(block);
            treeCache.put(event.getPlayer().getUniqueId(), tree);
            block.setType(Material.AIR);
            EnhancedUtils.damageTool(event.getPlayer(), false);
            for (ItemStack drop : block.getDrops(tool)) {
                for (ItemStack rest : event.getPlayer().getInventory().addItem(drop).values()) {
                    event.getBlock().getWorld().dropItemNaturally(event.getPlayer().getLocation(), rest);
                }
            }
            event.getPlayer().updateInventory();
        }
    }

}
