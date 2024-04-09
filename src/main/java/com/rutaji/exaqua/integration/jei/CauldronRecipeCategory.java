package com.rutaji.exaqua.integration.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.block.ModBlocks;
import com.rutaji.exaqua.data.recipes.CauldronRecipie;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class CauldronRecipeCategory implements IRecipeCategory<CauldronRecipie>{
    public final static ResourceLocation UID = new ResourceLocation(ExAqua.MOD_ID,"cauldron");
    public final static ResourceLocation TEXTURE = new ResourceLocation(ExAqua.MOD_ID,"gui/cauldronjei.png");

    private final IDrawable BACKGROUND;
    private final  IDrawable ICON;
    private final IGuiHelper HELPER;

    private final int WIDTH = 200;

    public CauldronRecipeCategory(IGuiHelper helper) {
        this.BACKGROUND = helper.createDrawable(TEXTURE,0,0,WIDTH,36);
        this.ICON = helper.createDrawableIngredient(new ItemStack(ModBlocks.CAULDRON.get()));
        HELPER = helper;
    }


    @Override
    public @NotNull ResourceLocation getUid() {
        return UID;
    }

    @Override
    public @NotNull Class<? extends CauldronRecipie> getRecipeClass() {
        return CauldronRecipie.class;
    }

    @Override
    public @NotNull String getTitle() {
        return "Crafting Cauldron";
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return BACKGROUND;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return ICON;
    }



    @Override
    public void setIngredients(CauldronRecipie recipe, @NotNull IIngredients ingredients) {
        if(recipe.INPUT_FLUID != Fluids.EMPTY)
        {
            ingredients.setInput(VanillaTypes.FLUID,new FluidStack(recipe.INPUT_FLUID,1000));
        }
        if(recipe.INPUT_ITEM != ItemStack.EMPTY)
        {
           ingredients.setInput(VanillaTypes.ITEM,recipe.INPUT_ITEM);
        }
        if(recipe.OUTPUT_FLUID != Fluids.EMPTY)
        {
            ingredients.setOutput(VanillaTypes.FLUID,new FluidStack(recipe.OUTPUT_FLUID,1000));
        }
        if(recipe.OUTPUT_ITEM != ItemStack.EMPTY)
        {
            ingredients.setOutput(VanillaTypes.ITEM,recipe.OUTPUT_ITEM);
        }



    }

    @Override
    public void setRecipe(@NotNull IRecipeLayout recipeLayout, CauldronRecipie recipe, @NotNull IIngredients ingredients) {

        if(recipe.INPUT_FLUID != Fluids.EMPTY)
        {
            recipeLayout.getFluidStacks().init(0,true,12,9);
        }
        if(recipe.INPUT_ITEM != ItemStack.EMPTY)
        {
            recipeLayout.getItemStacks().init(0,true,36,8);
        }
        if(recipe.OUTPUT_ITEM != ItemStack.EMPTY)
        {
            if(recipe.OUTPUT_FLUID == Fluids.EMPTY)
            {
                recipeLayout.getItemStacks().init(1, false, 150, 8);
            }
            else
            {
                recipeLayout.getItemStacks().init(1, false, 174, 8);
            }
        }
        if(recipe.OUTPUT_FLUID != Fluids.EMPTY)
        {
            recipeLayout.getFluidStacks().init(1, false, 151, 9);
        }


        recipeLayout.getItemStacks().set(ingredients);
        recipeLayout.getFluidStacks().set(ingredients);

    }
    @Override
    public void draw(CauldronRecipie recipe, @NotNull MatrixStack matrixStack, double mouseX, double mouseY)
    {
        switch (recipe.TEMP)
        {
            case hot:
                Minecraft.getInstance().currentScreen.blit(matrixStack, 70, 8, 202, 1, 24, 18);
                break;
            case cold:
                Minecraft.getInstance().currentScreen.blit(matrixStack, 70, 8, 202, 21, 24, 18);
                break;
            case neutral:
                Minecraft.getInstance().currentScreen.blit(matrixStack, 70, 8, 202, 41, 24, 18);
                break;
        }
        if(recipe.INPUT_FLUID != Fluids.EMPTY)
        {
            Minecraft.getInstance().currentScreen.blit(matrixStack, 10, 8, 226, 1, 18, 18);
        }
        if(recipe.INPUT_ITEM != ItemStack.EMPTY)
        {
            Minecraft.getInstance().currentScreen.blit(matrixStack, 35, 8, 226, 1, 18, 18);
        }
        Minecraft.getInstance().currentScreen.blit(matrixStack, 149, 8, 226, 1, 18, 18);
        if(recipe.Has2Outputs())
        {
            Minecraft.getInstance().currentScreen.blit(matrixStack, 173, 8, 226, 1, 18, 18);
        }



    }



}
