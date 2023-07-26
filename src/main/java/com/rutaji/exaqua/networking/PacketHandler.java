package com.rutaji.exaqua.networking;

import com.rutaji.exaqua.ExAqua;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static int id(){return idPacket++;}
    private static int idPacket = 0;
    public static SimpleChannel CHANNEL;

    public static void init() {
        CHANNEL = NetworkRegistry.newSimpleChannel(
                // Create a ResourceLocation for your mod's channel
                new ResourceLocation(ExAqua.MOD_ID, "packethandler"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );
        // Register your packet class with the channel
        CHANNEL.registerMessage(
                id(),
                MyFluidStackPacket.class,
                MyFluidStackPacket::toBytes,
                MyFluidStackPacket::fromBytes,
                MyFluidStackPacket::handle

        );
    }


}
