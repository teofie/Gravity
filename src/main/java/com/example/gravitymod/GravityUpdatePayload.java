package com.example.gravitymod;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record GravityUpdatePayload(int startGravity,
                                   boolean automatic,
                                   int intervalSeconds,
                                   boolean increasing) implements CustomPayload {
    public static final CustomPayload.Id<GravityUpdatePayload> ID =
            new CustomPayload.Id<>(Identifier.of(GravityMod.MOD_ID, "update"));
    public static final PacketCodec<RegistryByteBuf, GravityUpdatePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, GravityUpdatePayload::startGravity,
            PacketCodecs.BOOLEAN, GravityUpdatePayload::automatic,
            PacketCodecs.VAR_INT, GravityUpdatePayload::intervalSeconds,
            PacketCodecs.BOOLEAN, GravityUpdatePayload::increasing,
            GravityUpdatePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
