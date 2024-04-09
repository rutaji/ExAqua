package com.rutaji.exaqua.data.recipes;

import com.google.gson.JsonObject;
import com.rutaji.exaqua.others.CauldronTemperature;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class CauldronRecipie implements ICauldronRecipie {
    //region Constructor
    public CauldronRecipie(ResourceLocation id, Fluid input, ItemStack inputItem, Fluid output, ItemStack outputitem,CauldronTemperature temp,int amount_in, int amount_out) {
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
    private final ResourceLocation ID;
    public final Fluid INPUT_FLUID;
    public final Fluid OUTPUT_FLUID;
    public final ItemStack INPUT_ITEM;
    public final ItemStack OUTPUT_ITEM;
    public final CauldronTemperature TEMP;
    public final int AMOUNT_INPUT;

    public final int AMOUNT_OUTPUT;



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


    @Override
    public @NotNull ItemStack getCraftingResult(@NotNull IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack getRecipeOutput()
    {
     return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }
    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }


    //region registration
    @Override
    public @NotNull IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CAULDRON_SERIALIZER.get();
    }

    public boolean Has2Outputs() {
        return OUTPUT_FLUID != Fluids.EMPTY && OUTPUT_ITEM != ItemStack.EMPTY;
    }

    public static class CauldronRecipeType implements IRecipeType<CauldronRecipie> {
        @Override
        public String toString() {
            return CauldronRecipie.TYPE_ID.toString();
        }
    }
    //endregion
    //region Serializer
    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<CauldronRecipie> {

        @Override
        public @NotNull CauldronRecipie read(@NotNull ResourceLocation recipeId, JsonObject json) {

            Fluid InputF = Fluids.EMPTY;
            ItemStack InputI = ItemStack.EMPTY;
            if(json.has("input_fluid"))
            {
                InputF = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(json.get("input_fluid").getAsString()));
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

            return new CauldronRecipie(recipeId, InputF,InputI,outputF,OutputI,temp,amount_in,amount_out);
        }

        @Nullable
        @Override
        public CauldronRecipie read(@NotNull ResourceLocation recipeId, PacketBuffer buffer) {
            String inputf = buffer.readString();
            Fluid InputF = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(inputf));
            ItemStack InputI =buffer.readItemStack();


            Fluid outputF = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(buffer.readString()));
            ItemStack OutputI = buffer.readItemStack();

            int amount = buffer.readInt();
            int amount_out = buffer.readInt();
            CauldronTemperature temp = buffer.readEnumValue(CauldronTemperature.class);

            return new CauldronRecipie(recipeId, InputF,InputI,outputF,OutputI,temp,amount,amount_out);
        }

        @Override
        public void write(PacketBuffer buffer, CauldronRecipie recipe) {

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