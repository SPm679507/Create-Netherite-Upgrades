package io.github.boosterproject.booster.content.redstone.wireless;

import java.util.UUID;
import com.mojang.authlib.GameProfile;
import io.github.boosterproject.booster.Booster;
import io.github.boosterproject.booster.registry.BoosterBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ForcedChunksSavedData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Booster.MOD_ID)
@PrefixGameTestTemplate(false)
public final class WirelessTerminalGameTest {
    @GameTest(template = "wireless_empty", timeoutTicks = 1000)
    public static void unloadedCrossDimensionPulseAndIndependentLeases(GameTestHelper helper) {
        var server = helper.getLevel().getServer();
        ServerLevel remote = server.getLevel(Level.END);
        helper.assertTrue(remote != null, "End dimension is available");
        UUID owner = UUID.randomUUID(), other = UUID.randomUUID();
        var alice = FakePlayerFactory.get(helper.getLevel(), new GameProfile(owner, "WirelessOwner"));
        var bob = FakePlayerFactory.get(helper.getLevel(), new GameProfile(other, "WirelessGuest"));
        BlockPos a = new BlockPos(8192, 100, 8192), b = a.offset(3, 0, 0);
        // An interrupted previous run may have left the test's own vanilla loading ticket behind.
        remote.setChunkForced(a.getX() >> 4, a.getZ() >> 4, false);
        var block = BoosterBlocks.NETHERITE_WIRELESS_TERMINAL.get();
        remote.setBlock(a, Blocks.AIR.defaultBlockState(), 3);
        remote.setBlock(b, Blocks.AIR.defaultBlockState(), 3);
        remote.setBlock(a.below(), Blocks.STONE.defaultBlockState(), 3);
        remote.setBlock(a.below(2), Blocks.REDSTONE_LAMP.defaultBlockState(), 3);
        remote.setBlock(a, block.defaultBlockState(), 3);
        remote.setBlock(b, block.defaultBlockState(), 3);
        block.setPlacedBy(remote, a, block.defaultBlockState(), alice, ItemStack.EMPTY);
        block.setPlacedBy(remote, b, block.defaultBlockState(), bob, ItemStack.EMPTY);
        UUID first = ((WirelessTerminalBlockEntity) remote.getBlockEntity(a)).terminalId();
        UUID second = ((WirelessTerminalBlockEntity) remote.getBlockEntity(b)).terminalId();
        WirelessTerminalData data = WirelessTerminalData.get(server);
        helper.assertTrue(data.configure(other, second, "Public remote", true), "owner publishes remote terminal");

        helper.runAtTickTime(400, () -> {
            helper.assertTrue(!remote.hasChunkAt(a), "remote terminal chunk must actually unload before activation");
            helper.assertTrue(data.visibleTo(owner).stream().anyMatch(t -> t.id().equals(first)), "unloaded own terminal remains listed");
            helper.assertTrue(data.visibleTo(other).stream().noneMatch(t -> t.id().equals(first)), "private location is hidden");
            helper.assertTrue(!WirelessTerminalService.activate(server, other, first), "private activation is rejected");
            helper.assertTrue(!remote.hasChunkAt(a), "rejected activation must not load the target");
            helper.assertTrue(WirelessTerminalService.activate(server, owner, first), "own unloaded cross-dimension activation succeeds");
            helper.assertTrue(WirelessTerminalService.activate(server, owner, second), "public terminal activates in the same server tick");
            helper.assertTrue(data.activation(first).equals(data.activation(second)), "simultaneous terminals have matching deadlines");
            assertSignal(helper, remote, a, 15);
            assertSignal(helper, remote, b, 15);
            helper.assertTrue(remote.getBlockState(a.below(2)).getValue(RedstoneLampBlock.LIT), "terminal powers a lamp through its solid support");
            helper.assertTrue(ticketCount(remote) == 2, "both terminals own independent ticking tickets");
        });
        helper.runAtTickTime(422, () -> {
            assertSignal(helper, remote, a, 0);
            assertSignal(helper, remote, b, 0);
            helper.assertTrue(ticketCount(remote) == 2, "one-second pulse ends before loading lease");
        });
        helper.runAtTickTime(430, () -> {
            helper.assertTrue(WirelessTerminalService.activate(server, owner, first), "repeat activation renews the first terminal");
            assertSignal(helper, remote, a, 15);
            assertSignal(helper, remote, b, 0);
            helper.assertTrue(remote.getBlockState(a.below(2)).getValue(RedstoneLampBlock.LIT), "repeat activation powers the support circuit again");
        });
        helper.runAtTickTime(426, () -> {
            helper.assertTrue(!remote.hasNeighborSignal(a.below(2)), "support circuit loses redstone power after the pulse");
            reportPendingLamp(remote, a);
        });
        helper.runAtTickTime(452, () -> assertSignal(helper, remote, a, 0));
        helper.runAtTickTime(458, () -> helper.assertTrue(!remote.hasNeighborSignal(a.below(2)), "renewed pulse also removes support circuit power"));
        helper.runAtTickTime(702, () -> {
            helper.assertTrue(!remote.getBlockState(a.below(2)).getValue(RedstoneLampBlock.LIT), "vanilla lamp scheduled ticks execute while the remote chunk is held");
            helper.assertTrue(data.activation(second) == null && ticketCount(remote) == 1, "second terminal releases its ticket at fifteen seconds");
            helper.assertTrue(data.activation(first) != null && remote.hasChunkAt(a),
                "renewed terminal keeps the shared chunk loaded");
        });
        helper.runAtTickTime(732, () -> {
            helper.assertTrue(data.activation(first) == null && ticketCount(remote) == 0, "renewed lease releases its own ticket");
            helper.assertTrue(data.get(first) != null && data.get(second) != null, "unloading does not unregister terminals");
        });
        helper.runAtTickTime(820, () -> {
            helper.assertTrue(!remote.hasChunkAt(a), "target chunk can unload again after both leases finish");
            remote.setChunkForced(a.getX() >> 4, a.getZ() >> 4, true);
            helper.assertTrue(WirelessTerminalService.activate(server, owner, first), "terminal activates with another loading source present");
            helper.assertTrue(remote.getBlockState(a.below(2)).getValue(RedstoneLampBlock.LIT), "lamp lights before terminal removal");
            remote.setBlock(a, Blocks.AIR.defaultBlockState(), 3);
            helper.assertTrue(data.get(first) == null && data.get(second) != null && ticketCount(remote) == 0, "removal unregisters only the broken terminal and releases its ticket");
            helper.runAfterDelay(6, () -> {
                helper.assertTrue(!remote.hasNeighborSignal(a.below(2)), "breaking removes support circuit power");
                reportPendingLamp(remote, a);
                // Vanilla scheduled ticks wait for asynchronously loaded entities, especially in this accelerated server.
                helper.succeedWhen(() -> {
                    helper.assertTrue(!remote.getBlockState(a.below(2)).getValue(RedstoneLampBlock.LIT), "breaking a powered terminal notifies the support circuit");
                    helper.assertTrue(remote.getForcedChunks().contains(new net.minecraft.world.level.ChunkPos(a).toLong()), "removal preserves another source's forced chunk");
                    remote.setChunkForced(a.getX() >> 4, a.getZ() >> 4, false);
                });
            });
        });
    }

