package io.github.boosterproject.booster.client;

import com.simibubi.create.CreateClient;
import com.simibubi.create.content.fluids.PipeAttachmentModel;
import com.simibubi.create.content.fluids.tank.FluidTankModel;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import io.github.boosterproject.booster.Booster;
import io.github.boosterproject.booster.registry.BoosterBlockEntityTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class BoosterClient {
    private BoosterClient() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(BoosterClient::registerRenderers);
        modEventBus.addListener(BoosterClient::clientSetup);
        CreateClient.MODEL_SWAPPER.getCustomBlockModels()
            .register(new ResourceLocation(Booster.MOD_ID, "powerful_mechanical_pump"), PipeAttachmentModel::withAO);
        CreateClient.MODEL_SWAPPER.getCustomBlockModels()
            .register(new ResourceLocation(Booster.MOD_ID, "netherite_fluid_tank"), FluidTankModel::standard);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
            BoosterBlockEntityTypes.POWERFUL_MECHANICAL_PUMP.get(),
            PowerfulMechanicalPumpRenderer::new
        );
        event.registerBlockEntityRenderer(
            BoosterBlockEntityTypes.NETHERITE_FLUID_TANK.get(),
            NetheriteFluidTankRenderer::new
        );
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> SimpleBlockEntityVisualizer
            .builder(BoosterBlockEntityTypes.POWERFUL_MECHANICAL_PUMP.get())
            .factory(PowerfulMechanicalPumpVisual::new)
            .apply());
    }
}
