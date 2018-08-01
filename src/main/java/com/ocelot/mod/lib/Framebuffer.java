package com.ocelot.mod.lib;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Framebuffer {

	public static final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
	public static final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);

	public int framebufferTextureWidth;
	public int framebufferTextureHeight;
	public int framebufferWidth;
	public int framebufferHeight;
	public boolean useDepth;
	public int framebufferObject;
	public int framebufferTexture;
	public int depthBuffer;
	public float[] framebufferColor;
	public int framebufferFilter;

	public Framebuffer(int width, int height, boolean useDepthIn) {
		this.useDepth = useDepthIn;
		this.framebufferObject = 0;
		this.framebufferTexture = 0;
		this.depthBuffer = 0;
		this.framebufferColor = new float[4];
		this.framebufferColor[0] = 1.0F;
		this.framebufferColor[1] = 1.0F;
		this.framebufferColor[2] = 1.0F;
		this.framebufferColor[3] = 0.0F;
		this.createBindFramebuffer(width, height);
	}

	public void createBindFramebuffer(int width, int height) {
		if (!OpenGlHelper.isFramebufferEnabled()) {
			this.framebufferWidth = width;
			this.framebufferHeight = height;
		} else {
			GlStateManager.enableDepth();
			this.createFramebuffer(width, height);
			this.checkFramebufferComplete();
			OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.framebufferObject);
		}
	}

	public void deleteFramebuffer() {
		if (OpenGlHelper.isFramebufferEnabled()) {
			this.unbindFramebufferTexture();
			this.unbindFramebuffer();
			OpenGlHelper.glDeleteRenderbuffers(this.depthBuffer);
			TextureUtil.deleteTexture(this.framebufferTexture);
			OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.framebufferObject);
			OpenGlHelper.glDeleteFramebuffers(this.framebufferObject);
		}
	}

	public void createFramebuffer(int width, int height) {
		this.framebufferWidth = width;
		this.framebufferHeight = height;
		this.framebufferTextureWidth = width;
		this.framebufferTextureHeight = height;

		if (!OpenGlHelper.isFramebufferEnabled()) {
			this.framebufferClear();
		} else {
			this.framebufferObject = OpenGlHelper.glGenFramebuffers();
			this.framebufferTexture = TextureUtil.glGenTextures();

			if (this.useDepth) {
				this.depthBuffer = OpenGlHelper.glGenRenderbuffers();
			}

			this.setFramebufferFilter(9728);
			GlStateManager.bindTexture(this.framebufferTexture);
			GlStateManager.glTexImage2D(3553, 0, 32856, this.framebufferTextureWidth, this.framebufferTextureHeight, 0, 6408, 5121, (IntBuffer) null);
			OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.framebufferObject);
			OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, 3553, this.framebufferTexture, 0);

			if (this.useDepth) {
				OpenGlHelper.glBindRenderbuffer(OpenGlHelper.GL_RENDERBUFFER, this.depthBuffer);
				if (!this.stencilEnabled) {
					OpenGlHelper.glRenderbufferStorage(OpenGlHelper.GL_RENDERBUFFER, 33190, this.framebufferTextureWidth, this.framebufferTextureHeight);
					OpenGlHelper.glFramebufferRenderbuffer(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, OpenGlHelper.GL_RENDERBUFFER, this.depthBuffer);
				} else {
					OpenGlHelper.glRenderbufferStorage(OpenGlHelper.GL_RENDERBUFFER, org.lwjgl.opengl.EXTPackedDepthStencil.GL_DEPTH24_STENCIL8_EXT, this.framebufferTextureWidth, this.framebufferTextureHeight);
					OpenGlHelper.glFramebufferRenderbuffer(OpenGlHelper.GL_FRAMEBUFFER, org.lwjgl.opengl.EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, OpenGlHelper.GL_RENDERBUFFER, this.depthBuffer);
					OpenGlHelper.glFramebufferRenderbuffer(OpenGlHelper.GL_FRAMEBUFFER, org.lwjgl.opengl.EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, OpenGlHelper.GL_RENDERBUFFER, this.depthBuffer);
				}
			}

			this.framebufferClear();
			this.unbindFramebufferTexture();
		}
	}

	public void setFramebufferFilter(int framebufferFilterIn) {
		if (OpenGlHelper.isFramebufferEnabled()) {
			this.framebufferFilter = framebufferFilterIn;
			GlStateManager.bindTexture(this.framebufferTexture);
			GlStateManager.glTexParameteri(3553, 10241, framebufferFilterIn);
			GlStateManager.glTexParameteri(3553, 10240, framebufferFilterIn);
			GlStateManager.glTexParameteri(3553, 10242, 10496);
			GlStateManager.glTexParameteri(3553, 10243, 10496);
			GlStateManager.bindTexture(0);
		}
	}

	public void checkFramebufferComplete() {
		int i = OpenGlHelper.glCheckFramebufferStatus(OpenGlHelper.GL_FRAMEBUFFER);

		if (i != OpenGlHelper.GL_FRAMEBUFFER_COMPLETE) {
			if (i == OpenGlHelper.GL_FB_INCOMPLETE_ATTACHMENT) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
			} else if (i == OpenGlHelper.GL_FB_INCOMPLETE_MISS_ATTACH) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
			} else if (i == OpenGlHelper.GL_FB_INCOMPLETE_DRAW_BUFFER) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
			} else if (i == OpenGlHelper.GL_FB_INCOMPLETE_READ_BUFFER) {
				throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
			} else {
				throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + i);
			}
		}
	}

	public void bindFramebufferTexture() {
		if (OpenGlHelper.isFramebufferEnabled()) {
			GlStateManager.bindTexture(this.framebufferTexture);
		}
	}

	public void unbindFramebufferTexture() {
		if (OpenGlHelper.isFramebufferEnabled()) {
			GlStateManager.bindTexture(0);
		}
	}

	public void bindFramebuffer(boolean p_147610_1_) {
		if (OpenGlHelper.isFramebufferEnabled()) {
			OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.framebufferObject);

			if (p_147610_1_) {
				GlStateManager.viewport(0, 0, this.framebufferWidth, this.framebufferHeight);
			}
		}
	}

	public void unbindFramebuffer() {
		if (OpenGlHelper.isFramebufferEnabled()) {
			OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0);
			Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
		}
	}

	public void setFramebufferColor(float red, float green, float blue, float alpha) {
		this.framebufferColor[0] = red;
		this.framebufferColor[1] = green;
		this.framebufferColor[2] = blue;
		this.framebufferColor[3] = alpha;
	}

	public void framebufferRender(int x, int y, int width, int height) {
		this.framebufferRenderExt(x, y, width, height, true);
	}

	public void framebufferRenderExt(int x, int y, int width, int height, boolean param5) {
		if (OpenGlHelper.isFramebufferEnabled()) {
			GlStateManager.disableDepth();
//			GlStateManager.matrixMode(5889);
//			GlStateManager.loadIdentity();
//			GlStateManager.ortho(0, (double) width, (double) height, 0, 1000.0D, 3000.0D);
//			GlStateManager.matrixMode(5888);
//			GlStateManager.loadIdentity();
//			GlStateManager.translate(0.0F, 0.0F, -2000.0F);
			GlStateManager.enableTexture2D();
			GlStateManager.disableLighting();

			if (param5) {
				GlStateManager.disableBlend();
				GlStateManager.enableColorMaterial();
			}

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			this.bindFramebufferTexture();
			float f2 = (float) this.framebufferWidth / (float) this.framebufferTextureWidth;
			float f3 = (float) this.framebufferHeight / (float) this.framebufferTextureHeight;
			GlStateManager.translate(x, y, 0);
			
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
			bufferbuilder.pos((double) 0, (double) height, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
			bufferbuilder.pos((double) width, (double) height, 0.0D).tex((double) f2 * (1 / f2), 0.0D).color(255, 255, 255, 255).endVertex();
			bufferbuilder.pos((double) width, (double) 0, 0.0D).tex((double) f2 * (1 / f2), (double) f3).color(255, 255, 255, 255).endVertex();
			bufferbuilder.pos((double) 0, (double) 0, 0.0D).tex(0.0D, (double) f3).color(255, 255, 255, 255).endVertex();
			tessellator.draw();
			
			this.unbindFramebufferTexture();
			GlStateManager.enableLighting();
		}
	}

	public void framebufferClear() {
		GlStateManager.clearColor(this.framebufferColor[0], this.framebufferColor[1], this.framebufferColor[2], this.framebufferColor[3]);
		int i = 16384;

		if (this.useDepth) {
			GlStateManager.clearDepth(1.0D);
			i |= 256;
		}

		GlStateManager.clear(i);
	}

	/* ================================ FORGE START ================================================ */
	private boolean stencilEnabled = false;

	/**
	 * Attempts to enabled 8 bits of stencil buffer on this FrameBuffer. Modders must call this directly to set things up. This is to prevent the default cause where graphics cards do not support stencil bits. Modders should check the below 'isStencilEnabled' to check if another modder has already enabled them.
	 *
	 * Note: As of now the only thing that is checked is if FBOs are supported entirely, in the future we may expand to check for errors.
	 *
	 * @return True if the FBO was re-initialized with stencil bits.
	 */
	public boolean enableStencil() {
		if (!OpenGlHelper.isFramebufferEnabled())
			return false;
		stencilEnabled = true;
		this.createBindFramebuffer(framebufferWidth, framebufferHeight);
		return true; // TODO: Find a way to detect if this failed?
	}

	/**
	 * Returns wither or not this FBO has been successfully initialized with stencil bits. If not, and a modder wishes it to be, they must call enableStencil.
	 */
	public boolean isStencilEnabled() {
		return this.stencilEnabled;
	}
	/* ================================ FORGE END ================================================ */
}