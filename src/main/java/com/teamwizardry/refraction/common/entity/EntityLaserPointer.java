package com.teamwizardry.refraction.common.entity;

import com.google.common.collect.ImmutableList;
import com.teamwizardry.librarianlib.features.base.entity.LivingBaseEntityMod;
import com.teamwizardry.refraction.common.item.ItemLaserPen;
import com.teamwizardry.refraction.init.ModItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.UUID;


/**
 * Created by TheCodeWarrior
 */
public class EntityLaserPointer extends LivingEntity implements IEntityAdditionalSpawnData {
	public static final DataParameter<Byte> AXIS_HIT = EntityDataManager.createKey(EntityLaserPointer.class, DataSerializers.BYTE);
	public static final DataParameter<Boolean> HAND_HIT = EntityDataManager.createKey(EntityLaserPointer.class, DataSerializers.BOOLEAN);

	private WeakReference<PlayerEntity> player;

	public EntityLaserPointer(EntityType<EntityLaserPointer> laserPointer, World worldIn, PlayerEntity player, boolean hit) {
		super(laserPointer, worldIn);
		this.player = new WeakReference<>(player);
		dataManager.set(HAND_HIT, hit);
	}

	public EntityLaserPointer(EntityType<EntityLaserPointer> laserPointer, World worldIn) {
		super(laserPointer, worldIn);
	}

	@Override
	public EntitySize getSize(Pose pose) {
		return EntitySize.fixed(0.1F, 0.1F);
	}

	@Override
	protected void damageEntity(@Nonnull DamageSource damageSrc, float damageAmount) {
		// noop
	}

	@Override
	public boolean isInRangeToRenderDist(double distance) {
		return true;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public void onUpdate() {
		updateRayPos();
	}

	public void updateRayPos() {
		if (player == null || player.get() == null) {
			this.setDead();
		} else if (Objects.requireNonNull(player.get()).getActiveItemStack().isEmpty() || player.get().getActiveItemStack().getItem() != ModItems.LASER_PEN) {
			this.setDead();
		} else {
			RayTraceResult res = rayTrace(player.get(), ItemLaserPen.RANGE);
			Vec3d pos = null;
			if (res != null) {
				pos = res.hitVec;
				this.markPotionsDirty();
				this.dataManager.set(AXIS_HIT, (byte) res.sideHit.getAxis().ordinal());
			} else {
				pos = player.get().getLook(1).scale(ItemLaserPen.RANGE).add(player.get().getPositionEyes(1));
				this.markPotionsDirty();
				this.dataManager.set(AXIS_HIT, (byte) 255);

			}
			this.setPositionAndUpdate(pos.x, pos.y, pos.z);
		}
	}

	public RayTraceResult rayTrace(EntityPlayer player, double blockReachDistance) {
		Vec3d cross = player.getLook(1).crossProduct(new Vec3d(0, player.getEyeHeight(), 0)).normalize().scale(player.width / 2);
		if (!dataManager.get(HAND_HIT)) cross = cross.scale(-1);
		Vec3d vec3d = new Vec3d(player.posX + cross.x, player.posY + player.getEyeHeight() + cross.y, player.posZ + cross.z);
		Vec3d vec3d1 = this.getVectorForRotation(player.rotationPitch, player.rotationYawHead);
		Vec3d vec3d2 = vec3d.add(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
		return player.world.rayTraceBlocks(vec3d, vec3d2, false, false, true);
	}

	@Nonnull
	@Override
	public EnumHandSide getPrimaryHand() {
		return null;
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(AXIS_HIT, (byte) 0);
		this.dataManager.register(HAND_HIT, false);
	}

	@Nonnull
	@Override
	public Iterable<ItemStack> getArmorInventoryList() {
		return ImmutableList.of();
	}

	@Nonnull
	@Override
	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
		return ItemStack.EMPTY;
	}


	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slotIn, @Nullable ItemStack stack) {

	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		boolean b = player == null || player.get() == null;
		buffer.writeBoolean(b);
		if (!b) {
			EntityPlayer p = player.get();
			if (p != null) {
				buffer.writeLong(p.getPersistentID().getMostSignificantBits());
				buffer.writeLong(p.getPersistentID().getLeastSignificantBits());
			}
		}
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		boolean b = buffer.readBoolean();
		if (!b) {
			UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
			player = new WeakReference<>(world.getPlayerEntityByUUID(uuid));
		}
	}
}
