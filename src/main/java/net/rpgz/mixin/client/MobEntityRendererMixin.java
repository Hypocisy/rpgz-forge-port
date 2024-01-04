package net.rpgz.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;

@OnlyIn(Dist.CLIENT)
@Mixin(MobRenderer.class)
public abstract class MobEntityRendererMixin<T extends Mob, M extends EntityModel<T>> extends LivingEntityRenderer<T, M> {

    public MobEntityRendererMixin(EntityRendererProvider.Context ctx, M model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Override
    protected boolean isShaking(T entity) {
        if (entity.isDeadOrDying()) {
            return false;
        }
        return super.isShaking(entity);
    }

    @Override
    protected void setupRotations(T entity, @NotNull PoseStack poseStack, float animationProgress, float bodyYaw, float tickDelta) {
        if (entity.deathTime > 0) {
            this.shadowRadius = 0F;
            float f = ((float) entity.deathTime + tickDelta - 1.0F) / 20.0F * 1.6F;
            if (f > 1.0F) {
                f = 1.0F;
            }
            Float lyinganglebonus = 1F;
            if (this.getFlipDegrees(entity) > 90F) {
                lyinganglebonus = 2.5F;
            }
            poseStack.translate(0.0D, (double) ((entity.getBbWidth() / 4.0D) * f) * lyinganglebonus, 0.0D);
            if (entity.isBaby()) {
                // (double) -((entity.getHeight()) * f) * lyinganglebonus
                poseStack.translate(-(double) ((entity.getBbHeight() / 2) * f), 0.0D, 0.0D);
            }
        }
        super.setupRotations(entity, poseStack, animationProgress, bodyYaw, tickDelta);
    }

    @Override
    protected float getAttackAnim(T entity, float tickDelta) {
        if (entity.isDeadOrDying()) {
            return 0.0f;
        }
        return super.getAttackAnim(entity, tickDelta);
    }

}
