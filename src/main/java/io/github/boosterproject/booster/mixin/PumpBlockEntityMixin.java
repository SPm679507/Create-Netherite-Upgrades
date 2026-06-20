package io.github.boosterproject.booster.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import io.github.boosterproject.booster.content.fluids.pump.BoosterPumpPressure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = PumpBlockEntity.class, remap = false)
public abstract class PumpBlockEntityMixin {
    /**
     * Target: PumpBlockEntity.distributePressureTo(Direction).
     * This is the single remote-network pressure source, immediately before
     * FluidTransportBehaviour.addPressure receives pressure / parallelBranches.
     * BoosterPumpPressure only scales PowerfulMechanicalPumpBlockEntity instances,
     * so Create's regular mechanical pump keeps the original Math.abs(getSpeed()).
     */
    @ModifyExpressionValue(
        method = "distributePressureTo",
        at = @At(value = "INVOKE", target = "Ljava/lang/Math;abs(F)F")
    )
    private float booster$scaleRemoteNetworkPressure(float originalPressure) {
        return BoosterPumpPressure.scalePressure(
            (PumpBlockEntity) (Object) this,
            originalPressure,
            BoosterPumpPressure.PressureWriteTarget.REMOTE_NETWORK
        );
    }
}
