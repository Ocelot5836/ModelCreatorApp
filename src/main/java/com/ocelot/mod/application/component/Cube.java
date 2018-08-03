package com.ocelot.mod.application.component;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.ocelot.mod.application.ApplicationModelCreator;
import com.ocelot.mod.application.Camera;
import com.ocelot.mod.application.dialog.NamedBufferedImage;
import com.ocelot.mod.lib.NBTHelper;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class Cube implements Cloneable, INBTSerializable<NBTTagCompound> {

	private Vector3f position;
	private Vector3f size;
	private Vector3f rotation;
	private Face[] faces;
	private String name;
	private boolean shade;
	private boolean hasTransparency;

	protected Cube() {
		this(0, 0, 0, 0, 0, 0, 0, 0, 0);
	}

	protected Cube(Vector3f position, Vector3f size, Vector3f rotation) {
		this(position.x, position.y, position.z, size.x, size.y, size.z, rotation.x, rotation.y, rotation.z);
	}

	protected Cube(float x, float y, float z, float sizeX, float sizeY, float sizeZ, float rotationX, float rotationY, float rotationZ) {
		this.position = new Vector3f(x, y, z);
		this.size = new Vector3f(sizeX, sizeY, sizeZ);
		this.rotation = new Vector3f(rotationX, rotationY, rotationZ);
		this.faces = new Face[] { new Face(), new Face(), new Face(), new Face(), new Face(), new Face() };
		this.name = I18n.format("default.cube.name");
		this.shade = true;
		this.hasTransparency = false;
	}

	public void render(float x, float y, float z, Camera camera, float partialTicks) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		camera.rotate(partialTicks);

		float scale = 16f;

		GlStateManager.translate(position.x * scale, -(position.y + size.y - 1) * scale, position.z * scale);

		if (this.shade) {
			GlStateManager.pushMatrix();
			RenderHelper.enableStandardItemLighting();
			GlStateManager.popMatrix();
		} else {
			RenderHelper.disableStandardItemLighting();
		}

		GlStateManager.translate(8, -8, 8);
		GlStateManager.rotate(this.rotation.x, 1, 0, 0);
		GlStateManager.rotate(this.rotation.y, 0, 1, 0);
		GlStateManager.rotate(this.rotation.z, 0, 0, 1);
		GlStateManager.translate(-8, -8, -8);

		GlStateManager.translate(0, 16 * size.y, 0);

		if (!ApplicationModelCreator.isFastRender()) {
			if (faces[0] != Face.NULL_FACE) {
				Face face = faces[0];
				face.bindTexture();
				Vector4f textureCoords = face.getTextureCoords();

				if (face.getTexture() != null) {
					GlStateManager.enableTexture2D();
					GlStateManager.color(1, 1, 1, 1);
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
					{
						this.pos(size.x * scale, 0, 0, EnumFacing.DOWN, face.getTextureCoords().x / 16f + textureCoords.z / 16f, textureCoords.y / 16f);
						this.pos(size.x * scale, 0, size.z * scale, EnumFacing.DOWN, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
						this.pos(0, 0, size.z * scale, EnumFacing.DOWN, textureCoords.x / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
						this.pos(0, 0, 0, EnumFacing.DOWN, textureCoords.x / 16f, textureCoords.y / 16f);
					}
				} else {
					GlStateManager.color(1, 0, 1);
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
					{
						this.pos(size.x * scale, 0, 0, EnumFacing.DOWN);
						this.pos(size.x * scale, 0, size.z * scale, EnumFacing.DOWN);
						this.pos(0, 0, size.z * scale, EnumFacing.DOWN);
						this.pos(0, 0, 0, EnumFacing.DOWN);
					}
				}
				tessellator.draw();
				GlStateManager.disableTexture2D();
			}
		}

		if (faces[1] != Face.NULL_FACE) {
			Face face = faces[1];
			face.bindTexture();
			Vector4f textureCoords = face.getTextureCoords();

			if (face.getTexture() != null) {
				GlStateManager.enableTexture2D();
				GlStateManager.color(1, 1, 1, 1);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
				{
					this.pos(0, -size.y * scale, 0, EnumFacing.UP, textureCoords.x / 16f, textureCoords.y / 16f);
					this.pos(0, -size.y * scale, size.z * scale, EnumFacing.UP, textureCoords.x / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(size.x * scale, -size.y * scale, size.z * scale, EnumFacing.UP, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(size.x * scale, -size.y * scale, 0, EnumFacing.UP, face.getTextureCoords().x / 16f + textureCoords.z / 16f, textureCoords.y / 16f);
				}
			} else {
				GlStateManager.color(0, 1, 1);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
				{
					this.pos(0, -size.y * scale, 0, EnumFacing.UP);
					this.pos(0, -size.y * scale, size.z * scale, EnumFacing.UP);
					this.pos(size.x * scale, -size.y * scale, size.z * scale, EnumFacing.UP);
					this.pos(size.x * scale, -size.y * scale, 0, EnumFacing.UP);
				}
			}
			tessellator.draw();
			GlStateManager.disableTexture2D();
		}

		if (!ApplicationModelCreator.isFastRender()) {
			if (faces[2] != Face.NULL_FACE) {
				Face face = faces[2];
				face.bindTexture();
				Vector4f textureCoords = face.getTextureCoords();

				if (face.getTexture() != null) {
					GlStateManager.enableTexture2D();
					GlStateManager.color(1, 1, 1, 1);
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
					{
						this.pos(size.x * scale, 0, 0, EnumFacing.SOUTH, textureCoords.x / 16f, textureCoords.y / 16f);
						this.pos(0, 0, 0, EnumFacing.SOUTH, textureCoords.x / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
						this.pos(0, -size.y * scale, 0, EnumFacing.SOUTH, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
						this.pos(size.x * scale, -size.y * scale, 0, EnumFacing.SOUTH, face.getTextureCoords().x / 16f + textureCoords.z / 16f, textureCoords.y / 16f);
					}
				} else {
					GlStateManager.color(1, 0, 0);
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
					{
						this.pos(size.x * scale, 0, 0, EnumFacing.SOUTH);
						this.pos(0, 0, 0, EnumFacing.SOUTH);
						this.pos(0, -size.y * scale, 0, EnumFacing.SOUTH);
						this.pos(size.x * scale, -size.y * scale, 0, EnumFacing.SOUTH);
					}
				}
				tessellator.draw();
				GlStateManager.disableTexture2D();
			}
		}

		if (faces[3] != Face.NULL_FACE) {
			Face face = faces[3];
			face.bindTexture();
			Vector4f textureCoords = face.getTextureCoords();

			if (face.getTexture() != null) {
				GlStateManager.enableTexture2D();
				GlStateManager.color(1, 1, 1, 1);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
				{
					this.pos(0, 0, size.z * scale, EnumFacing.WEST, textureCoords.x / 16f, textureCoords.y / 16f);
					this.pos(size.x * scale, 0, size.z * scale, EnumFacing.WEST, textureCoords.x / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(size.x * scale, -size.y * scale, size.z * scale, EnumFacing.WEST, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(0, -size.y * scale, size.z * scale, EnumFacing.WEST, face.getTextureCoords().x / 16f + textureCoords.z / 16f, textureCoords.y / 16f);
				}
			} else {
				GlStateManager.color(0, 0, 1);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
				{
					this.pos(0, 0, size.z * scale, EnumFacing.WEST);
					this.pos(size.x * scale, 0, size.z * scale, EnumFacing.WEST);
					this.pos(size.x * scale, -size.y * scale, size.z * scale, EnumFacing.WEST);
					this.pos(0, -size.y * scale, size.z * scale, EnumFacing.WEST);
				}
			}
			tessellator.draw();
			GlStateManager.disableTexture2D();
		}

		if (faces[4] != Face.NULL_FACE) {
			Face face = faces[4];
			face.bindTexture();
			Vector4f textureCoords = face.getTextureCoords();

			if (face.getTexture() != null) {
				GlStateManager.enableTexture2D();
				GlStateManager.color(1, 1, 1, 1);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
				{
					this.pos(0, 0, 0, EnumFacing.WEST, textureCoords.x / 16f, textureCoords.y / 16f);
					this.pos(0, 0, size.z * scale, EnumFacing.WEST, textureCoords.x / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(0, -size.y * scale, size.z * scale, EnumFacing.WEST, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
					this.pos(0, -size.y * scale, 0, EnumFacing.WEST, face.getTextureCoords().x / 16f + textureCoords.z / 16f, textureCoords.y / 16f);
				}
			} else {
				GlStateManager.color(1, 1, 0);
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
				{
					this.pos(0, 0, 0, EnumFacing.WEST);
					this.pos(0, 0, size.z * scale, EnumFacing.WEST);
					this.pos(0, -size.y * scale, size.z * scale, EnumFacing.WEST);
					this.pos(0, -size.y * scale, 0, EnumFacing.WEST);
				}
			}
			tessellator.draw();
			GlStateManager.disableTexture2D();
		}

		if (!ApplicationModelCreator.isFastRender()) {
			if (faces[5] != Face.NULL_FACE) {
				Face face = faces[2];
				face.bindTexture();
				Vector4f textureCoords = face.getTextureCoords();

				if (face.getTexture() != null) {
					GlStateManager.enableTexture2D();
					GlStateManager.color(1, 1, 1, 1);
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
					{
						this.pos(size.x * scale, 0, size.z * scale, EnumFacing.EAST, textureCoords.x / 16f, textureCoords.y / 16f);
						this.pos(size.x * scale, 0, 0, EnumFacing.EAST, textureCoords.x / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
						this.pos(size.x * scale, -size.y * scale, 0, EnumFacing.EAST, textureCoords.x / 16f + textureCoords.z / 16f, textureCoords.y / 16f + textureCoords.w / 16f);
						this.pos(size.x * scale, -size.y * scale, size.z * scale, EnumFacing.EAST, face.getTextureCoords().x / 16f + textureCoords.z / 16f, textureCoords.y / 16f);
					}
				} else {
					GlStateManager.color(0, 1, 0);
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
					{
						this.pos(size.x * scale, 0, size.z * scale, EnumFacing.EAST);
						this.pos(size.x * scale, 0, 0, EnumFacing.EAST);
						this.pos(size.x * scale, -size.y * scale, 0, EnumFacing.EAST);
						this.pos(size.x * scale, -size.y * scale, size.z * scale, EnumFacing.EAST);
					}
				}
				tessellator.draw();
				GlStateManager.disableTexture2D();
			}
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
		this.faces[side.getIndex()] = new Face();
		return this;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getRotation() {
		return rotation;
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
		for (int i = 0; i < EnumFacing.values().length; i++) {
			EnumFacing facing = EnumFacing.values()[i];
			Face face = this.getFace(facing);
			if (face != Face.NULL_FACE) {
				nbt.setTag(facing.getName2(), face.serializeNBT());
			}
		}
		nbt.setString("name", this.name);
		nbt.setBoolean("shade", this.shade);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		this.position = NBTHelper.getVector3f(nbt.getCompoundTag("position"));
		this.size = NBTHelper.getVector3f(nbt.getCompoundTag("size"));
		this.rotation = NBTHelper.getVector3f(nbt.getCompoundTag("rotation"));
		for (int i = 0; i < EnumFacing.values().length; i++) {
			EnumFacing facing = EnumFacing.values()[i];
			if (nbt.hasKey(facing.getName2(), Constants.NBT.TAG_COMPOUND)) {
				Face face = new Face();
				face.deserializeNBT(nbt.getCompoundTag(facing.getName2()));
				this.setFace(facing, face);
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