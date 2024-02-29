package com.rutaji.exaqua.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class FrogiumBlock extends Block {
    public FrogiumBlock(Properties properties) {
        super(properties);
    }
    public @NotNull ActionResultType onBlockActivated(@NotNull BlockState state, @NotNull World worldIn, @NotNull BlockPos pos, PlayerEntity player, @NotNull Hand handIn, @NotNull BlockRayTraceResult hit)
    {
        player.addPotionEffect(new EffectInstance(Effects.DOLPHINS_GRACE, 15*20, 0));
        player.addPotionEffect(new EffectInstance(Effects.CONDUIT_POWER, 70*20, 0));
        return ActionResultType.SUCCESS;
    }
}
