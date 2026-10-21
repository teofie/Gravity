package com.example.gravitymod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public final class GravityCommand {
    private GravityCommand() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess registryAccess,
                                RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("gravity")
                .then(CommandManager.argument("value", IntegerArgumentType.integer(0, 100))
                        .executes(context -> {
                            int value = IntegerArgumentType.getInteger(context, "value");
                            GravityManager.setGravityPercent(value);
                            ServerCommandSource source = context.getSource();
                            source.sendFeedback(() -> Text.translatable("commands.gravity.set", value), true);
                            return value;
                        })));
    }
}
