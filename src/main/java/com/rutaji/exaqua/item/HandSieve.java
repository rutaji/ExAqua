package com.rutaji.exaqua.item;

import com.rutaji.exaqua.config.ServerModConfig;
import com.rutaji.exaqua.data.recipes.HandSieveRecipe;
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


public class HandSieve extends Item {
    //region Constructor
    public HandSieve(Properties properties) {
        super(properties);
    }

    //endregion
    //region Tags Constants
    /**
     * Key for nbt data,that stores amount of fluid as a int.
     */
    public static final String HOLDING_WATER = "HoldingWater"; //tag int
    /**
     * Key for nbt data,that stores fluid as a string.
     */
    public static final String FLUID_INSIDE = "FluidInside"; //tag string
    //endregion
    private int GetUsesFromBucket(){return ServerModConfig.HandSieveBucketUse.get();}

    /**
     *Called on item creation. Creates nbt tags for the new item.
     */
    public void onCreated(ItemStack stack, @NotNull World worldIn, @NotNull PlayerEntity playerIn) {
        stack.getOrCreateTag().putInt(HOLDING_WATER, 0);
        stack.getOrCreateTag().putString(FLUID_INSIDE, "");
    }

    /**
     * Returns description of the item. Adds to the description information about fluid, that this item is holding.
     */
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, @NotNull List<ITextComponent> tooltip, @NotNull ITooltipFlag flagIn) {
        if (stack.getOrCreateTag().getInt(HOLDING_WATER) != 0) {
            tooltip.add(new StringTextComponent(stack.getOrCreateTag().getString(FLUID_INSIDE) + ": " + stack.getOrCreateTag().getInt(HOLDING_WATER)));
        } else {
            tooltip.add(new StringTextComponent("empty"));
        }
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    /**
     * Called when player uses the item. If it's empty it tries to {@link HandSieve#PickUpSourceBlock pick up fluid source block }.
     * If it contains fluid, it searches recipie matching the fluid in {@link HandSieveRecipe recipes of type exaqua:handsieve }.
     * If it finds the recipie it {@link HandSieve#LowerWater lowers fluid amount by 1} and drops item from recipie (if random rool succeed).
     * If it doesn't find any recipie for the fluid, it {@link HandSieve#EmptyIt empties itself} and returns fail, otherwise it returns succes
     * @return Fail if it doesn't find any {@link HandSieveRecipe recipie} for the fluid, othervise succes.
     */
    @Override
    public @NotNull ActionResult<ItemStack> onItemRightClick(@NotNull World worldIn, PlayerEntity playerIn, @NotNull Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (IsEmpty(itemstack)) {
            return PickUpSourceBlock(worldIn, playerIn, itemstack);
        }
        else if (!worldIn.isRemote) {
            InventorySieve inv = new InventorySieve();
            Fluid fluidInside = GetFluidInside(itemstack);
            if (fluidInside == Fluids.EMPTY) {
                EmptyIt(itemstack);
                new ActionResult<>(ActionResultType.SUCCESS, itemstack);
            }
            inv.setFluidStack(new FluidStack(fluidInside, 5));
            Optional<HandSieveRecipe> recipe = worldIn.getRecipeManager()
                    .getRecipe(ModRecipeTypes.HANDSIEVE_RECIPE, inv, worldIn);
            if (!recipe.isPresent()) {
                EmptyIt(itemstack);
                playerIn.sendMessage(new StringTextComponent("no recipie for this fluid"), playerIn.getUniqueID());
                return new ActionResult<>(ActionResultType.FAIL, itemstack);
            }
            HandSieveRecipe foundRecipie = recipe.get();
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

    /**
     * Saves empty fluid into nbt of a given {@link HandSieve hand sieve} item.
     */
    public static void EmptyIt(@NotNull ItemStack itemStack) {
        itemStack.getOrCreateTag().putInt(HOLDING_WATER, 0);
        itemStack.getOrCreateTag().putString(FLUID_INSIDE, "");
    }
    /**
     * @return True if given {@link HandSieve hand sieve} item is empty.
     */

    public static boolean IsEmpty(@NotNull ItemStack itemStack) {
        return itemStack.getOrCreateTag().getInt(HOLDING_WATER) == 0;
    }

    /**
     * Lowers the amouth of fluid in given {@link HandSieve hand sieve} item by 1.
     */

    public static void LowerWater(@NotNull ItemStack itemStack) {
        LowerWater(itemStack, 1);
    }
    /**
     * Lowers the amouth of fluid in given {@link HandSieve hand sieve} item by given number.
     */

    public static void LowerWater(@NotNull ItemStack itemStack, int HowMuch) {
        itemStack.getOrCreateTag().putInt(HOLDING_WATER, itemStack.getOrCreateTag().getInt(HOLDING_WATER) - HowMuch);
        if (itemStack.getOrCreateTag().getInt(HOLDING_WATER) <= 0) {
            itemStack.getOrCreateTag().putString(FLUID_INSIDE, "");
        }
    }

    /**
     * Returns fluid inside {@link HandSieve hand sieve} item. If the fluid is no longer registered.
     */
    public static @NotNull Fluid GetFluidInside(@NotNull ItemStack itemStack)
    {
        Fluid ToReturn = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(itemStack.getOrCreateTag().getString(FLUID_INSIDE)));
        if (ToReturn == null)
        {
            EmptyIt(itemStack);
            return Fluids.EMPTY;
        }
        return ToReturn;
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
