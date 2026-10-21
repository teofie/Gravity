package com.example.gravitymod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;

public final class GravityManager {
    private static volatile int gravityPercent = 50;

    private GravityManager() {
    }

    public static int getGravityPercent() {
        return gravityPercent;
    }

    public static void setGravityPercent(int percent) {
        gravityPercent = Math.max(0, Math.min(100, percent));
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
}
