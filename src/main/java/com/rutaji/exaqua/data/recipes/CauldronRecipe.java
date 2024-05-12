package com.rutaji.exaqua.data.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.others.CauldronTemperature;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Handles recipe of type exaqua:cauldron.
 */
public class CauldronRecipe implements ICauldronRecipe {
    //region Constructor
    public CauldronRecipe(@NotNull ResourceLocation id, @NotNull Fluid input, @NotNull ItemStack inputItem, @NotNull Fluid output, @NotNull ItemStack outputitem, @NotNull CauldronTemperature temp, int amount_in, int amount_out) {
        this.ID = id;
        this.INPUT_FLUID = input;
        this.OUTPUT_FLUID = output;
        this.INPUT_ITEM = inputItem;
        this.OUTPUT_ITEM = outputitem;
        this.TEMP = temp;
        this.AMOUNT_INPUT = amount_in;
        this.AMOUNT_OUTPUT = amount_out;

    }
    //endregion
    @NotNull private final ResourceLocation ID;
    @NotNull public final Fluid INPUT_FLUID;
    @NotNull public final Fluid OUTPUT_FLUID;
    @NotNull public final ItemStack INPUT_ITEM;
    @NotNull public final ItemStack OUTPUT_ITEM;
    @NotNull public final CauldronTemperature TEMP;
    public final int AMOUNT_INPUT;

    public final int AMOUNT_OUTPUT;


    /** Returns True if inventory contains all Ingridients for recipe.
     * @param inv inventory should be instace of InventoryCauldron, othervise always return False.
     * @return True if inventory contains all Ingridients for recipe.
     */
    @Override
    public boolean matches(@NotNull IInventory inv, @NotNull World worldIn) {
        if(inv instanceof InventoryCauldron)
        {
            InventoryCauldron cauldron = (InventoryCauldron) inv;
            return ((cauldron.getTemp() == TEMP) &&
                    (INPUT_FLUID == Fluids.EMPTY || (cauldron.getFluid() == INPUT_FLUID && cauldron.amount >= AMOUNT_INPUT) ) &&
                    (INPUT_ITEM == ItemStack.EMPTY || (INPUT_ITEM.isItemEqual(cauldron.getStackInSlot(0))) && cauldron.getStackInSlot(0).getCount() >= INPUT_ITEM.getCount()));
        }
        return false;
    }
    //region Getters
    /**
     * @return item output of this recipe.
     */
    @Override
    public @NotNull ItemStack getCraftingResult(@NotNull IInventory inv) {
        return OUTPUT_ITEM;
    }
    /**
     * @return item output of this recipe.
     */
    @Override
    public @NotNull ItemStack getRecipeOutput()
    {
     return OUTPUT_ITEM;
    }
    /**
     * @return fluid result of this recipe.
     */
    public @NotNull Fluid getRecipeOutputFluid()
    {
        return OUTPUT_FLUID;
    }

    /**
     * @return resource location of the recipe.
     */

    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }
    //endregion

    /**
     * @return true if recipe has fluid output and item output.
     */
    public boolean Has2Outputs() {
        return OUTPUT_FLUID != Fluids.EMPTY && OUTPUT_ITEM != ItemStack.EMPTY;
    }

    //region registration

    /**
     * @return serializer for this recipe type.
     */
    @Override
    public @NotNull IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CAULDRON_SERIALIZER.get();
    }

    public static class CauldronRecipeType implements IRecipeType<CauldronRecipe> {
        @Override
        public String toString() {
            return CauldronRecipe.TYPE_ID.toString();
        }
    }
    //endregion
    //region Serializer
    /**
     * Class used to serialize and deserialize this recipe.
     */
    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<CauldronRecipe> {

        /**
         * Reads recipe from json.
         * @exception JsonSyntaxException if json syntax is wrong
         * @param recipeId resource location of the recipe
         * @param json json for cenversion
         * @return Loaded recipe
         */
        @Override
        public @NotNull CauldronRecipe read(@NotNull ResourceLocation recipeId, JsonObject json) {

            Fluid InputF = Fluids.EMPTY;
            ItemStack InputI = ItemStack.EMPTY;
            if(json.has("input_fluid"))
            {
                InputF = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(json.get("input_fluid").getAsString()));
                if (InputF == Fluids.EMPTY && !Objects.equals(json.get("input_fluid").getAsString(), "empty"))
                {
                    ExAqua.LOGGER.error("error in" + this + "fluid not found:" + json.get("input_fluid").getAsString());
                    throw new JsonSyntaxException("Error in "+ this + ". Fluid not found: "+ json.get("input_fluid").getAsString());
                }

            }
            if(json.has("input_item"))
            {
                InputI = ShapedRecipe.deserializeItem(json.get("input_item").getAsJsonObject());
            }


            Fluid outputF = Fluids.EMPTY;
            ItemStack OutputI = ItemStack.EMPTY;
            if(json.has("output_fluid"))
            {
                outputF = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(json.get("output_fluid").getAsString()));
                if (outputF == Fluids.EMPTY && !Objects.equals(json.get("output_fluid").getAsString(), "empty"))
                {
                    ExAqua.LOGGER.error("error in" + this + "fluid not found:" + json.get("output_fluid").getAsString());
                    throw new JsonSyntaxException("Error in "+ this + ". Fluid not found: "+ json.get("output_fluid").getAsString());
                }
            }
            if(json.has("Output_item"))
            {
                OutputI = ShapedRecipe.deserializeItem(json.get("Output_item").getAsJsonObject());
            }

            int amount_in = 0;
            if(json.has("amount_input")) amount_in = json.get("amount_input").getAsInt();
            int amount_out = 1;
            if(json.has("amount_Output")) amount_out = json.get("amount_Output").getAsInt();
            CauldronTemperature temp = CauldronTemperature.neutral;
            if (json.has("temperature")){temp = CauldronTemperature.valueOf(json.get("temperature").getAsString());}

            return new CauldronRecipe(recipeId, InputF,InputI,outputF,OutputI,temp,amount_in,amount_out);
        }

        /**
         * Reads recipe from a packetbuffer.
         */
        @Nullable
        @Override
        public CauldronRecipe read(@NotNull ResourceLocation recipeId, PacketBuffer buffer) {
            String inputf = buffer.readString();
            Fluid InputF = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(inputf));
            ItemStack InputI =buffer.readItemStack();


            Fluid outputF = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(buffer.readString()));
            ItemStack OutputI = buffer.readItemStack();

            int amount = buffer.readInt();
            int amount_out = buffer.readInt();
            CauldronTemperature temp = buffer.readEnumValue(CauldronTemperature.class);

            return new CauldronRecipe(recipeId, InputF,InputI,outputF,OutputI,temp,amount,amount_out);
        }

        /**
         * Converts recipe into a packet buffer.
         */
        @Override
        public void write(PacketBuffer buffer, CauldronRecipe recipe) {

           buffer.writeString(ForgeRegistries.FLUIDS.getKey(recipe.INPUT_FLUID).toString());
           buffer.writeItemStack(recipe.INPUT_ITEM);


           buffer.writeString(ForgeRegistries.FLUIDS.getKey(recipe.OUTPUT_FLUID).toString());
           buffer.writeItemStack(recipe.OUTPUT_ITEM);

           buffer.writeInt(recipe.AMOUNT_INPUT);
           buffer.writeInt(recipe.AMOUNT_OUTPUT);
           buffer.writeEnumValue(recipe.TEMP);

        }
    }
    //endregion

}