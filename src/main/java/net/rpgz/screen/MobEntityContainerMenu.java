package net.rpgz.screen;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.rpgz.init.SoundInit;
import net.rpgz.init.TagInit;
import org.jetbrains.annotations.NotNull;

public class MobEntityContainerMenu extends AbstractContainerMenu {
    private final SimpleContainer inventory;

    public MobEntityContainerMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer());
    }

    public MobEntityContainerMenu(int syncId, Inventory playerInventory, SimpleContainer simpleInventory) {
        super(MenuType.GENERIC_9x1, syncId);
        this.inventory = simpleInventory;

        int m;
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(inventory, m, 8 + m * 18, 20) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }
        for (m = 0; m < 3; ++m) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, m * 18 + 51));
            }
        }
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 109));
        }

    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Boolean rareItem = false;
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = (Slot) this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();
            if (itemStack.is(TagInit.RARE_ITEMS)) {
                rareItem = true;
            }
            if (index < this.inventory.getContainerSize()) {
                if (!this.moveItemStackTo(itemStack2, this.inventory.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemStack2, 0, this.inventory.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        if (rareItem) {
            player.playSound(SoundInit.COIN_LOOT_SOUND_EVENT.get(), 1F, 1F);
        } else {
            player.playSound(SoundInit.LOOT_SOUND_EVENT.get(), 1F, 1F);
        }

        return itemStack;
    }
}
