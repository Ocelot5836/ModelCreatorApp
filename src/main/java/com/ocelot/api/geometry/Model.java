package com.ocelot.api.geometry;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector3f;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ocelot.api.utils.NamedBufferedImage;
import com.ocelot.mod.Mod;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public class Model {

	private List<NamedBufferedImage> textures;

	private List<Cube> cubes;
	private String jsonName;
	private boolean ambientOcclusion;
	private NamedBufferedImage particle;

	public Model(List<Cube> cubes, String jsonName, boolean ambientOcclusion, NamedBufferedImage particle) {
		this.textures = new ArrayList<NamedBufferedImage>();
		this.cubes = new ArrayList<Cube>(cubes);
		this.jsonName = jsonName;
		this.ambientOcclusion = ambientOcclusion;
		this.particle = particle;

		int nextId = 0;
		for (Cube cube : cubes) {
			Face[] faces = cube.getFaces();
			for (int i = 0; i < faces.length; i++) {
				Face face = faces[i];
				if (face != Face.NULL_FACE) {
					NamedBufferedImage texture = face.getTexture();
					if (texture != null) {
						this.textures.add(texture);
					}
				}
			}
		}
	}

	public static class Serializer implements JsonSerializer<Model> {
		@Override
		public JsonElement serialize(Model src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject json = new JsonObject();
			JsonObject textures = new JsonObject();
			JsonArray elements = new JsonArray();

			/** Comment */
			json.addProperty("_comment", I18n.format("default.json.comment", "Ocelot5836", "https://mrcrayfish.com/tools?id=mc"));

			/** global properties */
			json.addProperty("ambientOcclusion", src.ambientOcclusion);

			/** Textures */
			int nextId = 0;
			Map<ResourceLocation, NamedBufferedImage> addedImages = new HashMap<ResourceLocation, NamedBufferedImage>();
			for (int i = 0; i < src.textures.size(); i++) {
				NamedBufferedImage image = src.textures.get(i);
				if (!addedImages.containsKey(image.getLocation())) {
					addedImages.put(image.getLocation(), image);
				}
			}
			int i = 0;
			for (ResourceLocation location : addedImages.keySet()) {
				textures.addProperty(Integer.toString(i), String.valueOf(src.textures.get(i).getLocation()).replaceAll("textures/", ""));
				i++;
			}

			if (src.particle != null && src.particle.getLocation() != null) {
				textures.addProperty("particle", src.particle.getLocation().toString().replaceAll("textures/", ""));
			}
			json.add("textures", textures);

			/** Elements */
			for (Cube cube : src.cubes) {
				JsonObject cubeObj = new JsonObject();
				JsonArray from = new JsonArray();
				JsonArray to = new JsonArray();
				JsonObject faces = new JsonObject();

				/** Cube name */
				cubeObj.addProperty("name", cube.getName());

				/** from */
				from.add(cube.getPosition().x);
				from.add(cube.getPosition().y);
				from.add(cube.getPosition().z);
				cubeObj.add("from", from);

				/** to */
				to.add(cube.getPosition().x + cube.getSize().x);
				to.add(cube.getPosition().y + cube.getSize().y);
				to.add(cube.getPosition().z + cube.getSize().z);
				cubeObj.add("to", to);

				/** rotation */
				Vector3f cubeRotation = cube.getRotation();
				EnumFacing.Axis rotationAxis = cubeRotation.x != 0 ? EnumFacing.Axis.X : cubeRotation.y != 0 ? EnumFacing.Axis.Y : cubeRotation.z != 0 ? EnumFacing.Axis.Z : null;
				if (rotationAxis != null) {
					Vector3f cubeRotationPoint = cube.getRotationPoint();
					JsonObject rotation = new JsonObject();

					JsonArray origin = new JsonArray();
					origin.add(cubeRotationPoint.x);
					origin.add(cubeRotationPoint.y);
					origin.add(cubeRotationPoint.z);
					rotation.add("origin", origin);

					rotation.addProperty("axis", rotationAxis.getName2());
					
					switch(rotationAxis) {
					case X:
						rotation.addProperty("angle", cubeRotation.x);
						break;
					case Y:
						rotation.addProperty("angle", cubeRotation.y);
						break;
					case Z:
						rotation.addProperty("angle", cubeRotation.z);
						break;
					default:
						rotation.addProperty("angle", 0);
						break;
					}

					cubeObj.add("rotation", rotation);
				}

				/** shade */
				cubeObj.addProperty("shade", cube.shouldShade());

				/** faces */
				for (i = 0; i < EnumFacing.values().length; i++) {
					EnumFacing facing = EnumFacing.values()[i];
					Face face = cube.getFace(facing);

					JsonObject faceObj = new JsonObject();

					/** texture */
					int textureID = -1;
					for (int j = 0; j < src.textures.size(); j++) {
						if (src.textures.get(j).getLocation().toString().equalsIgnoreCase(face.getTexture().getLocation().toString())) {
							textureID = j;
							break;
						}
					}
					faceObj.addProperty("texture", "#" + textureID);

					/** uv */
					JsonArray uv = new JsonArray();
					uv.add(face.getTextureCoords().x);
					uv.add(face.getTextureCoords().y);
					uv.add(face.getTextureCoords().z);
					uv.add(face.getTextureCoords().w);
					faceObj.add("uv", uv);

					/** cull face */
					if (face.isCullFace()) {
						faceObj.addProperty("cullface", facing.getName2());
					}

					faces.add(facing.getName2(), faceObj);
				}
				cubeObj.add("faces", faces);

				elements.add(cubeObj);
			}
			json.add("elements", elements);

			this.saveTexturesToDisc(src.jsonName, src.textures, src.particle);

			return json;
		}

		private void saveTexturesToDisc(String jsonName, List<NamedBufferedImage> textures, NamedBufferedImage particle) {
			try {
				File folder = new File(Loader.instance().getConfigDir(), Mod.MOD_ID + "/export/" + jsonName + "/textures");
				if (folder.exists()) {
					folder.delete();
				}

				folder.mkdirs();

				for (NamedBufferedImage image : textures) {
					File file = new File(folder, image.getLocation().getResourcePath().toString().substring(9));
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					ImageIO.write(image.getImage(), "png", file);
				}

				if (particle != null) {
					File file = new File(folder, particle.getLocation().getResourcePath().toString().substring(9));
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					ImageIO.write(particle.getImage(), "png", file);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}