package com.example.gravitymod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GravityMod implements ModInitializer {
    public static final String MOD_ID = "gravitymod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(GravityCommand::register);
        PayloadTypeRegistry.playC2S().register(GravityUpdatePayload.ID, GravityUpdatePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(GravityUpdatePayload.ID, GravityUpdatePayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(GravityUpdatePayload.ID, (payload, context) ->
            context.server().execute(() -> GravityManager.applySettings(
                context.server(),
                payload.startGravity(),
                payload.automatic(),
                payload.intervalSeconds(),
                payload.increasing()
            )));
        ServerLifecycleEvents.SERVER_STARTED.register(GravityManager::restore);
        ServerTickEvents.END_SERVER_TICK.register(GravityManager::tick);
        LOGGER.info("Gravity Mod initialized");
    }
}
