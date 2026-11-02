package io.github.boosterproject.booster.content.redstone.wireless;

import java.util.UUID;
import io.github.boosterproject.booster.Booster;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class WirelessTerminalService {
    public static final TicketController TICKETS = new TicketController(
        ResourceLocation.fromNamespaceAndPath(Booster.MOD_ID, "wireless_terminal"), (level, helper) -> {
            // Never reinstate stale tickets before all server dimensions exist.
            helper.getEntityTickets().keySet().forEach(helper::removeAllTickets);
            helper.getBlockTickets().keySet().forEach(helper::removeAllTickets);
        });

    private WirelessTerminalService() {}

    public static boolean activate(MinecraftServer server, UUID player, UUID id) {
        WirelessTerminalData data = WirelessTerminalData.get(server);
        WirelessTerminalData.Terminal terminal = data.get(id);
        if (terminal == null || !terminal.visibleTo(player)) return false;
        ServerLevel level = server.getLevel(terminal.dimension());
        if (level == null) return false;
        force(level, terminal, true);
        level.getChunk(terminal.pos().getX() >> 4, terminal.pos().getZ() >> 4);
        if (!(level.getBlockEntity(terminal.pos()) instanceof WirelessTerminalBlockEntity blockEntity)
            || !id.equals(blockEntity.terminalId())) {
            force(level, terminal, false);
            data.remove(id);
            return false;
        }
        if (!data.activate(player, id, server.overworld().getGameTime())) return false;
        blockEntity.updatePower(true);
        return true;
    }

    public static void remove(ServerLevel level, BlockPos pos, UUID id) {
        WirelessTerminalData data = WirelessTerminalData.get(level.getServer());
        WirelessTerminalData.Terminal terminal = data.get(id);
        if (terminal != null && terminal.matches(level.dimension(), pos)) {
            force(level, terminal, false);
            data.remove(id);
        }
    }

    public static void tick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        WirelessTerminalData data = WirelessTerminalData.get(server);
        long now = server.overworld().getGameTime();
        boolean restore = !data.ticketsRestored();
        data.markTicketsRestored();
        for (var entry : data.activations().entrySet()) {
            WirelessTerminalData.Terminal terminal = data.get(entry.getKey());
            if (terminal == null) { data.clearActivation(entry.getKey()); continue; }
            ServerLevel level = server.getLevel(terminal.dimension());
            if (level == null) { data.clearActivation(terminal.id()); continue; }
            if (restore && entry.getValue().loading(now)) {
                force(level, terminal, true);
                level.getChunk(terminal.pos().getX() >> 4, terminal.pos().getZ() >> 4);
            }
            if (level.hasChunkAt(terminal.pos()) && level.getBlockEntity(terminal.pos()) instanceof WirelessTerminalBlockEntity blockEntity
                && terminal.id().equals(blockEntity.terminalId())) {
                blockEntity.updatePower(entry.getValue().powered(now));
            }
            if (!entry.getValue().loading(now)) {
                force(level, terminal, false);
                data.clearActivation(terminal.id());
            }
        }
    }

    private static void force(ServerLevel level, WirelessTerminalData.Terminal terminal, boolean add) {
        TICKETS.forceChunk(level, terminal.id(), terminal.pos().getX() >> 4, terminal.pos().getZ() >> 4, add, true);
    }
}
