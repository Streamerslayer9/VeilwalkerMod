package com.veilwalker;

import net.minecraft.block.BlockState;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/** Veil Ore: drops XP like a normal ore and shimmers with veil dust. */
public class VeilOreBlock extends ExperienceDroppingBlock {
    public VeilOreBlock(IntProvider experience, Settings settings) {
        super(experience, settings);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        VeilFx.ambient(world, pos, random, VeilFx.VEIL_DUST_SMALL, 8, false);
    }
}
