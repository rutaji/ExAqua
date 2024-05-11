package com.rutaji.exaqua.tileentity;

import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.block.ModBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
/**
 * Registry for all modded tile entities.
 */
public class ModTileEntities {
    //region registr
    public static DeferredRegister<TileEntityType<?>> TILE_ENTITIES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ExAqua.MOD_ID);
    public static void register(IEventBus eventBus) {
        TILE_ENTITIES.register(eventBus);
    }
    //endregion

    public static RegistryObject<TileEntityType<SqueezerTileEntity>> SQUEEZERTILE =
            TILE_ENTITIES.register("squeezer", () -> TileEntityType.Builder.create(
                    SqueezerTileEntity::new, ModBlocks.SQUEEZER.get()).build(null));

    public static RegistryObject<TileEntityType<SieveTileEntity>> SIEVERTILE =
            TILE_ENTITIES.register("sieve", () -> TileEntityType.Builder.create(
                    SieveTileEntity::new, ModBlocks.IRONSIEVE.get(),ModBlocks.GOLDSIEVE.get(),ModBlocks.FROGIUMSIEVE.get(),ModBlocks.DIAMONDSIEVE.get()).build(null));
    public static RegistryObject<TileEntityType<CauldronTileEntity>> CAULDRON_ENTITY =
            TILE_ENTITIES.register("cauldron", () -> TileEntityType.Builder.create(
                    CauldronTileEntity::new,ModBlocks.CAULDRON.get()).build(null));
    public static RegistryObject<TileEntityType<AutoSqueezerTileEntity>> AUTO_SQUEEZER_ENTITY =
            TILE_ENTITIES.register("auto_squeezer", () -> TileEntityType.Builder.create(
                    AutoSqueezerTileEntity::new,ModBlocks.AUTO_SQUEEZER.get()).build(null));




}
