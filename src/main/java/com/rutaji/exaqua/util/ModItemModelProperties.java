package com.rutaji.exaqua.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;

public class ModItemModelProperties {
    public static void makeHandSieve(Item item) {
        ItemModelsProperties.registerProperty(item, new ResourceLocation("data"), (itemStack, clientworld, livingEntity) -> {
            return itemStack.getOrCreateTag().getString("FluidInside") == "water" ? 1F : 0F ;
        });
    }
}
