package com.veilwalker;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/** A plain block that emits particles from its exposed faces. */
public class AmbientBlock extends Block {
    private final ParticleEffect particle;
    private final int chance;
    private final boolean rising;

    public AmbientBlock(Settings settings, ParticleEffect particle, int chance, boolean rising) {
        super(settings);
        this.particle = particle;
        this.chance = chance;
        this.rising = rising;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        VeilFx.ambient(world, pos, random, particle, chance, rising);
    }
}
