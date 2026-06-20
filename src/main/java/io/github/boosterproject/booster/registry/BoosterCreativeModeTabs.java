package io.github.boosterproject.booster.registry;

import io.github.boosterproject.booster.Booster;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class BoosterCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Booster.MOD_ID);

    public static final RegistryObject<CreativeModeTab> BOOSTER =
        CREATIVE_MODE_TABS.register("booster", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.booster.booster"))
            .icon(() -> new ItemStack(BoosterItems.POWERFUL_MECHANICAL_PUMP.get()))
            .displayItems((parameters, output) -> {
                output.accept(BoosterItems.POWERFUL_MECHANICAL_PUMP.get());
                output.accept(BoosterItems.NETHERITE_SHEET.get());
            })
            .build());

    private BoosterCreativeModeTabs() {
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
