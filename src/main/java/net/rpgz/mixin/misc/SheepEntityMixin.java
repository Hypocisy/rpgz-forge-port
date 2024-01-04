package net.rpgz.mixin.misc;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.rpgz.access.InventoryAccess;
import net.rpgz.mixin.access.SheepEntityAccessor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Sheep.class)
public abstract class SheepEntityMixin extends Animal {
    public SheepEntityMixin(EntityType<? extends Animal> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void dropFromLootTable(DamageSource source, boolean causedByPlayer) {
        super.dropFromLootTable(source, causedByPlayer);
        if ((Object) this instanceof Sheep sheepEntity) {
            ((InventoryAccess) this).rpgz$addInventoryItem(new ItemStack(SheepEntityAccessor.getDROPS().get(sheepEntity.getColor())));
        }

    }
}
