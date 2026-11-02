package io.github.boosterproject.booster.content.redstone.wireless;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.LoadingModList;

public final class WirelessTerminalDataTest {
    private static int checks;

    public static void main(String[] args) {
        SharedConstants.tryDetectVersion();
        LoadingModList.of(List.of(), List.of(), List.of(), List.of(), Map.of());
        Bootstrap.bootStrap();
        WirelessTerminalData data = new WirelessTerminalData();
        UUID alice = UUID.randomUUID(), bob = UUID.randomUUID();
        UUID a = UUID.randomUUID(), b = UUID.randomUUID();
        check(data.visibleTo(alice).isEmpty(), "new world has no terminals");
        data.register(new WirelessTerminalData.Terminal(a, alice, "Alice", "矿场", Level.NETHER, new BlockPos(-32000, 80, 48000), false));
        check(data.get(a) != null, "placed terminal is registered even without a loaded chunk");
        check(data.visibleTo(alice).size() == 1, "owner sees private terminal across dimensions");
        check(data.visibleTo(bob).isEmpty(), "other players cannot see private position or name");
        check(!data.activate(bob, a, 100), "private activation is rejected");
        check(!data.configure(bob, a, "stolen", true), "non-owner cannot rename or publish");
        check(data.configure(alice, a, "矿场", true), "owner can publish");
        check(data.visibleTo(bob).size() == 1, "public terminal becomes visible");
        check(data.activate(bob, a, 100), "public terminal can be activated by another player");
        check(data.activation(a).signalUntil() == 120, "signal lasts one second");
        check(data.activation(a).loadUntil() == 400, "chunk lease lasts fifteen seconds");
        check(data.activation(a).powered(119) && !data.activation(a).powered(120), "signal stops exactly at deadline");
        check(data.activation(a).loading(399) && !data.activation(a).loading(400), "loading stops exactly at deadline");
        check(data.activate(alice, a, 115), "repeated activation succeeds");
        check(data.activation(a).signalUntil() == 135 && data.activation(a).loadUntil() == 415, "repeated activation renews both deadlines");
        check(data.configure(alice, a, "  新名字\n\t  ", false), "owner can revoke sharing and rename");
        check(data.get(a).name().equals("新名字"), "names discard control characters and surrounding whitespace");
        check(!data.activate(bob, a, 130), "stale public selection cannot bypass revocation");
        check(data.activation(a).loadUntil() == 415, "rejected request does not renew lease");
        data.register(new WirelessTerminalData.Terminal(b, alice, "Alice", "新名字", Level.OVERWORLD, new BlockPos(32000, 90, -48000), false));
        check(data.visibleTo(alice).size() == 2, "duplicate display names retain distinct identities");
        check(data.activate(alice, b, 200), "another terminal has its own timer");
        check(data.activation(a).loadUntil() == 415, "second timer does not alter first");
        CompoundTag saved = data.save(new CompoundTag(), null);
        WirelessTerminalData restored = WirelessTerminalData.load(saved, null);
        check(restored.get(a).equals(data.get(a)) && restored.get(b).equals(data.get(b)), "identity ownership dimension and names survive reload");
        check(restored.activation(a).equals(data.activation(a)) && restored.activation(b).equals(data.activation(b)), "signal and load deadlines survive reload");
        check(restored.visibleTo(bob).isEmpty(), "private visibility survives reload");
        restored.configure(alice, a, "x".repeat(100), false);
        check(restored.get(a).name().length() == WirelessTerminalData.MAX_NAME_LENGTH, "user names have a bounded stored length");
        restored.remove(a);
        check(restored.get(a) == null && restored.activation(a) == null, "breaking unregisters terminal and its activation");
        check(!restored.activate(alice, a, 300), "removed terminal cannot be activated");
        check(restored.get(b) != null && restored.activation(b) != null, "removal preserves unrelated terminal");
        check(!restored.configure(alice, UUID.randomUUID(), "missing", true), "missing settings target is rejected");
        for (int i = 0; i < 15; i++) {
            restored.register(new WirelessTerminalData.Terminal(UUID.randomUUID(), alice, "Alice", "Site " + i, Level.END, new BlockPos(i, 80, i), i % 2 == 0));
        }
        check(restored.visibleTo(alice).size() == 16, "all owner terminals remain available beyond one page");
        check(restored.visibleTo(bob).size() == 8, "only public records are exposed in larger lists");
        UUID impostor = UUID.randomUUID();
        restored.register(new WirelessTerminalData.Terminal(UUID.randomUUID(), impostor, "Alice", "Same player name", Level.END, BlockPos.ZERO, false));
        check(!restored.configure(impostor, b, "stolen", true), "same player name cannot impersonate the owner UUID");
        System.out.println("Wireless terminal regression checks passed (" + checks + " cases).");
    }

    private static void check(boolean condition, String description) {
        if (!condition) throw new AssertionError(description);
        checks++;
    }
}
