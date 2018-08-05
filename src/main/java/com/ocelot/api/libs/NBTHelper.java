package com.ocelot.api.libs;

import java.awt.image.BufferedImage;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import net.minecraft.nbt.NBTTagCompound;
import scala.actors.threadpool.Arrays;

public class NBTHelper {

	public static NBTTagCompound setVector(Vector2f vector) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setFloat("x", vector.x);
		nbt.setFloat("y", vector.y);
		return nbt;
	}

	public static NBTTagCompound setVector(Vector3f vector) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setFloat("x", vector.x);
		nbt.setFloat("y", vector.y);
		nbt.setFloat("z", vector.z);
		return nbt;
	}

	public static NBTTagCompound setVector(Vector4f vector) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setFloat("x", vector.x);
		nbt.setFloat("y", vector.y);
		nbt.setFloat("z", vector.z);
		nbt.setFloat("w", vector.w);
		return nbt;
	}

	public static NBTTagCompound setBufferedImage(BufferedImage image) {
		NBTTagCompound nbt = new NBTTagCompound();
		int width = image.getWidth();
		int height = image.getHeight();
		int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
		StringBuilder compressedPixels = new StringBuilder();

		int lastPixel = 0;

		int startingPixel = 0;
		int startingPixelColor = 0;
		int length = 0;
		for (int i = 0; i < pixels.length; i++) {
			int pixel = pixels[i];
			if (lastPixel == pixel) {
				length++;
			} else {
				compressedPixels.append(Integer.toHexString(startingPixelColor) + ":" + Integer.toHexString(startingPixel) + "_" + Integer.toHexString(startingPixel + length) + ",");
				startingPixelColor = pixel;
				startingPixel = i;
				length = 1;
			}
			lastPixel = pixel;
		}
		
		if(length > 0) {
			compressedPixels.append(Integer.toHexString(startingPixelColor) + ":" + Integer.toHexString(startingPixel) + "_" + Integer.toHexString(startingPixel + length) + ",");
		}

		nbt.setInteger("type", image.getType());
		nbt.setInteger("width", width);
		nbt.setInteger("height", height);
		nbt.setString("pixels", compressedPixels.toString().substring(0, compressedPixels.length() - 1));
		
		return nbt;
	}

	public static Vector2f getVector2f(NBTTagCompound nbt) {
		return new Vector2f(nbt.getFloat("x"), nbt.getFloat("y"));
	}

	public static Vector3f getVector3f(NBTTagCompound nbt) {
		return new Vector3f(nbt.getFloat("x"), nbt.getFloat("y"), nbt.getFloat("z"));
	}

	public static Vector4f getVector4f(NBTTagCompound nbt) {
		return new Vector4f(nbt.getFloat("x"), nbt.getFloat("y"), nbt.getFloat("z"), nbt.getFloat("w"));
	}

	public static BufferedImage getBufferedImage(NBTTagCompound nbt) {
		int width = nbt.getInteger("width");
		int height = nbt.getInteger("height");
		BufferedImage image = new BufferedImage(width, height, nbt.getInteger("type"));
		String compressedPixels = nbt.getString("pixels");
		String[] pixelColors = compressedPixels.split(",");
		int[] uncompressedPixelColors = new int[pixelColors.length];

		for (int i = 0; i < uncompressedPixelColors.length; i++) {
			uncompressedPixelColors[i] = Integer.parseUnsignedInt(pixelColors[i].split(":")[0], 16);
		}

		int[] pixels = new int[width * height];
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixelColors.length; j++) {
				String colorPositions = pixelColors[j].split(":")[1];
				int start = Integer.parseUnsignedInt(colorPositions.split("_")[0], 16);
				int end = Integer.parseUnsignedInt(colorPositions.split("_")[1], 16);
				if (i >= start && i < end) {
					pixels[i] = uncompressedPixelColors[j];
				}
			}
		}

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixelColor = pixels[x + y * width];
				image.setRGB(x, y, pixelColor);
			}
		}

		return image;
	}

	public static void main(String[] args) {
		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		NBTTagCompound nbt = NBTHelper.setBufferedImage(image);
		getBufferedImage(nbt);
	}
}