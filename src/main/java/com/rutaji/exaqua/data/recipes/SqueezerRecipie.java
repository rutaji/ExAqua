package com.rutaji.exaqua.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rutaji.exaqua.block.ModBlocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class SqueezerRecipie implements ISqueezerRecipie {

    private final ResourceLocation ID;
    private final FluidStack OUTPUT;
    private final NonNullList<Ingredient> RECIPIEITEMS;

    public SqueezerRecipie(ResourceLocation id, FluidStack output,
                                    NonNullList<Ingredient> recipeItems) {
        this.ID = id;
        this.OUTPUT = output;
        this.RECIPIEITEMS = recipeItems;

    }
    @Override
    public boolean matches(IInventory inv, World worldIn) {
        return RECIPIEITEMS.get(0).test(inv.getStackInSlot(0));
    }

    @Override
    public ItemStack getCraftingResult(IInventory inv) {
        return ItemStack.EMPTY;
    }
    @Override
    public NonNullList<Ingredient> getIngredients(){
        return RECIPIEITEMS;
    }
    @Override //Does not provide anything because this recipie doesn´t produce items
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }
    public FluidStack getRealOutput(){
        return OUTPUT.copy();
    }

    public ItemStack getIcon() {
        return new ItemStack(ModBlocks.SQUEEZER.get());
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.SQUEEZER_SERIALIZER.get();
    }
    public static class SqueezerRecipeType implements IRecipeType<SqueezerRecipie> {
        @Override
        public String toString() {
            return SqueezerRecipie.TYPE_ID.toString();
        }
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>>
            implements IRecipeSerializer<SqueezerRecipie> {

        @Override
        public SqueezerRecipie read(ResourceLocation recipeId, JsonObject json) {
            JsonObject output1 = JSONUtils.getJsonObject(json, "output");
            String OutputFluid = output1.get("fluid").getAsString();
            int OutputAmount = output1.get("amount").getAsInt();
            FluidStack output = new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(OutputFluid)) ,OutputAmount);


            JsonArray ingredients = JSONUtils.getJsonArray(json, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(1, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.deserialize(ingredients.get(i)));
            }

            return new SqueezerRecipie(recipeId, output,
                    inputs);
        }

        @Nullable
        @Override
        public SqueezerRecipie read(ResourceLocation recipeId, PacketBuffer buffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(1, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.read(buffer));
            }

            FluidStack output = new FluidStack(ForgeRegistries.FLUIDS.getValue(new ResourceLocation(buffer.readString())),buffer.readInt());
            return new SqueezerRecipie(recipeId, output, inputs);
        }

        @Override
        public void write(PacketBuffer buffer, SqueezerRecipie recipe) {
            buffer.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.write(buffer);
            }
            buffer.writeString(ForgeRegistries.FLUIDS.getKey(recipe.OUTPUT.getFluid()).toString());
            buffer.writeInt(recipe.OUTPUT.getAmount());

        }
    }
}
