package com.example.gravitymod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GravityMod implements ModInitializer {
    public static final String MOD_ID = "gravitymod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(GravityCommand::register);
        LOGGER.info("Gravity Mod initialized");
    }
}
