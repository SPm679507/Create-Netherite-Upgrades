package io.github.boosterproject.booster.registry;

import io.github.boosterproject.booster.Booster;
import io.github.boosterproject.booster.content.fluids.pump.PowerfulMechanicalPumpBlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BoosterItems {
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, Booster.MOD_ID);

    public static final RegistryObject<Item> POWERFUL_MECHANICAL_PUMP =
        ITEMS.register("powerful_mechanical_pump", () -> new PowerfulMechanicalPumpBlockItem(
            BoosterBlocks.POWERFUL_MECHANICAL_PUMP.get(),
            new Item.Properties()
        ));

    private BoosterItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
