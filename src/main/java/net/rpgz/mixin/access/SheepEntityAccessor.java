package net.rpgz.mixin.access;

import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SheepEntity.class)
public interface SheepEntityAccessor {

    @Accessor("DROPS")
    static Map<DyeColor, ItemConvertible> getDROPS() {
        throw new AssertionError("This should not occur!");
    }
}
