package com.rutaji.exaqua.block;

import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.item.ModItems;
import com.rutaji.exaqua.others.CustomItemGroup;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.OreBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.NotImplementedException;

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
        ModItems.ITEMS.register(name,()-> new BlockItem(block.get(),new Item.Properties().group(CustomItemGroup.EX_AQUA_GROUP)));
    }
    public static RegistryObject<Block> GetSIEVE(Tiers t)
    {
        switch (t)
        {
            case iron: return  IRONSIEVE;
            case gold: return  GOLDSIEVE;
            case frogium: return  FROGIUMSIEVE;
            case diamond: return  DIAMONDSIEVE;
        }

        throw new NotImplementedException("Sieve doesn´t exist");
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
    //endregion
    //public static final RegistryObject<Block> SIEVE =registerBlock("sieve",() -> new SieveBlock(AbstractBlock.Properties.create(Material.IRON)));
    //public static final RegistryObject<Block> SIEVE =registerBlock("sieve",() -> new SieveBlock(AbstractBlock.Properties.create(Material.IRON),Tiers.iron));
    public static final RegistryObject<Block> IRONSIEVE =registerBlock("ironsieve",() -> new SieveBlock(AbstractBlock.Properties.create(Material.IRON),Tiers.iron));
    public static final RegistryObject<Block> GOLDSIEVE =registerBlock("goldsieve",() -> new SieveBlock(AbstractBlock.Properties.create(Material.IRON),Tiers.gold));
    public static final RegistryObject<Block> FROGIUMSIEVE =registerBlock("frogiumsieve",() -> new SieveBlock(AbstractBlock.Properties.create(Material.IRON),Tiers.frogium));
    public static final RegistryObject<Block> DIAMONDSIEVE =registerBlock("diamondsieve",() -> new SieveBlock(AbstractBlock.Properties.create(Material.IRON),Tiers.diamond));
    public static final RegistryObject<Block> SQUEEZER =registerBlock("squeezer",() -> new squeezerBlock(AbstractBlock.Properties.create(Material.IRON)));
    public static final RegistryObject<Block> F_ORE_END =registerBlock("frogium_ore_end",() -> new OreBlock(AbstractBlock.Properties.create(Material.ROCK)));
    public static final RegistryObject<Block> F_ORE_STONE =registerBlock("frogium_ore_stone",() -> new Block(AbstractBlock.Properties.create(Material.ROCK)));

}
