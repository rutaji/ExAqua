package com.rutaji.exaqua.block;

import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.item.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModBlocks {
    //region register
    public  static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExAqua.MOD_ID);
    //helper method to register block and its item

    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name,block);
        registerBlockItem(name,toReturn);

        return toReturn;
    }
    private static <T extends Block> void registerBlockItem(String name,RegistryObject<T> block){
        ModItems.ITEMS.register(name,()-> new BlockItem(block.get(),new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
    //endregion
    public static final RegistryObject<Block> SIEVE =registerBlock("sieve",() -> new SieveBlock(AbstractBlock.Properties.create(Material.IRON)));
    public static final RegistryObject<Block> SQUEEZER =registerBlock("squeezer",() -> new squeezerBlock(AbstractBlock.Properties.create(Material.IRON)));
}
