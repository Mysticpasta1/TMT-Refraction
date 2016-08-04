package com.teamwizardry.refraction.common.item;

import com.teamwizardry.refraction.Refraction;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by LordSaad44
 */
public class ItemScrewDriver extends Item {

	public ItemScrewDriver() {
		setRegistryName("screw_driver");
		setUnlocalizedName("screw_driver");
		GameRegistry.register(this);
		setMaxStackSize(1);
		setCreativeTab(Refraction.tab);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
}