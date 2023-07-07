package com.rutaji.exaqua.item;

import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.item.logic.HandSieve;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {

    //region register
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ExAqua.MOD_ID);

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
    //endregion


    public static final RegistryObject<Item> HANDSIEVE = ITEMS.register("handsieve",()->new HandSieve(new Item.Properties().group(ItemGroup.BUILDING_BLOCKS).maxStackSize(1)));



}
