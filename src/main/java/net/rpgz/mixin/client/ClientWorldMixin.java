package net.rpgz.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin {

    @Inject(method = "Lnet/minecraft/client/world/ClientWorld;tickEntity(Lnet/minecraft/entity/Entity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V"))
    private void tickEntityMixin(Entity entity, CallbackInfo info) {
        if (!entity.isAlive() && entity instanceof MobEntity) {
            entity.age = -1; // age has to be 0 or -1 cause of some models (example: guardian)
        }
    }

}