package com.rutaji.exaqua.item.logic;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.loot.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.extensions.IForgeFluid;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.io.Console;
import java.util.List;

public class HandSieve extends Item {

    public HandSieve(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    private int HoldingWater = 0;
    private String FluidInside = "";

    private ResourceLocation customIcon = new ResourceLocation("exaqua", "extra/handsievewater");


    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
        if(HoldingWater != 0) {
            tooltip.add(new StringTextComponent(FluidInside + ": " + HoldingWater));
        }
        else{
            tooltip.add(new StringTextComponent("empty"));
        }
        super.addInformation(stack,worldIn,tooltip,flagIn);
    }
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if(HoldingWater == 0)
        {
            RayTraceResult raytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
            if (raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
                return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemstack);
            }
            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) raytraceresult;
            BlockPos blockpos = blockraytraceresult.getPos();
            Direction direction = blockraytraceresult.getFace();
            BlockPos blockpos1 = blockpos.offset(direction);
            if (worldIn.isBlockModifiable(playerIn, blockpos) && playerIn.canPlayerEdit(blockpos1, direction, itemstack)) {
                BlockState blockstate1 = worldIn.getBlockState(blockpos);
                if (blockstate1.getBlock() instanceof IBucketPickupHandler) {
                    Fluid fluid = ((IBucketPickupHandler) blockstate1.getBlock()).pickupFluid(worldIn, blockpos, blockstate1);
                    if (fluid != Fluids.EMPTY) {
                        if (fluid == Fluids.WATER) {
                            HoldingWater = 20;
                            FluidInside = "water";

                        }
                    }
                }
            }
        }
        else{
            if (!worldIn.isRemote()) {
                LootTable lootTable = worldIn.getServer().getLootTableManager().getLootTableFromLocation(new ResourceLocation("exaqua", "extra/handsieve"));
                LootContext.Builder lootContextBuilder = new LootContext.Builder((ServerWorld) worldIn)
                        .withRandom(worldIn.rand);
                LootContext lootContext = lootContextBuilder.build(LootParameterSets.EMPTY);
                List<ItemStack> lootItems = lootTable.generate(lootContext);
                HoldingWater--;
                if(HoldingWater == 0){

                }
                for (ItemStack I: lootItems) {
                    playerIn.entityDropItem(I);
                }
            }

        }

        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemstack);
    }


}
