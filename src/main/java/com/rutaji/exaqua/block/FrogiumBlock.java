package com.rutaji.exaqua.block;

import com.rutaji.exaqua.tileentity.AutoSqueezerTileEntity;
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
    /**
     * Called when player interacts with a block. Gives player potion effects. If player is null does nothing.
     * @param state block state of interacted block.
     * @param worldIn world of interacted block.
     * @param pos position of interacted block.
     * @param player player which interacted with the block.
     * @param handIn hand of the player.
     * @param hit ray trace result.
     * @return succes if player ins't null, otherwise fail.
     */
    public @NotNull ActionResultType onBlockActivated(@NotNull BlockState state, @NotNull World worldIn, @NotNull BlockPos pos, PlayerEntity player, @NotNull Hand handIn, @NotNull BlockRayTraceResult hit)
    {
        if (player == null){return ActionResultType.FAIL;}
        player.addPotionEffect(new EffectInstance(Effects.DOLPHINS_GRACE, 15*20, 0));
        player.addPotionEffect(new EffectInstance(Effects.CONDUIT_POWER, 70*20, 0));
        return ActionResultType.SUCCESS;
    }
}
