package com.rutaji.exaqua.data.recipes;

import com.google.gson.JsonObject;
import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.block.ModBlocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class SqueezerRecipie implements ISqueezerRecipie {

    private final ResourceLocation ID;
    private final FluidStack OUTPUT;
    private final Ingredient INPUT;

    public SqueezerRecipie(ResourceLocation id, FluidStack output, Ingredient recipeItems) {
        this.ID = id;
        this.OUTPUT = output;
        this.INPUT = recipeItems;

    }
    @Override
    public boolean matches(IInventory inv, @NotNull World worldIn) {
        return INPUT.test(inv.getStackInSlot(0));
    }
    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return NonNullList.withSize(1,INPUT);
    }

    @Override
    public @NotNull ItemStack getCraftingResult(@NotNull IInventory inv) {
        return ItemStack.EMPTY;
    }
    @Override //Does not provide anything because this recipie doesn´t produce items
    public @NotNull ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }
    public FluidStack getRealOutput(){
        return OUTPUT.copy();
    }

    public @NotNull ItemStack getIcon() {
        return new ItemStack(ModBlocks.SQUEEZER.get());
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }

    @Override
    public @NotNull IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.SQUEEZER_SERIALIZER.get();
    }
    public static class SqueezerRecipeType implements IRecipeType<SqueezerRecipie> {
        @Override
        public String toString() {
            return SqueezerRecipie.TYPE_ID.toString();
        }
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<SqueezerRecipie> {

        @Override
        public @NotNull SqueezerRecipie read(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            JsonObject output1 = JSONUtils.getJsonObject(json, "output");
            String OutputFluid = output1.get("fluid").getAsString();
            int OutputAmount = output1.get("amount").getAsInt();

            Fluid f = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(OutputFluid));
            if(f == null){
                ExAqua.LOGGER.error("error in" + recipeId.getPath() + "fluid not found:" + OutputFluid);
                f= Fluids.EMPTY;}
            FluidStack output = new FluidStack( f,OutputAmount);

            JsonObject j = JSONUtils.getJsonObject(json, "input");
            Ingredient input = Ingredient.deserialize(j);

            return new SqueezerRecipie(recipeId, output, input);
        }

        @Nullable
        @Override
        public SqueezerRecipie read(@NotNull ResourceLocation recipeId, @NotNull PacketBuffer buffer) {
            Ingredient input = Ingredient.read(buffer);

            String OutputFluid = buffer.readString();
            Fluid f =ForgeRegistries.FLUIDS.getValue(new ResourceLocation(OutputFluid));
            if(f == null){
                ExAqua.LOGGER.error("error in" + recipeId.getPath() + "fluid not found:" + OutputFluid);
                f= Fluids.EMPTY;}
            FluidStack output = new FluidStack(f,buffer.readInt());
            return new SqueezerRecipie(recipeId, output, input);
        }

        @Override
        public void write(@NotNull PacketBuffer buffer, SqueezerRecipie recipe) {
            recipe.INPUT.write(buffer);

            buffer.writeString(ForgeRegistries.FLUIDS.getKey(recipe.OUTPUT.getFluid()).toString());
            buffer.writeInt(recipe.OUTPUT.getAmount());

        }
    }
}
