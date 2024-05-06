package com.teamwizardry.refraction.init;

import com.teamwizardry.refraction.common.item.*;
import com.teamwizardry.refraction.common.item.armor.ItemArmorReflectiveAlloy;
import net.minecraft.inventory.EquipmentSlotType;

/**
 * Created by Demoniaque
 */
public class ModItems {

	public static ItemLaserPen LASER_PEN;
	public static ItemScrewDriver SCREW_DRIVER;
	public static ItemReflectiveAlloy REFLECTIVE_ALLOY;
	public static ItemBook BOOK;
	public static ItemGrenade GRENADE;
	public static ItemPhotonCannon PHOTON_CANNON;
	public static ItemArmorReflectiveAlloy HELMET;
	public static ItemArmorReflectiveAlloy CHESTPLATE;
	public static ItemArmorReflectiveAlloy LEGGINGS;
	public static ItemArmorReflectiveAlloy BOOTS;
	public static ItemLightCartridge LIGHT_CARTRIDGE;

	public static void init() {
		LASER_PEN = new ItemLaserPen();
		SCREW_DRIVER = new ItemScrewDriver();
		REFLECTIVE_ALLOY = new ItemReflectiveAlloy();
		BOOK = new ItemBook();
		GRENADE = new ItemGrenade();
		PHOTON_CANNON = new ItemPhotonCannon();
		HELMET = new ItemArmorReflectiveAlloy("ref_alloy_helmet",EquipmentSlotType.HEAD);
		CHESTPLATE = new ItemArmorReflectiveAlloy("ref_alloy_chestplate", EquipmentSlotType.CHEST);
		LEGGINGS = new ItemArmorReflectiveAlloy("ref_alloy_leggings", EquipmentSlotType.LEGS);
		BOOTS = new ItemArmorReflectiveAlloy("ref_alloy_boots", EquipmentSlotType.FEET);
		LIGHT_CARTRIDGE = new ItemLightCartridge();
	}

	public static void initModels() {
		HELMET.initModel();
		CHESTPLATE.initModel();
		LEGGINGS.initModel();
		BOOTS.initModel();
	}
}
