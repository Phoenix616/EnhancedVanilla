package de.themoep.enhancedvanilla.mechanics.treecutter;

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
