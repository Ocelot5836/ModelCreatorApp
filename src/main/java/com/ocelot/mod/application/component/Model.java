package com.ocelot.mod.application.component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class Model {

	private Map<ResourceLocation, Integer> textures;
	private List<Cube> cubes;

	public Model(List<Cube> cubes) {
		this.textures = new HashMap<ResourceLocation, Integer>();
		this.cubes = new ArrayList<Cube>(cubes);

		for (Cube cube : cubes) {
			Face[] faces = cube.getFaces();
			for (int i = 0; i < faces.length; i++) {
				Face face = faces[i];
				if (face != Face.NULL_FACE) {
					ResourceLocation texture = face.getTexture();
					if (texture != null) {
						this.textures.put(texture, this.textures.size());
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

			/** Textures */
			for (ResourceLocation location : src.textures.keySet()) {
				textures.addProperty(Integer.toString(src.textures.get(location)), String.valueOf(location));
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

				/** faces */
				for (int i = 0; i < EnumFacing.values().length; i++) {
					EnumFacing facing = EnumFacing.values()[i];
					Face face = cube.getFace(facing);

					if (face.getTexture() != null) {
						JsonObject faceObj = new JsonObject();

						/** texture */
						faceObj.addProperty("texture", src.textures.get(face.getTexture()));
						
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
				}
				cubeObj.add("faces", faces);

				elements.add(cubeObj);
			}
			json.add("elements", elements);

			return json;
		}
	}
}