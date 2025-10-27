package aysta3045.ChineseDelight.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.block.SoundType;

import java.util.Collections;
import java.util.List;

public class SesameBaleBlock extends Block {
    public SesameBaleBlock() {
        super(Properties.of()
                .mapColor(MapColor.COLOR_YELLOW)
                .strength(0.5f) // 硬度
                .sound(SoundType.GRASS) // 使用草的声音
                .ignitedByLava() // 可以被岩浆点燃
        );
    }

    // 确保空手破坏时掉落自身
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Collections.singletonList(new ItemStack(this));
    }
}