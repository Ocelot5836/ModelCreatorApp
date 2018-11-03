package com.ocelot.mod.application.component;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GLHelper;
import com.ocelot.api.geometry.Camera;
import com.ocelot.api.geometry.Cube;
import com.ocelot.api.geometry.Face;
import com.ocelot.api.utils.Lib;
import com.ocelot.api.utils.NamedBufferedImage;
import com.ocelot.mod.Usernames;
import com.ocelot.mod.application.ApplicationModelCreator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
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
	private List<Face> faces;
	private boolean ambientOcclusion;
	private NamedBufferedImage particle;

	public ComponentModelArea(int x, int y, int width, int height, Camera camera) {
		super(x, y);
		this.width = width;
		this.height = height;
		this.camera = camera;
		this.cubes = new ArrayList<Cube>();
		this.faces = new ArrayList<Face>();
		this.particle = null;
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
			mc.fontRenderer.drawString(I18n.format("app.mca.mc.author_note", Usernames.OCELOT5836), 0, 0, Color.WHITE.getRGB(), true);
			GlStateManager.popMatrix();

			GlStateManager.pushMatrix();
			this.camera.rotate(partialTicks);

			GlStateManager.disableTexture2D();
			
			ScaledResolution res = new ScaledResolution(mc);
			
			GlStateManager.glLineWidth(res.getScaleFactor());
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
			if (ApplicationModelCreator.isTransparencyEnabled()) {
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			} else {
				GlStateManager.disableAlpha();
				GlStateManager.disableBlend();
			}

			for (int i = 0; i < this.cubes.size(); i++) {
				GlStateManager.pushMatrix();
				GlStateManager.scale(0.5, 0.5, 0.5);
				this.cubes.get(i).queueFaceRenders(this.faces, this.camera, partialTicks);
				GlStateManager.popMatrix();
			}

			this.renderFaces(this.faces);
			mc.entityRenderer.setupOverlayRendering();

			GlStateManager.enableTexture2D();
			GlStateManager.disableDepth();
			RenderHelper.disableStandardItemLighting();
		}
		GlStateManager.popMatrix();
		GLHelper.popScissor();
	}

	private void renderFaces(List<Face> faces) {
		this.camera.rotate(Minecraft.getMinecraft().getRenderPartialTicks());
		faces.sort((face1, face2) -> {
			if (face1.hasTransparency() && face2.hasTransparency()) {
				Cube cube1 = face1.getParentCube();
				Cube cube2 = face2.getParentCube();

				double fromDistance = Lib.distance(camera.getPosition(), cube2.getPosition()) - Lib.distance(camera.getPosition(), cube1.getPosition());

				double ToSize1 = Lib.distance(camera.getPosition(), cube2.getPosition().x - cube2.getSize().x, cube2.getPosition().y - cube2.getSize().y, cube2.getPosition().z - cube2.getSize().z);
				double ToSize2 = Lib.distance(camera.getPosition(), cube1.getPosition().x - cube1.getSize().x, cube1.getPosition().y - cube1.getSize().y, cube1.getPosition().z - cube1.getSize().z);

				double toDistance = ToSize1 - ToSize2;

				return cube2.getPosition().y < cube1.getPosition().y ? 1 : -1;
			} else {
				return face1.hasTransparency() ? 1 : -1;
			}
		});
		GlStateManager.scale(0.5, 0.5, 0.5);
		for (Face face : this.faces) {
			GlStateManager.pushMatrix();
			face.getParentCube().applyLighting();
			face.getParentCube().applyRenderTransforms();
			face.render(false, 16f);
			GlStateManager.popMatrix();
		}

		for (Face face : this.faces) {
			GlStateManager.pushMatrix();
			face.getParentCube().applyLighting();
			face.getParentCube().applyRenderTransforms();
			face.render(true, 16f);
			GlStateManager.popMatrix();
		}
		this.faces.clear();
	}

	public void updateCubes(List<Cube> cubes) {
		this.cubes.clear();
		this.cubes.addAll(cubes);
	}

	public void clear() {
		this.cubes.clear();
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
		this.faces.clear();
	}

	public List<Cube> getCubes() {
		return cubes;
	}

	public boolean hasAmbientOcclusion() {
		return ambientOcclusion;
	}

	public NamedBufferedImage getParticle() {
		return particle;
	}

	public void setAmbientOcclusion(boolean ambientOcclusion) {
		this.ambientOcclusion = ambientOcclusion;
	}

	public void setParticle(NamedBufferedImage particle) {
		this.particle = particle;
	}
}