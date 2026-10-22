package com.example.gravitymod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

public final class GravityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(GravityUpdatePayload.ID, (payload, context) ->
            context.client().execute(() -> context.client().setScreen(new GravityScreen(
                payload.startGravity(),
                payload.automatic(),
                payload.intervalSeconds(),
                payload.increasing()
            ))));
    }

    public static void sendSettings(int startGravity,
                                    boolean automatic,
                                    int intervalSeconds,
                                    boolean increasing) {
        if (MinecraftClient.getInstance().getNetworkHandler() != null) {
            ClientPlayNetworking.send(new GravityUpdatePayload(
                    startGravity,
                    automatic,
                    intervalSeconds,
                    increasing
            ));
        }
    }
}
