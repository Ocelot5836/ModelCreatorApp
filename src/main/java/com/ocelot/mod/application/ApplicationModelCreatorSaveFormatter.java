package com.ocelot.mod.application;

import java.util.ArrayList;
import java.util.List;

import com.ocelot.api.geometry.Cube;
import com.ocelot.api.geometry.ModelData;
import com.ocelot.api.utils.NamedBufferedImage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public class ApplicationModelCreatorSaveFormatter {

	public static final String MODEL_CREATOR_SAVE_VERSION_10 = "1.0";
	public static final String MODEL_CREATOR_SAVE_VERSION_11 = "1.1";

	public static NBTTagCompound convert10to11(NBTTagCompound nbt) {
		NBTTagCompound converted = new NBTTagCompound();

		List<NamedBufferedImage> textures = new ArrayList<NamedBufferedImage>();
		List<Cube> cubes = new ArrayList<Cube>();
		boolean ambientOcclusion = false;
		NamedBufferedImage particle = null;

		if (nbt.hasKey("textures", Constants.NBT.TAG_LIST)) {
			NBTTagList texturesTag = nbt.getTagList("textures", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < texturesTag.tagCount(); i++) {
				textures.add(new NamedBufferedImage(texturesTag.getCompoundTagAt(i)));
			}
		}

		if (nbt.hasKey("cubes", Constants.NBT.TAG_LIST)) {
			NBTTagList cubesTag = nbt.getTagList("cubes", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < cubesTag.tagCount(); i++) {
				cubes.add(new Cube(cubesTag.getCompoundTagAt(i), textures));
			}
		}

		ambientOcclusion = nbt.getBoolean("ambientOcclusion");

		if (nbt.hasKey("particle", Constants.NBT.TAG_COMPOUND)) {
			particle = new NamedBufferedImage(nbt.getCompoundTag("particle"));
		}

		converted.setString("version", MODEL_CREATOR_SAVE_VERSION_11);
		converted.setTag("modelData", new ModelData(cubes, ambientOcclusion, particle).serializeNBT());

		return converted;
	}
}