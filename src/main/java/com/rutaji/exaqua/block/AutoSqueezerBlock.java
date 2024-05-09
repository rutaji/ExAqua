package com.rutaji.exaqua.block;

import com.rutaji.exaqua.container.AutoSqueezerContainer;
import com.rutaji.exaqua.tileentity.AutoSqueezerTileEntity;
import com.rutaji.exaqua.tileentity.IMyLiquidTankTile;
import com.rutaji.exaqua.tileentity.ModTileEntities;
import net.minecraft.block.*;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class AutoSqueezerBlock extends Block implements IBucketPickupHandler, ILiquidContainer {

    //region constructor
    public AutoSqueezerBlock(AbstractBlock.Properties properties) {
        super(properties);
    }
    //endregion

    //region model
    /**
     * Shape of a model.
     */
    public final VoxelShape SHAPE = Stream.of(
            Block.makeCuboidShape(1, 0, 1, 15, 1, 15),
            Block.makeCuboidShape(1, 1, 1, 2, 10, 2),
            Block.makeCuboidShape(14, 1, 1, 15, 10, 2),
            Block.makeCuboidShape(14, 1, 14, 15, 10, 15),
            Block.makeCuboidShape(1, 1, 14, 2, 10, 15),
            Block.makeCuboidShape(1, 10, 1, 15, 13, 15),
            Block.makeCuboidShape(4, 6, 4, 12, 10, 12),
            Block.makeCuboidShape(4, 1, 4, 12, 5, 12)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();


    /**
     * @return {@link AutoSqueezerBlock#SHAPE Shape} of a model.
     */
    public @NotNull VoxelShape getShape(@NotNull BlockState blockState, @NotNull IBlockReader worlIn, @NotNull BlockPos pos, @NotNull ISelectionContext context)
    {
        return SHAPE;
    }
    //endregion

    /**
     * Called when player interacts with a block. Opens UI.
     * @param state block state of interacted block.
     * @param worldIn world of interacted block.
     * @param pos position of interacted block.
     * @param player player which interacted with the block.
     * @param handIn hand of the player.
     * @param hit ray trace result.
     * @return success if no exception was thrown.
     * @exception IllegalStateException if block's tile entity isn't instance of {@link AutoSqueezerTileEntity AutoSqueezerTileEntity}.
     */
    @Override
    public @NotNull ActionResultType onBlockActivated(@NotNull BlockState state, World worldIn, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand handIn, @NotNull BlockRayTraceResult hit) {
        if(!worldIn.isRemote()) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if(tileEntity instanceof AutoSqueezerTileEntity) {

                INamedContainerProvider containerProvider = createContainerProvider(worldIn, pos);
                NetworkHooks.openGui(((ServerPlayerEntity) player), containerProvider, tileEntity.getPos());

            } else {
                throw new IllegalStateException("Wrong TileEntity!");
            }
        }
        return ActionResultType.SUCCESS;
    }
    //region UI

    /**
     * Creates container provider, that provides {@link AutoSqueezerContainer AutoSqueezerContainer}
     * @return {@link AutoSqueezerContainer AutoSqueezerContainer} provider.
     */
    private INamedContainerProvider createContainerProvider(World worldIn, BlockPos pos) {
        return new INamedContainerProvider() {
            @Override
            public @NotNull ITextComponent getDisplayName() {
                return new TranslationTextComponent("screen.exaqua.autosqueezercontainer");
            }
            @Override
            public Container createMenu(int i, @NotNull PlayerInventory inventory, @NotNull PlayerEntity playerEn) {
                return new AutoSqueezerContainer(i,worldIn,pos,inventory,playerEn);
            }
        };

    }
    //endregion
    //region tile entity

    /**
     * @return true.
     */
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    /**
     * Returns tile entity of this block, {@link AutoSqueezerTileEntity AutoSqueezerTileEntity}. All tile entities all registered in {@link ModTileEntities ModTileEntities}.
     * @return tile entity of this block.
     */
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.AUTO_SQUEEZER_ENTITY.get().create();
    }
    //endregion
    //region bucket implementation

    /**
     * Picks up one bucket of fluid from the block. If block doesn't contain any fluid returns empty. If block contains less than one bucket returns empty.
     * If the block doesn't implement {@link IMyLiquidTankTile IMyLiquidTankTile} returns false.
     * @return picked up fluid. Can be empty.
     */
    @Override
    public @NotNull Fluid pickupFluid(IWorld worldIn, @NotNull BlockPos pos, @NotNull BlockState state) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof IMyLiquidTankTile){
            if(((IMyLiquidTankTile) tileEntity).GetTank().getFluidAmount() >= 1000){
                return ((IMyLiquidTankTile) tileEntity).GetTank().drain(1000, IFluidHandler.FluidAction.EXECUTE).getFluid();
            }
            return Fluids.EMPTY;
        }
        return Fluids.EMPTY;
    }

    /**
     * Returns true if block can contained provided fluid. If tile entity of this block doesn't implement {@link IMyLiquidTankTile IMyLiquidTankTile} always returns false.
     * @return true if block can contained provided fluid.
     */
    @Override
    public boolean canContainFluid(IBlockReader worldIn, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Fluid fluidIn) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof IMyLiquidTankTile)
        {
            return  ((IMyLiquidTankTile)tileEntity).GetTank().isFluidValid(new FluidStack(fluidIn,1000));
        }
        return false;
    }

    /**
     * Adds 1 bucket of fluid into tile entity and returns true. If fluid is not source, no fluid will be recieved and returns false;
     * If tile entity doesn't implement {@link IMyLiquidTankTile IMyLiquidTankTile} block cannot store fluids and returns false.
     * @return true if successfully stored fluid, otherwise false.
     */
    @Override
    public boolean receiveFluid(IWorld worldIn, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull FluidState fluidStateIn) {

        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (fluidStateIn.isSource() && tileEntity instanceof IMyLiquidTankTile)
        {
            ((IMyLiquidTankTile)tileEntity).GetTank().AddBucket(fluidStateIn.getFluid());
            return true;
        }
        return false;
    }
    //endregion
}
