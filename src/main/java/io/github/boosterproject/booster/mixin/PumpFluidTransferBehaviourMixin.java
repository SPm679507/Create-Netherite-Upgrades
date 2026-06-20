package io.github.boosterproject.booster.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import io.github.boosterproject.booster.content.fluids.pump.BoosterPumpPressure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.simibubi.create.content.fluids.pump.PumpBlockEntity$PumpFluidTransferBehaviour", remap = false)
public abstract class PumpFluidTransferBehaviourMixin {
    /**
     * Target: PumpBlockEntity.PumpFluidTransferBehaviour.tick().
     * This is the pump's own PipeConnection pressure write. The following
     * pressure.set(!pull, 0f) call is untouched, so Create's direction semantics
     * remain unchanged. BoosterPumpPressure only scales Booster's pump BE.
     */
    @ModifyExpressionValue(
        method = "tick",
        at = @At(value = "INVOKE", target = "Ljava/lang/Math;abs(F)F")
    )
    private float booster$scalePumpInterfacePressure(float originalPressure) {
        FluidTransportBehaviour behaviour = (FluidTransportBehaviour) (Object) this;
        return BoosterPumpPressure.scalePressure(
            behaviour.blockEntity,
            originalPressure,
            BoosterPumpPressure.PressureWriteTarget.PUMP_INTERFACE
        );
    }
}
