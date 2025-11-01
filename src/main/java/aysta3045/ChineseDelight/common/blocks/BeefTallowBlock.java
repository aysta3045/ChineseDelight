package aysta3045.ChineseDelight.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.block.SoundType;

import java.util.Collections;
import java.util.List;

public class BeefTallowBlock extends Block {
    public BeefTallowBlock() {
        super(Properties.of()
                .mapColor(MapColor.COLOR_YELLOW) // 地图颜色
                .strength(1.0f, 5.0f) // 硬度、爆炸抗性
                .friction(0.8f) // 摩擦力（比冰高，比普通方块略滑）
                .speedFactor(0.95f) // 移动速度因子（略慢）
                .sound(SoundType.SLIME_BLOCK) // 使用黏液块音效
        );
    }

    // 确保空手破坏时掉落自身
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return Collections.singletonList(new ItemStack(this));
    }
}