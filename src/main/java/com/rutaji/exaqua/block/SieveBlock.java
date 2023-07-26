package com.rutaji.exaqua.block;

import com.rutaji.exaqua.tileentity.ModTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class SieveBlock extends Block {
    public SieveBlock(Properties properties) {
        super(properties);
    }


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.SIEVERTILE.get().create();
    }

}
