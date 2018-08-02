package com.ocelot.mod.lib;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

public class ModelCreatorFileConverter {
	
	public static final String MODEL_CREATOR_SAVE_VERSION_10 = "1.0";
	public static final String MODEL_CREATOR_SAVE_VERSION_11 = "1.1";
	public static final String MODEL_CREATOR_SAVE_VERSION_12 = "1.2";

	public static NBTTagCompound convert10To11(NBTTagCompound nbt) {
		NBTTagCompound newNbt = nbt.copy();
		newNbt.setString("version", "1.1");
		NBTTagList oldCubes = nbt.getTagList("cubes", Constants.NBT.TAG_COMPOUND);
		NBTTagList cubes = new NBTTagList();
		for (NBTBase base : oldCubes) {
			if(base instanceof NBTTagCompound) {
				cubes.appendTag(convert10CubeTo11Cube((NBTTagCompound) base));
			}
		}
		newNbt.setTag("cubes", cubes);
		return newNbt;
	}
	
	public static NBTTagCompound convert11To12(NBTTagCompound nbt) {
		NBTTagCompound newNbt = nbt.copy();
		return newNbt;
	}

	private static NBTTagCompound convert10CubeTo11Cube(NBTTagCompound nbt) {
		NBTTagCompound newNbt = nbt.copy();

		for (int i = 0; i < EnumFacing.values().length; i++) {
			EnumFacing facing = EnumFacing.values()[i];
			if (newNbt.hasKey(facing.getName2(), Constants.NBT.TAG_COMPOUND)) {
				newNbt.setTag(facing.getName2(), convert10FaceTo11Face(newNbt.getCompoundTag(facing.getName2())));
			}
		}

		return newNbt;
	}

	private static NBTTagCompound convert10FaceTo11Face(NBTTagCompound nbt) {
		NBTTagCompound newNbt = nbt.copy();
		if (nbt.hasKey("texture", Constants.NBT.TAG_STRING)) {
			newNbt.setTag("texture", NBTHelper.setBufferedImage(Lib.loadImage(new ResourceLocation(nbt.getString("texture")))));
		}
		return newNbt;
	}
}