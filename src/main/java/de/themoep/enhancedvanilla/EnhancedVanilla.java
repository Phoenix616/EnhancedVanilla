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
        registerMechanic(new DoorKnocking(this));
        
        // The following mechanics are based on ideas by SimplySarc: https://youtu.be/NSsac8V3BpA
        registerMechanic(new BetterCompass(this));
        registerMechanic(new MorePigs(this));
        registerMechanic(new HealItems(this));
        // TODO: Attacking Polar Bears (all mobs?)
        // TODO: Auto breeding of certain mobs? (Difficulty: Rabbits)
        // TODO: Rabbits eating food
        // TODO: Skeletons sink under water?
        // TODO: Spreading of moss stone? (Difficulty: Meh)
        // TODO: Wither Bones + wither bone meal (Difficulty: Resourcepack -.-')
        // TODO: Herd behaviour (see enhanced llamas)
        // TODO: Hoe for leave bulks?
        // TODO: Unbreak iron golems (they don't spawn ;_;)
        // TODO: Gap jumping mobs (Difficulty: Muh Pathfinders)
        // TODO: Parrot eggs?
        // TODO: Clear effects with water bottles?
        // TODO: Improved structures generation e.g. updated villages, mineshafts with tnt etc. (Difficulty: Custom Worldgenerator)
        // TODO: Blaze sound fix
        // TODO: Swing through grass/similar blocks with swords
        // TODO: Firework boost in boats and minearts?
        // TODO: Pigzombies guarding quartz
        // TODO: Aggressive Pigzombie Eyecolors (Difficulty: Custom Skull)
        // TODO: Irregular Netherportals
        // TODO: Sharp blocks hurt when mined without fists
        // TODO: Decaying leaves ave effect (Difficulty: Repeating Tasks :/)
        // TODO: Biome spawners (Difficulty: Resourcepack \o/)
        // TODO: Natural drying sponges (Biome/Lava/Nether/Fire)
        // TODO: Fuel efficiency/speed
        // TODO: Cat type due to fish used to tame
        // TODO: Spawn special villager types in Villages (Witches, Vindicator, etc.)
        // TODO: Passive Mobs
        // TODO: Enchantmenttable displays item
        // TODO: Item despawn effect (flashing? particles?) (Difficulty: Delayed tasks or stuff)
        
        // Minecon Earth:
        // TODO: Blaze King! <3

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
