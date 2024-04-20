package com.rutaji.exaqua.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import org.jetbrains.annotations.NotNull;

public class FrogiumOre extends Block {
    public FrogiumOre(Properties properties) {
        super(properties);
    }

    /**
     * Called on block destruction. Creates water on it's position and calls block update.
     */
    public void onPlayerDestroy(IWorld worldIn, @NotNull BlockPos pos, @NotNull BlockState state)
    {
        worldIn.setBlockState(pos, Blocks.WATER.getDefaultState(),1);
    }


}

