package net.rpgz.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Mutable
    @Final
    @Shadow
    Minecraft minecraft;

    @Inject(method = "pick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"))
    private void updateTargetedEntityMixin(float tickDelta, CallbackInfo info) {
        Entity entity = this.minecraft.getCameraEntity();
        if (this.minecraft.hitResult != null && this.minecraft.hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) this.minecraft.hitResult).getBlockPos();
            if (!this.minecraft.level.getBlockState(pos).isCollisionShapeFullBlock(this.minecraft.level, pos)) {
                float reachDinstance = this.minecraft.gameMode.getPickRange();
                Vec3 vec3d = this.minecraft.player.getEyePosition(tickDelta);
                Vec3 vec3d2 = this.minecraft.player.getViewVector(tickDelta);
                Vec3 vec3d3 = vec3d.add(vec3d2.x * reachDinstance, vec3d2.y * reachDinstance, vec3d2.z * reachDinstance);
                AABB box = entity.getBoundingBox().expandTowards(vec3d2.multiply(reachDinstance, reachDinstance, reachDinstance)).inflate(1.0D, 1.0D, 1.0D);
                EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(entity, vec3d, vec3d3, box, (entityx) -> {
                    return !entityx.isSpectator() && entityx.isPickable();
                }, 5D);
                if (entityHitResult != null) {
                    this.minecraft.hitResult = entityHitResult;
                }
            }
        }
    }

}