package aysta3045.ChineseDelight.common.registry;

import aysta3045.ChineseDelight.ChineseDelight;
import aysta3045.ChineseDelight.common.blocks.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ChineseDelight.MODID);

    // 芝麻作物
    public static final RegistryObject<Block> SESAME_CROP = BLOCKS.register("sesame_crop",
            () -> new SesameCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT)
                    .noCollission()
                    .noOcclusion()
                    .randomTicks()
                    .instabreak()
            ));

    // 一捆芝麻穗
    public static final RegistryObject<Block> SESAME_BALE = BLOCKS.register("sesame_bale",
            () -> new SesameBaleBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW)
                    .strength(0.5f)
                    .sound(SoundType.GRASS)
                    .ignitedByLava() // 可以被岩浆点燃
            ));

    // 牛油块
    public static final RegistryObject<Block> BEEF_TALLOW_BLOCK = BLOCKS.register("beef_tallow_block",
            () -> new BeefTallowBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_YELLOW) // 地图颜色
                    .strength(0.8f, 2.5f) // 硬度、爆炸抗性
                    .friction(0.8f) // 摩擦力（比冰高，比普通方块略滑）
                    .speedFactor(0.95f) // 移动速度因子（略慢）
                    .sound(SoundType.SLIME_BLOCK)
            ));

    // 发酵罐
    public static final RegistryObject<Block> FERMENTATION_JAR = BLOCKS.register("fermentation_jar",
            () -> new FermentationJarBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BROWN)
                    .strength(2.0f)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .sound(SoundType.DECORATED_POT)
            ));
}