package com.veilwalker;

import com.veilwalker.item.EchoAnchorItem;
import com.veilwalker.item.PhaseBladeItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {
    public static final Item VEIL_SHARD = register("veil_shard", new Item(new Item.Settings()));

    public static final Item ECHO_ANCHOR = register("echo_anchor",
            new EchoAnchorItem(new Item.Settings().maxDamage(48).rarity(Rarity.RARE)));

    public static final Item PHASE_BLADE = register("phase_blade",
            new PhaseBladeItem(ToolMaterials.DIAMOND, new Item.Settings()
                    .rarity(Rarity.EPIC)
                    .attributeModifiers(SwordItem.createAttributeModifiers(ToolMaterials.DIAMOND, 3, -2.4f))));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Veilwalker.MOD_ID, name), item);
    }

    public static void init() {}
}
