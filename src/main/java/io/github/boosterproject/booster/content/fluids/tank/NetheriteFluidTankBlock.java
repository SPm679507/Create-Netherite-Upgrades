package io.github.boosterproject.booster.content.fluids.tank;

import com.simibubi.create.content.fluids.tank.FluidTankBlock;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import io.github.boosterproject.booster.registry.BoosterBlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class NetheriteFluidTankBlock extends FluidTankBlock {
    public NetheriteFluidTankBlock(Properties properties) {
        super(properties, false);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Class<FluidTankBlockEntity> getBlockEntityClass() {
        return (Class) NetheriteFluidTankBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FluidTankBlockEntity> getBlockEntityType() {
        return BoosterBlockEntityTypes.NETHERITE_FLUID_TANK.get();
    }
}
