package com.example.gravitymod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;

public final class GravityManager {
    private static volatile int gravityPercent = 50;
    private static volatile boolean automatic;
    private static volatile int automaticIntervalSeconds = 60;
    private static volatile boolean automaticIncreasing;
    private static int automaticTicks;

    private GravityManager() {
    }

    public static int getGravityPercent() {
        return gravityPercent;
    }

    public static void setGravityPercent(int percent) {
        gravityPercent = Math.max(0, Math.min(100, percent));
    }

    public static synchronized void applySettings(int startGravity,
                                                   boolean automaticMode,
                                                   int intervalSeconds,
                                                   boolean increasing) {
        applySettings(null, startGravity, automaticMode, intervalSeconds, increasing);
    }

    public static synchronized void applySettings(MinecraftServer server,
                                                   int startGravity,
                                                   boolean automaticMode,
                                                   int intervalSeconds,
                                                   boolean increasing) {
        gravityPercent = Math.max(0, Math.min(100, startGravity));
        automatic = automaticMode;
        automaticIntervalSeconds = Math.max(1, intervalSeconds);
        automaticIncreasing = increasing;
        automaticTicks = 0;
        save(server);
    }

    public static synchronized void restore(MinecraftServer server) {
        GravityPersistentState state = getState(server);
        gravityPercent = state.getGravityPercent();
        automatic = state.isAutomatic();
        automaticIntervalSeconds = state.getIntervalSeconds();
        automaticIncreasing = state.isIncreasing();
        automaticTicks = 0;
    }

    public static boolean isAutomatic() {
        return automatic;
    }

    public static int getAutomaticIntervalSeconds() {
        return automaticIntervalSeconds;
    }

    public static boolean isAutomaticIncreasing() {
        return automaticIncreasing;
    }

    public static void tick(MinecraftServer server) {
        if (!automatic) {
            return;
        }

        automaticTicks++;
        if (automaticTicks < automaticIntervalSeconds * 20) {
            return;
        }

        automaticTicks = 0;
        setGravityPercent(gravityPercent + (automaticIncreasing ? 1 : -1));
        if (gravityPercent == 0 || gravityPercent == 100) {
            automatic = false;
        }
        save(server);
    }

    public static boolean affects(Entity entity) {
        return entity instanceof LivingEntity || entity instanceof ItemEntity;
    }

    public static double scale(Entity entity, double vanillaGravity) {
        if (!affects(entity)) {
            return vanillaGravity;
        }
        if (gravityPercent == 0) {
            return 0.0D;
        }
        return vanillaGravity * Math.max(getMultiplier(), 0.04D);
    }

    public static double scaleFallDistance(double fallDistance) {
        return fallDistance * getMultiplier();
    }

    public static boolean isNearZero() {
        return gravityPercent <= 1;
    }

    public static boolean isZero() {
        return gravityPercent == 0;
    }

    private static double getMultiplier() {
        return gravityPercent / 50.0;
    }

    private static GravityPersistentState getState(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager().getOrCreate(
            GravityPersistentState.TYPE
        );
    }

    private static void save(MinecraftServer server) {
        if (server != null) {
            getState(server).setValues(
                    gravityPercent,
                    automatic,
                    automaticIntervalSeconds,
                    automaticIncreasing
            );
        }
    }
}
