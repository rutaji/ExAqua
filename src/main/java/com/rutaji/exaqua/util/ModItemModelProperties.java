package com.rutaji.exaqua.util;

import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.item.HandSieve;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;

/**
 * Class to register item model properties on client.
 * Used only on {@link HandSieve hand sieve item} to change model accordning to color of fluid inside.
 */
public class ModItemModelProperties {

    /**
     * registers color property for {@link HandSieve hand sieve item}. Used to change texture acording to color of the fluid in hand sieve. Color is calculated in {@link ColorsToFloat ColorsToFloat}.
     */
    public static void makeHandSieve(Item item) {
        ItemModelsProperties.registerProperty(item, new ResourceLocation("color"), (itemStack, clientworld, livingEntity) -> ColorsToFloat.Get(itemStack.getOrCreateTag().getString(HandSieve.FLUID_INSIDE)));
    }
}
