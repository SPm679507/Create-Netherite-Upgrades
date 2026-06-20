package io.github.boosterproject.booster.mixin;

import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.pump.PumpBlock;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = FluidPropagator.class, remap = false)
public abstract class FluidPropagatorMixin {
    /**
     * Target: FluidPropagator.propagateChangedPipe(...).
     * Create 6.0.8 discovers pump BEs here but filters them through
     * AllBlocks.MECHANICAL_PUMP.has(targetState). Replacing only that predicate
     * with PumpBlock.isPump(targetState) keeps vanilla pumps valid and also
     * includes Booster's PowerfulMechanicalPumpBlock because it extends PumpBlock.
     */
    @Redirect(
        method = "propagateChangedPipe",
        at = @At(
            value = "INVOKE",
            target = "Lcom/tterrag/registrate/util/entry/BlockEntry;has(Lnet/minecraft/world/level/block/state/BlockState;)Z",
            remap = false
        )
    )
    private static boolean booster$recognizePumpSubclasses(BlockEntry<?> entry, BlockState targetState) {
        return entry.has(targetState) || PumpBlock.isPump(targetState);
    }
}
