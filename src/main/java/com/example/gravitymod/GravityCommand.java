package com.example.gravitymod;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class GravityCommand {
    private GravityCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("gravity")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    if (source.getPlayer() == null) {
                        return 0;
                    }
                    ServerPlayNetworking.send(source.getPlayer(), new GravityUpdatePayload(
                            GravityManager.getGravityPercent(),
                            GravityManager.isAutomatic(),
                            GravityManager.getAutomaticIntervalSeconds(),
                            GravityManager.isAutomaticIncreasing()
                    ));
                    return 1;
                })
                .then(CommandManager.literal("check")
                        .executes(context -> {
                            ServerCommandSource source = context.getSource();
                            int value = GravityManager.getGravityPercent();
                            source.sendFeedback(() -> Text.translatable("commands.gravity.check", value), false);
                            return value;
                        })));
    }
}
