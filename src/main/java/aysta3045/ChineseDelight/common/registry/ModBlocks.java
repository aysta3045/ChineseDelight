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
            SesameBaleBlock::new);

    // 熬油锅
    public static final RegistryObject<Block> OIL_COOKING_POT = BLOCKS.register("oil_cooking_pot",
            () -> new OilCookingPotBlock(Block.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(0.5F)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(OilCookingPotBlock.LIT) ? 13 : 0)
                    .sound(SoundType.STONE)));
}