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

import de.themoep.enhancedvanilla.EnhancedVanilla;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;


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
        if (!(block.isEmpty())  || block.isLiquid())
            return;
        
        Block against = block.getRelative(((Directional) event.getClickedBlock().getState().getBlockData()).getFacing().getOppositeFace());
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
