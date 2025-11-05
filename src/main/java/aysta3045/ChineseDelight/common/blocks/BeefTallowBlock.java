package aysta3045.ChineseDelight.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.Collections;
import java.util.List;

public class BeefTallowBlock extends Block {
    public BeefTallowBlock(Properties Properties) {
        super(Properties);
    }

    // 确保空手破坏时掉落自身
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Collections.singletonList(new ItemStack(this));
    }
}