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
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class HealItems extends AdvancedEnhancedMechanic implements Listener {
    
    private Map<EntityType, HealSetting> healSettings = new HashMap<>();
    
    public HealItems(EnhancedVanilla plugin) {
        super(plugin);
    }
    
    @Override
    public void loadConfig() {
        super.loadConfig();
        for (String typeStr : getConfig().getConfigurationSection("mobs").getKeys(false)) {
            healSettings.put(EntityType.valueOf(typeStr.toUpperCase()), new HealSetting(getConfig().getConfigurationSection(typeStr)));
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityClick(PlayerInteractEntityEvent event) {
        if (!isEnabled()
                || event.getHand() != EquipmentSlot.HAND
                || !(event.getRightClicked() instanceof LivingEntity)
                || event.getPlayer().getInventory().getItemInMainHand() == null
                || event.getPlayer().getInventory().getItemInMainHand().getType().isAir()) {
            return;
        }
        
        heal((LivingEntity) event.getRightClicked(), event.getPlayer().getInventory().getItemInMainHand().getType());
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityHit(EntityDamageByEntityEvent event) {
        if (!isEnabled()
                || !(event.getDamager() instanceof Projectile)
                || !(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        
        HealSetting setting = healSettings.get(event.getEntity().getType());
        if (setting != null) {
            Material material = null;
            switch (event.getDamager().getType()) {
                case SNOWBALL:
                    material = Material.SNOWBALL;
                    break;
                case ARROW:
                    material = Material.ARROW;
                    break;
                case SPECTRAL_ARROW:
                    material = Material.SPECTRAL_ARROW;
                    break;
                case SMALL_FIREBALL:
                    material = Material.FIRE_CHARGE;
                    break;
                case FIREWORK:
                    material = Material.FIREWORK_ROCKET;
                    break;
                case SPLASH_POTION:
                    material = Material.SPLASH_POTION;
                    break;
                case WITHER_SKULL:
                    material = Material.WITHER_SKELETON_SKULL;
                    break;
            }
            if (heal((LivingEntity) event.getEntity(), material)) {
                event.setCancelled(true);
            }
        }
    }
    
    private boolean heal(LivingEntity entity, Material material) {
        if (entity == null || entity.getHealth() == entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() || material == null) {
            return false;
        }
    
        HealSetting setting = healSettings.get(entity.getType());
        if (setting != null) {
            Double amount = setting.getAmount(material);
            if (amount != null) {
                double newHealth = entity.getHealth() + amount;
                if (newHealth > entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()) {
                    newHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                }
                entity.setHealth(newHealth);
                return true;
            }
        }
        return false;
    }
    
    private class HealSetting {
        private Map<Material, Double> amounts = new HashMap<>();
    
        public HealSetting(ConfigurationSection config) {
            for (String materialStr : config.getKeys(false)) {
                Material material = Material.matchMaterial(materialStr);
                if (material == null) {
                    log(Level.WARNING, "Heal item for " + config.getName() + " is invalid! " + config.getName() + " will not be healable with " + materialStr + "!");
                } else {
                    amounts.put(material, config.getDouble(materialStr));
                }
            }
        }
    
        public Double getAmount(Material material) {
            return amounts.get(material);
        }
    }
}
