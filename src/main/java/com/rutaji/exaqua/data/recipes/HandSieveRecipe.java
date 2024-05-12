package com.rutaji.exaqua.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.rutaji.exaqua.ExAqua;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Handles recipes of type exaqua:handsieve.
 */
public class HandSieveRecipe implements IHandSieveRecipe {
    //region Constructor
    public HandSieveRecipe(ResourceLocation id, @NotNull Fluid input, @NotNull List<RoolItem> output, int chance) {
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
    /**
     * Returns chance for every item in percents. In the same order as {@link HandSieveRecipe#GetAllPossibleOutputs GetAllPossibleOutputs()}.
     */
    public @NotNull List<Double> GetChances(){return  Chances;}
    private final @NotNull List<Double> Chances;
    private final ResourceLocation ID;
    private static final Random RANDOM = new Random();
    public final @NotNull List<RoolItem> RESULTS;
    public final @Nonnull Fluid INPUTFLUID;
    public final int SUM;
    public final int SUCCESCHANCE;

    /**
     * Returns True if inventory contains all Ingridients for recipie. Inventory should be instace of InventorySieve, othervise always return False.
     * @param inv inventory should be instace of InventorySieve, othervise always return False.
     * @return returns True if inventory contains all Ingridients for recipie.
     */
    @Override
    public boolean matches(@NotNull IInventory inv, @NotNull World worldIn) {
        if(inv instanceof InventorySieve)
        {
            FluidStack f = ((InventorySieve) inv).getFluid();
            return f.getFluid() == INPUTFLUID;
        }
        return false;
    }

    /**
     * Returns randomly True/False. Chance for True is equal to {@link HandSieveRecipe#SUCCESCHANCE SUCCESCHANCE} of this recipie.
     */
    public boolean IsSucces()
    {
        int random = RANDOM.nextInt(100)+1;
        return random <= SUCCESCHANCE;
    }

    /**
     * Same as calling {@link HandSieveRecipe#GetRandomItemStack getRandomItemStack()}. Parametr does nothing. Is there only because of Inheritance.
     * @param inv Does nothing.
     * @see HandSieveRecipe#GetRandomItemStack
     */
    @Override
    public @NotNull ItemStack getCraftingResult(@NotNull IInventory inv) {return GetRandomItemStack();}
    /**
     * Recipie output is random. This method always returns empty.
     */
    @Override
    public @NotNull ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    /**
     * Returns list of all items that can returned by this recipie. In the same order as {@link HandSieveRecipe#GetChances() GetChances()}.
     */
    public List<ItemStack> GetAllPossibleOutputs() {
        List<ItemStack> results  = new ArrayList<>();
        for (RoolItem r: RESULTS)
        {
            results.add(r.item.copy());
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

    /**
     * @return Number of possible outputs
     */
    public int GetSize(){return RESULTS.size();}


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

    /** Returns output of this recipie. Output item is randomly choosen from {@link HandSieveRecipe#RESULTS RESULT}.
     * To get all possible outputs of this recipie use {@link HandSieveRecipe#GetAllPossibleOutputs() GetAllPossibleOutputs()}.
     * To get chances (in percents) use {@link HandSieveRecipe#GetChances() GetChances()}.
     * @return random output item of the recipie.
     */
    public ItemStack GetRandomItemStack()
    {

        if(SUM == 0)
        {
            ExAqua.LOGGER.warn("HandSieve recipie has sum of changes 0.Recipie resource location: {}",getId());
            return ItemStack.EMPTY;
        }
        int random = RANDOM.nextInt(SUM) + 1;
        for (RoolItem r: RESULTS)
        {
            if(random <= r.chance){return r.item.copy();}
        }
        return ItemStack.EMPTY;

    }
    //region registration

    /**
     * @return serializer for this recipie.
     */
    @Override
    public @NotNull IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.HANDSIEVE_SERIALIZER.get();
    }

    public static class HandSieveRecipeType implements IRecipeType<HandSieveRecipe> {
        @Override
        public String toString() {
            return HandSieveRecipe.TYPE_ID.toString();
        }
    }
    //endregion
    //region Serializer

    /**
     * Class used to serialize and deserialize this recipe.
     */
    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<HandSieveRecipe> {

        /**
         * Reads recipe from json.
         * @exception JsonSyntaxException if json syntax is wrong.
         * @param recipeId resource location of the recipe.
         * @param json json for cenversion.
         * @return  Loaded recipe.
         */
        @Override
        public @NotNull HandSieveRecipe read(@NotNull ResourceLocation recipeId, JsonObject json) {

            String fluidString = json.get("fluid").getAsString();
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidString));
            if(fluid == Fluids.EMPTY && !Objects.equals(fluidString, "empty")){
                ExAqua.LOGGER.error("Error in {}. Fluid not found: {}",recipeId.getPath(),fluidString);
                throw new JsonSyntaxException("Error in "+ recipeId.getPath() + ". Fluid not found: "+ json.get("fluid").getAsString());
            }

            JsonArray OutputsJson = JSONUtils.getJsonArray(json, "outputs");
            List<RoolItem> Outputs = new ArrayList<>();
            for (int i = 0; i < OutputsJson.size(); i++) {
                JsonObject j = OutputsJson.get(i).getAsJsonObject();
                Outputs.add(new RoolItem( ShapedRecipe.deserializeItem(j.get("item").getAsJsonObject()),j.get("weight").getAsInt()));
            }
            int chance = json.get("success").getAsInt();
            return new HandSieveRecipe(recipeId, fluid,Outputs,chance);
        }
        /**
         * Reads recipe from a packetbuffer.
         */
        @Nullable
        @Override
        public HandSieveRecipe read(@NotNull ResourceLocation recipeId, PacketBuffer buffer) {
            Fluid Input = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(buffer.readString()));
            List<RoolItem> Results = new ArrayList<>();
            int size = buffer.readInt();
            for(int i =0;i< size;i++)
            {
                Results.add(new RoolItem(buffer.readItemStack(),buffer.readInt()));

            }
            int chance = buffer.readInt();
            return new HandSieveRecipe(recipeId, Input,Results,chance);
        }

        /**
         * Converts recipe into a packet buffer.
         */
        @Override
        public void write(PacketBuffer buffer, HandSieveRecipe recipe) {
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

