package com.veilwalker;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup VEILWALKER = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Veilwalker.MOD_ID, "main"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModItems.ECHO_ANCHOR))
                    .displayName(Text.translatable("itemGroup.veilwalker.main"))
                    .entries((context, entries) -> {
                        entries.add(ModItems.ECHO_ANCHOR);
                        entries.add(ModItems.PHASE_BLADE);
                        entries.add(ModItems.VEIL_SHARD);
                        entries.add(ModBlocks.VEIL_ORE);
                        entries.add(ModBlocks.VEIL_BLOCK);
                        entries.add(ModBlocks.ECHO_LANTERN);
                    })
                    .build());

    public static void init() {}
}
