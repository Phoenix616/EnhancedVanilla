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

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.TreeSpecies;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class EnhancedUtils {

    private static final Random RANDOM = new Random();

    public static List<Block> getTree(Block block) {
        if(!Tag.LOGS.isTagged(block.getType()))
            // No logs, no tree!
            return null;

        TreeSpecies species = getSpecies(block);
        Material log = block.getType();
        Material leaves = getLeaves(species);

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
                    if (Tag.LEAVES.isTagged(next.getType())) {
                        if (leaves == next.getType()) {
                            if (!((Leaves) next.getState().getBlockData()).isPersistent()){
                                // There are leaves that are decayable so it's probably a tree! \o/
                                isTree++;
                            } else {
                                // Not a natural tree
                                return null;
                            }
                        } else {
                            // Different leaves => stop walinmg this route
                            continue;
                        }
                    } else if (log == next.getType()) {
                        // queue next point
                        queue.addLast(next);
                        tree.add(next);
                    } else {
                        switch(next.getType()) {
                            case WATER:
                            case VINE:
                            case AIR:
                            case SNOW:
                            case STONE:
                            case GRASS:
                            case MYCELIUM:
                            case COARSE_DIRT:
                            case DIRT:
                                // we hit something that isn't logs => stop walking this route
                                continue;
                            default:
                                // we hit something solid that isn't part of a tree? So that's probable not a tree!!!
                                return null;
                        } // switch
                    } // if
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
     * Get the leaves of a TreeSpecies
     * @param species The species
     * @return The leaves or null if the species doesn't have them or is unknown
     */
    public static Material getLeaves(TreeSpecies species) {
        switch (species) {
            case ACACIA:
                return Material.ACACIA_LEAVES;
            case BIRCH:
                return Material.BIRCH_LEAVES;
            case DARK_OAK:
                return Material.DARK_OAK_LEAVES;
            case GENERIC:
                return Material.OAK_LEAVES;
            case JUNGLE:
                return Material.JUNGLE_LEAVES;
            case REDWOOD:
                return Material.SPRUCE_LEAVES;
            default:
                return null;
        }
    }

    /**
     * Get the TreeSpecies from a block
     * @param block The block tog et it from
     * @return The TreeSpecies or null if the block can't belong to a tree
     */
    public static TreeSpecies getSpecies(Block block) {
        switch (block.getType()) {
            case ACACIA_BOAT:
            case ACACIA_BUTTON:
            case ACACIA_DOOR:
            case ACACIA_FENCE:
            case ACACIA_FENCE_GATE:
            case ACACIA_LEAVES:
            case ACACIA_LOG:
            case ACACIA_PLANKS:
            case ACACIA_PRESSURE_PLATE:
            case ACACIA_SAPLING:
            case ACACIA_SIGN:
            case ACACIA_SLAB:
            case ACACIA_STAIRS:
            case ACACIA_TRAPDOOR:
            case ACACIA_WALL_SIGN:
            case ACACIA_WOOD:
                return TreeSpecies.ACACIA;
            case BIRCH_BOAT:
            case BIRCH_BUTTON:
            case BIRCH_DOOR:
            case BIRCH_FENCE:
            case BIRCH_FENCE_GATE:
            case BIRCH_LEAVES:
            case BIRCH_LOG:
            case BIRCH_PLANKS:
            case BIRCH_PRESSURE_PLATE:
            case BIRCH_SAPLING:
            case BIRCH_SIGN:
            case BIRCH_SLAB:
            case BIRCH_STAIRS:
            case BIRCH_TRAPDOOR:
            case BIRCH_WALL_SIGN:
            case BIRCH_WOOD:
                return TreeSpecies.BIRCH;
            case DARK_OAK_BOAT:
            case DARK_OAK_BUTTON:
            case DARK_OAK_DOOR:
            case DARK_OAK_FENCE:
            case DARK_OAK_FENCE_GATE:
            case DARK_OAK_LEAVES:
            case DARK_OAK_LOG:
            case DARK_OAK_PLANKS:
            case DARK_OAK_PRESSURE_PLATE:
            case DARK_OAK_SAPLING:
            case DARK_OAK_SIGN:
            case DARK_OAK_SLAB:
            case DARK_OAK_STAIRS:
            case DARK_OAK_TRAPDOOR:
            case DARK_OAK_WALL_SIGN:
            case DARK_OAK_WOOD:
                return TreeSpecies.DARK_OAK;
            case JUNGLE_BOAT:
            case JUNGLE_BUTTON:
            case JUNGLE_DOOR:
            case JUNGLE_FENCE:
            case JUNGLE_FENCE_GATE:
            case JUNGLE_LEAVES:
            case JUNGLE_LOG:
            case JUNGLE_PLANKS:
            case JUNGLE_PRESSURE_PLATE:
            case JUNGLE_SAPLING:
            case JUNGLE_SIGN:
            case JUNGLE_SLAB:
            case JUNGLE_STAIRS:
            case JUNGLE_TRAPDOOR:
            case JUNGLE_WALL_SIGN:
            case JUNGLE_WOOD:
                return TreeSpecies.JUNGLE;
            case OAK_BOAT:
            case OAK_BUTTON:
            case OAK_DOOR:
            case OAK_FENCE:
            case OAK_FENCE_GATE:
            case OAK_LEAVES:
            case OAK_LOG:
            case OAK_PLANKS:
            case OAK_PRESSURE_PLATE:
            case OAK_SAPLING:
            case OAK_SIGN:
            case OAK_SLAB:
            case OAK_STAIRS:
            case OAK_TRAPDOOR:
            case OAK_WALL_SIGN:
            case OAK_WOOD:
                return TreeSpecies.GENERIC;
            case SPRUCE_BOAT:
            case SPRUCE_BUTTON:
            case SPRUCE_DOOR:
            case SPRUCE_FENCE:
            case SPRUCE_FENCE_GATE:
            case SPRUCE_LEAVES:
            case SPRUCE_LOG:
            case SPRUCE_PLANKS:
            case SPRUCE_PRESSURE_PLATE:
            case SPRUCE_SAPLING:
            case SPRUCE_SIGN:
            case SPRUCE_SLAB:
            case SPRUCE_STAIRS:
            case SPRUCE_TRAPDOOR:
            case SPRUCE_WALL_SIGN:
            case SPRUCE_WOOD:
                return TreeSpecies.REDWOOD;
            default:
                return null;
        }
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
        if (tool == null || tool.getType().isAir()) {
            return null;
        }

        ItemMeta meta = tool.getItemMeta();
        if (!(meta instanceof Damageable) || meta.isUnbreakable()) {
            return null;
        }

        int unbreaking = tool.getEnchantmentLevel(Enchantment.DURABILITY);
        boolean damage = unbreaking == 0 || RANDOM.nextDouble() < 1 / (unbreaking + 1);
        if (damage) {
            ((Damageable) meta).setDamage(((Damageable) meta).getDamage() + 1);
            tool.setItemMeta(meta);
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
