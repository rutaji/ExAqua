package com.rutaji.exaqua.others;

import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

/**
 * Enum of temperature used in {@link com.rutaji.exaqua.data.recipes.CauldronRecipe cauldron recipies}.
 * Passed in {@link com.rutaji.exaqua.data.recipes.InventoryCauldron InventoryCauldron} to {@link com.rutaji.exaqua.data.recipes.CauldronRecipe#matches}
 * @see com.rutaji.exaqua.data.recipes.CauldronRecipe
 * @see com.rutaji.exaqua.data.recipes.InventoryCauldron
 */
public enum CauldronTemperature {
    cold,
    hot,
    neutral
}
