package com.rutaji.exaqua.data.recipes;

import com.rutaji.exaqua.ExAqua;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRecipeTypes {
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZER =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ExAqua.MOD_ID);
    public static final RegistryObject<SqueezerRecipie.Serializer> SQUEEZER_SERIALIZER
            = RECIPE_SERIALIZER.register("squeezer", SqueezerRecipie.Serializer::new);
    public static final RegistryObject<SieveRecipie.Serializer> SIEVE_SERIALIZER
            = RECIPE_SERIALIZER.register("sieve", SieveRecipie.Serializer::new);
    public static final RegistryObject<HandSieveRecipie.Serializer> HANDSIEVE_SERIALIZER
            = RECIPE_SERIALIZER.register("handsieve", HandSieveRecipie.Serializer::new);
    public static final RegistryObject<CauldronRecipie.Serializer> CAULDRON_SERIALIZER
            = RECIPE_SERIALIZER.register("cauldron", CauldronRecipie.Serializer::new);

    public static IRecipeType<SqueezerRecipie> SQUEEZER_RECIPE
            = new SqueezerRecipie.SqueezerRecipeType();
    public static IRecipeType<HandSieveRecipie> HANDSIEVE_RECIPE
            = new HandSieveRecipie.HandSieveRecipeType();
    public static IRecipeType<SieveRecipie> SIEVE_RECIPE
            = new SieveRecipie.SieveRecipeType();
    public static IRecipeType<CauldronRecipie> CAULDRON_RECIPE
            = new CauldronRecipie.CauldronRecipeType();
    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZER.register(eventBus);

        Registry.register(Registry.RECIPE_TYPE, SqueezerRecipie.TYPE_ID, SQUEEZER_RECIPE);
        Registry.register(Registry.RECIPE_TYPE, SieveRecipie.TYPE_ID, SIEVE_RECIPE);
        Registry.register(Registry.RECIPE_TYPE, HandSieveRecipie.TYPE_ID, HANDSIEVE_RECIPE);
        Registry.register(Registry.RECIPE_TYPE, CauldronRecipie.TYPE_ID, CAULDRON_RECIPE);
    }
}
