package net.rpgz.mixin.misc;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChickenEntity.class)
public abstract class ChickenEntityMixin extends AnimalEntity {
    @Shadow
    public float flapProgress;
    @Shadow
    public float maxWingDeviation;
    @Shadow
    public float prevMaxWingDeviation;
    @Shadow
    public float prevFlapProgress;

    public ChickenEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tickMovement", at = @At(value = "HEAD"), cancellable = true)
    public void tickMovementChickenBlaze(CallbackInfo info) {
        if (this.isDead()) {
            super.tickMovement();
            this.flapProgress = 0.0F;
            this.maxWingDeviation = 0.0F;
            this.prevMaxWingDeviation = 0.0F;
            this.prevFlapProgress = 0.0F;
            info.cancel();
        }
    }

}