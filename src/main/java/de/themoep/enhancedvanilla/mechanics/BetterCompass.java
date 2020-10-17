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
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class BetterCompass extends AdvancedEnhancedMechanic implements Listener {
    private String mode;
    private Direction direction = null;
    private boolean spawnPoint = false;
    private boolean worldSpawn = false;
    private boolean deathPoint = false;
    private double x = 0;
    private double z = 0;
    private Map<UUID, Location> deathPoints = new HashMap<>();
    
    public BetterCompass(EnhancedVanilla plugin) {
        super(plugin);
    }
    
    @Override
    public void loadConfig() {
        super.loadConfig();
        mode = getConfig().getString("point-to");
        try {
            direction = Direction.valueOf(mode.toUpperCase());
        } catch (IllegalArgumentException e) {
            if ("spawnpoint".equalsIgnoreCase(mode)) {
                spawnPoint = true;
            } else if ("worldspawn".equalsIgnoreCase(mode)) {
                worldSpawn = true;
            } else if ("deathpoint".equalsIgnoreCase(mode)) {
                deathPoint = true;
            } else if (mode.contains("/")) {
                String[] parts = mode.split("/", 2);
                try {
                    x = Double.parseDouble(parts[0]);
                    z = Double.parseDouble(parts[1]);
                } catch (IllegalArgumentException e1) {
                    log(Level.WARNING, "Wrong coordinates point-to setting '" + mode + "': " + e1.getMessage());
                    setEnabled(false);
                }
            } else {
                log(Level.WARNING, "Unknown point-to setting '" + mode + "'");
                setEnabled(false);
            }
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        setCompass(event.getPlayer());
    }
    
    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        setCompass(event.getPlayer());
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        setCompass(event.getPlayer());
    }
    
    public void setCompass(Player player) {
        if (!isEnabled()) {
            return;
        }
        Location target;
        if (direction != null) {
            target = player.getLocation().add(direction.getModX() * 100000, 0, direction.getModZ() * 100000);
        } else if (spawnPoint) {
            target = player.getBedSpawnLocation();
        } else if (worldSpawn) {
            target = player.getWorld().getSpawnLocation();
        } else if (deathPoint) {
            target = deathPoints.get(player.getUniqueId());
        } else {
            target = new Location(player.getWorld(), x, 0, z);
        }
    
        if (target != null && target.getWorld().equals(player.getWorld())) {
            player.setCompassTarget(target);
        } else {
            player.setCompassTarget(player.getWorld().getSpawnLocation()); // Is there really no way to spin the compass light in the nether/end? :/
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        deathPoints.put(event.getEntity().getUniqueId(), event.getEntity().getLocation());
    }
    
    private enum Direction {
        NORTH(0, -1),
        EAST(1, 0),
        SOUTH(0, 1),
        WEST(-1, 0);
    
        private final int modX;
        private final int modZ;
    
        Direction(int modX, int modZ) {
            this.modX = modX;
            this.modZ = modZ;
        }
    
        public int getModX() {
            return modX;
        }
    
        public int getModZ() {
            return modZ;
        }
    }
}
