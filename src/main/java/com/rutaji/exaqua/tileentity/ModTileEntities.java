package com.rutaji.exaqua.tileentity;

import com.rutaji.exaqua.ExAqua;
import com.rutaji.exaqua.block.ModBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {
    //region registr
    public static DeferredRegister<TileEntityType<?>> TILE_ENTITIES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ExAqua.MOD_ID);
    public static void register(IEventBus eventBus) {
        TILE_ENTITIES.register(eventBus);
    }
    //endregion

    public static RegistryObject<TileEntityType<SqueezerTile>> SQUEEZERTILE =
            TILE_ENTITIES.register("squeezer", () -> TileEntityType.Builder.create(
                    SqueezerTile::new, ModBlocks.SQUEEZER.get()).build(null));

    public static RegistryObject<TileEntityType<SieveTileEntity>> SIEVERTILE =
            TILE_ENTITIES.register("sieve", () -> TileEntityType.Builder.create(
                    SieveTileEntity::new, ModBlocks.SIEVE.get()).build(null));




}
