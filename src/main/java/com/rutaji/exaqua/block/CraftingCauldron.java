package com.rutaji.exaqua.block;

import com.rutaji.exaqua.container.CauldronContainer;
import com.rutaji.exaqua.container.SqueezerContainer;
import com.rutaji.exaqua.tileentity.*;
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
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class CraftingCauldron extends Block implements ILiquidContainer, IBucketPickupHandler {
    public CraftingCauldron(Properties properties) {
        super(properties);
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!worldIn.isRemote()) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if(tileEntity instanceof CauldronEntity) {
                //region UI
                INamedContainerProvider containerProvider = createContainerProvider(worldIn, pos);
                NetworkHooks.openGui(((ServerPlayerEntity) player), containerProvider, tileEntity.getPos());
                //endregion
            } else {
                throw new IllegalStateException("Wrong TileEntity!");
            }
        }
        return ActionResultType.SUCCESS;
    }
    //region UI
    private INamedContainerProvider createContainerProvider(World worldIn, BlockPos pos) {
        return new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("");
            }
            @Nullable
            @Override
            public Container createMenu(int i, PlayerInventory inventory, PlayerEntity playerEn) {
                return new CauldronContainer(i,worldIn,pos,inventory,playerEn);
            }
        };

    }
    //endregion
    //region model
    public final VoxelShape SHAPE=  Stream.of(
            Block.makeCuboidShape(0, 1, 12, 2, 10, 14),
            Block.makeCuboidShape(2, 1, 14, 4, 10, 16),
            Block.makeCuboidShape(14, 1, 12, 16, 10, 14),
            Block.makeCuboidShape(12, 1, 14, 14, 10, 16),
            Block.makeCuboidShape(2, 1, 0, 4, 10, 2),
            Block.makeCuboidShape(0, 1, 2, 2, 10, 4),
            Block.makeCuboidShape(12, 1, 0, 14, 10, 2),
            Block.makeCuboidShape(14, 1, 2, 16, 10, 4),
            Block.makeCuboidShape(2, 1, 2, 3, 16, 14),
            Block.makeCuboidShape(13, 1, 2, 14, 16, 14),
            Block.makeCuboidShape(2, 1, 2, 14, 16, 3),
            Block.makeCuboidShape(2, 1, 13, 14, 16, 14),
            Block.makeCuboidShape(0, 0, 0, 16, 1, 16)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();
    public VoxelShape getShape(BlockState blockState, IBlockReader worlIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
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
        return ModTileEntities.CAULDRON_ENTITY.get().create();
    }
    //endregion
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

}
