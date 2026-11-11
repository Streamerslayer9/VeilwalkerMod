package com.veilwalker;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Veilwalker implements ModInitializer {
    public static final String MOD_ID = "veilwalker";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModComponents.init();
        ModBlocks.init();
        ModItems.init();
        ModItemGroups.init();
        ModWorldGen.init();
        LOGGER.info("Veilwalker loaded - the veil is thin.");
    }
}
