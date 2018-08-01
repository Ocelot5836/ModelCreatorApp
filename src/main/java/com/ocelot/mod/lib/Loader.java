package com.ocelot.mod.lib;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

/**
 * <em><b>Copyright (c) 2018 The Zerra Team.</b></em>
 * 
 * <br>
 * </br>
 * 
 * Has the capability to load data to and from memory.
 * 
 * @author Ocelot5836
 */
public class Loader {

	/**
	 * Loads an image to a byte buffer. Used when loading textures.
	 * 
	 * @param image
	 *            The image to load to a byte buffer
	 * @return The buffer created from the image or null if the image was null
	 * @throws NullPointerException
	 *             Throws this if the image was null
	 */
	public static ByteBuffer loadToByteBuffer(BufferedImage image) throws NullPointerException {
		int width = image.getWidth();
		int height = image.getHeight();
		int[] pixels = new int[width * height];
		pixels = image.getRGB(0, 0, width, height, null, 0, width);

		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int color = pixels[x + y * width];
				buffer.put((byte) ((color >> 16) & 0xff));
				buffer.put((byte) ((color >> 8) & 0xff));
				buffer.put((byte) (color & 0xff));
				buffer.put((byte) ((color >> 24) & 0xff));
			}
		}
		buffer.flip();
		return buffer;
	}
}