package com.rutaji.exaqua.networking;

import com.rutaji.exaqua.ExAqua;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static int id(){return idPacket++;}
    private static int idPacket = 0;
    public static SimpleChannel CHANNEL;

    public static void init() {
        CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(ExAqua.MOD_ID, "packethandler"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );
        // Register packets here
        CHANNEL.registerMessage(
                id(),
                MyFluidStackPacket.class,
                MyFluidStackPacket::toBytes,
                MyFluidStackPacket::fromBytes,
                MyFluidStackPacket::handle

        );
        CHANNEL.registerMessage(
                id(),
                MyEnergyPacket.class,
                MyEnergyPacket::toBytes,
                MyEnergyPacket::fromBytes,
                MyEnergyPacket::handle

        );
    }


}
