package com.rutaji.exaqua.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.block.SieveTiers;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
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
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SieveRecipie implements ISieveRecipie {


    private final ResourceLocation ID;

    public SieveRecipie(ResourceLocation id, FluidStack input,List<RoolItem> output,int time,int rf,SieveTiers tier) {
        this.ID = id;
        this.INPUTFLUID = input;
        this.RESULT = output;
        this.TIME = time;
        this.RF = rf;
        int sum=0;
        for (RoolItem r: RESULT)
        {

            sum+=r.chance;
            r.chance = sum;
        }
        this.SUM = sum;
        this.TIER = tier;
        Chances = CountChances();
    }
    private final List<Double> Chances;
    public List<Double> GetChances(){return  Chances;}
    private static final Random RANDOM = new Random();
    public final List<RoolItem> RESULT;
    public final FluidStack INPUTFLUID;
    public final int TIME;
    public final int RF;
    public final int SUM;
    public final SieveTiers TIER;


    public int GetSize(){
        return  RESULT.size();
    }
    @Override
    public boolean matches(@NotNull IInventory inv, @NotNull World worldIn) {
        if(inv instanceof InventorySieve)
        {
            FluidStack f = ((InventorySieve) inv).getFluid();
            return f.isFluidEqual(INPUTFLUID) && f.getAmount() >= INPUTFLUID.getAmount() && TIER.equals(((InventorySieve) inv).GetTier());
        }
        return false;
    }

    @Override
    public @NotNull ItemStack getCraftingResult(@NotNull IInventory inv) {
        return ItemStack.EMPTY;
    }

    @Override // does not provide anything because this recipe doesn´t have clear output
    public @NotNull ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    public List<Double> CountChances()
    {
        List<Double> result = new ArrayList<>();
        int i =0;
        for (RoolItem r : RESULT)
        {
            result.add(((double)(r.chance - i)/SUM)*100);
            i = r.chance;
        }
        return result;
    }
    public List<ItemStack> GetAllPossibleOutputs() {
        List<ItemStack> results  = new ArrayList<>();
        for (RoolItem r: RESULT)
        {
            results.add(r.item);
        }
        return results;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }

    @Override
    public @NotNull IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.SIEVE_SERIALIZER.get();
    }

    public ItemStack getRandomItemStack()
    {

        if(SUM == 0){System.out.println("Recipe doesn´t have a chance");return ItemStack.EMPTY;}
        int random = RANDOM.nextInt(SUM) + 1;
        for (RoolItem r: RESULT)
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
        public @NotNull SieveRecipie read(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {

            JsonObject InputJson = JSONUtils.getJsonObject(json, "input");
            String OutputFluid = InputJson.get("fluid").getAsString();
            int FluidtAmount = InputJson.get("amount").getAsInt();

            Fluid f = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(OutputFluid));
            if(f == null){
                ExAqua.LOGGER.error("error in" + recipeId.getPath() + "fluid not found:" + OutputFluid);
                throw new JsonSyntaxException("Error in "+ recipeId.getPath() + ". Fluid not found: "+ json.get("fluid").getAsString());}
            FluidStack Input = new FluidStack(f ,FluidtAmount);


            JsonArray OutputsJson = JSONUtils.getJsonArray(json, "outputs");
            List<RoolItem> Outputs = new ArrayList<>();
            for (int i = 0; i < OutputsJson.size(); i++) {
                 JsonObject j = OutputsJson.get(i).getAsJsonObject();
                Outputs.add(new RoolItem( ShapedRecipe.deserializeItem(j.get("item").getAsJsonObject()),j.get("weight").getAsInt()));
            }
            int time = json.get("time").getAsInt();
            int rf = json.get("rf").getAsInt();
            String tier = json.get("tier").getAsString();

            return new SieveRecipie(recipeId, Input,Outputs,time,rf,SieveTiers.valueOf(tier));
        }

        @Nullable
        @Override
        public SieveRecipie read(@NotNull ResourceLocation recipeId, PacketBuffer buffer) {
            String OutputFluid = buffer.readString();
            Fluid f = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(OutputFluid));
            if(f == null){
                ExAqua.LOGGER.error("error in" + recipeId.getPath() + "fluid not found:" + OutputFluid);
                f= Fluids.EMPTY;}
            FluidStack Input = new FluidStack(f,buffer.readInt());

            List<RoolItem> Results = new ArrayList<>();
            int size = buffer.readInt();
            for(int i =0;i< size;i++)
            {
                Results.add(RoolItem.Read(buffer));

            }
            int time = buffer.readInt();
            int rf = buffer.readInt();
            SieveTiers tier = buffer.readEnumValue(SieveTiers.class);
            return new SieveRecipie(recipeId, Input,Results,time,rf,tier);
        }

        @Override
        public void write(PacketBuffer buffer, SieveRecipie recipe) {
            buffer.writeString(ForgeRegistries.FLUIDS.getKey(recipe.INPUTFLUID.getFluid()).toString());
            buffer.writeInt(recipe.INPUTFLUID.getAmount());

            int size = recipe.RESULT.size();
            buffer.writeInt(size);
            for (RoolItem R: recipe.RESULT)
            {
                RoolItem.Write(buffer,R);
            }
            buffer.writeInt(recipe.TIME);
            buffer.writeInt(recipe.RF);
            buffer.writeEnumValue(recipe.TIER);


        }
    }
    //endregion

}
