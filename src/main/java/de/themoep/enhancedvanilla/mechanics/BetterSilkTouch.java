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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class BetterSilkTouch extends AdvancedEnhancedMechanic implements Listener {

    private Set<Material> blocks = new HashSet<>();

    public BetterSilkTouch(EnhancedVanilla plugin) {
        super(plugin);
    }

    @Override
    public void loadConfig() {
        super.loadConfig();

        for (String matStr : getConfig().getStringList("blocks")) {
            Material mat = Material.matchMaterial(matStr);
            if (mat != null) {
                blocks.add(mat);
            } else {
                log(Level.SEVERE, matStr + " is not a valid Bukkit Material name!");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isEnabled())
            return;

        ItemStack tool = event.getPlayer().getInventory().getItemInMainHand();
        if (tool == null || !tool.containsEnchantment(Enchantment.SILK_TOUCH))
            return;

        if (!event.getPlayer().hasPermission(getPermissionNode()))
            return;

        if (!blocks.contains(event.getBlock().getType()) || !event.isDropItems())
            return;

        ItemStack drop = event.getBlock().getState().getData().toItemStack(1);
        if (event.getBlock().getDrops().size() > 0) {
            event.setDropItems(false);
        }
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), drop);
    }
}
