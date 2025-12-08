package aysta3045.ChineseDelight.common.registry;

import aysta3045.ChineseDelight.ChineseDelight;
import aysta3045.ChineseDelight.common.entity.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ChineseDelight.MODID);

    // 鸭
    public static final RegistryObject<EntityType<ChineseDelightDuck>> CHINESE_DELIGHT_DUCK = ENTITY_TYPES.register("chinese_delight_duck",
            () -> EntityType.Builder.of(ChineseDelightDuck::new, MobCategory.CREATURE)
                    .sized(0.4F, 0.7F)
                    .clientTrackingRange(10)
                    .build("chinese_delight_duck"));

    // 鸭蛋实体（投掷物）
    public static final RegistryObject<EntityType<DuckEggEntity>> DUCK_EGG = ENTITY_TYPES.register("duck_egg",
            () -> EntityType.Builder.<DuckEggEntity>of(DuckEggEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("duck_egg"));
}