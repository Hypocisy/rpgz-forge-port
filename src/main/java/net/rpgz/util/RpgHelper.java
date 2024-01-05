package net.rpgz.util;


import com.mrbysco.spoiled.compat.ct.SpoilManager;
import com.mrbysco.spoiled.datagen.client.SpoiledLanguageProvider;
import com.mrbysco.spoiled.recipe.SpoilRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import net.rpgz.RpgzMain;
import net.rpgz.access.InventoryAccess;

import java.util.Iterator;
import java.util.List;

public class RpgHelper {

    private static final boolean isSpoiledZLoaded = ModList.get().isLoaded("Spoiled");

    public static void addStackToInventory(Mob mobEntity, ItemStack stack, Level level) {
        if (!level.isClientSide() && !stack.isEmpty()) {
            if (isSpoiledZLoaded) {
                // TODO find a spoiled way to discard items
            }
            ((InventoryAccess) mobEntity).getInventory().addItem(stack);
        }
    }

}
