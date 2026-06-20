package io.github.boosterproject.booster.content.fluids.pump;

import com.simibubi.create.content.fluids.pump.PumpBlock;
import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import io.github.boosterproject.booster.registry.BoosterBlockEntityTypes;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class PowerfulMechanicalPumpBlock extends PumpBlock {
    public PowerfulMechanicalPumpBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<? extends PumpBlockEntity> getBlockEntityType() {
        return BoosterBlockEntityTypes.POWERFUL_MECHANICAL_PUMP.get();
    }
}
