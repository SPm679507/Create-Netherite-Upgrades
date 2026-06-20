package io.github.boosterproject.booster.content.fluids.pump;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class PowerfulMechanicalPumpBlockItem extends BlockItem {
    public PowerfulMechanicalPumpBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.booster.powerful_mechanical_pump"));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
