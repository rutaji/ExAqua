package com.rutaji.exaqua.integration.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.block.ModBlocks;
import com.rutaji.exaqua.data.recipes.CauldronRecipie;
import com.rutaji.exaqua.data.recipes.SieveRecipie;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

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
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends CauldronRecipie> getRecipeClass() {
        return CauldronRecipie.class;
    }

    @Override
    public String getTitle() {
        return "Crafting Cauldron";
    }

    @Override
    public IDrawable getBackground() {
        return BACKGROUND;
    }

    @Override
    public IDrawable getIcon() {
        return ICON;
    }



    private final int X = 5;
    private final int Y = 60;


    @Override
    public void setIngredients(CauldronRecipie recipe, IIngredients ingredients) {
        if(recipe.INPUT != Fluids.EMPTY)
        {
            ingredients.setInput(VanillaTypes.FLUID,new FluidStack(recipe.INPUT,1000));
        }
        if(recipe.INPUT_ITEM != ItemStack.EMPTY)
        {
            ingredients.setInput(VanillaTypes.ITEM,recipe.INPUT_ITEM);
        }
        if(recipe.OUTPUT != Fluids.EMPTY)
        {
            ingredients.setOutput(VanillaTypes.FLUID,new FluidStack(recipe.OUTPUT,1000));
        }
        else
        {
            ingredients.setOutput(VanillaTypes.ITEM,recipe.OUTPUT_ITEM);
        }



    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CauldronRecipie recipe, IIngredients ingredients) {

        if(recipe.INPUT != Fluids.EMPTY)
        {
            recipeLayout.getFluidStacks().init(0,true,12,9);
        }
        if(recipe.INPUT_ITEM != ItemStack.EMPTY)
        {
            recipeLayout.getItemStacks().init(0,true,36,8);
        }
        if(recipe.OUTPUT == Fluids.EMPTY)
        {
            recipeLayout.getItemStacks().init(1, false, 150, 8);
        }
        else{
            recipeLayout.getFluidStacks().init(1, false, 151, 9);
        }


        recipeLayout.getItemStacks().set(ingredients);
        recipeLayout.getFluidStacks().set(ingredients);

    }
    @Override
    public void draw(CauldronRecipie recipe, MatrixStack matrixStack, double mouseX, double mouseY)
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

    }



}
