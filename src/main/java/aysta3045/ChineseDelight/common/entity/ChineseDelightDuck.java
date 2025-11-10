package aysta3045.ChineseDelight.common.entity;

import aysta3045.ChineseDelight.common.registry.ModEntityTypes;
import aysta3045.ChineseDelight.common.registry.ModItems;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;

public class ChineseDelightDuck extends Chicken {
    private static final EntityDataAccessor<Boolean> DATA_IS_CHINESE_DELIGHT_DUCK =
            SynchedEntityData.defineId(ChineseDelightDuck.class, EntityDataSerializers.BOOLEAN);

    // 添加自定义的蛋计时器
    private int duckEggTime = this.random.nextInt(6000) + 6000;

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
        compound.putInt("DuckEggTime", this.duckEggTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setChineseDelightDuck(compound.getBoolean("IsChineseDelightDuck"));
        if (compound.contains("DuckEggTime")) {
            this.duckEggTime = compound.getInt("DuckEggTime");
        }
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

    // 修复交配生成小鸡的bug - 重写getBreedOffspring方法
    @Override
    public Chicken getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        // 确保返回的是鸭子实体而不是鸡
        return ModEntityTypes.CHINESE_DELIGHT_DUCK.get().create(level);
    }

    @Override
    public void aiStep() {
        // 完全重写aiStep方法，只调用LivingEntity的aiStep，不调用Chicken的
        // 这是彻底解决下鸡蛋问题的关键
        super.aiStep(); // 调用LivingEntity的aiStep

        // 重置父类的eggTime，防止父类Chicken下鸡蛋
        if (this.eggTime <= 10) {
            this.eggTime = this.random.nextInt(6000) + 6000;
        }

        // 翅膀动画逻辑（与原版鸡相同）
        this.oFlap = this.flap;
        this.oFlapSpeed = this.flapSpeed;
        this.flapSpeed += (this.onGround() ? -1.0F : 4.0F) * 0.3F;
        this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);

        if (!this.onGround() && this.flapping < 1.0F) {
            this.flapping = 1.0F;
        }

        this.flapping *= 0.9F;
        Vec3 vec3 = this.getDeltaMovement();

        if (!this.onGround() && vec3.y < 0.0D) {
            this.setDeltaMovement(vec3.multiply(1.0D, 0.6D, 1.0D));
        }

        this.flap += this.flapping * 2.0F;

        // 下鸭蛋逻辑 - 只下鸭蛋，不下鸡蛋
        if (!this.level().isClientSide && this.isAlive() && !this.isBaby() && --this.duckEggTime <= 0) {
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);

            // 下鸭蛋
            this.spawnAtLocation(ModItems.DUCK_EGG.get());

            this.duckEggTime = this.random.nextInt(6000) + 6000; // 5-10分钟下一次蛋
        }
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

        // 掉落鸭毛
        dropDuckFeathers(looting);
    }

    private void dropDuckFeathers(int looting) {
        // 基础掉落数量
        int baseCount = 1;

        // 随机额外掉落（25%概率多掉1个）
        if (this.random.nextFloat() < 0.25f) {
            baseCount++;
        }

        // 抢夺附魔效果
        int lootingBonus = 0;
        if (looting > 0) {
            lootingBonus = this.random.nextInt(looting + 1);
        }

        // 总掉落数量
        int totalCount = baseCount + lootingBonus;

        // 确保至少掉落1个
        if (totalCount < 1) {
            totalCount = 1;
        }

        // 限制最大掉落数量
        if (totalCount > 5) {
            totalCount = 5;
        }

        this.spawnAtLocation(ModItems.DUCK_FEATHER.get(), totalCount);
    }
}