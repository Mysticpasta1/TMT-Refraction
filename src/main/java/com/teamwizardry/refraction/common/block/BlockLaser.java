package com.teamwizardry.refraction.common.block;

import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer;
import com.teamwizardry.librarianlib.features.utilities.client.TooltipHelper;
import com.teamwizardry.refraction.api.CapsUtils;
import com.teamwizardry.refraction.api.Constants;
import com.teamwizardry.refraction.api.beam.IBeamImmune;
import com.teamwizardry.refraction.api.soundmanager.ISoundEmitter;
import com.teamwizardry.refraction.common.item.ItemScrewDriver;
import com.teamwizardry.refraction.common.tile.TileLaser;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.DirectionProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * Created by Demoniaque
 */
public class BlockLaser extends DirectionalBlock implements IBeamImmune, ISoundEmitter {

	public static final DirectionProperty FACING = DirectionalBlock.FACING;

	public BlockLaser(Properties properties) {
		//"laser", Material.IRON
		super(properties.hardnessAndResistance(1F, 1F)
				.sound(SoundType.METAL).tickRandomly());

		setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
	}

	private TileLaser getTE(World world, BlockPos pos) {
		return (TileLaser) world.getTileEntity(pos);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		TooltipHelper.addToTooltip(tooltip, "simple_name." + Constants.MOD_ID + ":" + getRegistryName().getPath());
	}

	@Override
	public boolean hasComparatorInputOverride(@Nonnull BlockState state) {
		return true;
	}

	@Override
	public int getComparatorInputOverride(@Nonnull BlockState blockState, @Nonnull World worldIn, @Nonnull BlockPos pos) {
		TileLaser te = getTE(worldIn, pos);
		if (!te.inventory.getHandler().getStackInSlot(0).isEmpty())
			return (int) (te.inventory.getHandler().getStackInSlot(0).getCount() / 64.0 * 15);
		else return 0;
	}

	@Override
	public ActionResultType onBlockActivated(@Nonnull BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull PlayerEntity playerIn, @Nonnull Hand hand, BlockRayTraceResult result) {
		ItemStack heldItem = playerIn.getHeldItem(hand);

		if (!heldItem.isEmpty()) {
			if (heldItem.getItem() != Items.GLOWSTONE_DUST) return ActionResultType.PASS;

			TileLaser laser = getTE(worldIn, pos);
			if (laser == null) return ActionResultType.PASS;
			ItemStack stack = heldItem.copy();
			stack.setCount(1);
			ItemStack left = laser.inventory.getHandler().insertItem(0, stack, false);
			if (left.isEmpty()) heldItem.setCount(heldItem.getCount() - 1);
			laser.tick();
		}
		return ActionResultType.SUCCESS;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		if (context.getPlacementYaw() > 45) return this.getDefaultState().with(FACING, Direction.UP);
		if (context.getPlacementYaw() < -45) return this.getDefaultState().with(FACING, Direction.DOWN);

		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileLaser();
	}

	@Override
	public boolean shouldEmit(@Nonnull World world, @Nonnull BlockPos pos) {
		TileLaser laser = (TileLaser) world.getTileEntity(pos);
		return !(world.isBlockPowered(pos) || world.getStrongPower(pos) > 0) && laser != null && !laser.inventory.getHandler().getStackInSlot(0).isEmpty();
	}

	@Override
	public boolean isToolEffective(BlockState state, ToolType tool) {
		return super.isToolEffective(state, tool) || Objects.equals(tool.getName(), ItemScrewDriver.SCREWDRIVER_TOOL_CLASS);
	}

	@Override
	public boolean isImmune(@Nonnull World world, @Nonnull BlockPos pos) {
		return true;
	}
}
