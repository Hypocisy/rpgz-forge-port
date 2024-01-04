package net.rpgz.access;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public interface InventoryAccess {

    public SimpleContainer getInventory();

    public void addInventoryItem(ItemStack stack);
}
