package com.ocelot.mod.lib;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

import com.ocelot.mod.Mod;
import com.ocelot.mod.Usernames;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * <em><b>Copyright (c) 2018 Ocelot5836.</b></em>
 * 
 * <br>
 * </br>
 * 
 * Contains some basic library methods.
 * 
 * @author Ocelot5836
 */
public class Lib {

	/**
	 * Loads all the sprites specified from a buffered image.
	 * 
	 * @param image
	 *            The image to fetch the sprites from
	 * @param spriteWidth
	 *            The width of each sprite
	 * @param spriteHeight
	 *            The height of each sprite
	 * @param spritesPerRow
	 *            The number of sprites in each row
	 * @return The sprites loaded
	 */
	public static List<BufferedImage[]> loadSpritesFromBufferedImage(BufferedImage image, int spriteWidth, int spriteHeight, int[] spritesPerRow) {
		List<BufferedImage[]> sprites = new ArrayList<BufferedImage[]>();
		for (int i = 0; i < spritesPerRow.length; i++) {
			BufferedImage[] sprite = new BufferedImage[spritesPerRow[i]];
			for (int j = 0; j < spritesPerRow[i]; j++) {
				sprite[j] = image.getSubimage(j * spriteWidth, i * spriteHeight, spriteWidth, spriteHeight);
			}
			sprites.add(sprite);
		}
		return sprites;
	}

	/**
	 * Loads text to a string from a resource location.
	 * 
	 * @param location
	 *            The location of the text file
	 * @return The text compressed into a string
	 */
	@SideOnly(Side.CLIENT)
	public static String loadTextToString(ResourceLocation location) {
		try {
			return IOUtils.toString(Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream(), Charset.defaultCharset());
		} catch (IOException e) {
			Mod.logger().warn("Could not load text " + location + ". Could cause issues later on.");
		}
		return "";
	}

	/**
	 * Loads an image from file.
	 * 
	 * @param location
	 *            The location of the file
	 * @return The image loaded
	 */
	@SideOnly(Side.CLIENT)
	public static BufferedImage loadImage(ResourceLocation location) {
		try {
			return loadImageE(location);
		} catch (IOException e) {
			e.printStackTrace();
			return new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		}
	}

	/**
	 * Loads an image from file. This one throws an exception.
	 * 
	 * @param location
	 *            The location of the file
	 * @return The image loaded
	 * @throws IOException
	 *             If the image could not be found or had an error loading
	 */
	@SideOnly(Side.CLIENT)
	public static BufferedImage loadImageE(ResourceLocation location) throws IOException {
		return ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream());
	}

	/**
	 * Allows the easy ability to turn multiple objects or just one into an array.
	 * 
	 * @param obj
	 *            The objects to put into an array.
	 * @param <T>
	 *            The type of array that will be returned
	 * @return The array generated with the parameters
	 */
	public static <T> T[] asArray(T... obj) {
		return obj;
	}

	/**
	 * Parses a single-step equation.
	 * 
	 * @param equation
	 *            The equation
	 * @return The number that was calculated from the equation
	 */
	public static double parseEquation(String equation) throws Exception {
		try {
			String[] values = equation.split("+");
			if (equation.startsWith("+")) {
				double value1 = Double.parseDouble(values[1]);
				double value2 = Double.parseDouble(values[2]);
				return value1 + value2;
			} else {
				double value1 = Double.parseDouble(values[0]);
				double value2 = Double.parseDouble(values[1]);
				return value1 + value2;
			}
		} catch (Exception e1) {
			try {
				String[] values = equation.split("-");
				if (equation.startsWith("-")) {
					double value1 = Double.parseDouble(values[1]);
					double value2 = Double.parseDouble(values[2]);
					return value1 - value2;
				} else {
					double value1 = Double.parseDouble(values[0]);
					double value2 = Double.parseDouble(values[1]);
					return value1 - value2;
				}
			} catch (Exception e2) {
				try {
					String[] values = equation.split("*");
					double value1 = Double.parseDouble(values[0]);
					double value2 = Double.parseDouble(values[1]);
					return value1 * value2;
				} catch (Exception e3) {
					try {
						String[] values = equation.split("/");
						double value1 = Double.parseDouble(values[0]);
						double value2 = Double.parseDouble(values[1]);
						return value1 / value2;
					} catch (Exception e4) {
						throw new Exception("Could not parse equation from \'" + equation + "\'", e4);
					}
				}
			}
		}
	}

	/**
	 * @param player
	 *            The player to check the username of
	 * @return Whether or not the player is MrCrayfish
	 */
	public static boolean isUserMrCrayfish(EntityPlayer player) {
		return Usernames.MR_CRAYFISH.equalsIgnoreCase(player.getName());
	}

	/**
	 * @param player
	 *            The player to check the username of
	 * @return Whether or not the player is Ocelot5836
	 */
	public static boolean isUserOcelot5836(EntityPlayer player) {
		return Usernames.OCELOT5836.equalsIgnoreCase(player.getName());
	}

	/**
	 * Converts a string of text into the Creyfush language.
	 * 
	 * @param message
	 *            The message to convert
	 * @return The converted message
	 */
	public static String convertToCrayfish(String message) {
		return message.replaceAll(Usernames.MR_CRAYFISH, "MrCreyfush");
	}

	/**
	 * Gets the width of a default string of text.
	 * 
	 * @param text
	 *            The text to get the width of
	 * @return The width of the text
	 */
	public static int getDefaultTextWidth(String text) {
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		boolean flag = fontRenderer.getUnicodeFlag();
		fontRenderer.setUnicodeFlag(false);
		int width = fontRenderer.getStringWidth(text);
		fontRenderer.setUnicodeFlag(flag);
		return width;
	}
}