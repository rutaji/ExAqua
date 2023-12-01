package com.rutaji.exaqua.data.recipes;

import com.google.gson.JsonObject;
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

public class CauldronRecipie implements ICauldronRecipie {
    //region Constructor
    public CauldronRecipie(ResourceLocation id, Fluid input, ItemStack inputItem, Fluid output, ItemStack outputitem,CauldronTemperature temp,int amount) {
        this.ID = id;
        this.INPUT = input;
        OUTPUT = output;
        INPUT_ITEM = inputItem;
        OUTPUT_ITEM = outputitem;
        TEMP = temp;
        this.AMOUNT = amount;
    }
    //endregion
    private final ResourceLocation ID;
    public final Fluid INPUT;
    public final Fluid OUTPUT;
    public final ItemStack INPUT_ITEM;
    public final ItemStack OUTPUT_ITEM;
    public final CauldronTemperature TEMP;
    public final int AMOUNT;




    @Override
    public boolean matches(@NotNull IInventory inv, @NotNull World worldIn) {
        if(inv instanceof InventoryCauldron)
        {
            return  ((INPUT == Fluids.EMPTY && ( ((InventoryCauldron) inv).getFluid() == Fluids.EMPTY || ((InventoryCauldron) inv).getFluid() == OUTPUT ))||( ((InventoryCauldron) inv).amount >= AMOUNT &&(((InventoryCauldron) inv).getFluid().isEquivalentTo(INPUT)))) &&
                    (INPUT_ITEM == ItemStack.EMPTY || (inv.getStackInSlot(0).isItemEqual(INPUT_ITEM) && inv.getStackInSlot(0).getCount() >= INPUT_ITEM.getCount())) &&
                    TEMP == ((InventoryCauldron) inv).getTemp() ;
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
     if(OUTPUT == null){return OUTPUT_ITEM;}
     return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }



    //region registration
    @Override
    public @NotNull IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CAULDRON_SERIALIZER.get();
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

            if(json.has("fluidi"))
            {
                InputF = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(json.get("fluidi").getAsString()));
            }

            if(json.has("itemi"))
            {
                InputI = ShapedRecipe.deserializeItem(json.get("itemi").getAsJsonObject());
            }



            Fluid outputF = Fluids.EMPTY;
            ItemStack OutputI = ItemStack.EMPTY;
            String output = json.get("outputtype").getAsString();
            int amount;
            switch (output)
            {
                case "fluid":
                    outputF = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(json.get("output").getAsString()));
                    break;
                case "item":
                    OutputI = ShapedRecipe.deserializeItem(json.get("output").getAsJsonObject());

            }
            amount = json.get("amount").getAsInt();
            CauldronTemperature temp = CauldronTemperature.valueOf(json.get("temp").getAsString());

            return new CauldronRecipie(recipeId, InputF,InputI,outputF,OutputI,temp,amount);
        }

        @Nullable
        @Override
        public CauldronRecipie read(@NotNull ResourceLocation recipeId, PacketBuffer buffer) {
            Fluid InputF;
            ItemStack InputI;

            String inputf = buffer.readString();
            InputF = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(inputf));
            InputI = buffer.readItemStack();



            Fluid outputF = Fluids.EMPTY;
            ItemStack OutputI = ItemStack.EMPTY;
            int amount;
            String output = buffer.readString();
            switch (output)
            {
                case "fluid":
                    outputF = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(buffer.readString()));
                    break;
                case "item":
                    OutputI = buffer.readItemStack();

            }
            amount = buffer.readInt();
            CauldronTemperature temp = buffer.readEnumValue(CauldronTemperature.class);


            return new CauldronRecipie(recipeId, InputF,InputI,outputF,OutputI,temp,amount);
        }

        @Override
        public void write(PacketBuffer buffer, CauldronRecipie recipe) {

           buffer.writeString(ForgeRegistries.FLUIDS.getKey(recipe.INPUT).toString());


           buffer.writeItemStack(recipe.INPUT_ITEM);




           String output = recipe.OUTPUT != Fluids.EMPTY ? "fluid" : "item";
           buffer.writeString(output);
            switch (output)
            {
                case "fluid":
                    buffer.writeString(ForgeRegistries.FLUIDS.getKey(recipe.OUTPUT).toString());
                    break;
                case "item":
                    buffer.writeItemStack(recipe.OUTPUT_ITEM);

            }
            buffer.writeInt(recipe.AMOUNT);
            buffer.writeEnumValue(recipe.TEMP);

        }
    }
    //endregion

}