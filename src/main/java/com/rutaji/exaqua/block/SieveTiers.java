package com.rutaji.exaqua.block;

import com.rutaji.exaqua.item.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.apache.commons.lang3.NotImplementedException;

public enum SieveTiers {
    error,
    iron,
    gold,
    frogium,
    diamond;
    public Item GetSymbol()
    {
        switch (this)
        {
            case iron: return Items.IRON_INGOT;
            case gold: return  Items.GOLD_INGOT;
            case frogium: return ModItems.FROGIUM.get();
            case diamond: return  Items.DIAMOND;
        }

        throw new NotImplementedException("Sieve doesn´t exist");
    }

}
