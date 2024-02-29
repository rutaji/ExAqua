package com.rutaji.exaqua.integration.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.block.ModBlocks;
import com.rutaji.exaqua.data.recipes.SqueezerRecipie;
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

public class SqueezerRecipeCategory implements IRecipeCategory<SqueezerRecipie>{
    public final static ResourceLocation UID = new ResourceLocation(ExAqua.MOD_ID,"squeezer");
    public final static ResourceLocation TEXTURE = new ResourceLocation(ExAqua.MOD_ID,"gui/squeezerjei.png");

    private final IDrawable BACKGROUND;
    private final  IDrawable ICON;
    private final IGuiHelper HELPER;

    public SqueezerRecipeCategory(IGuiHelper helper) {
        this.BACKGROUND = helper.createDrawable(TEXTURE,0,0,176,85);
        this.ICON = helper.createDrawableIngredient(new ItemStack(ModBlocks.SQUEEZER.get()));
        HELPER = helper;
    }


    @Override
    public @NotNull ResourceLocation getUid() {
        return UID;
    }

    @Override
    public @NotNull Class<? extends SqueezerRecipie> getRecipeClass() {
        return SqueezerRecipie.class;
    }

    @Override
    public @NotNull String getTitle() {
        return ModBlocks.SQUEEZER.get().getTranslatedName().toString();
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
    public void setIngredients(SqueezerRecipie recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        FluidStack f = recipe.getRealOutput();
        f.setAmount(1000);
        ingredients.setOutput(VanillaTypes.FLUID,f);


    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, @NotNull SqueezerRecipie recipe, @NotNull IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 80, 29);
        recipeLayout.getFluidStacks().init(0,false,80,55);


        recipeLayout.getItemStacks().set(ingredients);
        recipeLayout.getFluidStacks().set(ingredients);

    }
    @Override
    public void draw(SqueezerRecipie recipe, @NotNull MatrixStack matrixStack, double mouseX, double mouseY)
    {
        Minecraft.getInstance().fontRenderer.drawString(matrixStack, recipe.getRealOutput().getAmount() + " mB", 97 , 64, 0x111111);
    }



}
