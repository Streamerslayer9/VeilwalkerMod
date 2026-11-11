package com.veilwalker;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class ModBlocks {
    public static final Block VEIL_ORE = register("veil_ore",
            new VeilOreBlock(UniformIntProvider.create(2, 5),
                    AbstractBlock.Settings.create()
                            .mapColor(MapColor.DEEPSLATE_GRAY)
                            .strength(4.5f, 3.0f)
                            .requiresTool()
                            .luminance(state -> 3)
                            .sounds(BlockSoundGroup.DEEPSLATE)));

    public static final Block VEIL_BLOCK = register("veil_block",
            new AmbientBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.PURPLE)
                    .strength(4.0f, 6.0f)
                    .requiresTool()
                    .luminance(state -> 6)
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK),
                    VeilFx.VEIL_DUST_SMALL, 10, false));

    public static final Block ECHO_LANTERN = register("echo_lantern",
            new AmbientBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.CYAN)
                    .strength(1.0f)
                    .luminance(state -> 14)
                    .sounds(BlockSoundGroup.GLASS),
                    ParticleTypes.END_ROD, 6, true));

    private static Block register(String name, Block block) {
        Identifier id = Identifier.of(Veilwalker.MOD_ID, name);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void init() {}
}
