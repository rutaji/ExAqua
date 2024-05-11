package com.rutaji.exaqua.integration.jei;

import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.data.recipes.*;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Used for compatibility with jei mod. Jei adds UI for viewing recipes.
 * Registery for all jei recipe categories.
 */
@JeiPlugin
public class ExAquaJei implements IModPlugin {
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(ExAqua.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new SqueezerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(
                new SieveRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(
                new HandSieveRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(
                new CauldronRecipeCategory(registration.getJeiHelpers().getGuiHelper()));


    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().world).getRecipeManager();

        registration.addRecipes(rm.getRecipesForType(ModRecipeTypes.SQUEEZER_RECIPE).stream()
                        .filter(r -> r instanceof SqueezerRecipe).collect(Collectors.toList()),
                SqueezerRecipeCategory.UID);

        registration.addRecipes(rm.getRecipesForType(ModRecipeTypes.SIEVE_RECIPE).stream()
                        .filter(r -> r instanceof SieveRecipe).collect(Collectors.toList()),
                SieveRecipeCategory.UID);
        registration.addRecipes(rm.getRecipesForType(ModRecipeTypes.HANDSIEVE_RECIPE).stream()
                        .filter(r -> r instanceof HandSieveRecipe).collect(Collectors.toList()),
                HandSieveRecipeCategory.UID);
        registration.addRecipes(rm.getRecipesForType(ModRecipeTypes.CAULDRON_RECIPE).stream()
                        .filter(r -> r instanceof CauldronRecipe).collect(Collectors.toList()),
                CauldronRecipeCategory.UID);
    }
}
