package com.ocelot.api.utils;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

/**
 * Handles any methods that have to deal with rendering things onto the screen.
 * 
 * @author Ocelot5836
 */
public class RenderingHelper {

	private static final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);

	/**
	 * Draws a hollow rectangle of color.
	 * 
	 * @param left
	 *            The x position
	 * @param top
	 *            The y position
	 * @param right
	 *            The x plus width
	 * @param bottom
	 *            The y plus height
	 * @param color
	 *            The color
	 */
	public static void drawRect(double left, double top, double right, double bottom, int color) {
		if (left < right) {
			double i = left;
			left = right;
			right = i;
		}

		if (top < bottom) {
			double i = top;
			top = bottom;
			bottom = i;
		}

		float a = (float) (color >> 24 & 255) / 255.0F;
		float r = (float) (color >> 16 & 255) / 255.0F;
		float g = (float) (color >> 8 & 255) / 255.0F;
		float b = (float) (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.disableTexture2D();
		GlStateManager.color(r, g, b, a);
		bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		bufferbuilder.pos((double) left, (double) top, 0.0D).endVertex();
		bufferbuilder.pos((double) right, (double) top, 0.0D).endVertex();
		bufferbuilder.pos((double) right, (double) top, 0.0D).endVertex();
		bufferbuilder.pos((double) right, (double) bottom, 0.0D).endVertex();
		bufferbuilder.pos((double) right, (double) bottom, 0.0D).endVertex();
		bufferbuilder.pos((double) left, (double) bottom, 0.0D).endVertex();
		bufferbuilder.pos((double) left, (double) bottom, 0.0D).endVertex();
		bufferbuilder.pos((double) left, (double) top, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableTexture2D();
	}

	/**
	 * Draws a solid rectangle of color.
	 * 
	 * @param left
	 *            The x position
	 * @param top
	 *            The y position
	 * @param right
	 *            The x plus width
	 * @param bottom
	 *            The y plus height
	 * @param color
	 *            The color
	 */
	public static void fillRect(double left, double top, double right, double bottom, int color) {
		if (left < right) {
			double i = left;
			left = right;
			right = i;
		}

		if (top < bottom) {
			double i = top;
			top = bottom;
			bottom = i;
		}

		float a = (float) (color >> 24 & 255) / 255.0F;
		float r = (float) (color >> 16 & 255) / 255.0F;
		float g = (float) (color >> 8 & 255) / 255.0F;
		float b = (float) (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.disableTexture2D();
		GlStateManager.color(r, g, b, a);
		bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		bufferbuilder.pos((double) left, (double) bottom, 0.0D).endVertex();
		bufferbuilder.pos((double) right, (double) bottom, 0.0D).endVertex();
		bufferbuilder.pos((double) right, (double) top, 0.0D).endVertex();
		bufferbuilder.pos((double) left, (double) top, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableTexture2D();
	}

	/**
	 * The exact same as the {@link Gui} method, except that is takes in doubles instead of integers.
	 * 
	 * @see Gui#drawScaledCustomSizeModalRect(int, int, float, float, int, int, int, int, float, float)
	 */
	public static void drawScaledCustomSizeModalRect(double x, double y, double u, double v, double uWidth, double vHeight, double width, double height, double tileWidth, double tileHeight) {
		double zLevel = 0.0;
		double f = 1.0 / tileWidth;
		double f1 = 1.0 / tileHeight;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x, (y + height), zLevel).tex((u * f), ((v + vHeight) * f1)).endVertex();
		bufferbuilder.pos((x + width), (y + height), zLevel).tex(((u + uWidth) * f), ((v + vHeight) * f1)).endVertex();
		bufferbuilder.pos((x + width), y, zLevel).tex(((u + uWidth) * f), (v * f1)).endVertex();
		bufferbuilder.pos(x, y, zLevel).tex((u * f), (v * f1)).endVertex();
		tessellator.draw();
	}

	/**
	 * The exact same as the {@link Gui} method, except that is takes in doubles instead of integers.
	 * 
	 * @see Gui#drawTexturedModalRect(int, int, TextureAtlasSprite, int, int)
	 */
	public static void drawTexturedModalRect(double x, double y, TextureAtlasSprite textureSprite, double width, double height) {
		double zLevel = 0.0;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(x + 0, y + height, zLevel).tex((double) textureSprite.getMinU(), (double) textureSprite.getMaxV()).endVertex();
		bufferbuilder.pos(x + width, y + height, zLevel).tex((double) textureSprite.getMaxU(), (double) textureSprite.getMaxV()).endVertex();
		bufferbuilder.pos(x + width, y + 0, zLevel).tex((double) textureSprite.getMaxU(), (double) textureSprite.getMinV()).endVertex();
		bufferbuilder.pos(x + 0, y + 0, zLevel).tex((double) textureSprite.getMinU(), (double) textureSprite.getMinV()).endVertex();
		tessellator.draw();
	}

	public static void renderCube(double x1, double y1, double z1, double x2, double y2, double z2) {
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex3d(x1, y1, z1);
			GL11.glVertex3d(x1, y1, z2);
			GL11.glVertex3d(x1, y2, z2);
			GL11.glVertex3d(x1, y2, z1);

			GL11.glVertex3d(x2, y1, z1);
			GL11.glVertex3d(x1, y1, z1);
			GL11.glVertex3d(x1, y2, z1);
			GL11.glVertex3d(x2, y2, z1);

			GL11.glVertex3d(x1, y1, z2);
			GL11.glVertex3d(x2, y1, z2);
			GL11.glVertex3d(x2, y2, z2);
			GL11.glVertex3d(x1, y2, z2);

			GL11.glVertex3d(x2, y1, z2);
			GL11.glVertex3d(x2, y1, z1);
			GL11.glVertex3d(x2, y2, z1);
			GL11.glVertex3d(x2, y2, z2);

			GL11.glVertex3d(x1, y2, z1);
			GL11.glVertex3d(x1, y2, z2);
			GL11.glVertex3d(x2, y2, z2);
			GL11.glVertex3d(x2, y2, z1);

			GL11.glVertex3d(x1, y1, z1);
			GL11.glVertex3d(x1, y1, z2);
			GL11.glVertex3d(x2, y1, z2);
			GL11.glVertex3d(x2, y1, z1);
		}
		GL11.glEnd();
	}
	
	public static Matrix4f getProjectionMatrix() {
		GlStateManager.getFloat(GL11.GL_PROJECTION_MATRIX, projection);
		return (Matrix4f) new Matrix4f().load(projection.asReadOnlyBuffer());
	}
}