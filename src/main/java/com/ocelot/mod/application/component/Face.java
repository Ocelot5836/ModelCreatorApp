package com.ocelot.mod.application.component;

import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.util.vector.Vector4f;

import com.google.common.collect.Maps;
import com.ocelot.api.utils.TextureUtils;
import com.ocelot.mod.application.dialog.NamedBufferedImage;
import com.ocelot.mod.lib.Lib;
import com.ocelot.mod.lib.NBTHelper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class Face implements Cloneable, INBTSerializable<NBTTagCompound> {

	private static final Map<ResourceLocation, ResourceLocation> TEXTURE_CACHE = Maps.<ResourceLocation, ResourceLocation>newHashMap();

	public static final Face NULL_FACE = null;

	private ResourceLocation textureLocation;
	private NamedBufferedImage texture;
	private Vector4f textureCoords;
	private boolean cullFace;

	protected Face() {
		this.textureLocation = null;
		this.texture = null;
		this.textureCoords = new Vector4f(0, 0, 16, 16);
		this.cullFace = false;
	}

	public void bindTexture() {
		if (this.texture != null) {
			TextureUtils.bindTexture(this.textureLocation);
		}
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
		Face newFace = new Face();
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
	}

	public static Face fromTag(NBTTagCompound nbt) {
		Face face = new Face();
		face.deserializeNBT(nbt);
		return face;
	}

	public static void clearCache() {
		TEXTURE_CACHE.clear();
	}
}