    private static int ticketCount(ServerLevel level) {
        var forced = level.getDataStorage().get(ForcedChunksSavedData.factory(), "chunks");
        return forced == null ? 0 : forced.getEntityForcedChunks().getTickingChunks().size();
    }

    private static void reportPendingLamp(ServerLevel level, BlockPos terminal) {
        if (!level.getBlockState(terminal.below(2)).getValue(RedstoneLampBlock.LIT)) return;
        long chunk = new net.minecraft.world.level.ChunkPos(terminal).toLong();
        System.out.println("Wireless lamp pending: entitiesLoaded=" + level.areEntitiesLoaded(chunk)
            + ", chunkTicking=" + level.getChunkSource().isPositionTicking(chunk)
            + ", lampScheduled=" + level.getBlockTicks().hasScheduledTick(terminal.below(2), Blocks.REDSTONE_LAMP)
            + ", neighborSignal=" + level.hasNeighborSignal(terminal.below(2)));
    }

    private static void assertSignal(GameTestHelper helper, ServerLevel level, BlockPos pos, int expected) {
        helper.assertTrue(level.hasChunkAt(pos), "target chunk is loaded");
        helper.assertTrue(level.getSignal(pos, Direction.NORTH) == expected, "redstone output matches expected " + expected);
    }
}
