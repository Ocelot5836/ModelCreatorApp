package com.ocelot.api.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import net.minecraft.nbt.NBTTagCompound;
import scala.reflect.io.ZipArchive;

/**
 * Contains some helper methods when attempting to read/write to NBT.
 * 
 * @author Ocelot5836
 */
public class NBTHelper {

	/**
	 * Saves a {@link Vector2f} to NBT.
	 * 
	 * @param vector
	 *            The vector to set to NBT
	 * @return The tag that the vector was saved to
	 */
	public static NBTTagCompound setVector(Vector2f vector) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setFloat("x", vector.x);
		nbt.setFloat("y", vector.y);
		return nbt;
	}

	/**
	 * Saves a {@link Vector3f} to NBT.
	 * 
	 * @param vector
	 *            The vector to set to NBT
	 * @return The tag that the vector was saved to
	 */
	public static NBTTagCompound setVector(Vector3f vector) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setFloat("x", vector.x);
		nbt.setFloat("y", vector.y);
		nbt.setFloat("z", vector.z);
		return nbt;
	}

	/**
	 * Saves a {@link Vector4f} to NBT.
	 * 
	 * @param vector
	 *            The vector to set to NBT
	 * @return The tag that the vector was saved to
	 */
	public static NBTTagCompound setVector(Vector4f vector) {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setFloat("x", vector.x);
		nbt.setFloat("y", vector.y);
		nbt.setFloat("z", vector.z);
		nbt.setFloat("w", vector.w);
		return nbt;
	}

	/**
	 * Saves a {@link BufferedImage} to a Base64 encoded string.
	 * 
	 * @param image
	 *            The image to encode
	 * @return The encoded string that the image was saved to
	 */
	public static String encodeBufferedImage(BufferedImage image) {
		String imageString = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		try {
			ImageIO.write(image, "PNG", stream);
			byte[] imageBytes = stream.toByteArray();

			imageString = Base64.encodeBase64String(imageBytes);

			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageString;
	}

	/**
	 * Saves a {@link BufferedImage} to NBT.
	 * 
	 * @param image
	 *            The image to write to NBT
	 * @return The tag that the image was saved to
	 */
	@Deprecated
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

		if (length > 0) {
			compressedPixels.append(Integer.toHexString(startingPixelColor) + ":" + Integer.toHexString(startingPixel) + "_" + Integer.toHexString(startingPixel + length) + ",");
		}

		nbt.setInteger("type", image.getType());
		nbt.setInteger("width", width);
		nbt.setInteger("height", height);
		nbt.setString("pixels", compressedPixels.toString().substring(0, compressedPixels.length() - 1));

		return nbt;
	}

	/**
	 * Reads a vector from NBT.
	 * 
	 * @param nbt
	 *            The tag that contains the vector
	 * @return The vector created from the tag
	 */
	public static Vector2f getVector2f(NBTTagCompound nbt) {
		return new Vector2f(nbt.getFloat("x"), nbt.getFloat("y"));
	}

	/**
	 * Reads a vector from NBT.
	 * 
	 * @param nbt
	 *            The tag that contains the vector
	 * @return The vector created from the tag
	 */
	public static Vector3f getVector3f(NBTTagCompound nbt) {
		return new Vector3f(nbt.getFloat("x"), nbt.getFloat("y"), nbt.getFloat("z"));
	}

	/**
	 * Reads a vector from NBT.
	 * 
	 * @param nbt
	 *            The tag that contains the vector
	 * @return The vector created from the tag
	 */
	public static Vector4f getVector4f(NBTTagCompound nbt) {
		return new Vector4f(nbt.getFloat("x"), nbt.getFloat("y"), nbt.getFloat("z"), nbt.getFloat("w"));
	}

	/**
	 * Reads a buffered image from a Base64 encoded string.
	 * 
	 * @param imageString
	 *            The encoded image as a string
	 * @return The image created from the string
	 */
	public static BufferedImage getBufferedImage(String imageString) {
		BufferedImage image = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(imageString));
			image = ImageIO.read(bis);
			bis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}

	/**
	 * Reads a buffered image from NBT.
	 * 
	 * @param nbt
	 *            The image inside of an {@link NBTTagCompound}
	 * @return The image created from the tag
	 */
	@Deprecated
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
		BufferedImage image = new BufferedImage(4096, 4096, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < image.getWidth() * image.getHeight(); i++) {
			image.setRGB(i % image.getWidth(), i / image.getHeight(), (int) (Math.random() * 255f));
		}

		long oldSnapshot = System.currentTimeMillis();
		System.out.println(NBTHelper.setBufferedImage(image).toString().length());

		long newSnapshot = System.currentTimeMillis();
		System.out.println(NBTHelper.encodeBufferedImage(image).length());

		System.out.println("Took " + (int) (System.currentTimeMillis() - oldSnapshot) + "ms for the old compression system and " + (int) (System.currentTimeMillis() - newSnapshot) + "ms for the new system. Difference of " + (int) ((System.currentTimeMillis() - oldSnapshot) - (System.currentTimeMillis() - newSnapshot)) + "ms.");
	}
}