package de.themoep.enhancedvanilla;

import de.themoep.enhancedvanilla.mechanics.*;
import de.themoep.enhancedvanilla.mechanics.bettersilktouch.BetterSilkTouch;
import de.themoep.enhancedvanilla.mechanics.treecutter.TreeCutter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * EnhancedVanilla
 * Copyright (C) 2016 Max Lee (https://github.com/Phoenix616/)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Mozilla Public License as published by
 * the Mozilla Foundation, version 2.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Mozilla Public License v2.0 for more details.
 * <p/>
 * You should have received a copy of the Mozilla Public License v2.0
 * along with this program. If not, see <http://mozilla.org/MPL/2.0/>.
 */

public class EnhancedVanilla extends JavaPlugin {

    private Map<String, EnhancedMechanic> mechanics = new LinkedHashMap<>();

    public void onEnable() {
        registerMechanic(new TreeCutter(this));
        registerMechanic(new ChickenShearing(this));
        registerMechanic(new LadderDropDown(this));
        registerMechanic(new MinecartBlockPlacement(this));
        registerMechanic(new ShaveSnowLayers(this));
        registerMechanic(new BetterSilkTouch(this));

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
