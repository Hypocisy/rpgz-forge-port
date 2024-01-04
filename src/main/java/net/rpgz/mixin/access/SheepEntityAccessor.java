package net.rpgz.mixin.access;

import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(Sheep.class)
public interface SheepEntityAccessor {

    @Accessor("ITEM_BY_DYE")
    static Map<DyeColor, Item> getDROPS() {
        throw new AssertionError("This should not occur!");
    }
}
