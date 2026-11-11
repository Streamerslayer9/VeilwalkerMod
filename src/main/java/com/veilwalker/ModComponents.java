package com.veilwalker;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.GlobalPos;

public class ModComponents {
    /** The saved anchor location (dimension + block position) stored on an Echo Anchor stack. */
    public static final ComponentType<GlobalPos> ANCHOR = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(Veilwalker.MOD_ID, "anchor"),
            ComponentType.<GlobalPos>builder().codec(GlobalPos.CODEC).build()
    );

    public static void init() {}
}
