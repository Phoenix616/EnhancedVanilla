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
import de.themoep.enhancedvanilla.mechanics.BetterSilkTouch;
import de.themoep.enhancedvanilla.mechanics.treecutter.TreeCutter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;

public class EnhancedVanilla extends JavaPlugin {

    private Map<String, EnhancedMechanic> mechanics = new LinkedHashMap<>();

    public void onEnable() {
        registerMechanic(new TreeCutter(this));
        registerMechanic(new ChickenShearing(this));
        registerMechanic(new LadderDropDown(this));
        registerMechanic(new MinecartBlockPlacement(this));
        registerMechanic(new ShaveSnowLayers(this));
        registerMechanic(new BetterSilkTouch(this));
        registerMechanic(new DoorKnocking(this));
        registerMechanic(new MoreSounds(this));
        registerMechanic(new MoreParticles(this));

        // The following mechanics are based on ideas by SimplySarc: https://youtu.be/NSsac8V3BpA
        registerMechanic(new BetterCompass(this));
        registerMechanic(new BiggerLitters(this));
        registerMechanic(new HealItems(this));

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

    public void registerMechanic(EnhancedMechanic mechanic) {
        mechanics.put(mechanic.getClass().getTypeName(), mechanic);
    }
}
