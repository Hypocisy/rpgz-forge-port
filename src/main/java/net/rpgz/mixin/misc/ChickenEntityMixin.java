package net.rpgz.mixin.misc;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Chicken.class)
public abstract class ChickenEntityMixin extends Animal {
    @Shadow
    public float flapProgress;
    @Shadow
    public float maxWingDeviation;
    @Shadow
    public float prevMaxWingDeviation;
    @Shadow
    public float prevFlapProgress;

    public ChickenEntityMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "aiStep", at = @At(value = "HEAD"), cancellable = true)
    public void aiStepChickenBlaze(CallbackInfo info) {
        if (this.isDeadOrDying()) {
            super.tick();
            this.flapProgress = 0.0F;
            this.maxWingDeviation = 0.0F;
            this.prevMaxWingDeviation = 0.0F;
            this.prevFlapProgress = 0.0F;
            info.cancel();
        }
    }

}