package com.ocelot.api.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import com.ocelot.api.utils.NamedBufferedImage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

public class ModelData implements INBTSerializable<NBTTagCompound> {

	private List<NamedBufferedImage> textures;
	private List<Cube> cubes;
	private boolean ambientOcclusion;
	private NamedBufferedImage particle;

	public ModelData(Collection<Cube> cubes, boolean ambientOcclusion, NamedBufferedImage particle) {
		this.textures = new ArrayList<NamedBufferedImage>();
		this.cubes = new ArrayList<Cube>(cubes);
		this.ambientOcclusion = ambientOcclusion;
		this.particle = particle;

		for (Cube cube : this.cubes) {
			Face[] faces = cube.getFaces();
			for (int i = 0; i < faces.length; i++) {
				Face face = faces[i];
				if (face != Face.NULL_FACE) {
					NamedBufferedImage texture = face.getTexture();
					if (!this.textures.contains(texture) && texture != null) {
						this.textures.add(texture);
					}
				}
			}
		}
	}

	public ModelData(NBTTagCompound nbt) {
		this.textures = new ArrayList<NamedBufferedImage>();
		this.cubes = new ArrayList<Cube>();
		this.ambientOcclusion = false;
		this.particle = null;
		this.deserializeNBT(nbt);

		for (Cube cube : this.cubes) {
			Face[] faces = cube.getFaces();
			for (int i = 0; i < faces.length; i++) {
				Face face = faces[i];
				if (face != Face.NULL_FACE) {
					NamedBufferedImage texture = face.getTexture();
					if (!this.textures.contains(texture) && texture != null) {
						this.textures.add(texture);
					}
				}
			}
		}
	}

	public NamedBufferedImage[] getTextures() {
		return textures.toArray(new NamedBufferedImage[0]);
	}

	public Cube[] getCubes() {
		return cubes.toArray(new Cube[0]);
	}

	public boolean isAmbientOcclusion() {
		return ambientOcclusion;
	}

	@Nullable
	public NamedBufferedImage getParticle() {
		return particle;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound nbt = new NBTTagCompound();

		if (!this.textures.isEmpty()) {
			NBTTagList textures = new NBTTagList();
			for (NamedBufferedImage texture : this.textures) {
				textures.appendTag(texture.serializeNBT());
			}
			nbt.setTag("textures", textures);
		}

		if (!this.cubes.isEmpty()) {
			NBTTagList cubes = new NBTTagList();
			for (Cube cube : this.cubes) {
				cubes.appendTag(cube.serializeNBT(this.textures));
			}
			nbt.setTag("cubes", cubes);
		}

		if (this.ambientOcclusion) {
			nbt.setBoolean("ambientOcclusion", this.ambientOcclusion);
		}

		if (this.particle != null) {
			nbt.setTag("particle", this.particle.serializeNBT());
		}

		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		System.out.println(nbt);
		
		if (nbt.hasKey("textures", Constants.NBT.TAG_LIST)) {
			NBTTagList textures = nbt.getTagList("textures", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < textures.tagCount(); i++) {
				this.textures.add(new NamedBufferedImage(textures.getCompoundTagAt(i)));
			}
		}

		if (nbt.hasKey("cubes", Constants.NBT.TAG_LIST)) {
			NBTTagList cubes = nbt.getTagList("cubes", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < cubes.tagCount(); i++) {
				this.cubes.add(new Cube(cubes.getCompoundTagAt(i), this.textures));
			}
			System.out.println(cubes);
		}

		if (nbt.hasKey("ambientOcclusion", Constants.NBT.TAG_BYTE)) {
			this.ambientOcclusion = nbt.getBoolean("ambientOcclusion");
		}

		if (nbt.hasKey("particle", Constants.NBT.TAG_COMPOUND)) {
			this.particle = new NamedBufferedImage(nbt.getCompoundTag("particle"));
		}
		
		System.out.println();
	}
}