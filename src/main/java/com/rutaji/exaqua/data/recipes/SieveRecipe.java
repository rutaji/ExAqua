package com.rutaji.exaqua.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.others.SieveTiers;
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
/**
 * Handles recipe of type exaqua:sieve.
 */
public class SieveRecipe implements ISieveRecipe {

    private final ResourceLocation ID;

    public SieveRecipe(ResourceLocation id, FluidStack input, List<RoolItem> output, int time, int rf, SieveTiers tier) {
        this.ID = id;
        this.INPUTFLUID = input;
        this.RESULTS = output;
        this.TIME = time;
        this.RF = rf;
        int sum=0;
        for (RoolItem r: RESULTS)
        {

            sum+=r.chance;
            r.chance = sum;
        }
        this.SUM = sum;
        this.TIER = tier;
        Chances = CountChances();
    }
    private final List<Double> Chances;
    /**
     * Returns chance for every item in percents. In the same order as {@link SieveRecipe#GetAllPossibleOutputs GetAllPossibleOutputs()}.
     */
    public List<Double> GetChances(){return  Chances;}
    private static final Random RANDOM = new Random();
    public final List<RoolItem> RESULTS;
    public final FluidStack INPUTFLUID;
    public final int TIME;
    public final int RF;
    public final int SUM;
    public final SieveTiers TIER;

    /**
     * @return Number of possible outputs
     */
    public int GetSize(){
        return  RESULTS.size();
    }

    /**
     * Returns True if inventory contains all Ingridients for recipe. Inventory should be instace of InventorySieve, othervise always return False.
     * @param inv inventory should be instace of InventorySieve, othervise always return False.
     * @return returns True if inventory contains all Ingridients for recipe.
     */
    @Override
    public boolean matches(@NotNull IInventory inv, @NotNull World worldIn) {
        if(inv instanceof InventorySieve)
        {
            FluidStack f = ((InventorySieve) inv).getFluid();
            return f.isFluidEqual(INPUTFLUID) && f.getAmount() >= INPUTFLUID.getAmount() && TIER.equals(((InventorySieve) inv).GetTier());
        }
        return false;
    }

    /**
     * Same as calling {@link SieveRecipe#getRandomItemStack getRandomItemStack()}. Parametr does nothing. Is there only because of Inheritance.
     * @param inv Does nothing.
     * @see SieveRecipe#getRandomItemStack
     */
    @Override
    public @NotNull ItemStack getCraftingResult(@NotNull IInventory inv) {
        return getRandomItemStack();
    }

    /**
     * Recipe output is random. This method always returns empty.
     */
    @Override
    public @NotNull ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    private @NotNull List<Double> CountChances()
    {
        List<Double> result = new ArrayList<>();
        int i =0;
        for (RoolItem r : RESULTS)
        {
            result.add(((double)(r.chance - i)/SUM)*100);
            i = r.chance;
        }
        return result;
    }
    /**
     * Returns list of all items that can returned by this recipe. In the same order as {@link SieveRecipe#GetChances() GetChances()}.
     */
    public List<ItemStack> GetAllPossibleOutputs() {
        List<ItemStack> results  = new ArrayList<>();
        for (RoolItem r: RESULTS)
        {
            results.add(r.item);
        }
        return results;
    }

    /**
     * @return resource location of the recipe.
     */
    @Override
    public @NotNull ResourceLocation getId() {
        return ID;
    }


    @Override
    public @NotNull IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.SIEVE_SERIALIZER.get();
    }

    /** Returns output of this recipe. Output item is randomly choosen from {@link SieveRecipe#RESULTS RESULT}.
     * To get all possible outputs of this recipe use {@link SieveRecipe#GetAllPossibleOutputs() GetAllPossibleOutputs()}.
     * To get chances (in percents) use {@link SieveRecipe#GetChances() GetChances()}.
     * @return random output item of the recipe.
     */
    public ItemStack getRandomItemStack()
    {

        if(SUM == 0){System.out.println("Recipe doesn´t have a chance");return ItemStack.EMPTY;}
        int random = RANDOM.nextInt(SUM) + 1;
        for (RoolItem r: RESULTS)
        {
            if(random <= r.chance){return r.item.copy();}
        }
        return ItemStack.EMPTY;

    }

    /**
     * serializer for this recipe.
     */
    public static class SieveRecipeType implements IRecipeType<SieveRecipe> {
        @Override
        public String toString() {
            return SieveRecipe.TYPE_ID.toString();
        }
    }
    //region Serializer
    /**
     * Class used to serialize and deserialize this recipe.
     */
    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<SieveRecipe> {

        /**
         * Reads recipe from json.
         * @exception JsonSyntaxException if json syntax is wrong.
         * @param recipeId resource location of the recipe.
         * @param json json for cenversion.
         * @return  Loaded recipe.
         */
        @Override
        public @NotNull SieveRecipe read(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {

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

            return new SieveRecipe(recipeId, Input,Outputs,time,rf,SieveTiers.valueOf(tier));
        }

        /**
         * Reads recipe from a packetbuffer.
         */
        @Nullable
        @Override
        public SieveRecipe read(@NotNull ResourceLocation recipeId, PacketBuffer buffer) {
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
            return new SieveRecipe(recipeId, Input,Results,time,rf,tier);
        }

        /**
         * Converts recipe into a packet buffer.
         */
        @Override
        public void write(PacketBuffer buffer, SieveRecipe recipe) {
            buffer.writeString(ForgeRegistries.FLUIDS.getKey(recipe.INPUTFLUID.getFluid()).toString());
            buffer.writeInt(recipe.INPUTFLUID.getAmount());

            int size = recipe.RESULTS.size();
            buffer.writeInt(size);
            for (RoolItem R: recipe.RESULTS)
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
