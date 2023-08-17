package com.rutaji.exaqua.integration.jei;

import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.block.ModBlocks;
import com.rutaji.exaqua.data.recipes.SqueezerRecipie;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class SqueezerRecipeCategory implements IRecipeCategory<SqueezerRecipie> {
    public final static ResourceLocation UID = new ResourceLocation(ExAqua.MOD_ID,"squeezer");
    public final static ResourceLocation TEXTURE = new ResourceLocation(ExAqua.MOD_ID,"gui/squeezer.png");

    private final IDrawable BACKGROUND;
    private final  IDrawable ICON;

    public SqueezerRecipeCategory(IGuiHelper helper) {
        this.BACKGROUND = helper.createDrawable(TEXTURE,0,0,176,85);
        this.ICON = helper.createDrawableIngredient(new ItemStack(ModBlocks.SQUEEZER.get()));

    }


    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends SqueezerRecipie> getRecipeClass() {
        return SqueezerRecipie.class;
    }

    @Override
    public String getTitle() {
        return ModBlocks.SQUEEZER.get().getTranslatedName().toString();
    }

    @Override
    public IDrawable getBackground() {
        return BACKGROUND;
    }

    @Override
    public IDrawable getIcon() {
        return ICON;
    }

    @Override
    public void setIngredients(SqueezerRecipie recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());//
        net.minecraft.util.NonNullList<Ingredient> t = recipe.getIngredients();
        ingredients.setOutput(VanillaTypes.FLUID,recipe.getRealOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, SqueezerRecipie recipe, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 79, 30);
        recipeLayout.getFluidStacks().init(0,false,79,55);
        recipeLayout.getItemStacks().set(ingredients);
        recipeLayout.getFluidStacks().set(ingredients);

    }
}
