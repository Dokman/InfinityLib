package com.infinityraider.infinitylib.block;

import com.infinityraider.infinitylib.block.property.InfProperty;
import com.infinityraider.infinitylib.block.property.InfPropertyConfiguration;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BlockBase extends Block implements IInfinityBlock {
    private final String internalName;

    public BlockBase(String name, Properties properties) {
        super(properties);
        this.internalName = name;
        this.setDefaultState(this.getPropertyConfiguration().defineDefault(this.getStateContainer().getBaseState()));
    }

    @Override
    protected final void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        this.getPropertyConfiguration().fillStateContainer(builder);
    }

    protected abstract InfPropertyConfiguration getPropertyConfiguration();

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public final BlockState rotate(BlockState state, Rotation rot) {
        return this.getPropertyConfiguration().handleRotation(state, rot);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public final BlockState mirror(BlockState state, Mirror mirror) {
        return this.getPropertyConfiguration().handleMirror(state, mirror);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return this.getPropertyConfiguration().isWaterLoggable() && InfProperty.Defaults.waterlogged().fetch(state)
                ? Fluids.WATER.getStillFluidState(false)
                : super.getFluidState(state);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public BlockState updatePostPlacement(BlockState ownState, Direction dir, BlockState otherState, IWorld world, BlockPos pos, BlockPos otherPos) {
        if (this.getPropertyConfiguration().isWaterLoggable() && InfProperty.Defaults.waterlogged().fetch(ownState)) {
            world.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.updatePostPlacement(ownState, dir, otherState, world, pos, otherPos);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    @Nonnull
    public String getInternalName() {
        return this.internalName;
    }

    public final BlockState waterlog(BlockState state, World world, BlockPos pos) {
        if(this.getPropertyConfiguration().isWaterLoggable()) {
            FluidState fluid = world.getFluidState(pos);
            state = InfProperty.Defaults.waterlogged().apply(state, fluid.getFluid() == Fluids.WATER);
        }
        return state;
    }

    public boolean addToInventoryOrDrop(ItemStack stack, World world, BlockPos pos, @Nullable PlayerEntity player) {
        if(player != null) {
            if(player.addItemStackToInventory(stack)) {
                return true;
            }
        }
        ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
        return world.addEntity(entity);
    }
}
