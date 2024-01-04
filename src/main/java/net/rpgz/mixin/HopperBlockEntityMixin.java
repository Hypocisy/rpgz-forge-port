package net.rpgz.mixin;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
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

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin implements InventoryAccess {
    private static int ticking = 0;

    @Inject(method = "suckInItems(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/entity/Hopper;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/HopperBlockEntity;getSourceContainer(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/entity/Hopper;)Lnet/minecraft/world/Container;"), cancellable = true)
    private static void extractMixin(Level level, Hopper hopper, CallbackInfoReturnable<Boolean> info) {
        if (ConfigInit.CONFIG.hopper_extracting) {
            ticking++;
            if (ticking >= 20) {
                BlockPos pos = BlockPos.containing(hopper.getLevelX(), hopper.getLevelY(), hopper.getLevelZ());
                AABB box = new AABB(pos).contract(0.0D, 1.0D, 0.0D);
                List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, box, EntityPredicate);
                if (!list.isEmpty()) {
                    Iterator<LivingEntity> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        LivingEntity livingEntity = (LivingEntity) iterator.next();
                        if (livingEntity.isDeadOrDying()) {
                            if (((InventoryAccess) livingEntity).getInventory() != null) {
                                Direction direction = Direction.DOWN;
                                info.setReturnValue(!isInventoryEmpty(((InventoryAccess) livingEntity).getInventory(), direction) && getAvailableSlots(((InventoryAccess) livingEntity).getInventory(), direction).anyMatch((i) -> {
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
    private static boolean tryTakeInItemFromSlot(Hopper hopper, Inventory inventory, int slot, Direction side) {
        return false;
    }

    @Shadow
    private static boolean isInventoryEmpty(Inventory inv, Direction facing) {
        return false;
    }

    @Shadow
    private static IntStream getAvailableSlots(Inventory inventory, Direction side) {
        return null;
    }
}
