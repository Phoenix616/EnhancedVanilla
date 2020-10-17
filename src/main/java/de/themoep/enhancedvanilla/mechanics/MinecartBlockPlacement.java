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
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class MinecartBlockPlacement extends EnhancedMechanic implements Listener {

    public MinecartBlockPlacement(EnhancedVanilla plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMinecartRightClick(PlayerInteractAtEntityEvent event) {
        if (!isEnabled())
            return;

        if (event.getHand() != EquipmentSlot.HAND)
            return;

        if (event.getRightClicked().getType() != EntityType.MINECART)
            return;

        ItemStack clicked = event.getPlayer().getInventory().getItemInMainHand();
        if (clicked == null)
            return;

        if (!event.getPlayer().hasPermission(getPermissionNode() + "." + clicked.getType().toString().toLowerCase()))
            return;

        try {
            EntityType newCart = EntityType.valueOf("MINECART_" + clicked.getType());
            event.setCancelled(true);
            event.getRightClicked().remove();
            event.getRightClicked().getWorld().spawnEntity(event.getRightClicked().getLocation(), newCart);
        } catch (IllegalArgumentException ignored) {
            // Not a minecart type
        }

    }
}
