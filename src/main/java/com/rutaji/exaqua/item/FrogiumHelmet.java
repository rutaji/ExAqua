package com.rutaji.exaqua.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class FrogiumHelmet extends ArmorItem {
    public FrogiumHelmet(IArmorMaterial materialIn, EquipmentSlotType slot, Item.Properties builderIn) {
        super(materialIn, slot, builderIn);
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player)
    {
        player.addPotionEffect(new EffectInstance(Effects.NIGHT_VISION, 18*20, 0));
        player.addPotionEffect(new EffectInstance(Effects.WEAKNESS, 18*20,0 ));
    }
}