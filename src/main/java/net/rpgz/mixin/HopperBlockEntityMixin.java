package net.rpgz.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.phys.AABB;
import net.rpgz.access.InventoryAccess;
import net.rpgz.init.ConfigInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin implements InventoryAccess {
    private static int ticking = 0;
    private static final Predicate<Entity> EXCEPT_SPECTATOR = (entity) -> !entity.isSpectator();

    @Inject(method = "suckInItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;getItemsAtAndAbove(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/entity/Hopper;)Ljava/util/List;"), cancellable = true)
    private static void extractMixin(Level level, Hopper hopper, CallbackInfoReturnable<Boolean> info) {
        if (ConfigInit.CONFIG.hopper_extracting) {
            ticking++;
            if (ticking >= 20) {
                BlockPos pos = BlockPos.containing(hopper.getLevelX(), hopper.getLevelY(), hopper.getLevelZ());
                AABB box = new AABB(pos).contract(0.0D, 1.0D, 0.0D);
                List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, box, EXCEPT_SPECTATOR);
                if (!list.isEmpty()) {
                    for (LivingEntity livingEntity : list) {
                        if (livingEntity.isDeadOrDying()) {
                            if (((InventoryAccess) livingEntity).getInventory() != null) {
                                Direction direction = Direction.DOWN;
                                info.setReturnValue(isEmptyContainer(((InventoryAccess) livingEntity).getInventory(), direction) ? false
                                        : getSlots(((InventoryAccess) livingEntity).getInventory(), direction).anyMatch((i) -> {
                                    return tryTakeInItemFromSlot(hopper, ((InventoryAccess) livingEntity).getInventory(), i, direction);
                                }));
                            }
                        }
                    }
                }
                ticking = 0;
            }
        }
    }

    @Shadow
    private static boolean tryTakeInItemFromSlot(Hopper hopper, Container inventory, int slot, Direction side) {
        return false;
    }

    @Shadow
    private static boolean isEmptyContainer(Container inventory, Direction facing) {
        return false;
    }

    @Shadow
    private static IntStream getSlots(Container inventory, Direction side) {
        return null;
    }
}
