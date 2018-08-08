package com.ocelot.api.utils;

import java.awt.image.BufferedImage;

import com.ocelot.api.libs.NBTHelper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * <em><b>Copyright (c) 2018 Ocelot5836.</b></em>
 * 
 * <br>
 * </br>
 * 
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

	/**
	 * Creates a new named image from a tag.
	 * 
	 * @param nbt
	 *            The tag that contains the data
	 * @return The image created from that data
	 */
	public static NamedBufferedImage fromTag(NBTTagCompound nbt) {
		NamedBufferedImage image = new NamedBufferedImage();
		image.deserializeNBT(nbt);
		
		image.hasTransparency = false;
		for (int y = 0; y < image.getImage().getHeight(); y++) {
			for (int x = 0; x < image.getImage().getWidth(); x++) {
				if (((image.getImage().getRGB(x, y) << 24) & 0xff) != 255 && ((image.getImage().getRGB(x, y) << 24) & 0xff) != 0) {
					image.hasTransparency = true;
					break;
				}
			}
		}
		
		return image;
	}
}