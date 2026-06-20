package io.github.boosterproject.booster.client;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleAxisRotatingVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;
import io.github.boosterproject.booster.content.fluids.pump.PowerfulMechanicalPumpBlockEntity;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.Direction;

public class PowerfulMechanicalPumpVisual extends SingleAxisRotatingVisual<PowerfulMechanicalPumpBlockEntity> {
    private static final Color GEAR_COLOR = new Color(0x202020);

    public PowerfulMechanicalPumpVisual(VisualizationContext context, PowerfulMechanicalPumpBlockEntity blockEntity,
                                        float partialTick) {
        super(context, blockEntity, partialTick, Direction.SOUTH, Models.partial(AllPartialModels.MECHANICAL_PUMP_COG));
        applyGearColor();
    }

    @Override
    public void update(float pt) {
        super.update(pt);
        applyGearColor();
    }

    @Override
    public void tick(Context context) {
        super.tick(context);
        applyGearColor();
    }

    private void applyGearColor() {
        rotatingModel.setColor(GEAR_COLOR)
            .setChanged();
    }
}
