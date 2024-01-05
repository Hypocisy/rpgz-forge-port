package net.rpgz.mixin.client;

import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {

    @Inject(method = "getPackedLightCoords", at = @At("TAIL"), cancellable = true)
    private void getLightMixin(T entity, float tickDelta, CallbackInfoReturnable<Integer> info) {
        if (entity instanceof Mob mobEntity && mobEntity.isDeadOrDying()) {
            AABB box = entity.getBoundingBox();
            BlockPos blockPos = new BlockPos(Mth.floor(box.getCenter().x()), Mth.floor(box.maxY), Mth.floor(box.getCenter().z())).above(4);
            info.setReturnValue(LightTexture.pack(this.getBlockLightLevel(entity, blockPos), this.getSkyLightLevel(entity, blockPos)));
        }
    }

    @Shadow
    protected int getBlockLightLevel(T entity, BlockPos blockPos) {
        return 0;
    }

    @Shadow
    protected int getSkyLightLevel(T entity, BlockPos pos) {
        return 0;
    }

}
