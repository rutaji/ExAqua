package com.rutaji.exaqua.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rutaji.exaqua.others.CauldronTemperature;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import sun.invoke.empty.Empty;

import javax.annotation.Nullable;
import java.lang.invoke.SwitchPoint;
import java.util.ArrayList;
import java.util.List;

public class CauldronRecipie implements ICauldronRecipie {
    //region Constructor
    public CauldronRecipie(ResourceLocation id, Fluid input, ItemStack inputItem, Fluid output, ItemStack outputitem,CauldronTemperature temp) {
        this.ID = id;
        this.INPUT = input;
        OUTPUT = output;
        INPUT_ITEM = inputItem;
        OUTPUT_ITEM = outputitem;
        TEMP = temp;
    }
    //endregion
    private final ResourceLocation ID;
    public final Fluid INPUT;
    public final Fluid OUTPUT;
    public final ItemStack INPUT_ITEM;
    public final ItemStack OUTPUT_ITEM;
    public final CauldronTemperature TEMP;




    @Override
    public boolean matches(IInventory inv, World worldIn) {
        if(inv instanceof InventoryFluid)
        {
            return ((InventoryFluid) inv).getFluid().isEquivalentTo(INPUT) &&
                    inv.getStackInSlot(0).isItemEqual(INPUT_ITEM) && inv.getStackInSlot(0).getCount() >= INPUT_ITEM.getCount();
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
            ItemStack InputI = ItemStack.EMPTY;

            String input = json.get("input").getAsString();
            switch (input)
            {
                case "fluid":
                    InputF = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(json.get("fluidi").getAsString()));
                    break;
                case "item":
                    InputI = ShapedRecipe.deserializeItem(json.get("itemi").getAsJsonObject());
            }


            Fluid outputF = null;
            ItemStack OutputI = ItemStack.EMPTY;
            String output = json.get("output").getAsString();
            switch (output)
            {
                case "fluid":
                    outputF = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(json.get("fluido").getAsString()));
                    break;
                case "item":
                    OutputI = ShapedRecipe.deserializeItem(json.get("itemu").getAsJsonObject());
            }


            return new CauldronRecipie(recipeId, InputF,InputI,outputF,OutputI,CauldronTemperature.valueOf(json.get("temp").getAsString()));
        }

        @Nullable
        @Override
        public CauldronRecipie read(ResourceLocation recipeId, PacketBuffer buffer) {
            Fluid InputF = null;
            ItemStack InputI = ItemStack.EMPTY;

            String input = buffer.readString();
            switch (input)
            {
                case "fluid":
                    InputF = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(buffer.readString()));
                    break;
                case "item":
                    InputI = buffer.readItemStack();
            }


            Fluid outputF = null;
            ItemStack OutputI = ItemStack.EMPTY;
            String output = buffer.readString();
            switch (output)
            {
                case "fluid":
                    outputF = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(buffer.readString()));
                    break;
                case "item":
                    OutputI = buffer.readItemStack();
            }
            CauldronTemperature temp = buffer.readEnumValue(CauldronTemperature.class);


            return new CauldronRecipie(recipeId, InputF,InputI,outputF,OutputI,temp);
        }

        @Override
        public void write(PacketBuffer buffer, CauldronRecipie recipe) {
            String input = recipe.INPUT != null ? "fluid" : "item";
            buffer.writeString(input);
            switch (input)
            {
                case "fluid":
                    buffer.writeString(ForgeRegistries.FLUIDS.getKey(recipe.INPUT).toString());
                    break;
                case "item":
                    buffer.writeItemStack(recipe.INPUT_ITEM);
            }
           String output = recipe.INPUT != null ? "fluid" : "item";
           buffer.writeString(output);
            switch (output)
            {
                case "fluid":
                    buffer.writeString(ForgeRegistries.FLUIDS.getKey(recipe.OUTPUT).toString());
                    break;
                case "item":
                    buffer.writeItemStack(recipe.OUTPUT_ITEM);
            }
            buffer.writeEnumValue(recipe.TEMP);

        }
    }
    //endregion

}