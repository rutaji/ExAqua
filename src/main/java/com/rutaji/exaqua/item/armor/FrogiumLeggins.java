package com.rutaji.exaqua.item.armor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class FrogiumLeggins extends ArmorItem {
    public FrogiumLeggins(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builderIn) {
        super(materialIn, slot, builderIn);
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player)
    {
        player.addPotionEffect(new EffectInstance(Effects.SPEED, 20, 1));
        player.addPotionEffect(new EffectInstance(Effects.HUNGER, 7*20, 8));
    }
}