package com.rutaji.exaqua.block;

import com.rutaji.exaqua.container.SieveContainer;
import com.rutaji.exaqua.tileentity.IMyLiquidTankTIle;
import com.rutaji.exaqua.tileentity.ModTileEntities;
import com.rutaji.exaqua.tileentity.SieveTileEntity;
import mekanism.api.math.FloatingLong;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class SieveBlock extends Block implements ILiquidContainer, IBucketPickupHandler {
    //region Constructor

    public SieveBlock(Properties properties, SieveTiers t) {
        super(properties);
        Tier = t;

    }
    //endregion
    //region tier
    private final SieveTiers Tier;
    public SieveTiers GetTier(){return  Tier;}
    //endregion

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos,
                                             PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);//todo move into if after debuging
        if(!worldIn.isRemote()) {



            if(tileEntity instanceof SieveTileEntity) {

                INamedContainerProvider containerProvider = createContainerProvider(worldIn, pos);

                NetworkHooks.openGui(((ServerPlayerEntity)player), containerProvider, tileEntity.getPos());
            } else {
                throw new IllegalStateException("Container provider is missing!");
            }
        }
        return ActionResultType.SUCCESS;
    }

    private INamedContainerProvider createContainerProvider(World worldIn, BlockPos pos) {
        return new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("screen.exaqua.sievecontainer");
            }

            @Nullable
            @Override
            public Container createMenu(int i, PlayerInventory inventory, PlayerEntity playerEn) {
                return new SieveContainer(i,worldIn,pos,inventory,playerEn);
            }
        };

    }
    //region bucket implementation
    @Override
    public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof IMyLiquidTankTIle){
            if(((IMyLiquidTankTIle) tileEntity).GetTank().getFluidAmount() >= 1000){
                return ((IMyLiquidTankTIle) tileEntity).GetTank().drain(1000, IFluidHandler.FluidAction.EXECUTE).getFluid();
            }
            return Fluids.EMPTY;
        }
        return Fluids.EMPTY;
    }
    @Override
    public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof IMyLiquidTankTIle)
        {
            return  ((IMyLiquidTankTIle)tileEntity).GetTank().CanTakeFluid(fluidIn);
        }
        return false;
    }

    @Override
    public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {

        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof IMyLiquidTankTIle)
        {
            ((IMyLiquidTankTIle)tileEntity).GetTank().AddBucket(fluidStateIn.getFluid());
        }
        return true;
    }
    //endregion

    //region TileEntity
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        TileEntity t = ModTileEntities.SIEVERTILE.get().create();
        if(t instanceof  SieveTileEntity){((SieveTileEntity) t).tier = this.GetTier();}
        return t;
    }
    //endregion

}
