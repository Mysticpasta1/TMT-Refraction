package com.teamwizardry.refraction.client.render;

import com.teamwizardry.refraction.api.ConfigValues;
import com.teamwizardry.refraction.client.core.RenderLaserUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by TheCodeWarrior
 */
public class LaserRenderer {
	public static final LaserRenderer INSTANCE = new LaserRenderer();

	protected Map<LaserRenderInfo, Integer> lasers = new ConcurrentHashMap<>();

	private LaserRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static void add(Vec3d start, Vec3d end, Color color) {
		INSTANCE.lasers.put(new LaserRenderInfo(start, end, color), ConfigValues.BEAM_PARTICLE_LIFE);
	}

	@SubscribeEvent
	public void unload(WorldEvent.Unload event) {
		lasers.clear();
	}

	@SubscribeEvent
	public void load(WorldEvent.Load event) {
		lasers.clear();
	}

	@SubscribeEvent
	public void renderWorldLast(RenderWorldLastEvent event) {
		EntityPlayer rootPlayer = Minecraft.getMinecraft().player;
		double x = rootPlayer.lastTickPosX + (rootPlayer.posX - rootPlayer.lastTickPosX) * event.getPartialTicks();
		double y = rootPlayer.lastTickPosY + (rootPlayer.posY - rootPlayer.lastTickPosY) * event.getPartialTicks();
		double z = rootPlayer.lastTickPosZ + (rootPlayer.posZ - rootPlayer.lastTickPosZ) * event.getPartialTicks();

		GlStateManager.disableCull();
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(GL11.GL_GEQUAL, 1f / 255f);
		if (ConfigValues.ADDITIVE_BLENDING) {
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		}
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bb = tessellator.getBuffer();
		bb.setTranslation(-x, -y, -z);
		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

		// vvv actual rendering stuff vvv

		for (LaserRenderInfo info : lasers.keySet())
			RenderLaserUtil.renderLaser(info.color, info.start, info.end, bb);

		tessellator.draw();
		bb.setTranslation(0, 0, 0);
		if (ConfigValues.ADDITIVE_BLENDING) {
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		}
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
	}

	@SubscribeEvent
	public void tick(TickEvent.ClientTickEvent event) {
		if (event.phase == TickEvent.Phase.START) {
//			update();
		}
	}

	public void update() {
		for(LaserRenderInfo info : lasers.keySet()) {
			if (lasers.get(info) <= 0) {
				lasers.remove(info);
			} else {
				lasers.put(info, lasers.get(info) - 1);
			}
		}
	}

	public static class LaserRenderInfo {
		public Vec3d start, end;
		public Color color;

		public LaserRenderInfo(Vec3d start, Vec3d end, Color color) {
			this.start = start;
			this.end = end;
			this.color = color == null ? Color.WHITE : color;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			LaserRenderInfo that = (LaserRenderInfo) o;

			if (!start.equals(that.start)) return false;
			if (!end.equals(that.end)) return false;
			return color.equals(that.color);

		}

		@Override
		public int hashCode() {
			int result = start.hashCode();
			result = 31 * result + end.hashCode();
			result = 31 * result + color.hashCode();
			return result;
		}
	}
}
