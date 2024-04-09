package com.rutaji.exaqua.integration.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.data.recipes.HandSieveRecipie;
import com.rutaji.exaqua.item.ModItems;
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

public class HandSieveRecipeCategory implements IRecipeCategory<HandSieveRecipie>{
    public final static ResourceLocation UID = new ResourceLocation(ExAqua.MOD_ID,"handsieve");
    public final static ResourceLocation TEXTURE = new ResourceLocation(ExAqua.MOD_ID,"gui/sievejei.png");

    private final IDrawable BACKGROUND;
    private final  IDrawable ICON;
    private final IGuiHelper HELPER;

    private final int WIDTH = 200;

    public HandSieveRecipeCategory(IGuiHelper helper) {
        this.BACKGROUND = helper.createDrawable(TEXTURE,0,0,WIDTH,220);
        this.ICON = helper.createDrawableIngredient(new ItemStack(ModItems.HANDSIEVE.get()));
        HELPER = helper;
    }


    @Override
    public @NotNull ResourceLocation getUid() {
        return UID;
    }

    @Override
    public @NotNull Class<? extends HandSieveRecipie> getRecipeClass() {
        return HandSieveRecipie.class;
    }

    @Override
    public @NotNull String getTitle() {
        return "Hand sieve";
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
    public void setIngredients(HandSieveRecipie recipe, IIngredients ingredients) {
        FluidStack f = new FluidStack(recipe.INPUTFLUID,1000);
        ingredients.setInput(VanillaTypes.FLUID,f);
        List<ItemStack> output = recipe.GetAllPossibleOutputs();
        ingredients.setOutputs(VanillaTypes.ITEM,output);



    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, HandSieveRecipie recipe, @NotNull IIngredients ingredients) {
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
    public void draw(HandSieveRecipie recipe, @NotNull MatrixStack matrixStack, double mouseX, double mouseY)
    {
        int x = X;
        int y = Y;


        for(int i =0;i < recipe.GetSize();i++ )
        {
            assert Minecraft.getInstance().currentScreen != null;
            Minecraft.getInstance().currentScreen.blit(matrixStack,x,y,201,0,18,18);
            x+=34;
            if(x > WIDTH -5) {x = X;y+=40;}
        }
        x = X;
        y = Y;
        String s;
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
