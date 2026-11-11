package com.veilwalker;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

public class ModWorldGen {
    public static final RegistryKey<PlacedFeature> VEIL_ORE_PLACED =
            RegistryKey.of(RegistryKeys.PLACED_FEATURE, Identifier.of(Veilwalker.MOD_ID, "veil_ore"));

    public static void init() {
        BiomeModifications.addFeature(
                BiomeSelectors.foundInOverworld(),
                GenerationStep.Feature.UNDERGROUND_ORES,
                VEIL_ORE_PLACED);
    }
}
