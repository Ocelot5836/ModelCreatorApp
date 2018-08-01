package com.ocelot.mod.application.component;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GLHelper;
import com.ocelot.mod.game.core.gfx.Camera;
import com.ocelot.mod.lib.Maths;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;

public class ComponentModelArea extends Component {

	public int width;
	public int height;

	private Camera camera;
	private List<Cube> cubes;
	private Matrix4f projectionMatrix;

	public ComponentModelArea(int x, int y, int width, int height, Camera camera) {
		super(x, y);
		this.width = width;
		this.height = height;
		this.camera = camera;
		this.cubes = new ArrayList<Cube>();
		this.projectionMatrix = Maths.createProjectionMatrix(width / height, 90, 0.3f, 1000.0f);
	}

	@Override
	protected void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {

		GLHelper.pushScissor(x, y, this.width, this.height);
		GlStateManager.pushMatrix();
		{
			GlStateManager.enableDepth();

			GlStateManager.translate(x, y, 0);
			this.camera.translate(partialTicks);

			float cubeSize = 16f;

			GlStateManager.pushMatrix();
			this.camera.rotate(partialTicks);
			GlStateManager.translate(0, 0, cubeSize * 8);
			GlStateManager.rotate(90, 1, 0, 0);
			mc.fontRenderer.drawString(I18n.format("default.model_creator.author_note", "Ocelot5836"), 0, 0, Color.WHITE.getRGB(), true);
			GlStateManager.popMatrix();

			GlStateManager.pushMatrix();
			this.camera.rotate(partialTicks);

			GlStateManager.disableTexture2D();

			GlStateManager.scale(0.5, 0.5, 0.5);
			GlStateManager.color(140f / 255f, 140f / 255f, 153f / 255f, 1);
			{
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder buffer = tessellator.getBuffer();
				buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
				for (int lineZ = 0; lineZ <= 16; lineZ++) {
					for (int i = 0; i < 2; i++) {
						buffer.pos(i * cubeSize * cubeSize, 0, lineZ * cubeSize).endVertex();
					}
				}
				tessellator.draw();
			}

			{
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder buffer = tessellator.getBuffer();
				buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
				for (int lineX = 0; lineX <= 16; lineX++) {
					for (int i = 0; i < 2; i++) {
						buffer.pos(lineX * cubeSize, 0, i * cubeSize * cubeSize).endVertex();
					}
				}
				tessellator.draw();
			}

			GlStateManager.popMatrix();

			GlStateManager.enableLighting();
			GlStateManager.disableCull();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

			for (Cube cube : this.cubes) {
				GlStateManager.pushMatrix();
				GlStateManager.scale(0.5, 0.5, 0.5);
				RenderHelper.enableGUIStandardItemLighting();
				cube.render(0, 0, 0, this.camera, this.projectionMatrix, partialTicks);
				GlStateManager.popMatrix();
			}
			mc.entityRenderer.setupOverlayRendering();

			GlStateManager.enableTexture2D();
			GlStateManager.disableDepth();
			GlStateManager.enableCull();
			RenderHelper.disableStandardItemLighting();
		}
		GlStateManager.popMatrix();
		GLHelper.popScissor();
	}

	public Cube addCube(float x, float y, float z, float sizeX, float sizeY, float sizeZ, float rotationX, float rotationY, float rotationZ) {
		return this.addCube(new Cube(x, y, z, sizeX, sizeY, sizeZ, rotationX, rotationY, rotationZ));
	}

	public Cube addCube(Cube cube) {
		this.cubes.add(cube);
		return cube;
	}

	public void removeCube(int index) {
		this.cubes.remove(index);
	}

	public void cleanUp() {
		this.cubes.clear();
	}

	public List<Cube> getCubes() {
		return cubes;
	}
}