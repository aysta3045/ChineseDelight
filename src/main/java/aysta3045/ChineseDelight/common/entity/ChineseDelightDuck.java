// ChineseDelightDuck.java
package aysta3045.ChineseDelight.common.entity;

import aysta3045.ChineseDelight.common.registry.ModItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraftforge.registries.RegistryObject;

public class ChineseDelightDuck extends Chicken {
    private static final EntityDataAccessor<Boolean> DATA_IS_CHINESE_DELIGHT_DUCK =
            SynchedEntityData.defineId(ChineseDelightDuck.class, EntityDataSerializers.BOOLEAN);

    public ChineseDelightDuck(EntityType<? extends Chicken> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.4D));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, Ingredient.of(Items.WHEAT_SEEDS), false));
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_CHINESE_DELIGHT_DUCK, true);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("IsChineseDelightDuck", this.isChineseDelightDuck());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setChineseDelightDuck(compound.getBoolean("IsChineseDelightDuck"));
    }

    public boolean isChineseDelightDuck() {
        return this.entityData.get(DATA_IS_CHINESE_DELIGHT_DUCK);
    }

    public void setChineseDelightDuck(boolean isChineseDelightDuck) {
        this.entityData.set(DATA_IS_CHINESE_DELIGHT_DUCK, isChineseDelightDuck);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.CHICKEN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.CHICKEN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.CHICKEN_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.getItem() == Items.WHEAT_SEEDS ||
                stack.getItem() == Items.MELON_SEEDS ||
                stack.getItem() == Items.PUMPKIN_SEEDS ||
                stack.getItem() == Items.BEETROOT_SEEDS;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);

        // 掉落生鸭肉或熟鸭肉
        if (this.isOnFire()) {
            this.spawnAtLocation(ModItems.COOKED_DUCK_MEAT.get());
        } else {
            this.spawnAtLocation(ModItems.DUCK_MEAT.get());
        }

        // 神秘抢夺附魔
        int featherCount = 1 + this.random.nextInt(2) + (looting > 0 ? this.random.nextInt(looting + 1) : 0);
        this.spawnAtLocation(ModItems.DUCK_FEATHER.get(), featherCount);
    }


}