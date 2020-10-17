package de.themoep.enhancedvanilla.mechanics.treecutter;

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

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

public class TreeCutterBlockBreakEvent extends BlockBreakEvent {
    private final List<Block> tree;

    public TreeCutterBlockBreakEvent(Block block, Player player, List<Block> tree) {
        super(block, player);
        this.tree = tree;
    }

    public List<Block> getTree() {
        return tree;
    }
}
