package io.github.boosterproject.booster.registry;

import io.github.boosterproject.booster.Booster;
import io.github.boosterproject.booster.content.fluids.pump.PowerfulMechanicalPumpBlock;
import io.github.boosterproject.booster.content.fluids.tank.NetheriteFluidTankBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BoosterBlocks {
    public static final DeferredRegister<net.minecraft.world.level.block.Block> BLOCKS =
        DeferredRegister.create(ForgeRegistries.BLOCKS, Booster.MOD_ID);

    public static final RegistryObject<PowerfulMechanicalPumpBlock> POWERFUL_MECHANICAL_PUMP =
        BLOCKS.register("powerful_mechanical_pump", () -> new PowerfulMechanicalPumpBlock(
            BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
                .mapColor(MapColor.STONE)
        ));

    public static final RegistryObject<NetheriteFluidTankBlock> NETHERITE_FLUID_TANK =
        BLOCKS.register("netherite_fluid_tank", () -> new NetheriteFluidTankBlock(
            BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK)
                .noOcclusion()
                .isRedstoneConductor((state, getter, pos) -> true)
        ));

    private BoosterBlocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
