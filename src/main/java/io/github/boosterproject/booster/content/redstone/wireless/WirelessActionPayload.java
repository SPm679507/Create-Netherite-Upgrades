package io.github.boosterproject.booster.content.redstone.wireless;

import java.util.UUID;
import io.github.boosterproject.booster.Booster;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record WirelessActionPayload(int containerId, int action, UUID target, String name, boolean shared, int page) implements CustomPacketPayload {
    public static final int PAGE = 0, SELECT = 1, ACTIVATE = 2, SELECT_PAGE = 3, CLEAR = 4, SAVE = 5;
    public static final UUID NO_TARGET = new UUID(0, 0);
    public static final Type<WirelessActionPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Booster.MOD_ID, "wireless_action"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WirelessActionPayload> STREAM_CODEC = StreamCodec.of(
        (buf, value) -> {
            buf.writeVarInt(value.containerId); buf.writeVarInt(value.action); buf.writeUUID(value.target);
            buf.writeUtf(value.name, 128); buf.writeBoolean(value.shared); buf.writeVarInt(value.page);
        }, buf -> new WirelessActionPayload(buf.readVarInt(), buf.readVarInt(), buf.readUUID(), buf.readUtf(128), buf.readBoolean(), buf.readVarInt()));

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
