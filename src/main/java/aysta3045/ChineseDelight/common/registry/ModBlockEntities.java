package aysta3045.ChineseDelight.common.registry;

import aysta3045.ChineseDelight.ChineseDelight;
import aysta3045.ChineseDelight.common.block.entity.ChineseCookingPotBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ChineseDelight.MODID);

    // 烹饪锅方块实体
    public static final RegistryObject<BlockEntityType<ChineseCookingPotBlockEntity>> CHINESE_COOKING_POT =
            BLOCK_ENTITIES.register("chinese_cooking_pot",
                    () -> BlockEntityType.Builder.of(ChineseCookingPotBlockEntity::new,
                            ModBlocks.CHINESE_COOKING_POT.get()).build(null));
}