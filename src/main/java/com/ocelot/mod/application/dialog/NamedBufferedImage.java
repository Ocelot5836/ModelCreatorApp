package com.ocelot.mod.application.dialog;

import java.awt.image.BufferedImage;

import com.ocelot.api.libs.NBTHelper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public class NamedBufferedImage implements INBTSerializable<NBTTagCompound> {

	private BufferedImage image;
	private ResourceLocation location;
	private boolean hasTransparency;

	private NamedBufferedImage() {
	}

	public NamedBufferedImage(BufferedImage image, ResourceLocation location) {
		this.image = image;
		this.location = location;
		this.hasTransparency = image.getTransparency() == BufferedImage.TRANSLUCENT;
	}

	public BufferedImage getImage() {
		return image;
	}

	public ResourceLocation getLocation() {
		return location;
	}
	
	public boolean hasTransparency() {
		return hasTransparency;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("image", NBTHelper.setBufferedImage(this.image));
		nbt.setString("location", this.location.toString());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.image = NBTHelper.getBufferedImage(nbt.getCompoundTag("image"));
		this.location = new ResourceLocation(nbt.getString("location"));
	}

	public static NamedBufferedImage fromTag(NBTTagCompound nbt) {
		NamedBufferedImage image = new NamedBufferedImage();
		image.deserializeNBT(nbt);
		image.hasTransparency = image.getImage().getTransparency() == BufferedImage.TRANSLUCENT;
		return image;
	}
}