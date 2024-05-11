package com.rutaji.exaqua.data.recipes;

import com.rutaji.exaqua.ExAqua;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
/**
 * Registry for all recipe types and their serializers from this mod.
 */
public class ModRecipeTypes {
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZER =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ExAqua.MOD_ID);
    public static final RegistryObject<SqueezerRecipe.Serializer> SQUEEZER_SERIALIZER
            = RECIPE_SERIALIZER.register("squeezer", SqueezerRecipe.Serializer::new);
    public static final RegistryObject<SieveRecipe.Serializer> SIEVE_SERIALIZER
            = RECIPE_SERIALIZER.register("sieve", SieveRecipe.Serializer::new);
    public static final RegistryObject<HandSieveRecipe.Serializer> HANDSIEVE_SERIALIZER
            = RECIPE_SERIALIZER.register("handsieve", HandSieveRecipe.Serializer::new);
    public static final RegistryObject<CauldronRecipe.Serializer> CAULDRON_SERIALIZER
            = RECIPE_SERIALIZER.register("cauldron", CauldronRecipe.Serializer::new);

    public static IRecipeType<SqueezerRecipe> SQUEEZER_RECIPE
            = new SqueezerRecipe.SqueezerRecipeType();
    public static IRecipeType<HandSieveRecipe> HANDSIEVE_RECIPE
            = new HandSieveRecipe.HandSieveRecipeType();
    public static IRecipeType<SieveRecipe> SIEVE_RECIPE
            = new SieveRecipe.SieveRecipeType();
    public static IRecipeType<CauldronRecipe> CAULDRON_RECIPE
            = new CauldronRecipe.CauldronRecipeType();
    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZER.register(eventBus);

        Registry.register(Registry.RECIPE_TYPE, SqueezerRecipe.TYPE_ID, SQUEEZER_RECIPE);
        Registry.register(Registry.RECIPE_TYPE, SieveRecipe.TYPE_ID, SIEVE_RECIPE);
        Registry.register(Registry.RECIPE_TYPE, HandSieveRecipe.TYPE_ID, HANDSIEVE_RECIPE);
        Registry.register(Registry.RECIPE_TYPE, CauldronRecipe.TYPE_ID, CAULDRON_RECIPE);
    }
}
