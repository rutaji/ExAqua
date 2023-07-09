package com.rutaji.exaqua.integration.jei;

import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.data.recipes.ModRecipeTypes;
import com.rutaji.exaqua.data.recipes.SqueezerRecipie;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;
import java.util.stream.Collectors;

@JeiPlugin
public class ExAquaJei implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ExAqua.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new SqueezerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().world).getRecipeManager();
        registration.addRecipes(rm.getRecipesForType(ModRecipeTypes.SQUEEZER_RECIPE).stream()
                        .filter(r -> r instanceof SqueezerRecipie).collect(Collectors.toList()),
                SqueezerRecipeCategory.UID);
    }
}
