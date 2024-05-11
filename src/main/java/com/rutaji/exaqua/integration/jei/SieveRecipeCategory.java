package com.rutaji.exaqua.integration.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.block.ModBlocks;
import com.rutaji.exaqua.data.recipes.CauldronRecipe;
import com.rutaji.exaqua.data.recipes.SieveRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Adds jei UI for recipe of exaqua:sieve.
 * @see SieveRecipe
 */
public class SieveRecipeCategory implements IRecipeCategory<SieveRecipe>{
    public final static ResourceLocation UID = new ResourceLocation(ExAqua.MOD_ID,"sieve");
    public final static ResourceLocation TEXTURE = new ResourceLocation(ExAqua.MOD_ID,"gui/sievejei.png");

    private final IDrawable BACKGROUND;
    private final  IDrawable ICON;
    private final IGuiHelper HELPER;

    private final int WIDTH = 200;

    public SieveRecipeCategory(IGuiHelper helper) {
        this.BACKGROUND = helper.createDrawable(TEXTURE,0,0,WIDTH,220);
        this.ICON = helper.createDrawableIngredient(new ItemStack(ModBlocks.DIAMONDSIEVE.get()));
        HELPER = helper;
    }


    @Override
    public @NotNull ResourceLocation getUid() {
        return UID;
    }

    @Override
    public @NotNull Class<? extends SieveRecipe> getRecipeClass() {
        return SieveRecipe.class;
    }

    @Override
    public @NotNull String getTitle() {
        return "Automatic Sieving";
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return BACKGROUND;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return ICON;
    }



    private final int X = 5;
    private final int Y = 60;


    @Override
    public void setIngredients(SieveRecipe recipe, IIngredients ingredients) {
        FluidStack f = recipe.INPUTFLUID.copy();
        f.setAmount(1000);
        ingredients.setInput(VanillaTypes.FLUID,f);
        List<ItemStack> output = recipe.GetAllPossibleOutputs();
        ingredients.setOutputs(VanillaTypes.ITEM,output);



    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, SieveRecipe recipe, @NotNull IIngredients ingredients) {
        recipeLayout.getFluidStacks().init(0,true,92,8);

        int x = X;
        int y = Y;
        int numberOfItems = recipe.GetSize();
        for(int i = 0 ;i < numberOfItems;i++ )
        {
            recipeLayout.getItemStacks().init(i, false, x, y);
            x+=34;
            if(x > WIDTH -5) {x = X;y+=40;}
        }
        recipeLayout.getItemStacks().set(ingredients);
        recipeLayout.getFluidStacks().set(ingredients);

    }
    @Override
    public void draw(SieveRecipe recipe, @NotNull MatrixStack matrixStack, double mouseX, double mouseY)
    {
        int x = X;
        int y = Y;

        //drawing tiless
        for(int i =0;i < recipe.GetSize();i++ )
        {
            assert Minecraft.getInstance().currentScreen != null;
            Minecraft.getInstance().currentScreen.blit(matrixStack,x,y,201,0,18,18);
            x+=34;
            if(x > WIDTH -5) {x = X;y+=40;}
        }
        x = X;
        y = Y;
        HELPER.createDrawableIngredient(new ItemStack(recipe.TIER.GetSymbol())).draw(matrixStack,10,10);
        String s;
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, recipe.INPUTFLUID.getAmount() + " mb" , 110 , 18, 0x111111);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, recipe.RF + " rf/t" , 110 , 28, 0x111111);
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, recipe.TIME + " t" , 110 , 38, 0x111111);
        //drawing procents
        for(double d : recipe.GetChances())
        {
            if(d < 10){
                s = String.format("%.1f%%",d);
            }
            else{
                s = String.format("%.0f%%",d);
            }
            Minecraft.getInstance().fontRenderer.drawString(matrixStack,s , x , y+25, 0x111111);
            x+=34;
            if(x > WIDTH -5) {x = X;y+=40;}
        }


    }



}
