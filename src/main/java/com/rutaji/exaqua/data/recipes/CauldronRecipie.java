package com.rutaji.exaqua.data.recipes;

import com.google.gson.JsonObject;
import com.rutaji.exaqua.others.CauldronTemperature;
import net.minecraft.fluid.Fluid;
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
    public CauldronRecipie(ResourceLocation id, Fluid input, ItemStack inputItem, Fluid output, ItemStack outputitem,CauldronTemperature temp) {
        this.ID = id;
        this.INPUT = input;
        OUTPUT = output;
        INPUT_ITEM = inputItem;
        OUTPUT_ITEM = outputitem;
        TEMP = temp;
        AMOUNT = 0;
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
    public boolean matches(IInventory inv, World worldIn) {
        if(inv instanceof InventoryCauldron)
        {
            return  ((INPUT == null && ( ((InventoryCauldron) inv).getFluid() == null || ((InventoryCauldron) inv).getFluid() == OUTPUT ))||( ((InventoryCauldron) inv).amount >= AMOUNT &&(((InventoryCauldron) inv).getFluid().isEquivalentTo(INPUT)))) &&
                    (INPUT_ITEM == null || (inv.getStackInSlot(0).isItemEqual(INPUT_ITEM) && inv.getStackInSlot(0).getCount() >= INPUT_ITEM.getCount())) &&
                    TEMP == ((InventoryCauldron) inv).getTemp() ;
        }
        return false;
    }


    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
     if(OUTPUT == null){return OUTPUT_ITEM;}
     return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }



    //region registration
    @Override
    public IRecipeSerializer<?> getSerializer() {
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
        public CauldronRecipie read(ResourceLocation recipeId, JsonObject json) {

            Fluid InputF = null;
            ItemStack InputI = null;

            if(json.has("fluidi"))
            {
                InputF = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(json.get("fluidi").getAsString()));
            }

            if(json.has("itemi"))
            {
                InputI = ShapedRecipe.deserializeItem(json.get("itemi").getAsJsonObject());
            }



            Fluid outputF = null;
            ItemStack OutputI = null;
            String output = json.get("outputtype").getAsString();
            int amount = 0;
            switch (output)
            {
                case "fluid":
                    outputF = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(json.get("output").getAsString()));
                    break;
                case "item":
                    OutputI = ShapedRecipe.deserializeItem(json.get("output").getAsJsonObject());
                    amount = json.get("amount").getAsInt();
            }
            CauldronTemperature temp = CauldronTemperature.valueOf(json.get("temp").getAsString());

            return new CauldronRecipie(recipeId, InputF,InputI,outputF,OutputI,temp,amount);
        }

        @Nullable
        @Override
        public CauldronRecipie read(ResourceLocation recipeId, PacketBuffer buffer) {
            Fluid InputF = null;
            ItemStack InputI = null;

            String inputf = buffer.readString();
            if(!inputf.equals("null")){InputF = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(inputf));}
            if(buffer.readBoolean()){InputI = buffer.readItemStack();}



            Fluid outputF = null;
            ItemStack OutputI = null;
            int amount = 0;
            String output = buffer.readString();
            switch (output)
            {
                case "fluid":
                    outputF = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(buffer.readString()));
                    break;
                case "item":
                    OutputI = buffer.readItemStack();
                    amount = buffer.readInt();
            }
            CauldronTemperature temp = buffer.readEnumValue(CauldronTemperature.class);


            return new CauldronRecipie(recipeId, InputF,InputI,outputF,OutputI,temp,amount);
        }

        @Override
        public void write(PacketBuffer buffer, CauldronRecipie recipe) {

           buffer.writeString(recipe.INPUT == null ? "null" : ForgeRegistries.FLUIDS.getKey(recipe.INPUT).toString());

            if(recipe.INPUT_ITEM == null)
            {
                buffer.writeBoolean(false);
            }
            else
            {
                buffer.writeBoolean(true);
                buffer.writeItemStack(recipe.INPUT_ITEM);
            }



           String output = recipe.OUTPUT != null ? "fluid" : "item";
           buffer.writeString(output);
            switch (output)
            {
                case "fluid":
                    buffer.writeString(ForgeRegistries.FLUIDS.getKey(recipe.OUTPUT).toString());
                    break;
                case "item":
                    buffer.writeItemStack(recipe.OUTPUT_ITEM);
                    buffer.writeInt(recipe.AMOUNT);
            }
            buffer.writeEnumValue(recipe.TEMP);

        }
    }
    //endregion

}