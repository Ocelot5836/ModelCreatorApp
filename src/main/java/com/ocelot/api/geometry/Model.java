package com.ocelot.api.geometry;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector3f;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ocelot.api.utils.NamedBufferedImage;
import com.ocelot.mod.ModelCreator;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class Model {

	private String jsonName;
	private ModelData data;

	public Model(String jsonName, ModelData data) {
		this.jsonName = jsonName;
		this.data = data;
	}
	
	public String getJsonName() {
		return jsonName;
	}
	
	public ModelData getData() {
		return data;
	}

	public static class Serializer implements JsonSerializer<Model> {
		@Override
		public JsonElement serialize(Model src, Type typeOfSrc, JsonSerializationContext context) {
			ModelData data = src.getData();
			JsonObject json = new JsonObject();
			JsonObject textures = new JsonObject();
			JsonArray elements = new JsonArray();

			/** global properties */
			if (!data.isAmbientOcclusion()) {
				json.addProperty("ambientOcclusion", data.isAmbientOcclusion());
			}

			/** Textures */
			Map<ResourceLocation, NamedBufferedImage> addedImages = new HashMap<ResourceLocation, NamedBufferedImage>();
			for (NamedBufferedImage image : data.getTextures()) {
				if (!addedImages.containsKey(image.getLocation())) {
					addedImages.put(image.getLocation(), image);
				}
			}

			int id = 0;
			for (ResourceLocation location : addedImages.keySet()) {
				textures.addProperty(Integer.toString(id), String.valueOf(location).replaceAll("textures/", "").replaceAll(".png", ""));
				id++;
			}

			if (data.getParticle() != null && data.getParticle().getLocation() != null) {
				textures.addProperty("particle", data.getParticle().getLocation().toString().replaceAll("textures/", "").replaceAll(".png", ""));
			}
			json.add("textures", textures);

			/** Elements */
			for (Cube cube : data.getCubes()) {
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

					switch (rotationAxis) {
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
				if (!cube.shouldShade()) {
					cubeObj.addProperty("shade", cube.shouldShade());
				}

				/** faces */
				for (int i = 0; i < EnumFacing.values().length; i++) {
					EnumFacing facing = EnumFacing.values()[i];
					Face face = cube.getFace(facing);
					if (face == null || face.getTexture() == null || face.getTextureCoords() == null || !face.isEnabled())
						continue;

					JsonObject faceObj = new JsonObject();

					/** texture */
					int textureID = -1;
					int j = 0;
					for (ResourceLocation location : addedImages.keySet()) {
						if (location.toString().equalsIgnoreCase(face.getTexture().getLocation().toString())) {
							textureID = j;
							break;
						}
						j++;
					}
					faceObj.addProperty("texture", "#" + textureID);

					/** uv */
					JsonArray uv = new JsonArray();
					if (!face.isFill()) {
						uv.add(face.getTextureCoords().x);
						uv.add(face.getTextureCoords().y);
						uv.add(face.getTextureCoords().z);
						uv.add(face.getTextureCoords().w);
					} else {
						uv.add(0);
						uv.add(0);
						uv.add(16);
						uv.add(16);
					}
					faceObj.add("uv", uv);

					/** texture rotation */
					if (face.getRotation() != 0) {
						faceObj.addProperty("rotation", face.getRotation());
					}

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

			this.saveTexturesToDisc(src.jsonName, data.getTextures(), data.getParticle());

			return json;
		}

		private void saveTexturesToDisc(String jsonName, NamedBufferedImage[] textures, NamedBufferedImage particle) {
			try {
				File folder = new File(Minecraft.getMinecraft().mcDataDir, ModelCreator.MOD_ID + "-export/" + jsonName + "/textures");
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

	public static class Deserializer implements JsonDeserializer<Model> {
		@Override
		public Model deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			return null;
		}
	}
}