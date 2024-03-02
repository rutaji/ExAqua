package com.rutaji.exaqua.item;

import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.data.recipes.HandSieveRecipie;
import com.rutaji.exaqua.data.recipes.InventorySieve;
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
import org.jetbrains.annotations.NotNull;

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
    public static final String HOLDING_WATER = "HoldingWater"; //tag int
    public static final String FLUID_INSIDE = "FluidInside"; //tag string
    //endregion
    private final ResourceLocation customIcon = new ResourceLocation("exaqua", "extra/handsievewater");
    private static final int UsesFromBucket = 20;

    public void onCreated(ItemStack stack, @NotNull World worldIn, @NotNull PlayerEntity playerIn) {
        stack.getOrCreateTag().putInt(HOLDING_WATER,0);
        stack.getOrCreateTag().putString(FLUID_INSIDE,"");
    }
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn){
        if(stack.getOrCreateTag().getInt(HOLDING_WATER) != 0) {
            tooltip.add(new StringTextComponent(stack.getOrCreateTag().getString(FLUID_INSIDE) + ": " + stack.getOrCreateTag().getInt(HOLDING_WATER)));
        }
        else{
            tooltip.add(new StringTextComponent("empty"));
        }
        super.addInformation(stack,worldIn,tooltip,flagIn);
    }

    @Override
    public @NotNull ActionResult<ItemStack> onItemRightClick(@NotNull World worldIn, PlayerEntity playerIn, @NotNull Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);

            if(itemstack.getOrCreateTag().getInt(HOLDING_WATER) == 0) //pokud prázdný
            {
                return PickUpSourceBlock(worldIn,playerIn,itemstack);
            }
            else
            {

                InventorySieve inv = new InventorySieve();
                inv.setFluidStack(new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(itemstack.getOrCreateTag().getString(FLUID_INSIDE))),5));

                Optional<HandSieveRecipie> recipe = worldIn.getRecipeManager()
                        .getRecipe(ModRecipeTypes.HANDSIEVE_RECIPE, inv, worldIn);
                if( !recipe.isPresent())
                {
                    EmptyIt(itemstack);
                    System.out.println("no recipie");
                    return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
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


        return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
    }
    private static void EmptyIt(ItemStack itemStack){
        itemStack.getOrCreateTag().putInt(HOLDING_WATER,0);
        itemStack.getOrCreateTag().putString(FLUID_INSIDE,"");
    }
    private static void LowerWater(ItemStack itemStack){
        LowerWater(itemStack,1);
    }
    private static void LowerWater(ItemStack itemStack,int HowMuch)
    {
        itemStack.getOrCreateTag().putInt(HOLDING_WATER, itemStack.getOrCreateTag().getInt(HOLDING_WATER) -HowMuch);
        if(itemStack.getOrCreateTag().getInt(HOLDING_WATER) <= 0)
        {
            itemStack.getOrCreateTag().putString(FLUID_INSIDE,"");
        }
    }
    private ActionResult<ItemStack> PickUpSourceBlock(World worldIn, PlayerEntity playerIn,ItemStack itemstack)
    {
        BlockRayTraceResult raytraceresult = rayTrace(worldIn, playerIn, RayTraceContext.FluidMode.SOURCE_ONLY);
        if (raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
            return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
        }
        BlockPos blockpos = raytraceresult.getPos();
        Direction direction = raytraceresult.getFace();
        BlockPos blockpos1 = blockpos.offset(direction);
        if (worldIn.isBlockModifiable(playerIn, blockpos) && playerIn.canPlayerEdit(blockpos1, direction, itemstack)) {
            BlockState blockstate1 = worldIn.getBlockState(blockpos);
            if (blockstate1.getBlock() instanceof IBucketPickupHandler) {
                Fluid fluid = ((IBucketPickupHandler) blockstate1.getBlock()).pickupFluid(worldIn, blockpos, blockstate1);
                if (fluid != Fluids.EMPTY) {
                    itemstack.getOrCreateTag().putInt(HOLDING_WATER,UsesFromBucket);
                    itemstack.getOrCreateTag().putString(FLUID_INSIDE, fluid.getRegistryName().toString());
                }
            }
        }
        return new ActionResult<>(ActionResultType.SUCCESS, itemstack);

    }


}
