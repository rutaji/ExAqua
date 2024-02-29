package com.rutaji.exaqua.util;

import com.rutaji.exaqua.ExAqua;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;

public class ModItemModelProperties {
    public static void makeHandSieve(Item item) {
        ItemModelsProperties.registerProperty(item, new ResourceLocation(ExAqua.MOD_ID,"color"), (itemStack, clientworld, livingEntity) -> ColorsToFloat.Get(itemStack.getOrCreateTag().getString("FluidInside")));
    }
}
