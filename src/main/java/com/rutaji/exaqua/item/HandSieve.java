package com.rutaji.exaqua.item;

import com.rutaji.exaqua.data.recipes.HandSieveRecipie;
import com.rutaji.exaqua.data.recipes.InventoryWithFluids;
import com.rutaji.exaqua.data.recipes.ModRecipeTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class HandSieve extends Item {
    //region Constructor
    public HandSieve(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }
    //endregion
    //region Tags Constants
    public static final String HoldingWater = "HoldingWater"; //tag int
    public static final String FluidInside = "FluidInside"; //tag string
    //endregion
    private final ResourceLocation customIcon = new ResourceLocation("exaqua", "extra/handsievewater");
    private static final int UsesFromBucket = 20;

    public void onCreated(ItemStack stack, World worldIn, PlayerEntity playerIn) {
        stack.getOrCreateTag().putInt(HoldingWater,0);
        stack.getOrCreateTag().putString(FluidInside,"");
    }
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
        if(stack.getOrCreateTag().getInt(HoldingWater) != 0) {
            tooltip.add(new StringTextComponent(stack.getOrCreateTag().getString(FluidInside) + ": " + stack.getOrCreateTag().getInt(HoldingWater)));
        }
        else{
            tooltip.add(new StringTextComponent("empty"));
        }
        super.addInformation(stack,worldIn,tooltip,flagIn);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

            if(itemstack.getOrCreateTag().getInt(HoldingWater) == 0) //pokud prázdný
            {
                return PickUpSourceBlock(worldIn,playerIn,itemstack);//todo maybe move to server or client
            }
            else
            {

                InventoryWithFluids inv = new InventoryWithFluids();
                inv.setFluidStack(new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(itemstack.getOrCreateTag().getString(FluidInside))),5));

                Optional<HandSieveRecipie> recipe = worldIn.getRecipeManager()
                        .getRecipe(ModRecipeTypes.HANDSIEVE_RECIPE, inv, worldIn);
                if( !recipe.isPresent())
                {
                    EmptyIt(itemstack);
                    System.out.println("Není recept !!!");
                    return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemstack);
                }
                recipe.ifPresent(iRecipe -> {
                    if (iRecipe instanceof HandSieveRecipie) {
                        if(!worldIn.isRemote()){
                            if(iRecipe.IsSucces())
                            {
                                playerIn.entityDropItem(iRecipe.GetRandomItemStack());
                            }
                        }

                        LowerWater(itemstack);
                    }
                });

            }


        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemstack);
    }
    private static void EmptyIt(ItemStack itemStack){
        itemStack.getOrCreateTag().putInt(HoldingWater,0);
        itemStack.getOrCreateTag().putString(FluidInside ,"");
    }
    private static void LowerWater(ItemStack itemStack){
        LowerWater(itemStack,1);
    }
    private static void LowerWater(ItemStack itemStack,int HowMuch)
    {
        itemStack.getOrCreateTag().putInt(HoldingWater, itemStack.getOrCreateTag().getInt(HoldingWater) -1);
        if(itemStack.getOrCreateTag().getInt(HoldingWater) == 0)
        {
            itemStack.getOrCreateTag().putString(FluidInside ,"");
        }
    }
    private ActionResult<ItemStack> PickUpSourceBlock(World worldIn, PlayerEntity playerIn,ItemStack itemstack)
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
                    itemstack.getOrCreateTag().putInt(HoldingWater,UsesFromBucket);
                    itemstack.getOrCreateTag().putString(FluidInside, fluid.getRegistryName().toString());

                }
            }
        }
        return new ActionResult<ItemStack>(ActionResultType.SUCCESS, itemstack);

    }


}
