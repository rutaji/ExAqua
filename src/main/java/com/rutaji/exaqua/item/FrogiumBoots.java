package com.rutaji.exaqua.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class FrogiumBoots extends ArmorItem {
    public FrogiumBoots(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builderIn) {
        super(materialIn, slot, builderIn);
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player)
    {
        player.addPotionEffect(new EffectInstance(Effects.LEVITATION, 20, 0));
        player.addPotionEffect(new EffectInstance(Effects.SLOWNESS, 13*20, 5));
    }
}
