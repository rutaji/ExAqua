package com.rutaji.exaqua.util;

import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.item.HandSieve;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;

public class ModItemModelProperties {
    public static void makeHandSieve(Item item) {
        ItemModelsProperties.registerProperty(item, new ResourceLocation("color"), (itemStack, clientworld, livingEntity) -> {
            return itemStack.getOrCreateTag().getString("FluidInside").equals("minecraft:water") ? ColorsToFloat.Blue : 0F ;
        });
    }
}
