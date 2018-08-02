package com.ocelot.mod.application.component;

import java.awt.image.BufferedImage;

import org.lwjgl.util.vector.Vector4f;

import com.ocelot.api.utils.TextureUtils;
import com.ocelot.mod.lib.Lib;
import com.ocelot.mod.lib.NBTHelper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class Face implements Cloneable, INBTSerializable<NBTTagCompound> {

	public static final Face NULL_FACE = null;

	private ResourceLocation textureLocation;
	private BufferedImage texture;
	private Vector4f textureCoords;
	private boolean cullFace;

	protected Face() {
		this.textureLocation = null;
		this.texture = null;
		this.textureCoords = new Vector4f(0, 0, 1, 1);
		this.cullFace = false;
	}

	public ResourceLocation getTextureLocation() {
		return textureLocation;
	}

	public BufferedImage getTexture() {
		return texture;
	}

	public Vector4f getTextureCoords() {
		return textureCoords;
	}

	public boolean isCullFace() {
		return cullFace;
	}

	public Face setTexture(ResourceLocation texture, Vector4f textureCoords) {
		return this.setTexture(Lib.loadImage(texture), textureCoords.x, textureCoords.y, textureCoords.z, textureCoords.w);
	}

	public Face setTexture(ResourceLocation texture, float u, float v, float width, float height) {
		return this.setTexture(Lib.loadImage(texture), u, v, width, height);
	}

	public Face setTexture(BufferedImage texture, Vector4f textureCoords) {
		return this.setTexture(texture, textureCoords.x, textureCoords.y, textureCoords.z, textureCoords.w);
	}

	public Face setTexture(BufferedImage texture, float u, float v, float width, float height) {
		this.texture = texture;
		this.textureLocation = TextureUtils.createBufferedImageTexture(texture);
		this.textureCoords.set(u, v, width, height);
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
		if (this.textureLocation != null) {
			nbt.setString("textureLocation", this.textureLocation.toString());
		}
		if (this.texture != null) {
			nbt.setTag("texture", NBTHelper.setBufferedImage(this.texture));
		}
		if (this.textureCoords != null) {
			nbt.setTag("textureCoords", NBTHelper.setVector(this.textureCoords));
		}
		nbt.setBoolean("cullFace", this.cullFace);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		if (nbt.hasKey("textureLocation", Constants.NBT.TAG_STRING)) {
			this.textureLocation = new ResourceLocation(nbt.getString("textureLocation"));
		}
		if (nbt.hasKey("texture", Constants.NBT.TAG_COMPOUND)) {
			this.texture = NBTHelper.getBufferedImage(nbt.getCompoundTag("texture"));
		}
		if (nbt.hasKey("textureCoords", Constants.NBT.TAG_COMPOUND)) {
			this.textureCoords = NBTHelper.getVector4f(nbt.getCompoundTag("textureCoords"));
		}
		this.cullFace = nbt.getBoolean("cullFace");
	}

	public static Face fromTag(NBTTagCompound nbt) {
		Face face = new Face();
		face.deserializeNBT(nbt);
		return face;
	}
}