package com.rutaji.exaqua.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SieveRecipie implements ISieveRecipie {


    private final ResourceLocation id;

    public SieveRecipie(ResourceLocation id, FluidStack input,List<RoolItem> output,int time,double rf) {
        this.id = id;
        this.InputFluid = input;
        this.Results = output;
        this.TIME = time;
        this.RF = rf;
        int sum=0;
        for (RoolItem r: Results)
        {

            sum+=r.chance;
            r.chance = sum;
        }
        this.sum = sum;
    }
    private static final Random RANDOM = new Random();
    public final List<RoolItem> Results;
    public final FluidStack InputFluid;
    public final int TIME;
    public final double RF;
    public final int sum;


    @Override
    public boolean matches(IInventory inv, World worldIn) {
        if(inv instanceof InventoryWithFluids)
        {
            FluidStack f = ((InventoryWithFluids) inv).getFluid();
            return f.isFluidEqual(InputFluid) && f.getAmount() >= InputFluid.getAmount();
        }
        return false;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return null;
    }

    @Override //todo does not provide anything claaassiicc
    public ItemStack getRecipeOutput() {
        return null;
    }
    public List<ItemStack> GetAllPossibleOutputs() {
        List<ItemStack> results  = new ArrayList<ItemStack>();
        for (RoolItem r:Results)
        {
            results.add(r.item);
        }
        return results;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.SIEVE_SERIALIZER.get();
    }

    public ItemStack getRandomItemStack()
    {

        if(sum == 0){System.out.println("Recipe doesn´t have a chance");return ItemStack.EMPTY;}
        int random = RANDOM.nextInt(sum) + 1;
        for (RoolItem r: Results)
        {
            if(random <= r.chance){return r.item.copy();}
        }
        return ItemStack.EMPTY;

    }

    public static class SieveRecipeType implements IRecipeType<SieveRecipie> {
        @Override
        public String toString() {
            return SieveRecipie.TYPE_ID.toString();
        }
    }
    //region Serializer
    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<SieveRecipie> {

        @Override
        public SieveRecipie read(ResourceLocation recipeId, JsonObject json) {

            JsonObject InputJson = JSONUtils.getJsonObject(json, "input");
            String Fluid = InputJson.get("fluid").getAsString();
            int FluidtAmount = InputJson.get("amount").getAsInt();
            FluidStack Input = new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(Fluid)) ,FluidtAmount);


            JsonArray OutputsJson = JSONUtils.getJsonArray(json, "outputs");
            List<RoolItem> Outputs = new ArrayList<>();
            for (int i = 0; i < OutputsJson.size(); i++) {
                 JsonObject j = OutputsJson.get(i).getAsJsonObject();
                Outputs.add(new RoolItem( ShapedRecipe.deserializeItem(j.get("item").getAsJsonObject()),j.get("chance").getAsInt()));
            }
            int time = json.get("time").getAsInt();
            double rf = json.get("rf").getAsDouble();
            return new SieveRecipie(recipeId, Input,Outputs,time,rf);
        }

        @Nullable
        @Override
        public SieveRecipie read(ResourceLocation recipeId, PacketBuffer buffer) {
            FluidStack Input = new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(buffer.readString())),buffer.readInt());

            List<RoolItem> Results = new ArrayList<>();
            int size = buffer.readInt();
            for(int i =0;i< size;i++)
            {
                Results.add(new RoolItem(buffer.readItemStack(),buffer.readInt()));

            }
            int time = buffer.readInt();
            double rf = buffer.readDouble();
            return new SieveRecipie(recipeId, Input,Results,time,rf);
        }

        @Override
        public void write(PacketBuffer buffer, SieveRecipie recipe) {
            buffer.writeString(ForgeRegistries.FLUIDS.getKey(recipe.InputFluid.getFluid()).toString());
            buffer.writeInt(recipe.InputFluid.getAmount());

            int size = recipe.Results.size();
            buffer.writeInt(size);
            for (RoolItem R: recipe.Results)
            {
                buffer.writeItemStack(R.item);
                buffer.writeInt(R.chance);
            }
            buffer.writeInt(recipe.TIME);
            buffer.writeDouble(recipe.RF);


        }
    }
    //endregion

}
