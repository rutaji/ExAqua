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
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class SqueezerRecipeCategory implements IRecipeCategory<SqueezerRecipie> {
    public final static ResourceLocation UID = new ResourceLocation(ExAqua.MOD_ID,"squeezer");
    public final static ResourceLocation TEXTURE = new ResourceLocation(ExAqua.MOD_ID,"gui/squeezer.png");

    private final IDrawable background;
    private final  IDrawable icon;

    public SqueezerRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE,0,0,176,85);
        this.icon = helper.createDrawableIngredient(new ItemStack(ModBlocks.SQUEEZER.get()));

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
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(SqueezerRecipie recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());//
        net.minecraft.util.NonNullList<Ingredient> t = recipe.getIngredients();
        ingredients.setOutput(VanillaTypes.ITEM,recipe.getRecipeOutput());//todo change the whole recipie => outpu = water
        ItemStack t2 = recipe.getRecipeOutput();
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, SqueezerRecipie recipe, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 79, 30);
        recipeLayout.getItemStacks().init(1, true, 79, 52);
        recipeLayout.getItemStacks().set(ingredients);
    }
}
