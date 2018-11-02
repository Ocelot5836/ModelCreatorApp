package com.ocelot.api.geometry;

import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;
import com.ocelot.api.utils.Lib;
import com.ocelot.api.utils.NBTHelper;
import com.ocelot.api.utils.NamedBufferedImage;
import com.ocelot.api.utils.TextureUtils;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public class Face implements INBTSerializable<NBTTagCompound> {

	private static final Map<ResourceLocation, ResourceLocation> TEXTURE_CACHE = Maps.<ResourceLocation, ResourceLocation>newHashMap();

	public static final Face NULL_FACE = null;

	private Cube parentCube;
	private EnumFacing faceDirection;
	private ResourceLocation textureLocation;
	private NamedBufferedImage texture;
	private Vector4f autoTextureCoords;
	private Vector4f textureCoords;
	private float rotation;
	private boolean cullFace;
	private boolean enabled;
	private boolean autoUV;
	private boolean fill;

	private Face(Cube parentCube) {
		this(parentCube, null);
	}

	protected Face(Cube parentCube, EnumFacing faceDirection) {
		this.parentCube = parentCube;
		this.faceDirection = faceDirection;
		this.textureLocation = null;
		this.texture = null;
		this.autoTextureCoords = new Vector4f(0, 0, 16, 16);
		this.textureCoords = new Vector4f(0, 0, 16, 16);
		this.rotation = 0;
		this.cullFace = false;
		this.enabled = true;
		this.autoUV = true;
		this.fill = false;
	}

	public void render(boolean renderTransparentFaces, float scale) {
		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		Vector3f size = this.parentCube.getSize();

		this.bindTexture();

		Vector4f textureCoords = new Vector4f(this.textureCoords);
		if (this.autoUV) {
			switch (this.faceDirection) {
			case DOWN:
				this.autoTextureCoords.set(0, 0, size.x, size.z);
				break;
			case EAST:
				this.autoTextureCoords.set(0, 0, size.y, size.z);
				break;
			case NORTH:
				this.autoTextureCoords.set(0, 0, size.y, size.x);
				break;
			case SOUTH:
				this.autoTextureCoords.set(0, 0, size.x, size.y);
				break;
			case UP:
				this.autoTextureCoords.set(0, 0, size.x, size.z);
				break;
			case WEST:
				this.autoTextureCoords.set(0, 0, size.z, size.y);
				break;
			default:
				break;
			}

			textureCoords.set(this.autoTextureCoords);
		}

		if (this.fill) {
			textureCoords.set(0, 0, 16, 16);
		}

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
					// GlStateManager.translate(this.parentCube.getSize().x * 8, -this.parentCube.getSize().x * 8, this.parentCube.getSize().y * 8);
					// GlStateManager.rotate(this.rotation, 0, 1, 0);
					// GlStateManager.translate(-this.parentCube.getSize().x * 8, this.parentCube.getSize().x * 8, -this.parentCube.getSize().y * 8);

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
					// GlStateManager.translate(this.parentCube.getSize().x * 8, -this.parentCube.getSize().x * 8, this.parentCube.getSize().y * 8);
					// GlStateManager.rotate(-90 + -this.rotation, 0, 0, 1);
					// GlStateManager.translate(-this.parentCube.getSize().x * 8, this.parentCube.getSize().x * 8, -this.parentCube.getSize().y * 8);

					this.pos(size.x * scale, 0, 0, EnumFacing.NORTH, textureCoords.x / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(0, 0, 0, EnumFacing.NORTH, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f);
					this.pos(0, -size.y * scale, 0, EnumFacing.NORTH, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f);
					this.pos(size.x * scale, -size.y * scale, 0, EnumFacing.NORTH, textureCoords.x / 16f, textureCoords.y / 16f);
				}
				Tessellator.getInstance().draw();
			} else {
				GlStateManager.color(1, 0, 0);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
				{
					this.pos(size.x * scale, 0, 0, EnumFacing.NORTH);
					this.pos(0, 0, 0, EnumFacing.NORTH);
					this.pos(0, -size.y * scale, 0, EnumFacing.NORTH);
					this.pos(size.x * scale, -size.y * scale, 0, EnumFacing.NORTH);
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
					// GlStateManager.translate(this.parentCube.getSize().x * 8, -this.parentCube.getSize().x * 8, this.parentCube.getSize().y * 8);
					// GlStateManager.rotate(90 + this.rotation, 0, 0, 1);
					// GlStateManager.translate(-this.parentCube.getSize().x * 8, this.parentCube.getSize().x * 8, -this.parentCube.getSize().y * 8);

					this.pos(size.x * scale, -size.y * scale, size.z * scale, EnumFacing.SOUTH, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f);
					this.pos(0, -size.y * scale, size.z * scale, EnumFacing.SOUTH, textureCoords.x / 16f, textureCoords.y / 16f);
					this.pos(0, 0, size.z * scale, EnumFacing.SOUTH, textureCoords.x / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(size.x * scale, 0, size.z * scale, EnumFacing.SOUTH, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
				}
				Tessellator.getInstance().draw();
			} else {
				GlStateManager.color(0, 0, 1);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
				{
					this.pos(0, 0, size.z * scale, EnumFacing.SOUTH);
					this.pos(size.x * scale, 0, size.z * scale, EnumFacing.SOUTH);
					this.pos(size.x * scale, -size.y * scale, size.z * scale, EnumFacing.SOUTH);
					this.pos(0, -size.y * scale, size.z * scale, EnumFacing.SOUTH);
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
					// GlStateManager.translate(0, -this.parentCube.getSize().x * 8, this.parentCube.getSize().y * 8);
					// GlStateManager.rotate(90 + this.rotation, 1, 0, 0);
					// GlStateManager.translate(0, this.parentCube.getSize().x * 8, -this.parentCube.getSize().y * 8);

					this.pos(0, -size.y * scale, size.z * scale, EnumFacing.WEST, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f);
					this.pos(0, -size.y * scale, 0, EnumFacing.WEST, textureCoords.x / 16f, textureCoords.y / 16f);
					this.pos(0, 0, 0, EnumFacing.WEST, textureCoords.x / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(0, 0, size.z * scale, EnumFacing.WEST, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
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
					this.pos(size.x * scale, 0, size.z * scale, EnumFacing.EAST, textureCoords.x / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(size.x * scale, 0, 0, EnumFacing.EAST, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f);
					this.pos(size.x * scale, -size.y * scale, 0, EnumFacing.EAST, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f);
					this.pos(size.x * scale, -size.y * scale, size.z * scale, EnumFacing.EAST, textureCoords.x / 16f, textureCoords.y / 16f);
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
			if (this.textureLocation == null) {
				this.loadTexture();
			}
			TextureUtils.bindTexture(this.textureLocation);
		}
	}

	private void loadTexture() {
		if (Lib.resourceExists(this.texture.getLocation())) {
			this.textureLocation = this.texture.getLocation();
		} else {
			if (!TEXTURE_CACHE.containsKey(this.texture.getLocation())) {
				this.textureLocation = TextureUtils.createBufferedImageTexture(this.texture.getImage());
				TEXTURE_CACHE.put(this.texture.getLocation(), this.textureLocation);
			} else {
				this.textureLocation = TEXTURE_CACHE.get(this.texture.getLocation());
			}
		}
	}

	public boolean hasTransparency() {
		return this.texture != null && this.texture.hasTransparency();
	}

	public NamedBufferedImage getTexture() {
		return texture;
	}

	public Vector4f getTextureCoords() {
		return autoUV ? autoTextureCoords : textureCoords;
	}

	public float getRotation() {
		return rotation;
	}

	public boolean isCullFace() {
		return cullFace;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isAutoUV() {
		return autoUV;
	}

	public boolean isFill() {
		return fill;
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
			this.texture = texture;
			this.textureCoords.set(u, v, width, height);
		}
		this.textureLocation = null;
		return this;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public Face setCullFace(boolean cullFace) {
		this.cullFace = cullFace;
		return this;
	}

	public Face setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public Face setAutoUV(boolean autoUV) {
		this.autoUV = autoUV;
		return this;
	}

	public Face setFill(boolean fill) {
		this.fill = fill;
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
		newFace.rotation = face.rotation;
		newFace.cullFace = face.cullFace;
		newFace.enabled = face.enabled;
		newFace.autoUV = face.autoUV;
		newFace.fill = face.fill;
		return newFace;
	}

	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("textureCoords", NBTHelper.setVector(this.textureCoords));
		nbt.setFloat("rotation", this.rotation);
		nbt.setBoolean("cullFace", this.cullFace);
		nbt.setBoolean("enabled", this.enabled);
		nbt.setBoolean("autoUV", this.autoUV);
		nbt.setBoolean("fill", this.fill);
		nbt.setInteger("facingDirection", this.faceDirection.getIndex());
		return nbt;
	}

	public void deserializeNBT(NBTTagCompound nbt, @Nullable NamedBufferedImage texture) {
		if (texture != null) {
			this.texture = texture;
		}
		this.textureCoords = NBTHelper.getVector4f(nbt.getCompoundTag("textureCoords"));
		this.rotation = nbt.getFloat("rotation");
		this.cullFace = nbt.getBoolean("cullFace");
		this.enabled = nbt.getBoolean("enabled");
		this.autoUV = nbt.getBoolean("autoUV");
		this.fill = nbt.getBoolean("fill");
		this.faceDirection = EnumFacing.getFront(nbt.getInteger("facingDirection"));
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.deserializeNBT(nbt, null);
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("texture", this.texture).add("textureCoords", this.textureCoords).add("rotation", this.rotation).add("cullFace", this.cullFace).add("enabled", this.enabled).add("autoUV", this.autoUV).add("fill", this.fill).add("direction", this.faceDirection).toString();
	}

	public static Face fromTag(Cube parentCube, NBTTagCompound nbt, @Nullable NamedBufferedImage textures) {
		Face face = new Face(parentCube);
		face.deserializeNBT(nbt, textures);
		return face;
	}

	public static Face fromTag(Cube parentCube, NBTTagCompound nbt) {
		return fromTag(parentCube, nbt, null);
	}

	public static void clearCache() {
		for (ResourceLocation location : TEXTURE_CACHE.keySet()) {
			TextureUtils.deleteTexture(TEXTURE_CACHE.get(location));
		}
		TEXTURE_CACHE.clear();
	}
}