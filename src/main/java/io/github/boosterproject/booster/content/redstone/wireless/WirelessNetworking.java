package io.github.boosterproject.booster.content.redstone.wireless;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class WirelessNetworking {
    private WirelessNetworking() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(WirelessActionPayload.TYPE, WirelessActionPayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player && player.containerMenu instanceof WirelessManagerMenu menu
                && menu.containerId == payload.containerId()) menu.handle(payload);
        }));
        registrar.playToClient(WirelessPagePayload.TYPE, WirelessPagePayload.STREAM_CODEC, (payload, context) -> context.enqueueWork(() -> {
            if (context.player().containerMenu instanceof WirelessManagerMenu menu && menu.containerId == payload.containerId()) menu.receive(payload);
        }));
    }
}
