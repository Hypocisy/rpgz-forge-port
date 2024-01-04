package net.rpgz.mixin.misc;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.SquidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Squid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SquidModel.class)
public abstract class SquidEntityModelMixin<T extends Entity> extends HierarchicalModel<T> {

    // Doesn't work for some reason
    @Inject(method = "setupAnim", at = @At(value = "HEAD"), cancellable = true)
    public void setAnglesMixin(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo info) {
        if (((Squid) entity).isDeadOrDying()) {
            info.cancel();
        }

    }

}