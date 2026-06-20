package io.github.boosterproject.booster;

import com.mojang.logging.LogUtils;
import com.simibubi.create.api.stress.BlockStressValues;
import io.github.boosterproject.booster.client.BoosterClient;
import io.github.boosterproject.booster.config.BoosterConfigs;
import io.github.boosterproject.booster.registry.BoosterBlockEntityTypes;
import io.github.boosterproject.booster.registry.BoosterBlocks;
import io.github.boosterproject.booster.registry.BoosterCreativeModeTabs;
import io.github.boosterproject.booster.registry.BoosterItems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(Booster.MOD_ID)
public class Booster {
    public static final String MOD_ID = "booster";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Booster() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BoosterConfigs.register(ModLoadingContext.get());
        BoosterBlocks.register(modEventBus);
        BoosterItems.register(modEventBus);
        BoosterCreativeModeTabs.register(modEventBus);
        BoosterBlockEntityTypes.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> BoosterClient.register(modEventBus));

        LOGGER.info("Booster loaded");
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> BlockStressValues.IMPACTS.register(
            BoosterBlocks.POWERFUL_MECHANICAL_PUMP.get(),
            () -> BoosterConfigs.SERVER.powerfulPumpStressImpact.get()
        ));
    }
}
