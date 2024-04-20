package com.rutaji.exaqua.data.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;

/**
 * Stores items and their chance. Used in {@link SieveRecipe SieveRecipe} and {@link HandSieveRecipe HandSieveRecipe}.
 */
public class RoolItem {
    public ItemStack item;
    public int chance;
    public RoolItem (ItemStack item,int chance){
        this.item = item;
        this.chance = chance;
    }

    /**
     * @return RoolItem read from packet buffer.
     */
    public static @NotNull RoolItem Read(@NotNull PacketBuffer buffer)
    {
        ItemStack item = buffer.readItemStack();
        int chance = buffer.readInt();
        return new RoolItem(item,chance);
    }

    /**
     * Writes given roolItem into a packet buffer.
     */
    public static void Write(@NotNull PacketBuffer buffer, @NotNull RoolItem roolItem)
    {
        buffer.writeItemStack(roolItem.item);
        buffer.writeInt(roolItem.chance);
    }
}
