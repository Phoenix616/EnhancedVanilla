package de.themoep.enhancedvanilla;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Leaves;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
public class EnhancedUtils {

    private static final Random RANDOM = new Random();

    public static List<Block> getTree(Block block) {
        if(block.getType() != Material.LOG && block.getType() != Material.LOG_2)
            // No logs, no tree!
            return null;

        final Set<Block> tree = new LinkedHashSet<>();
        final LinkedList<Block> queue = new LinkedList<>();

        int isTree = 0;

        queue.addLast(block);
        tree.add(block);

        while(!queue.isEmpty()) {
            final Block current = queue.removeFirst();
            for(int i = 0; i < 6; i++) {
                BlockFace face = BlockFace.values()[i];
                final Block next = current.getRelative(face);
                if(block.getLocation().distanceSquared(next.getLocation()) > 256) {
                    // Maximum range exceeded => stop walking
                    continue;
                }

                if(!tree.contains(next)) {
                    switch(next.getType()) {
                        case LEAVES:
                        case LEAVES_2:
                            if (((Leaves) next.getState().getData()).isDecayable()) {
                                // There are leaves that are decayable so it's probably a tree! \o/
                                isTree++;
                            } else {
                                return null;
                            }

                        case WATER:
                        case STATIONARY_WATER:
                        case VINE:
                        case AIR:
                        case SNOW:
                        case STONE:
                        case GRASS:
                        case DIRT:
                            // we hit something that isn't logs => stop walking this route
                            continue;

                        case LOG:
                        case LOG_2:
                            // queue next point
                            queue.addLast(next);
                            tree.add(next);
                            break;

                        default:
                            // we hit something solid that isn't part of a tree? So that's probable not a tree!!!
                            return null;
                    } // switch
                } // if
            } // for
        } // while

        if(isTree == 0) {
            // A tree has leaves, this has none
            return null;
        }

        return new ArrayList<>(tree);
    }

    /**
     * Damage the tool of a player by one while respecting unbreaking enchantment
     * (and the unbreakable tag on spigot). Updates the player's inventory
     * @param player The player to damage the tool for
     * @return The tool or <tt>null</tt> if the player doesn't have one or an error occurred
     */
    public static ItemStack damageTool(Player player) {
        return damageTool(player, true);
    }

    /**
     * Damage the tool of a player by one while respecting unbreaking enchantment
     * (and the unbreakable tag on spigot)
     * @param player The player to damage the tool for
     * @param update Update te player's invnentory?
     * @return The tool or <tt>null</tt> if the player doesn't have one or an error occurred
     */
    public static ItemStack damageTool(Player player, boolean update) {
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (tool == null) {
            return null;
        }

        try {
            if (tool.getItemMeta().spigot().isUnbreakable()) {
                return null;
            }
        } catch (Exception ignored) {
            // NoSuchMethod exception -> not a spigot server, can't check unbreakable :/
        }

        int unbreaking = tool.getEnchantmentLevel(Enchantment.DURABILITY);
        boolean damage = unbreaking == 0 || RANDOM.nextDouble() < 1 / (unbreaking + 1);
        if (damage) {
            tool.setDurability((short) (tool.getDurability() + 1));
            player.getInventory().setItemInMainHand(tool);
            if (update) {
                player.updateInventory();
            }
        }

        return tool;
    }
    
    /**
     * Get a random value from an array
     * @param values    The array
     * @return          A random value from the array
     */
    public static <T> T random(T[] values) {
        return values[RANDOM.nextInt(values.length)];
    }
    
    /**
     * Merge the attributes base values of different entities with each other
     * @param attribute The attribute to merge
     * @param entities  The entities to merge
     * @return          The merged value
     */
    public static double mergeAttributes(Attribute attribute, Attributable... entities) {
        double value = 0;
        int amount = 0;
        for (; amount < entities.length; amount++) {
            AttributeInstance instance = entities[amount].getAttribute(attribute);
            if (instance != null) {
                value += instance.getBaseValue();
            }
        }
        return amount > 0 ? value / amount : 0;
    }
    
    /**
     * Directly set the merged attributes
     * @param attribute The attribute to set
     * @param entities  The entities to merge. The attribute of the first one will be set!
     * @throws IllegalArgumentException If no entity was provided
     */
    public static void setMergedAttributes(Attribute attribute, LivingEntity... entities) {
        Validate.isTrue(entities.length > 1, "You need to at least provide two entities");
        AttributeInstance instance = entities[0].getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(mergeAttributes(attribute, entities));
        }
    }
}
