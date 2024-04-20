package com.rutaji.exaqua.item.armor;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class FrogiumChestplate extends ArmorItem {
    public FrogiumChestplate(IArmorMaterial materialIn, EquipmentSlotType slot, Item.Properties builderIn) {
        super(materialIn, slot, builderIn);
    }

    /**
     * Called on every tick, if this armor is equipped.
     * Gives potion effect to a player.
     */
    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player)
    {
        player.addPotionEffect(new EffectInstance(Effects.SATURATION, 2, 0));
        player.addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, 52*20, 4));
    }
}