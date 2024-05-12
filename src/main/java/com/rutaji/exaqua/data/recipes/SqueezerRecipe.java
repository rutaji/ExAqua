package com.rutaji.exaqua.data.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
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
import java.util.Objects;

/**
 * Handles recipe of type exaqua:squeezer.
 */
public class SqueezerRecipe implements ISqueezerRecipe {

    private final ResourceLocation ID;
    private final FluidStack OUTPUT;
    private final Ingredient INPUT;

    public SqueezerRecipe(ResourceLocation id, FluidStack output, Ingredient recipeItems) {
        this.ID = id;
        this.OUTPUT = output;
        this.INPUT = recipeItems;

    }
    /** Returns True if inventory contains Ingridient for recipe.Checks only slot 0.
     * @return True if inventory contains Ingridient for recipe.
     */
    @Override
    public boolean matches(IInventory inv, @NotNull World worldIn) {
        return INPUT.test(inv.getStackInSlot(0));
    }

    /**
     * @return Ingridients required as input.
     */
    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return NonNullList.withSize(1,INPUT);
    }

    /**
     * @return empty, because output is a fluid.
     */
    @Override
    public @NotNull ItemStack getCraftingResult(@NotNull IInventory inv) {
        return ItemStack.EMPTY;
    }
    /**
     * @return empty, because output is a fluid.
     */
    @Override //Does not provide anything because this recipie doesn´t produce items
    public @NotNull ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    /**
     * @return output of the recipe.
     */
    public FluidStack getRealOutput(){
        return OUTPUT.copy();
    }

    public @NotNull ItemStack getIcon() {
        return new ItemStack(ModBlocks.SQUEEZER.get());
    }

    /**
     * @return resource location of the recipe.
     */
    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }

    /**
     * @return serializer for this recipe type.
     * @see ModRecipeTypes
     */
    @Override
    public @NotNull IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.SQUEEZER_SERIALIZER.get();
    }
    public static class SqueezerRecipeType implements IRecipeType<SqueezerRecipe> {
        @Override
        public String toString() {
            return SqueezerRecipe.TYPE_ID.toString();
        }
    }

    /**
     * Class used to serialize and deserialize this recipe.
     */

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<SqueezerRecipe> {

        /**
         * Reads recipie from json.
         * @exception JsonSyntaxException if json syntax is wrong.
         * @param recipeId resource location of the recipe.
         * @param json json for cenversion.
         * @return  Loaded recipe.
         */
        @Override
        public @NotNull SqueezerRecipe read(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            JsonObject output1 = JSONUtils.getJsonObject(json, "output");
            String OutputFluid = output1.get("fluid").getAsString();
            int OutputAmount = output1.get("amount").getAsInt();

            Fluid f = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(OutputFluid));
            if(f == Fluids.EMPTY && !Objects.equals(OutputFluid, "empty")){
                ExAqua.LOGGER.error("error in" + recipeId.getPath() + "fluid not found:" + OutputFluid);
                f= Fluids.EMPTY;}
            FluidStack output = new FluidStack( f,OutputAmount);

            JsonObject j = JSONUtils.getJsonObject(json, "input");
            Ingredient input = Ingredient.deserialize(j);

            return new SqueezerRecipe(recipeId, output, input);
        }
        /**
         * Reads recipe from a packetbuffer.
         */
        @Nullable
        @Override
        public SqueezerRecipe read(@NotNull ResourceLocation recipeId, @NotNull PacketBuffer buffer) {
            Ingredient input = Ingredient.read(buffer);

            String OutputFluid = buffer.readString();
            Fluid f =ForgeRegistries.FLUIDS.getValue(new ResourceLocation(OutputFluid));
            if(f == null){
                ExAqua.LOGGER.error("error in" + recipeId.getPath() + "fluid not found:" + OutputFluid);
                f= Fluids.EMPTY;}
            FluidStack output = new FluidStack(f,buffer.readInt());
            return new SqueezerRecipe(recipeId, output, input);
        }
        /**
         * Converts recipe into a packet buffer.
         */
        @Override
        public void write(@NotNull PacketBuffer buffer, SqueezerRecipe recipe) {
            recipe.INPUT.write(buffer);

            buffer.writeString(ForgeRegistries.FLUIDS.getKey(recipe.OUTPUT.getFluid()).toString());
            buffer.writeInt(recipe.OUTPUT.getAmount());

        }
    }
}
