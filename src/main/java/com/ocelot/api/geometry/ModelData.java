package com.ocelot.api.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import com.ocelot.api.utils.NamedBufferedImage;

public class ModelData {

	private List<NamedBufferedImage> textures;
	private List<Cube> cubes;
	private boolean ambientOcclusion;
	private NamedBufferedImage particle;

	public ModelData(Collection<Cube> cubes, boolean ambientOcclusion, NamedBufferedImage particle) {
		this.textures = new ArrayList<NamedBufferedImage>();
		this.cubes = new ArrayList<Cube>(cubes);
		this.ambientOcclusion = ambientOcclusion;
		this.particle = particle;

		for (Cube cube : cubes) {
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
}