package com.rutaji.exaqua.block;

import com.rutaji.exaqua.tileentity.SieveTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class FrogiumBlock extends Block {
    public FrogiumBlock(Properties properties) {
        super(properties);
    }
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
    {
        player.addPotionEffect(new EffectInstance(Effects.DOLPHINS_GRACE, 15*20, 0));
        player.addPotionEffect(new EffectInstance(Effects.CONDUIT_POWER, 70*20, 0));
        return ActionResultType.SUCCESS;
    }
}
