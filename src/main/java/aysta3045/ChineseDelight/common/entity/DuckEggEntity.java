package aysta3045.ChineseDelight.common.entity;

import aysta3045.ChineseDelight.common.registry.ModEntityTypes;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class DuckEggEntity extends ThrowableItemProjectile {

    public DuckEggEntity(EntityType<? extends DuckEggEntity> type, Level level) {
        super(type, level);
    }

    public DuckEggEntity(Level level, LivingEntity shooter) {
        super(ModEntityTypes.DUCK_EGG.get(), shooter, level);
    }

    public DuckEggEntity(Level level, double x, double y, double z) {
        super(ModEntityTypes.DUCK_EGG.get(), x, y, z, level);
    }

    @Override
    protected Item getDefaultItem() {
        return aysta3045.ChineseDelight.common.registry.ModItems.DUCK_EGG.get();
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(
                        new ItemParticleOption(ParticleTypes.ITEM, this.getItem()),
                        this.getX(), this.getY(), this.getZ(),
                        (this.random.nextFloat() - 0.5) * 0.08,
                        (this.random.nextFloat() - 0.5) * 0.08,
                        (this.random.nextFloat() - 0.5) * 0.08
                );
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide) {
            if (this.random.nextInt(8) == 0) {
                int count = 1;
                if (this.random.nextInt(32) == 0) {
                    count = 4;
                }

                for (int i = 0; i < count; ++i) {
                    ChineseDelightDuck duck = ModEntityTypes.CHINESE_DELIGHT_DUCK.get().create(this.level());
                    if (duck != null) {
                        duck.setBaby(true);
                        duck.setAge(-24000);
                        duck.moveTo(this.getX(), this.getY(), this.getZ(),
                                this.getYRot(), 0.0F);
                        this.level().addFreshEntity(duck);
                    }
                }
            }
            this.level().broadcastEntityEvent(this, (byte)3);
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        result.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 0.0F);
    }
}