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
import org.bukkit.material.Attachable;


public class LadderDropDown extends EnhancedMechanic implements Listener {

    public LadderDropDown(EnhancedVanilla plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLadderRightClick(PlayerInteractEvent event) {
        if (!isEnabled())
            return;

        // Trying to place a block where you can't doesn't fire the event with that slot (e.g. a ladder on another ladder)
        if (/*event.getHand() != EquipmentSlot.HAND ||*/ event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (event.getClickedBlock() == null || event.getItem() == null)
            return;

        if (event.getClickedBlock().getType() != Material.LADDER || event.getItem().getType() != Material.LADDER)
            return;

        Block block = event.getClickedBlock().getRelative(BlockFace.DOWN);
        if (!(block.isEmpty())  || block.isLiquid() || block.getType() == Material.DOUBLE_PLANT || block.getType() == Material.LONG_GRASS)
            return;
        
        Block against = block.getRelative(((Attachable) event.getClickedBlock().getState().getData()).getAttachedFace());
        if (!against.getType().isOccluding())
            return;
        
        if (!event.getPlayer().hasPermission("enhancedvanilla.mechanics.dropdownladder"))
            return;

        BlockState oldState = block.getState();

        block.setType(event.getClickedBlock().getType());
        BlockState newState = block.getState();
        newState.setData(event.getClickedBlock().getState().getData());
        newState.update(true, true);

        BlockPlaceEvent placeEvent = new BlockPlaceEvent(block, oldState, against, event.getItem(), event.getPlayer(), true, event.getHand());
        plugin.getServer().getPluginManager().callEvent(placeEvent);

        if (placeEvent.isCancelled() || !placeEvent.canBuild()) {
            block.setType(oldState.getType());
            block.getState().setData(placeEvent.getBlockReplacedState().getData());
            block.getState().update(true, false);
        }

        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_LADDER_PLACE, 1.0F, 1.0F);
    }
}
