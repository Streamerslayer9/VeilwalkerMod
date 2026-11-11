package com.veilwalker;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class ModBlocks {
    public static final Block VEIL_ORE = register("veil_ore",
            new ExperienceDroppingBlock(UniformIntProvider.create(2, 5),
                    AbstractBlock.Settings.create()
                            .mapColor(MapColor.DEEPSLATE_GRAY)
                            .strength(4.5f, 3.0f)
                            .requiresTool()
                            .sounds(BlockSoundGroup.DEEPSLATE)));

    public static final Block VEIL_BLOCK = register("veil_block",
            new Block(AbstractBlock.Settings.create()
                    .mapColor(MapColor.PURPLE)
                    .strength(4.0f, 6.0f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.AMETHYST_BLOCK)));

    public static final Block ECHO_LANTERN = register("echo_lantern",
            new Block(AbstractBlock.Settings.create()
                    .mapColor(MapColor.CYAN)
                    .strength(1.0f)
                    .luminance(state -> 14)
                    .sounds(BlockSoundGroup.GLASS)));

    private static Block register(String name, Block block) {
        Identifier id = Identifier.of(Veilwalker.MOD_ID, name);
        Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void init() {}
}
