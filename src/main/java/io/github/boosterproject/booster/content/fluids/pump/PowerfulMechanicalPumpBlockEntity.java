package io.github.boosterproject.booster.content.fluids.pump;

import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import io.github.boosterproject.booster.registry.BoosterBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PowerfulMechanicalPumpBlockEntity extends PumpBlockEntity {
    public PowerfulMechanicalPumpBlockEntity(BlockPos pos, BlockState state) {
        this(BoosterBlockEntityTypes.POWERFUL_MECHANICAL_PUMP.get(), pos, state);
    }

    public PowerfulMechanicalPumpBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
