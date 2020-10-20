package de.themoep.enhancedvanilla;

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

import de.themoep.enhancedvanilla.mechanics.*;
import de.themoep.enhancedvanilla.mechanics.treecutter.TreeCutter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class EnhancedVanilla extends JavaPlugin {

    private Map<String, EnhancedMechanic> mechanics = new LinkedHashMap<>();

    private final List<Class<? extends EnhancedMechanic>> availableMechanics = Arrays.asList(
            TreeCutter.class,
            ChickenShearing.class,
            LadderDropDown.class,
            MinecartBlockPlacement.class,
            ShaveSnowLayers.class,
            BetterSilkTouch.class,
            DoorKnocking.class,
            MoreSounds.class,
            MoreParticles.class,
            // The following mechanics are based on ideas by SimplySarc: https://youtu.be/NSsac8V3BpA
            BetterCompass.class,
            BiggerLitters.class,
            HealItems.class
    );

    public void onEnable() {
        for (Class<? extends EnhancedMechanic> mechanic : availableMechanics) {
            try {
                Constructor<? extends EnhancedMechanic> constructor = null;
                try {
                    constructor = mechanic.getConstructor(getClass());
                } catch (NoSuchMethodException ignored) {}
                if (constructor == null) {
                    getLogger().severe("Unable to get constructor of " + mechanic);
                    continue;
                }

                EnhancedMechanic m = constructor.newInstance(this);

                mechanics.put(m.getName(), m);
                if (m instanceof Listener) {
                    getServer().getPluginManager().registerEvents((Listener) m, this);
                }
            } catch (Throwable e) {
                getLogger().log(Level.SEVERE, "Error while creating new instance of " + mechanic, e);
            }
        }

        loadConfig();
    }

    private void loadConfig() {
        saveDefaultConfig();
        reloadConfig();
        for (EnhancedMechanic mechanic : mechanics.values()) {
            mechanic.loadConfig();
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  {
        if (args.length > 0) {
            if ("reload".equalsIgnoreCase(args[0]) && sender.hasPermission("enhancedvanilla.command.reload")) {
                loadConfig();
                sender.sendMessage(ChatColor.GREEN + getName() + " config reloaded!");
                return true;
            }
        }
        return false;
    }
}
