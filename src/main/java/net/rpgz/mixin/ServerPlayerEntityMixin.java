package net.rpgz.mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(ServerPlayer.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "startSleepInBed", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void startSleepingMixin(BlockPos blockPos, CallbackInfoReturnable<Either<Player.BedSleepingProblem, Unit>> cir, Optional optAt, Player.BedSleepingProblem ret, Direction direction, double d0, double d1, Vec3 vec3, List list) {
        if (!list.isEmpty()) {
            List<Vindicator> removeList = new ArrayList<>();
            for (int o = 0; o < list.size(); ++o) {
                Vindicator entityFromList = (Vindicator) list.get(o);
                if (entityFromList.isDeadOrDying()) {
                    removeList.add(entityFromList);
                }
            }
            list.removeAll(removeList);
        }
    }
}
