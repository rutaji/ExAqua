package com.rutaji.exaqua.networking;

import com.rutaji.exaqua.tileentity.SqueezerTile;
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MyFluidStackPacket {

    public FluidStack PacketStack;
    public BlockPos pos;
    public MyFluidStackPacket(FluidStack f,BlockPos p) {
        PacketStack = f;
        pos = p;
    }

    public static MyFluidStackPacket fromBytes(PacketBuffer buffer) {
       return new MyFluidStackPacket(FluidStack.readFromPacket(buffer),buffer.readBlockPos());
    }

    public static void toBytes(MyFluidStackPacket packet, PacketBuffer buffer) {
        packet.PacketStack.writeToPacket(buffer);
        buffer.writeBlockPos(packet.pos);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        System.out.println("handle");

        TileEntity tileEntity = Minecraft.getInstance().world.getTileEntity(pos);
        if ( tileEntity instanceof  SqueezerTile) {
            ((SqueezerTile)tileEntity).Tank.setStack(PacketStack);
        }
    }
}

