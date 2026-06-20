package io.github.boosterproject.booster.content.fluids.tank;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

public class NetheriteFluidTankBlockItem extends BlockItem {
    public NetheriteFluidTankBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(
            "tooltip.createnetherite.netherite_fluid_tank",
            NetheriteFluidTankBlockEntity.getConfiguredCapacityMultiplier()
        ));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level level, Player player, ItemStack stack,
                                                BlockState state) {
        MinecraftServer server = level.getServer();
        if (server == null) {
            return false;
        }

        CompoundTag nbt = stack.getTagElement("BlockEntityTag");
        if (nbt != null) {
            nbt.remove("Luminosity");
            nbt.remove("Size");
            nbt.remove("Height");
            nbt.remove("Controller");
            nbt.remove("LastKnownPos");
            if (nbt.contains("TankContent")) {
                FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt.getCompound("TankContent"));
                if (!fluid.isEmpty()) {
                    fluid.setAmount(Math.min(
                        NetheriteFluidTankBlockEntity.getNetheriteCapacityMultiplierValue(),
                        fluid.getAmount()
                    ));
                    nbt.put("TankContent", fluid.writeToNBT(new CompoundTag()));
                }
            }
        }

        return super.updateCustomBlockEntityTag(pos, level, player, stack, state);
    }
}
