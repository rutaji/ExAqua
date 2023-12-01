package com.rutaji.exaqua.data.recipes;

import com.rutaji.exaqua.ExAqua;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;

public interface ISqueezerRecipie extends IRecipe<IInventory> {
    ResourceLocation TYPE_ID = new ResourceLocation(ExAqua.MOD_ID, "squeezer");

    @Override
    default @NotNull IRecipeType<?> getType(){
        return Registry.RECIPE_TYPE.getOptional(TYPE_ID).get();
    }

    @Override
    default boolean canFit(int width, int height) {
        return true;
    }

    @Override
    default boolean isDynamic(){
        return true;
    }
}
