package com.rutaji.exaqua.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import java.util.stream.Stream;

public class CraftingCauldron extends Block {
    public CraftingCauldron(Properties properties) {
        super(properties);
    }
    public final VoxelShape SHAPE=  Stream.of(
            Block.makeCuboidShape(0, 1, 12, 2, 10, 14),
            Block.makeCuboidShape(2, 1, 14, 4, 10, 16),
            Block.makeCuboidShape(14, 1, 12, 16, 10, 14),
            Block.makeCuboidShape(12, 1, 14, 14, 10, 16),
            Block.makeCuboidShape(2, 1, 0, 4, 10, 2),
            Block.makeCuboidShape(0, 1, 2, 2, 10, 4),
            Block.makeCuboidShape(12, 1, 0, 14, 10, 2),
            Block.makeCuboidShape(14, 1, 2, 16, 10, 4),
            Block.makeCuboidShape(2, 1, 2, 3, 16, 14),
            Block.makeCuboidShape(13, 1, 2, 14, 16, 14),
            Block.makeCuboidShape(2, 1, 2, 14, 16, 3),
            Block.makeCuboidShape(2, 1, 13, 14, 16, 14),
            Block.makeCuboidShape(0, 0, 0, 16, 1, 16)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();
    public VoxelShape getShape(BlockState blockState, IBlockReader worlIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }
}
