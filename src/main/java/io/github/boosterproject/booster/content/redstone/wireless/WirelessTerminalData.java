package io.github.boosterproject.booster.content.redstone.wireless;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public final class WirelessTerminalData extends SavedData {
    public static final int MAX_NAME_LENGTH = 48;
    public static final int SIGNAL_TICKS = 20;
    public static final int LOAD_TICKS = 300;
    private final Map<UUID, Terminal> terminals = new HashMap<>();
    private final Map<UUID, Activation> activations = new HashMap<>();
    private long revision;
    private boolean ticketsRestored;

    public record Terminal(UUID id, UUID owner, String ownerName, String name, ResourceKey<Level> dimension, BlockPos pos, boolean shared) {
        public boolean visibleTo(UUID player) { return shared || owner.equals(player); }
        public Component displayName() {
            Component suffix = name.isBlank() ? Component.translatable("wireless.createnetherite.default_name") : Component.literal(name);
            return Component.translatable("wireless.createnetherite.owned_name", ownerName, suffix);
        }
        public boolean matches(ResourceKey<Level> level, BlockPos position) {
            return dimension.equals(level) && pos.equals(position);
        }
    }
    public record Activation(long signalUntil, long loadUntil) {
        public boolean powered(long now) { return now < signalUntil; }
        public boolean loading(long now) { return now < loadUntil; }
    }
    public static WirelessTerminalData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
            new Factory<>(WirelessTerminalData::new, WirelessTerminalData::load), "createnetherite_wireless_terminals");
    }

    public List<Terminal> visibleTo(UUID player) {
        return terminals.values().stream().filter(t -> t.visibleTo(player))
            .sorted(Comparator.comparing(Terminal::ownerName).thenComparing(Terminal::name).thenComparing(Terminal::id)).toList();
    }

    public void register(Terminal terminal) {
        if (!terminal.equals(terminals.put(terminal.id(), terminal))) changed();
    }

    public Terminal get(UUID id) { return terminals.get(id); }
    public long revision() { return revision; }
    public Map<UUID, Activation> activations() { return Map.copyOf(activations); }
    public boolean ticketsRestored() { return ticketsRestored; }
    public void markTicketsRestored() { ticketsRestored = true; }

    public boolean activate(UUID player, UUID id, long now) {
        Terminal terminal = get(id);
        if (terminal == null || !terminal.visibleTo(player)) return false;
        activations.put(id, new Activation(now + SIGNAL_TICKS, now + LOAD_TICKS));
        changed();
        return true;
    }

    public boolean configure(UUID player, UUID id, String name, boolean shared) {
        Terminal terminal = get(id);
        if (terminal == null || !terminal.owner().equals(player)) return false;
        String cleaned = name.codePoints().filter(c -> !Character.isISOControl(c) && c != 167)
            .limit(MAX_NAME_LENGTH).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString().strip();
        register(new Terminal(id, player, terminal.ownerName(), cleaned, terminal.dimension(), terminal.pos(), shared));
        return true;
    }

    public Activation activation(UUID id) { return activations.get(id); }

    public void clearActivation(UUID id) {
        if (activations.remove(id) != null) changed();
    }

    public void remove(UUID id) {
        clearActivation(id);
        if (terminals.remove(id) != null) changed();
    }

    private void changed() {
        revision++;
        setDirty();
    }

    public static WirelessTerminalData load(CompoundTag tag, HolderLookup.Provider registries) {
        WirelessTerminalData data = new WirelessTerminalData();
        for (Tag element : tag.getList("Terminals", Tag.TAG_COMPOUND)) {
            CompoundTag entry = (CompoundTag) element;
            if (!entry.hasUUID("Id") || !entry.hasUUID("Owner")) continue;
            ResourceLocation dimension = ResourceLocation.tryParse(entry.getString("Dimension"));
            if (dimension == null) continue;
            UUID id = entry.getUUID("Id");
            data.terminals.put(id, new Terminal(id, entry.getUUID("Owner"), entry.getString("OwnerName"),
                entry.getString("Name"), ResourceKey.create(Registries.DIMENSION, dimension),
                BlockPos.of(entry.getLong("Pos")), entry.getBoolean("Shared")));
            if (entry.contains("LoadUntil", Tag.TAG_LONG)) {
                data.activations.put(id, new Activation(entry.getLong("SignalUntil"), entry.getLong("LoadUntil")));
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag entries = new ListTag();
        for (Terminal terminal : terminals.values()) {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("Id", terminal.id());
            entry.putUUID("Owner", terminal.owner());
            entry.putString("OwnerName", terminal.ownerName());
            entry.putString("Name", terminal.name());
            entry.putString("Dimension", terminal.dimension().location().toString());
            entry.putLong("Pos", terminal.pos().asLong());
            entry.putBoolean("Shared", terminal.shared());
            Activation active = activation(terminal.id());
            if (active != null) {
                entry.putLong("SignalUntil", active.signalUntil());
                entry.putLong("LoadUntil", active.loadUntil());
            }
            entries.add(entry);
        }
        tag.put("Terminals", entries);
        return tag;
    }
}
