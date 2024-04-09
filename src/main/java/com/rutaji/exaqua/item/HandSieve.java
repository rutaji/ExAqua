package com.rutaji.exaqua.item;

import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.config.ServerModConfig;
import com.rutaji.exaqua.data.recipes.HandSieveRecipie;
import com.rutaji.exaqua.data.recipes.InventorySieve;
import com.rutaji.exaqua.data.recipes.ModRecipeTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class HandSieve extends Item {
    //region Constructor
    public HandSieve(Properties properties) {
        super(properties);
    }

    //endregion
    //region Tags Constants
    public static final String HOLDING_WATER = "HoldingWater"; //tag int
    public static final String FLUID_INSIDE = "FluidInside"; //tag string
    //endregion
    private int GetUsesFromBucket(){return ServerModConfig.HandSieveBucketUse.get();}

    public void onCreated(ItemStack stack, @NotNull World worldIn, @NotNull PlayerEntity playerIn) {
        stack.getOrCreateTag().putInt(HOLDING_WATER, 0);
        stack.getOrCreateTag().putString(FLUID_INSIDE, "");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        if (stack.getOrCreateTag().getInt(HOLDING_WATER) != 0) {
            tooltip.add(new StringTextComponent(stack.getOrCreateTag().getString(FLUID_INSIDE) + ": " + stack.getOrCreateTag().getInt(HOLDING_WATER)));
        } else {
            tooltip.add(new StringTextComponent("empty"));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public @NotNull ActionResult<ItemStack> onItemRightClick(@NotNull World worldIn, PlayerEntity playerIn, @NotNull Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (IsEmpty(itemstack)) {
            return PickUpSourceBlock(worldIn, playerIn, itemstack);
        }
        else if (!worldIn.isRemote) {
            InventorySieve inv = new InventorySieve();
            Fluid fluidInside = GetFluidInside(itemstack);
            if (fluidInside == null) {
                EmptyIt(itemstack);
                new ActionResult<>(ActionResultType.SUCCESS, itemstack);
            }
            inv.setFluidStack(new FluidStack(fluidInside, 5));
            Optional<HandSieveRecipie> recipe = worldIn.getRecipeManager()
                    .getRecipe(ModRecipeTypes.HANDSIEVE_RECIPE, inv, worldIn);
            if (!recipe.isPresent()) {
                EmptyIt(itemstack);
                playerIn.sendMessage(new StringTextComponent("no recipie for this fluid"), playerIn.getUniqueID());
                return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
            }
            HandSieveRecipie foundRecipie = recipe.get();
            if (foundRecipie.IsSucces()) {
                playerIn.entityDropItem(foundRecipie.GetRandomItemStack());
            }
            Vector3d lookVector = playerIn.getLookVec();
            ((ServerWorld) worldIn).spawnParticle(ParticleTypes.RAIN, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(),
                        4, lookVector.x , 1.5d, lookVector.z , 0.1d);
            LowerWater(itemstack);

        }
        return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
    }

    //region stored fluid methods
    public static void EmptyIt(@NotNull ItemStack itemStack) {
        itemStack.getOrCreateTag().putInt(HOLDING_WATER, 0);
        itemStack.getOrCreateTag().putString(FLUID_INSIDE, "");
    }

    public static boolean IsEmpty(@NotNull ItemStack itemStack) {
        return itemStack.getOrCreateTag().getInt(HOLDING_WATER) == 0;
    }

    public static void LowerWater(@NotNull ItemStack itemStack) {
        LowerWater(itemStack, 1);
    }

    public static void LowerWater(@NotNull ItemStack itemStack, int HowMuch) {
        itemStack.getOrCreateTag().putInt(HOLDING_WATER, itemStack.getOrCreateTag().getInt(HOLDING_WATER) - HowMuch);
        if (itemStack.getOrCreateTag().getInt(HOLDING_WATER) <= 0) {
            itemStack.getOrCreateTag().putString(FLUID_INSIDE, "");
        }
    }
    public static Fluid GetFluidInside(@NotNull ItemStack itemStack)
    {
        return ForgeRegistries.FLUIDS.getValue(new ResourceLocation(itemStack.getOrCreateTag().getString(FLUID_INSIDE)));
    }

    //endregion
    private ActionResult<ItemStack> PickUpSourceBlock(World worldIn, PlayerEntity playerIn, ItemStack itemstack) {
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
                    SoundEvent soundevent = fluid.getAttributes().getFillSound();
                    playerIn.playSound(soundevent, 1.0F, 1.0F);
                    itemstack.getOrCreateTag().putInt(HOLDING_WATER, GetUsesFromBucket());
                    itemstack.getOrCreateTag().putString(FLUID_INSIDE, fluid.getRegistryName().toString());
                }
            }
        }
        return new ActionResult<>(ActionResultType.SUCCESS, itemstack);

    }


}
