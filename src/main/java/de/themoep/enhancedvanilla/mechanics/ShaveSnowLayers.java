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
import de.themoep.enhancedvanilla.EnhancedUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Snow;
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

        if (event.getClickedBlock().getType() != Material.SNOW && event.getClickedBlock().getType() != Material.SNOW_BLOCK)
            return;

        if (!event.getItem().getType().name().endsWith("_SHOVEL"))
            return;

        if (!event.getPlayer().hasPermission(getPermissionNode()))
            return;

        Block block = event.getClickedBlock();
        BlockState previous = event.getClickedBlock().getState();

        BlockState newState = block.getState();
        Snow data;
        if (block.getType() == Material.SNOW) {
            data = (Snow) newState.getBlockData();
            if (data.getLayers() == data.getMinimumLayers())
                return;
            data.setLayers(data.getLayers() - 1);
        } else { // is SNOW_BLOCK
            newState.setType(Material.SNOW);
            data = (Snow) newState.getBlockData();
            data.setLayers(7);
        }
        newState.setBlockData(data);
        newState.update(true, true);

        BlockPlaceEvent placeEvent = new BlockPlaceEvent(block, previous, block.getRelative(BlockFace.DOWN), event.getItem(), event.getPlayer(), true, event.getHand());
        plugin.getServer().getPluginManager().callEvent(placeEvent);

        if (placeEvent.isCancelled() || !placeEvent.canBuild()) {
            block.getState().setData(placeEvent.getBlockReplacedState().getData());
            block.getState().update(true, false);
        } else {
            EnhancedUtils.damageTool(event.getPlayer());
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.SNOWBALL, 2));
            block.getWorld().playSound(block.getLocation(), Sound.BLOCK_SNOW_PLACE, 1.0F, 1.5F);
        }
    }
}
