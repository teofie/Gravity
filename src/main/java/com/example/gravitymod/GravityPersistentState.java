package com.example.gravitymod;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

public final class GravityPersistentState extends PersistentState {
    private static final Codec<GravityPersistentState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("gravityPercent").forGetter(state -> state.gravityPercent),
        Codec.BOOL.fieldOf("automatic").forGetter(state -> state.automatic),
        Codec.INT.fieldOf("intervalSeconds").forGetter(state -> state.intervalSeconds),
        Codec.BOOL.fieldOf("increasing").forGetter(state -> state.increasing)
    ).apply(instance, GravityPersistentState::new));

    public static final PersistentStateType<GravityPersistentState> TYPE = new PersistentStateType<>(
        "gravitymod_settings",
            GravityPersistentState::new,
        CODEC,
            null
    );

    private int gravityPercent = 50;
    private boolean automatic;
    private int intervalSeconds = 60;
    private boolean increasing;

    private GravityPersistentState() {
    }

    private GravityPersistentState(int gravityPercent,
                                   boolean automatic,
                                   int intervalSeconds,
                                   boolean increasing) {
        setValues(gravityPercent, automatic, intervalSeconds, increasing);
    }

    public int getGravityPercent() {
        return gravityPercent;
    }

    public boolean isAutomatic() {
        return automatic;
    }

    public int getIntervalSeconds() {
        return intervalSeconds;
    }

    public boolean isIncreasing() {
        return increasing;
    }

    public void setValues(int gravityPercent, boolean automatic, int intervalSeconds, boolean increasing) {
        this.gravityPercent = Math.max(0, Math.min(100, gravityPercent));
        this.automatic = automatic;
        this.intervalSeconds = Math.max(1, intervalSeconds);
        this.increasing = increasing;
        markDirty();
    }

}
