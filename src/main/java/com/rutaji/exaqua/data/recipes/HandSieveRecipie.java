package com.rutaji.exaqua.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HandSieveRecipie implements IHandSieveRecipie {
    //region Constructor
    public HandSieveRecipie(ResourceLocation id, Fluid input, List<RoolItem> output,int chance) {
        this.ID = id;
        this.INPUTFLUID = input;
        this.RESULTS = output;
        this.SUCCESCHANCE = chance;
        int sum=0;
        for (RoolItem r: RESULTS)
        {

            sum+=r.chance;
            r.chance = sum;
        }
        this.SUM = sum;
        Chances = CountChances();
    }
    //endregion
    public List<Double> GetChances(){return  Chances;}
    private final List<Double> Chances;
    private final ResourceLocation ID;
    private static final Random RANDOM = new Random();
    public final List<RoolItem> RESULTS;
    public final Fluid INPUTFLUID;
    public final int SUM;
    public final int SUCCESCHANCE;


    @Override
    public boolean matches(IInventory inv, World worldIn) {
        if(inv instanceof InventoryWithFluids)
        {
            FluidStack f = ((InventoryWithFluids) inv).getFluid();
            return f.getFluid() == INPUTFLUID;
        }
        return false;
    }
    public boolean IsSucces()
    {
        int random = RANDOM.nextInt(100)+1;
        return random <= SUCCESCHANCE;
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return null;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    public List<ItemStack> GetAllPossibleOutputs() {
        List<ItemStack> results  = new ArrayList<ItemStack>();
        for (RoolItem r: RESULTS)
        {
            results.add(r.item);
        }
        return results;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public int GetSize(){return RESULTS.size();}
    public List<Double> CountChances()
    {
        List<Double> result = new ArrayList();
        int i =0;
        for (RoolItem r : RESULTS)
        {
            result.add(((double)(r.chance - i)/SUM)*100);
            i = r.chance;
        }
        return result;
    }

    public ItemStack GetRandomItemStack()
    {

        if(SUM == 0){System.out.println("Recipe doesn´t have a chance");return ItemStack.EMPTY;}
        int random = RANDOM.nextInt(SUM) + 1;
        for (RoolItem r: RESULTS)
        {
            if(random <= r.chance){return r.item.copy();}
        }
        return ItemStack.EMPTY;

    }
    //region registration
    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.HANDSIEVE_SERIALIZER.get();
    }

    public static class HandSieveRecipeType implements IRecipeType<HandSieveRecipie> {
        @Override
        public String toString() {
            return HandSieveRecipie.TYPE_ID.toString();
        }
    }
    //endregion
    //region Serializer
    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<HandSieveRecipie> {

        @Override
        public HandSieveRecipie read(ResourceLocation recipeId, JsonObject json) {

            Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(json.get("fluid").getAsString()));
            JsonArray OutputsJson = JSONUtils.getJsonArray(json, "outputs");
            List<RoolItem> Outputs = new ArrayList<>();
            for (int i = 0; i < OutputsJson.size(); i++) {
                JsonObject j = OutputsJson.get(i).getAsJsonObject();
                Outputs.add(new RoolItem( ShapedRecipe.deserializeItem(j.get("item").getAsJsonObject()),j.get("chance").getAsInt()));
            }
            int chance = json.get("success").getAsInt();
            return new HandSieveRecipie(recipeId, fluid,Outputs,chance);
        }

        @Nullable
        @Override
        public HandSieveRecipie read(ResourceLocation recipeId, PacketBuffer buffer) {
            Fluid Input = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(buffer.readString()));
            List<RoolItem> Results = new ArrayList<>();
            int size = buffer.readInt();
            for(int i =0;i< size;i++)
            {
                Results.add(new RoolItem(buffer.readItemStack(),buffer.readInt()));

            }
            int chance = buffer.readInt();
            return new HandSieveRecipie(recipeId, Input,Results,chance);
        }

        @Override
        public void write(PacketBuffer buffer, HandSieveRecipie recipe) {
            buffer.writeString(recipe.INPUTFLUID.getRegistryName().toString());
            int size = recipe.RESULTS.size();
            buffer.writeInt(size);
            for (RoolItem R: recipe.RESULTS)
            {
                buffer.writeItemStack(R.item);
                buffer.writeInt(R.chance);
            }
            buffer.writeInt(recipe.SUCCESCHANCE);

        }
    }
    //endregion

}

