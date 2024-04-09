package com.rutaji.exaqua.data.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

public class RoolItem {
    public ItemStack item;
    public int chance;
    public RoolItem (ItemStack item,int chance){
        this.item = item;
        this.chance = chance;
    }
    public static RoolItem Read(PacketBuffer buffer)
    {
        ItemStack item = buffer.readItemStack();
        int chance = buffer.readInt();
        return new RoolItem(item,chance);
    }
    public static void Write(PacketBuffer buffer,RoolItem roolItem)
    {
        buffer.writeItemStack(roolItem.item);
        buffer.writeInt(roolItem.chance);
    }
}
