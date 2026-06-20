package io.github.boosterproject.booster.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.fluids.tank.FluidTankRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import io.github.boosterproject.booster.content.fluids.tank.NetheriteFluidTankBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class NetheriteFluidTankRenderer extends SafeBlockEntityRenderer<NetheriteFluidTankBlockEntity> {
    private final FluidTankRenderer delegate;

    public NetheriteFluidTankRenderer(BlockEntityRendererProvider.Context context) {
        delegate = new FluidTankRenderer(context);
    }

    @Override
    protected void renderSafe(NetheriteFluidTankBlockEntity be, float partialTicks, PoseStack ms,
                              MultiBufferSource bufferSource, int light, int overlay) {
        delegate.render(be, partialTicks, ms, bufferSource, light, overlay);
    }

    @Override
    public boolean shouldRenderOffScreen(NetheriteFluidTankBlockEntity be) {
        return delegate.shouldRenderOffScreen(be);
    }
}
