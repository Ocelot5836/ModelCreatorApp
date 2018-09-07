package com.ocelot.api.geometry;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import com.google.common.collect.Lists;
import com.ocelot.api.libs.NBTHelper;
import com.ocelot.api.utils.NamedBufferedImage;
import com.ocelot.mod.lib.Lib;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class Cube implements Cloneable, INBTSerializable<NBTTagCompound> {

	private Vector3f position;
	private Vector3f size;
	private Vector3f rotation;
	private Vector3f rotationPoint;
	private Face[] faces;
	private String name;
	private boolean shade;
	private boolean hasTransparency;

	protected Cube() {
		this(0, 0, 0, 0, 0, 0, 0, 0, 0);
	}

	public Cube(Vector3f position, Vector3f size, Vector3f rotation) {
		this(position.x, position.y, position.z, size.x, size.y, size.z, rotation.x, rotation.y, rotation.z);
	}

	public Cube(float x, float y, float z, float sizeX, float sizeY, float sizeZ, float rotationX, float rotationY, float rotationZ) {
		this.position = new Vector3f(x, y, z);
		this.size = new Vector3f(sizeX, sizeY, sizeZ);
		this.rotation = new Vector3f(rotationX, rotationY, rotationZ);
		this.rotationPoint = new Vector3f(8, 8, 8);
		this.faces = new Face[] { new Face(this, EnumFacing.DOWN), new Face(this, EnumFacing.UP), new Face(this, EnumFacing.NORTH), new Face(this, EnumFacing.SOUTH), new Face(this, EnumFacing.WEST), new Face(this, EnumFacing.EAST) };
		this.name = I18n.format("app.mca.mc.cube.name");
		this.shade = true;
		this.hasTransparency = false;
	}

	public void applyRenderTransforms() {
		float scale = 16f;

		GlStateManager.translate(position.x * scale, -(position.y + size.y - 1) * scale, position.z * scale);

		GlStateManager.translate(0, -scale, 0);
		GlStateManager.translate(this.rotationPoint.x * scale, this.rotationPoint.y * scale, this.rotationPoint.z * scale);
		GlStateManager.rotate(this.rotation.x, 1, 0, 0);
		GlStateManager.rotate(this.rotation.y, 0, 1, 0);
		GlStateManager.rotate(this.rotation.z, 0, 0, 1);
		GlStateManager.translate(-this.rotationPoint.x * scale, -this.rotationPoint.y * scale, -this.rotationPoint.z * scale);

		GlStateManager.translate(0, scale * size.y, 0);
	}

	public void applyLighting() {
		if (this.shade) {
			RenderHelper.enableStandardItemLighting();
		} else {
			RenderHelper.disableStandardItemLighting();
		}
	}

	public void queueFaceRenders(List<Face> facesToRender, Camera camera, float partialTicks) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		camera.rotate(partialTicks);

		if (faces[EnumFacing.DOWN.ordinal()] != Face.NULL_FACE && faces[EnumFacing.DOWN.ordinal()].isEnabled()) {
			facesToRender.add(faces[EnumFacing.DOWN.ordinal()]);
		}

		if (faces[EnumFacing.NORTH.ordinal()] != Face.NULL_FACE && faces[EnumFacing.NORTH.ordinal()].isEnabled()) {
			facesToRender.add(faces[EnumFacing.NORTH.ordinal()]);
		}

		if (faces[EnumFacing.EAST.ordinal()] != Face.NULL_FACE && faces[EnumFacing.EAST.ordinal()].isEnabled()) {
			facesToRender.add(faces[EnumFacing.EAST.ordinal()]);
		}

		if (faces[EnumFacing.SOUTH.ordinal()] != Face.NULL_FACE && faces[EnumFacing.SOUTH.ordinal()].isEnabled()) {
			facesToRender.add(faces[EnumFacing.SOUTH.ordinal()]);
		}

		if (faces[EnumFacing.WEST.ordinal()] != Face.NULL_FACE && faces[EnumFacing.WEST.ordinal()].isEnabled()) {
			facesToRender.add(faces[EnumFacing.WEST.ordinal()]);
		}

		if (faces[EnumFacing.UP.ordinal()] != Face.NULL_FACE && faces[EnumFacing.UP.ordinal()].isEnabled()) {
			facesToRender.add(faces[EnumFacing.UP.ordinal()]);
		}
	}

	public Cube textureFace(EnumFacing face, NamedBufferedImage image) {
		this.getFace(face).setTexture(image, 0, 0, 1, 1);
		return this;
	}

	public Cube setTextureCoords(EnumFacing face, float u, float v, float width, float height) {
		this.getFace(face).setTexture(this.getFace(face).getTexture(), u, v, width, height);
		return this;
	}

	public Cube cullFace(EnumFacing face, boolean cull) {
		this.getFace(face).setCullFace(cull);
		return this;
	}

	public Cube removeFace(EnumFacing face) {
		this.faces[face.getIndex()] = Face.NULL_FACE;
		return this;
	}

	public Cube resetFace(EnumFacing side) {
		this.faces[side.getIndex()] = new Face(this, side);
		return this;
	}

	public double distance(Cube other) {
		return distance(other.getPosition().x, other.getPosition().y, other.getPosition().z);
	}

	public double distance(Vector3f other) {
		return distance(other.x, other.y, other.z);
	}

	public double distance(float otherX, float otherY, float otherZ) {
		return Lib.distance(this.position.x, this.position.y, this.position.z, otherX, otherY, otherZ);
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public Vector3f getRotationPoint() {
		return rotationPoint;
	}

	public Vector3f getSize() {
		return size;
	}

	public Face[] getFaces() {
		return faces;
	}

	public Face getFace(EnumFacing face) {
		return faces[face.getIndex()];
	}

	public String getName() {
		return name;
	}

	public boolean shouldShade() {
		return shade;
	}

	public boolean hasTransparency() {
		for (int i = 0; i < EnumFacing.values().length; i++) {
			Face face = this.getFace(EnumFacing.values()[i]);
			if (face.getTexture() != null && face.getTexture().hasTransparency()) {
				return true;
			}
		}
		return false;
	}

	public Cube setPosition(Vector3f position) {
		return this.setPosition(position.x, position.y, position.z);
	}

	public Cube setPosition(float x, float y, float z) {
		this.position.set(x, y, z);
		return this;
	}

	public Cube addPosition(float x, float y, float z) {
		return this.setPosition(this.position.x + x, this.position.y + y, this.position.z + z);
	}

	public Cube subPosition(float x, float y, float z) {
		return this.setPosition(this.position.x - x, this.position.y - y, this.position.z - z);
	}

	public Cube setRotation(Vector3f rotation) {
		return this.setRotation(rotation.x, rotation.y, rotation.z);
	}

	public Cube setRotation(float x, float y, float z) {
		this.rotation.set(x, y, z);
		return this;
	}

	public Cube addRotation(float x, float y, float z) {
		return this.setRotation(this.rotation.x + x, this.rotation.y + y, this.rotation.z + z);
	}

	public Cube subRotation(float x, float y, float z) {
		return this.setRotation(this.rotation.x - x, this.rotation.y - y, this.rotation.z - z);
	}

	public Cube setRotationPoint(Vector3f rotationPoint) {
		return this.setRotationPoint(rotationPoint.x, rotationPoint.y, rotationPoint.z);
	}

	public Cube setRotationPoint(float x, float y, float z) {
		this.rotationPoint.set(x, y, z);
		return this;
	}

	public Cube addRotationPoint(float x, float y, float z) {
		return this.setRotationPoint(this.rotationPoint.x + x, this.rotationPoint.y + y, this.rotationPoint.z + z);
	}

	public Cube subRotationPoint(float x, float y, float z) {
		return this.setRotationPoint(this.rotationPoint.x - x, this.rotationPoint.y - y, this.rotationPoint.z - z);
	}

	public Cube setSize(Vector3f size) {
		return this.setSize(size.x, size.y, size.z);
	}

	public Cube setSize(float x, float y, float z) {
		this.size.set(x, y, z);
		return this;
	}

	public Cube addSize(float x, float y, float z) {
		return this.setSize(this.size.x + x, this.size.y + y, this.size.z + z);
	}

	public Cube subSize(float x, float y, float z) {
		return this.setSize(this.size.x - x, this.size.y - y, this.size.z - z);
	}

	public Cube setFace(EnumFacing side, Face face) {
		this.faces[side.getIndex()] = face;
		return this;
	}

	public Cube setFaceEnabled(EnumFacing side, boolean enabled) {
		this.faces[side.getIndex()].setEnabled(enabled);
		return this;
	}

	public Cube setName(String name) {
		this.name = name;
		return this;
	}

	public void setShade(boolean shade) {
		this.shade = shade;
	}

	public Cube copy() {
		return copy(this);
	}

	public Cube copy(Cube cube) {
		Cube newCube = new Cube(cube.position.x, cube.position.y, cube.position.z, cube.size.x, cube.size.y, cube.size.z, cube.rotation.x, cube.rotation.y, cube.rotation.z);
		newCube.rotationPoint.set(cube.rotationPoint);
		for (int i = 0; i < newCube.faces.length; i++) {
			newCube.faces[i] = cube.faces[i].copy();
		}
		newCube.name = cube.name;
		newCube.shade = cube.shade;
		return newCube;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return this.copy();
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("position", NBTHelper.setVector(this.position));
		nbt.setTag("size", NBTHelper.setVector(this.size));
		nbt.setTag("rotation", NBTHelper.setVector(this.rotation));
		nbt.setTag("rotationPoint", NBTHelper.setVector(this.rotationPoint));

		NBTTagCompound faces = new NBTTagCompound();
		List<NamedBufferedImage> textures = Lists.<NamedBufferedImage>newArrayList();
		for (int i = 0; i < EnumFacing.values().length; i++) {
			EnumFacing facing = EnumFacing.values()[i];
			Face face = this.getFace(facing);
			int textureId = -1;
			if (face != Face.NULL_FACE) {
				if (face.getTexture() != null) {
					NamedBufferedImage texture = face.getTexture();
					boolean flag = true;
					for (int j = 0; j < textures.size(); j++) {
						NamedBufferedImage image = textures.get(j);
						if (image.getLocation().equals(texture.getLocation())) {
							textureId = j;
							flag = false;
							break;
						}
					}
					if (flag) {
						textures.add(texture);
					}
				}
				faces.setTag(facing.getName2(), face.serializeNBT(textureId));
			}
		}
		nbt.setTag("faces", faces);

		NBTTagList texturesList = new NBTTagList();
		for (NamedBufferedImage image : textures) {
			texturesList.appendTag(image.serializeNBT());
		}
		nbt.setTag("textures", texturesList);

		nbt.setString("name", this.name);
		nbt.setBoolean("shade", this.shade);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.position = NBTHelper.getVector3f(nbt.getCompoundTag("position"));
		this.size = NBTHelper.getVector3f(nbt.getCompoundTag("size"));
		this.rotation = NBTHelper.getVector3f(nbt.getCompoundTag("rotation"));
		this.rotationPoint = NBTHelper.getVector3f(nbt.getCompoundTag("rotationPoint"));
		
		NBTTagCompound faces = nbt.getCompoundTag("faces");
		List<NamedBufferedImage> textures = Lists.<NamedBufferedImage>newArrayList();
		
		NBTTagList texturesTag = nbt.getTagList("textures", Constants.NBT.TAG_COMPOUND);
		for(int i = 0; i < texturesTag.tagCount(); i++) {
			textures.add(NamedBufferedImage.fromTag(texturesTag.getCompoundTagAt(i)));
		}
				
		for (int i = 0; i < EnumFacing.values().length; i++) {
			EnumFacing facing = EnumFacing.values()[i];
			if (faces.hasKey(facing.getName2(), Constants.NBT.TAG_COMPOUND)) {
				this.setFace(facing, Face.fromTag(this, faces.getCompoundTag(facing.getName2()), textures));
			} else {
				this.setFace(facing, Face.NULL_FACE);
			}
		}
		
		this.name = nbt.getString("name");
		this.shade = nbt.getBoolean("shade");
	}

	public static Cube fromTag(NBTTagCompound nbt) {
		Cube cube = new Cube();
		cube.deserializeNBT(nbt);
		return cube;
	}
}