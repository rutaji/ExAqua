package com.rutaji.exaqua.block;

import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.item.ModItems;
import com.rutaji.exaqua.others.CustomItemGroup;
import com.rutaji.exaqua.others.SieveTiers;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

/**
 * Registry for all modded blocks.
 */
public class ModBlocks {
    //region register
    public  static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ExAqua.MOD_ID);


    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name,block);
        registerBlockItem(name,toReturn);
        return toReturn;
    }
    private static <T extends Block> void registerBlockItem(String name,RegistryObject<T> block){
        ModItems.ITEMS.register(name,()-> new BlockItem(block.get(),new Item.Properties().group(CustomItemGroup.EX_AQUA_GROUP)));
    }
    //endregion



    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }

    public static final RegistryObject<Block> IRONSIEVE =registerBlock("ironsieve",() -> new SieveBlock(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(6).notSolid(), SieveTiers.iron));
    public static final RegistryObject<Block> GOLDSIEVE =registerBlock("goldsieve",() -> new SieveBlock(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(6).notSolid(), SieveTiers.gold));
    public static final RegistryObject<Block> FROGIUMSIEVE =registerBlock("frogiumsieve",() -> new SieveBlock(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(6).notSolid(), SieveTiers.frogium));
    public static final RegistryObject<Block> DIAMONDSIEVE =registerBlock("diamondsieve",() -> new SieveBlock(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(6).notSolid(), SieveTiers.diamond));
    public static final RegistryObject<Block> SQUEEZER =registerBlock("squeezer",() -> new SqueezerBlock(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(6).notSolid()));
    public static final RegistryObject<Block> F_ORE_END =registerBlock("frogium_ore_end",() -> new Block(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(4)));
    public static final RegistryObject<Block> F_ORE_STONE =registerBlock("frogium_ore_stone",() -> new FrogiumOre(AbstractBlock.Properties.create(Material.ROCK).hardnessAndResistance(2)));
    public static final RegistryObject<Block> F_BLOCK =registerBlock("frogium_block",() -> new FrogiumBlock(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(4)));
    public static final RegistryObject<Block> CAULDRON =registerBlock("cauldron",() -> new CraftingCauldron(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(4).notSolid()));
    public static final RegistryObject<Block> MUD =registerBlock("mud",() -> new Block(AbstractBlock.Properties.create(Material.SAND).hardnessAndResistance(1)));
    public static final RegistryObject<Block> AUTO_SQUEEZER =registerBlock("auto_squeezer",() -> new AutoSqueezerBlock(AbstractBlock.Properties.create(Material.IRON).hardnessAndResistance(3)));

}
