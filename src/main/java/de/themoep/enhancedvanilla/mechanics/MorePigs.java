package de.themoep.enhancedvanilla.mechanics;

import de.themoep.enhancedvanilla.EnhancedVanilla;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

public class MorePigs extends AdvancedEnhancedMechanic implements Listener {
    
    private int amount = 1;
    
    public MorePigs(EnhancedVanilla plugin) {
        super(plugin);
    }
    
    @Override
    public void loadConfig() {
        super.loadConfig();
        amount = getConfig().getInt("amount");
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPigBreed(EntityBreedEvent event) {
        if (!isEnabled()) {
            return;
        }
        
        if (event.getEntity().getType() == EntityType.PIG) {
            for (int i = 1; i < amount; i++) {
                event.getEntity().getWorld().spawn(event.getEntity().getLocation(), Pig.class).setBaby();
            }
        }
    }
}
