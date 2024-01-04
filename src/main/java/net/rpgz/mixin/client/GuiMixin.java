package net.rpgz.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.rpgz.RpgzMain;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(Gui.class)
public abstract class GuiMixin {

    @Shadow
    @Final
    @Mutable
    protected Minecraft minecraft;


    private static final ResourceLocation LOOT_BAG_TEXTURE = new ResourceLocation(RpgzMain.MOD_ID, "textures/sprite/loot_bag.png");

    @Inject(method = "render", at = @At(value = "TAIL"))
    private void renderMixin(GuiGraphics guiGraphics, float f, CallbackInfo info) {
        this.rpgz$renderLootBag(guiGraphics);
    }

    @Unique
    private void rpgz$renderLootBag(GuiGraphics context) {
        if (this.minecraft.hitResult != null && this.minecraft.hitResult.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) this.minecraft.hitResult).getEntity();
            if (entity instanceof Mob deadBody) {
                if (deadBody.deathTime > 20) {
                    int scaledWidth = this.minecraft.getWindow().getGuiScaledWidth();
                    int scaledHeight = this.minecraft.getWindow().getGuiScaledHeight();
                    context.blit(LOOT_BAG_TEXTURE, (scaledWidth / 2), (scaledHeight / 2) - 16, 0.0F, 0.0F, 16, 16, 16, 16);
                }
            }
        }

    }

}