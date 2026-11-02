package io.github.boosterproject.booster.content.redstone.wireless;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import io.github.boosterproject.booster.Booster;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record WirelessPagePayload(int containerId, int page, int pages, int selectedCount, boolean terminalOnly, List<Entry> entries) implements CustomPacketPayload {
    public static final int PAGE_SIZE = 5;
    public static final Type<WirelessPagePayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Booster.MOD_ID, "wireless_page"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WirelessPagePayload> STREAM_CODEC = StreamCodec.of(WirelessPagePayload::write, WirelessPagePayload::read);

    public record Entry(UUID id, String ownerName, String name, String dimension, BlockPos pos, boolean shared, boolean owned, boolean selected) {
        public Component displayName() {
            Component suffix = name.isBlank() ? Component.translatable("wireless.createnetherite.default_name") : Component.literal(name);
            return Component.translatable("wireless.createnetherite.owned_name", ownerName, suffix);
        }
    }

    private static void write(RegistryFriendlyByteBuf buf, WirelessPagePayload value) {
        buf.writeVarInt(value.containerId); buf.writeVarInt(value.page); buf.writeVarInt(value.pages);
        buf.writeVarInt(value.selectedCount); buf.writeBoolean(value.terminalOnly); buf.writeVarInt(value.entries.size());
        for (Entry entry : value.entries) {
            buf.writeUUID(entry.id); buf.writeUtf(entry.ownerName, 64); buf.writeUtf(entry.name, 128);
            buf.writeUtf(entry.dimension); buf.writeBlockPos(entry.pos);
            buf.writeBoolean(entry.shared); buf.writeBoolean(entry.owned); buf.writeBoolean(entry.selected);
        }
    }

    private static WirelessPagePayload read(RegistryFriendlyByteBuf buf) {
        int container = buf.readVarInt(), page = buf.readVarInt(), pages = buf.readVarInt(), selected = buf.readVarInt();
        boolean terminalOnly = buf.readBoolean();
        int count = buf.readVarInt();
        if (count < 0 || count > PAGE_SIZE) throw new IllegalArgumentException("Invalid wireless page size");
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entries.add(new Entry(buf.readUUID(), buf.readUtf(64), buf.readUtf(128), buf.readUtf(), buf.readBlockPos(),
                buf.readBoolean(), buf.readBoolean(), buf.readBoolean()));
        }
        return new WirelessPagePayload(container, page, pages, selected, terminalOnly, List.copyOf(entries));
    }

    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
