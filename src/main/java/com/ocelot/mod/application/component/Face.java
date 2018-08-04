package com.ocelot.mod.application.component;

import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.google.common.collect.Maps;
import com.ocelot.api.utils.TextureUtils;
import com.ocelot.mod.application.dialog.NamedBufferedImage;
import com.ocelot.mod.lib.Lib;
import com.ocelot.mod.lib.NBTHelper;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class Face implements Cloneable, INBTSerializable<NBTTagCompound> {

	private static final Map<ResourceLocation, ResourceLocation> TEXTURE_CACHE = Maps.<ResourceLocation, ResourceLocation>newHashMap();

	public static final Face NULL_FACE = null;

	private Cube parentCube;
	private EnumFacing faceDirection;
	private ResourceLocation textureLocation;
	private NamedBufferedImage texture;
	private Vector4f textureCoords;
	private boolean cullFace;

	private Face(Cube parentCube) {
		this(parentCube, null);
	}

	protected Face(Cube parentCube, EnumFacing faceDirection) {
		this.textureLocation = null;
		this.texture = null;
		this.textureCoords = new Vector4f(0, 0, 16, 16);
		this.cullFace = false;
		this.faceDirection = faceDirection;
		this.parentCube = parentCube;
	}

	public void render(BufferBuilder buffer, Vector3f size, boolean renderTransparentFaces, float scale) {
		this.bindTexture();

		if (this.faceDirection == EnumFacing.DOWN) {
			if (this.getTexture() != null) {
				if ((renderTransparentFaces && !this.hasTransparency()) || (!renderTransparentFaces && this.hasTransparency()))
					return;
				GlStateManager.enableTexture2D();
				GlStateManager.color(1, 1, 1, 1);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
				{
					this.pos(size.x * scale, 0, 0, EnumFacing.DOWN, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f);
					this.pos(size.x * scale, 0, size.z * scale, EnumFacing.DOWN, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(0, 0, size.z * scale, EnumFacing.DOWN, textureCoords.x / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(0, 0, 0, EnumFacing.DOWN, textureCoords.x / 16f, textureCoords.y / 16f);
				}
				Tessellator.getInstance().draw();
			} else {
				GlStateManager.color(1, 0, 1);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
				{
					this.pos(size.x * scale, 0, 0, EnumFacing.DOWN);
					this.pos(size.x * scale, 0, size.z * scale, EnumFacing.DOWN);
					this.pos(0, 0, size.z * scale, EnumFacing.DOWN);
					this.pos(0, 0, 0, EnumFacing.DOWN);
				}
				Tessellator.getInstance().draw();
			}
			GlStateManager.disableTexture2D();
		}

		if (this.faceDirection == EnumFacing.UP) {
			if (this.getTexture() != null) {
				if ((renderTransparentFaces && !this.hasTransparency()) || (!renderTransparentFaces && this.hasTransparency()))
					return;
				GlStateManager.enableTexture2D();
				GlStateManager.color(1, 1, 1, 1);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
				{
					this.pos(0, -size.y * scale, 0, EnumFacing.UP, textureCoords.x / 16f, textureCoords.y / 16f);
					this.pos(0, -size.y * scale, size.z * scale, EnumFacing.UP, textureCoords.x / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(size.x * scale, -size.y * scale, size.z * scale, EnumFacing.UP, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(size.x * scale, -size.y * scale, 0, EnumFacing.UP, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f);
				}
				Tessellator.getInstance().draw();
			} else {
				GlStateManager.color(0, 1, 1);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
				{
					this.pos(0, -size.y * scale, 0, EnumFacing.UP);
					this.pos(0, -size.y * scale, size.z * scale, EnumFacing.UP);
					this.pos(size.x * scale, -size.y * scale, size.z * scale, EnumFacing.UP);
					this.pos(size.x * scale, -size.y * scale, 0, EnumFacing.UP);
				}
				Tessellator.getInstance().draw();
			}
			GlStateManager.disableTexture2D();
		}

		if (this.faceDirection == EnumFacing.NORTH) {
			if (this.getTexture() != null) {
				if ((renderTransparentFaces && !this.hasTransparency()) || (!renderTransparentFaces && this.hasTransparency()))
					return;
				GlStateManager.enableTexture2D();
				GlStateManager.color(1, 1, 1, 1);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
				{
					this.pos(size.x * scale, 0, 0, EnumFacing.SOUTH, textureCoords.x / 16f, textureCoords.y / 16f);
					this.pos(0, 0, 0, EnumFacing.SOUTH, textureCoords.x / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(0, -size.y * scale, 0, EnumFacing.SOUTH, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(size.x * scale, -size.y * scale, 0, EnumFacing.SOUTH, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f);
				}
				Tessellator.getInstance().draw();
			} else {
				GlStateManager.color(1, 0, 0);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
				{
					this.pos(size.x * scale, 0, 0, EnumFacing.SOUTH);
					this.pos(0, 0, 0, EnumFacing.SOUTH);
					this.pos(0, -size.y * scale, 0, EnumFacing.SOUTH);
					this.pos(size.x * scale, -size.y * scale, 0, EnumFacing.SOUTH);
				}
				Tessellator.getInstance().draw();
			}
			GlStateManager.disableTexture2D();
		}

		if (this.faceDirection == EnumFacing.SOUTH) {
			if (this.getTexture() != null) {
				if ((renderTransparentFaces && !this.hasTransparency()) || (!renderTransparentFaces && this.hasTransparency()))
					return;
				GlStateManager.enableTexture2D();
				GlStateManager.color(1, 1, 1, 1);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
				{
					this.pos(0, 0, size.z * scale, EnumFacing.WEST, textureCoords.x / 16f, textureCoords.y / 16f);
					this.pos(size.x * scale, 0, size.z * scale, EnumFacing.WEST, textureCoords.x / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(size.x * scale, -size.y * scale, size.z * scale, EnumFacing.WEST, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(0, -size.y * scale, size.z * scale, EnumFacing.WEST, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f);
				}
				Tessellator.getInstance().draw();
			} else {
				GlStateManager.color(0, 0, 1);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
				{
					this.pos(0, 0, size.z * scale, EnumFacing.WEST);
					this.pos(size.x * scale, 0, size.z * scale, EnumFacing.WEST);
					this.pos(size.x * scale, -size.y * scale, size.z * scale, EnumFacing.WEST);
					this.pos(0, -size.y * scale, size.z * scale, EnumFacing.WEST);
				}
				Tessellator.getInstance().draw();
			}
			GlStateManager.disableTexture2D();
		}

		if (this.faceDirection == EnumFacing.WEST) {
			if (this.getTexture() != null) {
				if ((renderTransparentFaces && !this.hasTransparency()) || (!renderTransparentFaces && this.hasTransparency()))
					return;
				GlStateManager.enableTexture2D();
				GlStateManager.color(1, 1, 1, 1);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
				{
					this.pos(0, 0, 0, EnumFacing.WEST, textureCoords.x / 16f, textureCoords.y / 16f);
					this.pos(0, 0, size.z * scale, EnumFacing.WEST, textureCoords.x / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(0, -size.y * scale, size.z * scale, EnumFacing.WEST, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(0, -size.y * scale, 0, EnumFacing.WEST, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f);
				}
				Tessellator.getInstance().draw();
			} else {
				GlStateManager.color(1, 1, 0);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
				{
					this.pos(0, 0, 0, EnumFacing.WEST);
					this.pos(0, 0, size.z * scale, EnumFacing.WEST);
					this.pos(0, -size.y * scale, size.z * scale, EnumFacing.WEST);
					this.pos(0, -size.y * scale, 0, EnumFacing.WEST);
				}
				Tessellator.getInstance().draw();
			}
			GlStateManager.disableTexture2D();
		}

		if (this.faceDirection == EnumFacing.EAST) {
			if (this.getTexture() != null) {
				if ((renderTransparentFaces && !this.hasTransparency()) || (!renderTransparentFaces && this.hasTransparency()))
					return;
				GlStateManager.enableTexture2D();
				GlStateManager.color(1, 1, 1, 1);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
				{
					this.pos(size.x * scale, 0, size.z * scale, EnumFacing.EAST, textureCoords.x / 16f, textureCoords.y / 16f);
					this.pos(size.x * scale, 0, 0, EnumFacing.EAST, textureCoords.x / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(size.x * scale, -size.y * scale, 0, EnumFacing.EAST, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(size.x * scale, -size.y * scale, size.z * scale, EnumFacing.EAST, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f);
				}
				Tessellator.getInstance().draw();
			} else {
				GlStateManager.color(0, 1, 0);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
				{
					this.pos(size.x * scale, 0, size.z * scale, EnumFacing.EAST);
					this.pos(size.x * scale, 0, 0, EnumFacing.EAST);
					this.pos(size.x * scale, -size.y * scale, 0, EnumFacing.EAST);
					this.pos(size.x * scale, -size.y * scale, size.z * scale, EnumFacing.EAST);
				}
				Tessellator.getInstance().draw();
			}
			GlStateManager.disableTexture2D();
		}
	}

	private void pos(float x, float y, float z, EnumFacing faceDirection) {
		this.pos(x, y, z, faceDirection, -1, -1);
	}

	private void pos(float x, float y, float z, EnumFacing faceDirection, float u, float v) {
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.pos(x, y, z);

		if (u >= 0 && v >= 0) {
			GlStateManager.enableTexture2D();
			buffer.tex(u, v);
		}
		switch (faceDirection) {
		case DOWN:
			buffer.normal(0, -1, 0);
			break;
		case UP:
			buffer.normal(0, 1, 0);
			break;
		case NORTH:
			buffer.normal(0, 0, -1);
			break;
		case SOUTH:
			buffer.normal(0, 0, 1);
			break;
		case EAST:
			buffer.normal(-1, 0, 0);
			break;
		case WEST:
			buffer.normal(1, 0, 0);
			break;
		default:
			buffer.normal(0, 0, 0);
			break;
		}
		buffer.endVertex();
	}

	public void bindTexture() {
		if (this.texture != null) {
			TextureUtils.bindTexture(this.textureLocation);
		}
	}

	public boolean hasTransparency() {
		return this.texture != null && this.texture.hasTransparency();
	}

	public NamedBufferedImage getTexture() {
		return texture;
	}

	public Vector4f getTextureCoords() {
		return textureCoords;
	}

	public boolean isCullFace() {
		return cullFace;
	}

	public EnumFacing getFaceDirection() {
		return faceDirection;
	}

	public Cube getParentCube() {
		return parentCube;
	}

	public Face setTexture(@Nullable NamedBufferedImage texture, Vector4f textureCoords) {
		return this.setTexture(texture, textureCoords.x, textureCoords.y, textureCoords.z, textureCoords.w);
	}

	public Face setTexture(@Nullable NamedBufferedImage texture, float u, float v, float width, float height) {
		if (texture == null) {
			this.texture = null;
			this.textureCoords.set(0, 0, 16, 16);
		} else {
			if (Lib.resourceExists(texture.getLocation())) {
				this.textureLocation = texture.getLocation();
			} else {
				if (!TEXTURE_CACHE.containsKey(texture.getLocation())) {
					this.textureLocation = TextureUtils.createBufferedImageTexture(texture.getImage());
					TEXTURE_CACHE.put(texture.getLocation(), this.textureLocation);
				} else {
					this.textureLocation = TEXTURE_CACHE.get(texture.getLocation());
				}
			}
			this.texture = texture;
			this.textureCoords.set(u, v, width, height);
		}
		return this;
	}

	public Face setCullFace(boolean cullFace) {
		this.cullFace = cullFace;
		return this;
	}

	public Face copy() {
		return copy(this);
	}

	public Face copy(Face face) {
		if (face == NULL_FACE)
			return NULL_FACE;
		Face newFace = new Face(face.parentCube, face.faceDirection);
		newFace.textureLocation = face.textureLocation;
		newFace.texture = face.texture;
		newFace.textureCoords.set(face.textureCoords);
		newFace.cullFace = face.cullFace;
		return newFace;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return this.copy();
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		if (this.texture != null) {
			nbt.setTag("texture", this.texture.serializeNBT());
		}
		nbt.setTag("textureCoords", NBTHelper.setVector(this.textureCoords));
		nbt.setBoolean("cullFace", this.cullFace);
		nbt.setInteger("facingDirection", this.faceDirection.getIndex());
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("texture", Constants.NBT.TAG_COMPOUND)) {
			this.texture = NamedBufferedImage.fromTag(nbt.getCompoundTag("texture"));
			this.textureLocation = TextureUtils.createBufferedImageTexture(this.texture.getImage());
		}
		this.textureCoords = NBTHelper.getVector4f(nbt.getCompoundTag("textureCoords"));
		this.cullFace = nbt.getBoolean("cullFace");
		this.faceDirection = EnumFacing.getFront(nbt.getInteger("facingDirection"));
	}

	public static Face fromTag(Cube parentCube, NBTTagCompound nbt) {
		Face face = new Face(parentCube);
		face.deserializeNBT(nbt);
		return face;
	}

	public static void clearCache() {
		TEXTURE_CACHE.clear();
	}
}