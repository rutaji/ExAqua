package com.rutaji.exaqua.networking;

import com.rutaji.exaqua.tileentity.IMYEnergyStorageTile;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MyEnergyPacket
{
    public int EnergyInPacket;
    public BlockPos pos;
    //region Constructor
    public MyEnergyPacket(int energy, BlockPos p) {
        EnergyInPacket = energy;
        pos = p;
    }
    //endregion

    public static MyEnergyPacket fromBytes(PacketBuffer buffer) {
        return new MyEnergyPacket(buffer.readInt(),buffer.readBlockPos());
    }

    public static void toBytes(MyEnergyPacket packet, PacketBuffer buffer) {
        buffer.writeInt(packet.EnergyInPacket);
        buffer.writeBlockPos(packet.pos);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        TileEntity tileEntity = Minecraft.getInstance().world.getTileEntity(pos);
        if ( tileEntity instanceof IMYEnergyStorageTile) {
            ((IMYEnergyStorageTile)tileEntity).GetEnergyStorage().setEnergy(EnergyInPacket);
            context.get().setPacketHandled(true);
        }
    }
}
