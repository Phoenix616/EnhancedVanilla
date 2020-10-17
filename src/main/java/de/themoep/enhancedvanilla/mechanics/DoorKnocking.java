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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.themoep.enhancedvanilla.EnhancedVanilla;
import de.themoep.enhancedvanilla.SoundInfo;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class DoorKnocking extends AdvancedEnhancedMechanic implements Listener {

    private Map<Material, SoundInfo> sounds = new HashMap<>();
    private boolean requiresSneaking;
    private boolean knockWithRightClick;

    Cache<UUID, Boolean> isBreakingBlock;

    public DoorKnocking(EnhancedVanilla plugin) {
        super(plugin);
    }

    @Override
    public void loadConfig() {
        super.loadConfig();

        requiresSneaking = getConfig().getBoolean("requires-sneaking");
        knockWithRightClick = getConfig().getBoolean("knock-with-right-click");

        if (knockWithRightClick) {
            isBreakingBlock = null;
        } else {
            isBreakingBlock = CacheBuilder.newBuilder().expireAfterAccess(250, TimeUnit.MILLISECONDS).build();
        }

        ConfigurationSection soundsCfg = getConfig().getConfigurationSection("sounds");
        if (soundsCfg != null) {
            for (String matStr : soundsCfg.getKeys(false)) {
                try {
                    Material mat = Material.valueOf(matStr.toUpperCase());
                    if (soundsCfg.isConfigurationSection(matStr)) {
                        sounds.put(mat, new SoundInfo(
                                soundsCfg.getString(matStr + ".sound"),
                                (float) soundsCfg.getDouble(matStr + ".volume", 1),
                                (float) soundsCfg.getDouble(matStr + ".pitch", 1))
                        );
                    } else {
                        sounds.put(mat, new SoundInfo(soundsCfg.getString(matStr)));
                    }
                } catch (NumberFormatException e) {
                    log(Level.SEVERE, matStr + " is not a valid Bukkit MaterialData!");
                } catch (IllegalArgumentException e) {
                    log(Level.SEVERE, matStr + " is not a valid Bukkit Material name!");
                }
            }
        } else {
            log(Level.WARNING, "No sounds defined in config!");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBlockDamage(BlockDamageEvent event) {
        if (!knockWithRightClick && isBreakingBlock.getIfPresent(event.getPlayer().getUniqueId()) == null) {
            isBreakingBlock.put(event.getPlayer().getUniqueId(), true);
        }
    }

    @EventHandler
    public void onPlayerDoorKnock(PlayerInteractEvent event) {
        if (!isEnabled())
            return;

        if (event.getHand() != EquipmentSlot.HAND)
            return;

        if (requiresSneaking) {
            if (!event.getPlayer().isSneaking()) {
                return;
            }
        } else if (event.getPlayer().isSneaking()) {
            return;
        }

        if (knockWithRightClick) {
            if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
                return;
            }
        } else {
            if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_AIR)
                return;

            if (isBreakingBlock.getIfPresent(event.getPlayer().getUniqueId()) != null)
                return;
        }

        if (!event.getPlayer().hasPermission(getPermissionNode()))
            return;

        if (event.getClickedBlock() == null || !sounds.containsKey(event.getClickedBlock().getType()))
            return;

        SoundInfo soundInfo = sounds.get(event.getClickedBlock().getType());

        event.getClickedBlock().getWorld().playSound(event.getClickedBlock().getLocation(), soundInfo.getSound(), soundInfo.getVolume(), soundInfo.getPitch());

        if (knockWithRightClick)
            event.setUseInteractedBlock(Event.Result.DENY);

        event.setCancelled(true);
    }

}
