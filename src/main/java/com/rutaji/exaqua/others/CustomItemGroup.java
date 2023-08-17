package com.rutaji.exaqua.others;

import com.rutaji.exaqua.item.ModItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class CustomItemGroup {
    public static final ItemGroup EX_AQUA_GROUP = new ItemGroup("ExAquaModTab") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.HANDSIEVE.get());
        }
    };

}
