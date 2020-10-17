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
import de.themoep.enhancedvanilla.SoundInfo;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.PortalCreateEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class MoreSounds extends AdvancedEnhancedMechanic implements Listener {

    private Map<String, SoundInfo> sounds = new HashMap<>();

    public MoreSounds(EnhancedVanilla plugin) {
        super(plugin);
    }

    @Override
    public void loadConfig() {
        super.loadConfig();

        ConfigurationSection soundsCfg = getConfig().getConfigurationSection("sounds");
        if (soundsCfg != null) {
            for (String type : soundsCfg.getKeys(false)) {
                if (soundsCfg.isConfigurationSection(type)) {
                    sounds.put(type, new SoundInfo(
                            soundsCfg.getString(type + ".sound"),
                            (float) soundsCfg.getDouble(type + ".volume", 1),
                            (float) soundsCfg.getDouble(type + ".pitch", 1))
                    );
                } else {
                    sounds.put(type, new SoundInfo(soundsCfg.getString(type)));
                }
            }
        } else {
            log(Level.WARNING, "No sounds defined in config!");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPortalCreation(PortalCreateEvent event) {
        if (isEnabled() && event.getReason() == PortalCreateEvent.CreateReason.NETHER_PAIR && !event.getBlocks().isEmpty()) {
            playSound(event.getBlocks().get(0).getLocation(), "nether-portal-creation");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (isEnabled()) {
            playSound(event.getPlayer().getLocation(), "player-join");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemDespawn(ItemDespawnEvent event) {
        if (isEnabled()) {
            playSound(event.getLocation(), "item-despawn");
        }
    }

    private void playSound(Location location, String sound) {
        if (location.getWorld() != null) {
            SoundInfo soundInfo = sounds.get(sound);
            if (soundInfo != null) {
                location.getWorld().playSound(location, soundInfo.getSound(), soundInfo.getVolume(), soundInfo.getPitch());
            }
        }
    }
}
