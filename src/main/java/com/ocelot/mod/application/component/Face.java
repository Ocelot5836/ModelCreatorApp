package com.ocelot.mod.application.component;

import java.lang.reflect.Type;

import org.lwjgl.util.vector.Vector4f;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.util.ResourceLocation;

public class Face implements Cloneable {

	public static final Face NULL_FACE = null;

	private ResourceLocation texture;
	private Vector4f textureCoords;
	private boolean cullFace;

	protected Face() {
		this.texture = null;
		this.textureCoords = new Vector4f(0, 0, 1, 1);
		this.cullFace = false;
	}

	public ResourceLocation getTexture() {
		return texture;
	}

	public Vector4f getTextureCoords() {
		return textureCoords;
	}

	public boolean isCullFace() {
		return cullFace;
	}

	public Face setTexture(ResourceLocation texture, Vector4f textureCoords) {
		return this.setTexture(texture, textureCoords.x, textureCoords.y, textureCoords.z, textureCoords.w);
	}

	public Face setTexture(ResourceLocation texture, float u, float v, float width, float height) {
		this.texture = texture;
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
		if(face == NULL_FACE)
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
}