package com.rutaji.exaqua.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;

public class FrogiumOre extends Block {
    public FrogiumOre(Properties properties) {
        super(properties);
    }
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
        worldIn.setBlockState(pos, Blocks.WATER.getDefaultState(),1);
    }


}

