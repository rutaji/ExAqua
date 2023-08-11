package com.rutaji.exaqua.data.recipes;

import net.minecraft.item.ItemStack;

public class RoolItem {
    public ItemStack item;
    public int chance;
    public RoolItem (ItemStack item,int chance){
        this.item = item;
        this.chance = chance;
    }
}
