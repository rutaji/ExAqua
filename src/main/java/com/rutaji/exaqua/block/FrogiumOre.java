package com.rutaji.exaqua.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class FrogiumOre extends Block {
    public FrogiumOre(Properties properties) {
        super(properties);
    }
    public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state)
    {
        worldIn.setBlockState(pos, Blocks.WATER.getDefaultState(),1);
    }


}

