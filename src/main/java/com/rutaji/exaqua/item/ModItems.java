package com.rutaji.exaqua.item;

import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.others.CustomItemGroup;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
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

    public static final RegistryObject<Item> HANDSIEVE = ITEMS.register("handsieve",()->new HandSieve(new Item.Properties().group(CustomItemGroup.EX_AQUA_GROUP).maxStackSize(1)));
    public static final RegistryObject<Item> FROGIUM = ITEMS.register("frogium",()->new Item(new Item.Properties().group(CustomItemGroup.EX_AQUA_GROUP)));

    public static final RegistryObject<Item> FROGIUM_BOOTS = ITEMS.register("frogium_boots",
            () -> new ArmorItem(FrogiumArmorMaterial.FROGIUM, EquipmentSlotType.FEET,
                    new Item.Properties().group(CustomItemGroup.EX_AQUA_GROUP)));

    public static final RegistryObject<Item> FROGIUM_CHESTPLATE = ITEMS.register("frogium_chestplate",
            () -> new ArmorItem(FrogiumArmorMaterial.FROGIUM, EquipmentSlotType.CHEST,
                    new Item.Properties().group(CustomItemGroup.EX_AQUA_GROUP)));

    public static final RegistryObject<Item> FROGIUM_LEEGINS = ITEMS.register("frogium_leggins",
            () -> new ArmorItem(FrogiumArmorMaterial.FROGIUM, EquipmentSlotType.LEGS,
                    new Item.Properties().group(CustomItemGroup.EX_AQUA_GROUP)));

    public static final RegistryObject<Item> FROGIUM_HELMET = ITEMS.register("frogium_helmet",
            () -> new ArmorItem(FrogiumArmorMaterial.FROGIUM, EquipmentSlotType.HEAD,
                    new Item.Properties().group(CustomItemGroup.EX_AQUA_GROUP)));




}
