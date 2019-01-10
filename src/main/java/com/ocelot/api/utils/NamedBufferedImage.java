package com.ocelot.api.utils;

import java.awt.image.BufferedImage;

import com.google.common.base.MoreObjects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * This is an image that is bound to a location.
 * 
 * @author Ocelot5836
 */
public class NamedBufferedImage implements INBTSerializable<NBTTagCompound> {

	private BufferedImage image;
	private ResourceLocation location;
	private boolean hasTransparency;

	private NamedBufferedImage() {
	}

	public NamedBufferedImage(BufferedImage image, ResourceLocation location) {
		this.image = image;
		this.location = location;
		this.hasTransparency = false;
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				if (((image.getRGB(x, y) << 24) & 0xff) != 255 && ((image.getRGB(x, y) << 24) & 0xff) != 0) {
					this.hasTransparency = true;
					break;
				}
			}
		}
	}

	public NamedBufferedImage(NBTTagCompound nbt) {
		this.image = null;
		this.location = null;
		this.hasTransparency = false;
		this.deserializeNBT(nbt);
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				if (((image.getRGB(x, y) << 24) & 0xff) != 255 && ((image.getRGB(x, y) << 24) & 0xff) != 0) {
					this.hasTransparency = true;
					break;
				}
			}
		}
	}

	/**
	 * @return The image that contains the RGB data
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * @return The location bound to this texture
	 */
	public ResourceLocation getLocation() {
		return location;
	}

	/**
	 * @return Whether or not this image has transparency
	 */
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

	@Override
	public int hashCode() {
		return 31 * this.image.hashCode() + this.location.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NamedBufferedImage) {
			NamedBufferedImage image = (NamedBufferedImage) obj;
			return image.image.equals(this.image) && image.location.equals(this.location);
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("image", this.image).add("location", this.location).add("hasTransparency", this.hasTransparency).toString();
	}
}