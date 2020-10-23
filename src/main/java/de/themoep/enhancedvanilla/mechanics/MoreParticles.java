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
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PigZombieAngerEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class MoreParticles extends AdvancedEnhancedMechanic implements Listener {

    private Map<String, ParticleInfo> particles = new HashMap<>();

    public MoreParticles(EnhancedVanilla plugin) {
        super(plugin);
    }

    @Override
    public void loadConfig() {
        super.loadConfig();

        ConfigurationSection particlesCfg = getConfig().getConfigurationSection("particles");
        if (particlesCfg != null) {
            for (String type : particlesCfg.getKeys(false)) {
                try {
                    if (particlesCfg.isConfigurationSection(type)) {
                        particles.put(type, new ParticleInfo(
                                particlesCfg.getString(type + ".particle"),
                                particlesCfg.getInt(type + ".count", 1),
                                particlesCfg.getDouble(type + ".radius", 0.5),
                                particlesCfg.getDouble(type + ".speed", 0)
                        ));
                    } else {
                        particles.put(type, new ParticleInfo(particlesCfg.getString(type)));
                    }
                } catch (IllegalArgumentException e) {
                    log(Level.SEVERE, "Error while loading " + type + ": " + e.getMessage());
                }
            }
        } else {
            log(Level.WARNING, "No sounds defined in config!");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPigZombieAnger(PigZombieAngerEvent event) {
        if (isEnabled() && event.getNewAnger() > 0) {
            playParticle(event.getEntity().getLocation(), "pig-zombie-anger");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemDespawn(ItemDespawnEvent event) {
        if (isEnabled()) {
            playParticle(event.getLocation(), "item-despawn");
        }
    }

    private void playParticle(Location location, String particle) {
        if (location.getWorld() != null) {
            ParticleInfo particleInfo = particles.get(particle);
            if (particleInfo != null) {
                location.getWorld().spawnParticle(
                        particleInfo.getParticle(),
                        location,
                        particleInfo.getCount(),
                        particleInfo.getRadius(),
                        particleInfo.getRadius(),
                        particleInfo.getRadius(),
                        particleInfo.getSpeed()
                );
            }
        }
    }

    private class ParticleInfo {
        private final Particle particle;
        private final int count;
        private final double radius;
        private final double speed;

        public ParticleInfo(String particle) {
            this(particle, 1, 0.5, 0);
        }

        public ParticleInfo(String particle, int count, double radius, double speed) {
            this.particle = Particle.valueOf(particle.toUpperCase().replace('-', '_'));
            this.count = count;
            this.radius = radius;
            this.speed = speed;
        }

        public Particle getParticle() {
            return particle;
        }

        public int getCount() {
            return count;
        }

        public double getRadius() {
            return radius;
        }

        public double getSpeed() {
            return speed;
        }
    }
}
