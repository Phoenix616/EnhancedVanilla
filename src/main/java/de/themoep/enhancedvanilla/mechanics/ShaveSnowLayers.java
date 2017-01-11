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

import de.themoep.enhancedvanilla.EnhancedVanilla;
import de.themoep.enhancedvanilla.EnhancedUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class ShaveSnowLayers extends EnhancedMechanic implements Listener {

    public ShaveSnowLayers(EnhancedVanilla plugin) {
        super(plugin);
    }

    @EventHandler
    public void onSnowRighclick(PlayerInteractEvent event) {
        if (!isEnabled())
            return;

        if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (event.getClickedBlock() == null || event.getItem() == null)
            return;

        if (event.getClickedBlock().getType() != Material.SNOW)
            return;

        if (!event.getItem().getType().toString().contains("SPADE"))
            return;

        if (!event.getPlayer().hasPermission(getPermissionNode()))
            return;

        Block block = event.getClickedBlock();
        BlockState previous = event.getClickedBlock().getState();

        BlockState newState = block.getState();
        byte stage = block.getState().getData().getData();
        if (stage == 0)
                return;
        stage--;

        newState.getData().setData(stage);
        newState.update(true, true);

        BlockPlaceEvent placeEvent = new BlockPlaceEvent(block, previous, block.getRelative(BlockFace.DOWN), event.getItem(), event.getPlayer(), true, event.getHand());
        plugin.getServer().getPluginManager().callEvent(placeEvent);

        if (placeEvent.isCancelled() || !placeEvent.canBuild()) {
            block.getState().setData(placeEvent.getBlockReplacedState().getData());
            block.getState().update(true, false);
        } else {
            EnhancedUtils.damageTool(event.getPlayer());
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.SNOW_BALL, 2));
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_SNOW_PLACE, 1.0F, 1.5F);
        }
    }
}
