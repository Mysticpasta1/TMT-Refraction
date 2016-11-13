package com.teamwizardry.refraction.common.block;

import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.client.util.TooltipHelper;
import com.teamwizardry.librarianlib.common.base.block.BlockMod;
import com.teamwizardry.refraction.api.IOpticConnectable;
import com.teamwizardry.refraction.api.PosUtils;
import com.teamwizardry.refraction.common.light.Beam;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author WireSegal
 *         Created at 10:33 PM on 10/31/16.
 */
public class BlockTranslocator extends BlockMod implements IOpticConnectable {

    public static final PropertyDirection DIRECTION = PropertyDirection.create("side");
    public static final PropertyBool CONNECTED = PropertyBool.create("connected");

    private static final AxisAlignedBB DOWN_AABB  = new AxisAlignedBB(1 / 16.0, 0, 1 / 16.0, 15 / 16.0, 10 / 16.0, 15 / 16.0);
    private static final AxisAlignedBB UP_AABB    = new AxisAlignedBB(1 / 16.0, 6  / 16.0, 1 / 16.0, 15 / 16.0, 1, 15 / 16.0);
    private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(1 / 16.0, 1 / 16.0, 0, 15 / 16.0, 15 / 16.0, 10 / 16.0);
    private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(1 / 16.0, 1 / 16.0, 6  / 16.0, 15 / 16.0, 15 / 16.0, 1);
    private static final AxisAlignedBB WEST_AABB  = new AxisAlignedBB(0, 1 / 16.0, 1 / 16.0, 10 / 16.0, 15 / 16.0, 15 / 16.0);
    private static final AxisAlignedBB EAST_AABB  = new AxisAlignedBB(6  / 16.0, 1 / 16.0, 1 / 16.0, 1, 15 / 16.0, 15 / 16.0);

    public BlockTranslocator() {
        super("translocator", Material.GLASS);
        setHardness(1F);
        setSoundType(SoundType.GLASS);
    }

    @Nonnull
    @Override
    public List<EnumFacing> getAvailableFacings(@NotNull IBlockState state, @NotNull IBlockAccess source, @NotNull BlockPos pos, @NotNull EnumFacing facing) {
        if (facing != state.getValue(DIRECTION)) return Lists.newArrayList();
        return Lists.newArrayList(state.getValue(DIRECTION).getOpposite());
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DIRECTION, CONNECTED);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        IBlockState fiber = worldIn.getBlockState(pos.offset(state.getValue(DIRECTION).getOpposite()));
        return state.withProperty(CONNECTED,
                fiber.getBlock() instanceof BlockOpticFiber &&
                fiber.getValue(BlockOpticFiber.FACING).contains(state.getValue(DIRECTION)));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(DIRECTION, EnumFacing.VALUES[meta % EnumFacing.VALUES.length]);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(DIRECTION)) {
            case DOWN:
                return DOWN_AABB;
            case UP:
                return UP_AABB;
            case NORTH:
                return NORTH_AABB;
            case SOUTH:
                return SOUTH_AABB;
            case WEST:
                return WEST_AABB;
            case EAST:
                return EAST_AABB;
            default:
                return NULL_AABB;
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(DIRECTION).getIndex();
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced)
    {
    	TooltipHelper.addToTooltip(tooltip, "simple_name.refraction:" + getRegistryName().getResourcePath());
    }
    
    @Override
    public boolean canRenderInLayer(BlockRenderLayer layer)
    {
    	return layer == BlockRenderLayer.CUTOUT;
    }

    @Override
    public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
    	return getDefaultState().withProperty(DIRECTION, facing.getOpposite());
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }

    @Override
    public void handleFiberBeam(@NotNull World world, @NotNull BlockPos pos, @NotNull Beam beam) {
        IBlockState state = world.getBlockState(pos);
        EnumFacing dir = state.getValue(BlockTranslocator.DIRECTION);
        if (!beam.slope.equals(PosUtils.getVecFromFacing(dir)))
            return;
        if (!world.isAirBlock(pos.offset(dir)))
        {
            Vec3d slope = beam.slope.normalize().scale(15.0/16.0);
            beam.createSimilarBeam(PosUtils.getSideCenter(pos, dir).add(slope), PosUtils.getVecFromFacing(dir)).spawn();
        }
        else beam.createSimilarBeam(PosUtils.getSideCenter(pos, dir), PosUtils.getVecFromFacing(dir)).spawn();
    }
}