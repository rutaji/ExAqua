package com.rutaji.exaqua.data.recipes;

import com.rutaji.exaqua.ExAqua;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for recipies of type exaqua:cauldron.
 * @see CauldronRecipe
 */
public interface ICauldronRecipe extends IRecipe<IInventory> {
    ResourceLocation TYPE_ID = new ResourceLocation(ExAqua.MOD_ID, "cauldron");

    @Override
    default @NotNull IRecipeType<?> getType(){
        return Registry.RECIPE_TYPE.getOptional(TYPE_ID).get();
    }

    @Override
    boolean matches(@NotNull IInventory inv, @NotNull World worldIn);

    @Override
    default boolean canFit(int width, int height) {
        return true;
    }

    @Override
    default boolean isDynamic(){
        return true;
    }
}