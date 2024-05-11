package com.rutaji.exaqua.others;

import com.rutaji.exaqua.block.ModBlocks;
import com.rutaji.exaqua.block.SieveBlock;
import com.rutaji.exaqua.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraftforge.fml.RegistryObject;
import org.apache.commons.lang3.NotImplementedException;



/**
 *
 * Used in {@link com.rutaji.exaqua.data.recipes.SieveRecipe recipies of type exaqua:sieve}.
 * {@link com.rutaji.exaqua.block.ModBlocks#IRONSIEVE Each register instance of sieve block is given it's tier, when registered}.
 * This tier is passed into its tile entity and used in {@link com.rutaji.exaqua.data.recipes.SieveRecipe#matches matching recipies}.
 * If you want to add new tier you just have to implement it in 2 motheds in this class.
 * Value error is used to indicate something went wrong insted of null.
 * @see SieveTiers#GetSIEVE
 * @see SieveTiers#GetSymbol()
 */
public enum SieveTiers {
    error,
    iron,
    gold,
    frogium,
    diamond;

    /**+
     * @return Item used as a symbol for this tier.
     * @exception NotImplementedException if tier is not implemented in this method.
     */
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
    /**
     * Returns registered {@link SieveBlock SieveBlock} with given tier.
     * @param tiers tier of the sive block to return.
     * @return registered {@link SieveBlock SieveBlock} with given tier.
     * @exception NotImplementedException if given tier isn't implemented in this method.
     * @see SieveTiers
     */
    public static RegistryObject<Block> GetSIEVE(SieveTiers tiers)
    {
        switch (tiers)
        {
            case iron: return  ModBlocks.IRONSIEVE;
            case gold: return  ModBlocks.GOLDSIEVE;
            case frogium: return  ModBlocks.FROGIUMSIEVE;
            case diamond: return  ModBlocks.DIAMONDSIEVE;
        }

        throw new NotImplementedException("Sieve doesn´t exist");
    }

}

