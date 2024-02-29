package com.rutaji.exaqua.networking;

import com.rutaji.exaqua.tileentity.IMYEnergyStorageTile;
import mekanism.api.math.FloatingLong;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MyEnergyPacket
{
    public FloatingLong PacketStack;
    public BlockPos pos;
    //region Constructor
    public MyEnergyPacket(FloatingLong energy, BlockPos p) {
        PacketStack = energy;
        pos = p;
    }
    //endregion

    public static MyEnergyPacket fromBytes(PacketBuffer buffer) {
        return new MyEnergyPacket(FloatingLong.readFromBuffer(buffer),buffer.readBlockPos());
    }

    public static void toBytes(MyEnergyPacket packet, PacketBuffer buffer) {
        packet.PacketStack.writeToBuffer(buffer);
        buffer.writeBlockPos(packet.pos);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        TileEntity tileEntity = Minecraft.getInstance().world.getTileEntity(pos);
        if ( tileEntity instanceof IMYEnergyStorageTile) {
            ((IMYEnergyStorageTile)tileEntity).GetEnergyStorage().setEnergy(PacketStack);
        }
    }
}
