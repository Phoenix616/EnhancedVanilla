package de.themoep.enhancedvanilla.mechanics;

import de.themoep.enhancedvanilla.EnhancedUtils;
import de.themoep.enhancedvanilla.EnhancedVanilla;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.material.Colorable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BiggerLitters extends AdvancedEnhancedMechanic implements Listener {
    
    private static final Random RANDOM = new Random();
    private Map<EntityType, LitterSetting> litterSettings = new HashMap<>();
    private boolean moreExp;
    
    public BiggerLitters(EnhancedVanilla plugin) {
        super(plugin);
    }
    
    @Override
    public void loadConfig() {
        super.loadConfig();
        moreExp = getConfig().getBoolean("more-exp");
        for (String typeStr : getConfig().getConfigurationSection("mobs").getKeys(false)) {
            litterSettings.put(EntityType.valueOf(typeStr.toUpperCase()), new LitterSetting(getConfig().getConfigurationSection(typeStr)));
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBreed(EntityBreedEvent event) {
        if (!isEnabled()) {
            return;
        }
        
        int amount = 1;
        LitterSetting litterSetting = litterSettings.get(event.getEntity().getType());
        if (litterSetting != null) {
            for (; amount < litterSetting.getRandomAmount(); amount++) {
                LivingEntity entity = event.getEntity().getWorld().spawn(event.getEntity().getLocation(), event.getEntity().getClass());
                if (entity instanceof Ageable) {
                    ((Ageable) entity).setBaby();
                }
                
                if (entity instanceof Tameable && !(entity instanceof AbstractHorse) && event.getMother() instanceof Tameable) {
                    ((Tameable) entity).setOwner(((Tameable) event.getMother()).getOwner());
                }
                
                if (entity instanceof Ocelot) {
                    if (event.getMother() instanceof Ocelot && event.getFather() instanceof Ocelot) {
                        if (((Ocelot) event.getMother()).isSitting() != ((Ocelot) event.getFather()).isSitting()) {
                            Ocelot mobile = (Ocelot) (((Ocelot) event.getMother()).isSitting() ? event.getFather() : event.getMother());
                            ((Ocelot) entity).setCatType(mobile.getCatType());
                        } else if (RANDOM.nextBoolean()) {
                            ((Ocelot) entity).setCatType(((Ocelot) event.getMother()).getCatType());
                        } else {
                            ((Ocelot) entity).setCatType(((Ocelot) event.getFather()).getCatType());
                        }
                    } else {
                        ((Ocelot) entity).setCatType(((Ocelot) event.getEntity()).getCatType());
                    }
                }
                
                if (entity instanceof AbstractHorse) {
                    EnhancedUtils.setMergedAttributes(Attribute.GENERIC_MAX_HEALTH, entity, event.getMother(), event.getFather());
                    EnhancedUtils.setMergedAttributes(Attribute.GENERIC_MOVEMENT_SPEED, entity, event.getMother(), event.getFather());
                    EnhancedUtils.setMergedAttributes(Attribute.HORSE_JUMP_STRENGTH, entity, event.getMother(), event.getFather());
                }
                
                if (entity instanceof Horse) {
                    Horse.Color color = ((Horse) event.getEntity()).getColor();
                    Horse.Style style = ((Horse) event.getEntity()).getStyle();
                    if (event.getMother() instanceof Horse && event.getFather() instanceof Horse) {
                        if (RANDOM.nextBoolean()) {
                            color = ((Horse) event.getMother()).getColor();
                        } else {
                            color = ((Horse) event.getFather()).getColor();
                        }
                        if (RANDOM.nextBoolean()) {
                            style = ((Horse) event.getMother()).getStyle();
                        } else {
                            style = ((Horse) event.getMother()).getStyle();
                        }
                    }
                    if (RANDOM.nextDouble() < 1/9) {
                        color = EnhancedUtils.random(Horse.Color.values());
                    }
                    if (RANDOM.nextDouble() < 1/9) {
                        style = EnhancedUtils.random(Horse.Style.values());
                    }
                    ((Horse) entity).setColor(color);
                    ((Horse) entity).setStyle(style);
                }
                
                if (entity instanceof Llama) {
                    Llama.Color color = ((Llama) event.getEntity()).getColor();
                    int strength = ((Llama) event.getEntity()).getStrength();
                    if (event.getMother() instanceof Llama && event.getFather() instanceof Llama) {
                        if (RANDOM.nextBoolean()) {
                            color = ((Llama) event.getMother()).getColor();
                        } else {
                            color = ((Llama) event.getFather()).getColor();
                        }
                        strength = 1 + RANDOM.nextInt(((Llama) event.getMother()).getStrength() > ((Llama) event.getFather()).getStrength()
                                ? ((Llama) event.getMother()).getStrength() : ((Llama) event.getFather()).getStrength());
                        if (RANDOM.nextDouble() < 0.03) {
                            strength++;
                        }
                        if (strength > 5) {
                            strength = 5;
                        }
                    }
                    ((Llama) entity).setColor(color);
                    ((Llama) entity).setStrength(strength);
                }
                
                if (entity instanceof Rabbit) {
                    Rabbit.Type type;
                    double r = RANDOM.nextDouble();
                    if (r > 0.999) {
                        type = Rabbit.Type.THE_KILLER_BUNNY;
                    } else if (r >= 0.95) {
                        switch (event.getEntity().getLocation().getBlock().getBiome()) {
                            case ICE_FLATS:
                            case ICE_MOUNTAINS:
                            case FROZEN_OCEAN:
                            case FROZEN_RIVER:
                            case TAIGA_COLD:
                            case TAIGA_COLD_HILLS:
                                if (RANDOM.nextDouble() < 0.8) {
                                    type = Rabbit.Type.WHITE;
                                } else {
                                    type = Rabbit.Type.BLACK_AND_WHITE;
                                }
                                break;
                            case DESERT:
                            case DESERT_HILLS:
                                type = Rabbit.Type.GOLD;
                                break;
                            default:
                                double r2 = RANDOM.nextDouble();
                                if (r2 < 0.5) {
                                    type = Rabbit.Type.BROWN;
                                } else if (r2 < 0.9) {
                                    type = Rabbit.Type.SALT_AND_PEPPER;
                                } else {
                                    type = Rabbit.Type.BLACK;
                                }
                                break;
                                
                        }
                    } else if (event.getMother() instanceof Rabbit && event.getFather() instanceof Rabbit) {
                        if (r >= 0.475) {
                            type = ((Rabbit) event.getMother()).getRabbitType();
                        } else {
                            type = ((Rabbit) event.getFather()).getRabbitType();
                        }
                    } else {
                        type = ((Rabbit) event.getEntity()).getRabbitType();
                    }
                    ((Rabbit) entity).setRabbitType(type);
                }
                
                if (entity instanceof Colorable) {
                    if (event.getMother() instanceof Colorable && event.getFather() instanceof Colorable) {
                        if (!((Colorable) event.getFather()).getColor().equals(((Colorable) event.getEntity()).getColor())
                                && !((Colorable) event.getMother()).getColor().equals(((Colorable) event.getEntity()).getColor())) {
                            ((Colorable) entity).setColor(((Colorable) event.getEntity()).getColor());
                        } else if (RANDOM.nextBoolean()) {
                            ((Colorable) entity).setColor(((Colorable) event.getMother()).getColor());
                        } else {
                            ((Colorable) entity).setColor(((Colorable) event.getFather()).getColor());
                        }
                    } else {
                        ((Colorable) entity).setColor(((Colorable) event.getEntity()).getColor());
                    }
                }
            }
        }
        if (moreExp) {
            event.setExperience(event.getExperience() * amount);
        }
    }
    
    private class LitterSetting {
        private final int min;
        private final int max;
        
        public LitterSetting(ConfigurationSection config) {
            min = config.getInt("min", 1);
            max = config.getInt("max", 10);
        }
    
        public int getRandomAmount() {
            return min + RANDOM.nextInt(max - min + 1);
        }
    }
}
