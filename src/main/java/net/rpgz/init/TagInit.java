package net.rpgz.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.rpgz.RpgzMain;

public class TagInit {
    public static final TagKey<Item> RARE_ITEMS = tagItem("rare_items");
    public static final TagKey<EntityType<?>> EXCLUDED_ENTITIES = tagEntity("excluded_entities");

    public static void init() {
    }

    private static TagKey<Block> tagBlock(String name) {
        return BlockTags.create(new ResourceLocation(RpgzMain.MOD_ID, name));
    }

    private static TagKey<EntityType<?>> tagEntity(String name) {
        return ForgeRegistries.ENTITY_TYPES.tags().createTagKey(new ResourceLocation(RpgzMain.MOD_ID, name));
    }

    private static TagKey<Item> tagItem(String name) {
        return ItemTags.create(new ResourceLocation(RpgzMain.MOD_ID, name));
    }
}
