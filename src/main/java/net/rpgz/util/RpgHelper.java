package net.rpgz.util;


import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import net.rpgz.access.InventoryAccess;

public class RpgHelper {

    private static final boolean isSpoiledZLoaded = ModList.get().isLoaded("spoiledz");

    public static void addStackToInventory(Mob mobEntity, ItemStack stack, Level level) {
        if (!level.isClientSide() && !stack.isEmpty()) {
            if (isSpoiledZLoaded) {
                SpoiledUtil.setItemStackSpoilage(level, stack, null);
            }
            ((InventoryAccess) mobEntity).getInventory().addItem(stack);
        }
    }

}
