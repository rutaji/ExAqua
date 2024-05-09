package com.rutaji.exaqua.block;

import com.rutaji.exaqua.container.AutoSqueezerContainer;
import com.rutaji.exaqua.container.SieveContainer;
import com.rutaji.exaqua.others.SieveTiers;
import com.rutaji.exaqua.tileentity.AutoSqueezerTileEntity;
import com.rutaji.exaqua.tileentity.IMyLiquidTankTile;
import com.rutaji.exaqua.tileentity.ModTileEntities;
import com.rutaji.exaqua.tileentity.SieveTileEntity;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class SieveBlock extends Block implements ILiquidContainer, IBucketPickupHandler {
    //region Constructor

    public SieveBlock(Properties properties, SieveTiers tier) {
        super(properties);
        Tier = tier;

    }
    //endregion
    //region model
    /**
     * Shape of a model.
     */
    private static final VoxelShape SHAPE = Stream.of(
            Block.makeCuboidShape(0, 0, 0, 2, 10, 2),
            Block.makeCuboidShape(14, 0, 0, 16, 10, 2),
            Block.makeCuboidShape(14, 0, 14, 16, 10, 16),
            Block.makeCuboidShape(0, 0, 14, 2, 10, 16),
            Block.makeCuboidShape(1, 10, 1, 15, 15, 15),
            Block.makeCuboidShape(14, 10, 0, 16, 16, 2),
            Block.makeCuboidShape(0, 10, 0, 2, 16, 2),
            Block.makeCuboidShape(0, 10, 14, 2, 16, 16),
            Block.makeCuboidShape(14, 10, 14, 16, 16, 16)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();
    /**
     * @return {@link AutoSqueezerBlock#SHAPE Shape} of a model.
     */
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState blockState, @NotNull IBlockReader worlIn, @NotNull BlockPos pos, @NotNull ISelectionContext context)
    {
        return SHAPE;
    }
    //endregion
    //region tier
    private final SieveTiers Tier;

    /**
     * @return tier of this block.
     * @see SieveTiers
     */
    public SieveTiers GetTier(){return  Tier;}
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
     * @exception IllegalStateException if block's tile entity isn't instance of {@link SieveTileEntity SieveTileEntity}.
     */
    @Override
    public @NotNull ActionResultType onBlockActivated(@NotNull BlockState state, World worldIn, @NotNull BlockPos pos,
                                                      @NotNull PlayerEntity player, @NotNull Hand handIn, @NotNull BlockRayTraceResult hit)
    {
        if(!worldIn.isRemote()) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if(tileEntity instanceof SieveTileEntity) {

                INamedContainerProvider containerProvider = createContainerProvider(worldIn, pos);

                NetworkHooks.openGui(((ServerPlayerEntity)player), containerProvider, tileEntity.getPos());
            } else {
                throw new IllegalStateException("Container provider is missing!");
            }
        }
        return ActionResultType.SUCCESS;
    }

    /**
     * Creates container provider, that provides {@link SieveContainer SieveContainer}
     * @return {@link SieveContainer SieveContainer} provider.
     */
    private INamedContainerProvider createContainerProvider(World worldIn, BlockPos pos) {
        return new INamedContainerProvider() {
            @Override
            public @NotNull ITextComponent getDisplayName() {
                return new TranslationTextComponent("screen.exaqua.sievecontainer");
            }

            @Override
            public Container createMenu(int i, @NotNull PlayerInventory inventory, @NotNull PlayerEntity playerEn) {
                return new SieveContainer(i,worldIn,pos,inventory,playerEn);
            }
        };

    }
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

    //region TileEntity

    /**
     * @return true
     */
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
    /**
     * Returns tile entity of this block, {@link SieveTileEntity SieveTileEntity}. All tile entities all registered in {@link ModTileEntities ModTileEntities}.
     * Also sets sieve tier in the tile entity to match sieve tier of this block.
     * @return tile entity of this block.
     * @see SieveTiers
     */
    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        TileEntity t = ModTileEntities.SIEVERTILE.get().create();
        if(t instanceof  SieveTileEntity){((SieveTileEntity) t).SetTier(this.GetTier());}
        return t;
    }
    //endregion

}
