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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class DoorKnocking extends AdvancedEnhancedMechanic implements Listener {

    private Map<MaterialData, Knock> sounds = new HashMap<>();
    private boolean requiresSneaking;
    private boolean knockWithRightclick;

    public DoorKnocking(EnhancedVanilla plugin) {
        super(plugin);
    }

    @Override
    public void loadConfig() {
        super.loadConfig();

        requiresSneaking = getConfig().getBoolean("requires-sneaking");
        knockWithRightclick = getConfig().getBoolean("knock-with-rightclick");

        ConfigurationSection soundsCfg = getConfig().getConfigurationSection("sounds");
        for (String matStr : soundsCfg.getKeys(false)) {
            try {
                MaterialData data = null;
                String[] parts = matStr.split(":");
                Material mat = Material.valueOf(parts[0].toUpperCase());
                if (parts.length > 1) {
                    data = new MaterialData(mat, Byte.parseByte(parts[1]));
                } else {
                    data = new MaterialData(mat);
                }

                if (soundsCfg.isConfigurationSection(matStr)) {
                    sounds.put(data, new Knock(
                            soundsCfg.getString("sound"),
                            (float) soundsCfg.getDouble("volume", 1),
                            (float) soundsCfg.getDouble("pitch", 1))
                    );
                } else {
                    sounds.put(data, new Knock(soundsCfg.getString(matStr)));
                }


            } catch (NumberFormatException e) {
                log(Level.SEVERE, matStr + " is not a valid Bukkit MaterialData!");
            } catch (IllegalArgumentException e) {
                log(Level.SEVERE, matStr + " is not a valid Bukkit Material name!");
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDoorKnock(PlayerInteractEvent event) {
        if (!isEnabled())
            return;

        if (event.getHand() != EquipmentSlot.HAND)
            return;

        if (requiresSneaking && !event.getPlayer().isSneaking())
            return;

        if (knockWithRightclick && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (!knockWithRightclick && event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        if (!event.getPlayer().hasPermission(getPermissionNode()))
            return;

        if (event.getClickedBlock() == null || !sounds.containsKey(event.getClickedBlock().getState().getData()))
            return;

        Knock knock = sounds.get(event.getClickedBlock().getState().getData());

        event.getClickedBlock().getWorld().playSound(event.getClickedBlock().getLocation(), knock.getSound(), knock.getVolume(), knock.getPitch());
    }

    private class Knock {
        private final String sound;
        private final float volume;
        private final float pitch;

        public Knock(String sound) {
            this(sound, 1, 1);
        }

        public Knock(String sound, float volume, float pitch) {
            this.sound = sound;
            this.volume = volume;
            this.pitch = pitch;
        }

        public String getSound() {
            return sound;
        }

        public float getVolume() {
            return volume;
        }

        public float getPitch() {
            return pitch;
        }
    }
}
