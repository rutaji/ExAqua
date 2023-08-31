package com.rutaji.exaqua.item;

import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.Fluids.ModFluids;
import com.rutaji.exaqua.item.armor.*;
import com.rutaji.exaqua.others.CustomItemGroup;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BucketItem;
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
    public static final RegistryObject<Item> FROGIUM_DUST = ITEMS.register("frogium_dust",()->new Item(new Item.Properties().group(CustomItemGroup.EX_AQUA_GROUP)));

    public static final RegistryObject<Item> FROGIUM_BOOTS = ITEMS.register("frogium_boots",
            () -> new FrogiumBoots(FrogiumArmorMaterial.FROGIUM, EquipmentSlotType.FEET,
                    new Item.Properties().group(CustomItemGroup.EX_AQUA_GROUP)));

    public static final RegistryObject<Item> FROGIUM_CHESTPLATE = ITEMS.register("frogium_chestplate",
            () -> new FrogiumChestplate(FrogiumArmorMaterial.FROGIUM, EquipmentSlotType.CHEST,
                    new Item.Properties().group(CustomItemGroup.EX_AQUA_GROUP)));

    public static final RegistryObject<Item> FROGIUM_LEEGINS = ITEMS.register("frogium_leggins",
            () -> new FrogiumLeggins(FrogiumArmorMaterial.FROGIUM, EquipmentSlotType.LEGS,
                    new Item.Properties().group(CustomItemGroup.EX_AQUA_GROUP)));

    public static final RegistryObject<Item> FROGIUM_HELMET = ITEMS.register("frogium_helmet",
            () -> new FrogiumHelmet(FrogiumArmorMaterial.FROGIUM, EquipmentSlotType.HEAD,
                    new Item.Properties().group(CustomItemGroup.EX_AQUA_GROUP)));
    public static final RegistryObject<Item> MUD_BUCKET = ITEMS.register("mud_bucket",
            () -> new BucketItem(()-> ModFluids.MUD_FLUID.get(),
                    new Item.Properties().maxStackSize(1).group(CustomItemGroup.EX_AQUA_GROUP)));




}
