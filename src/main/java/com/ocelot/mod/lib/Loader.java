package com.ocelot.mod.lib;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import com.ocelot.mod.game.core.model.Model;

import net.minecraft.client.Minecraft;

/**
 * <em><b>Copyright (c) 2018 The Zerra Team.</b></em>
 * 
 * <br>
 * </br>
 * 
 * Has the capability to load data to and from memory.
 * 
 * @author Ocelot5836
 */
public class Loader {

	private static List<Integer> vaos = new ArrayList<Integer>();
	private static List<Integer> vbos = new ArrayList<Integer>();
	private static List<Integer> textures = new ArrayList<Integer>();

	/**
	 * Deletes the vertex arrays, vertex buffer objects, and textures from memory.
	 */
	public static void cleanUp() {
		for (Integer vao : vaos) {
			GL30.glDeleteVertexArrays(vao);
		}

		for (Integer vbo : vbos) {
			GL15.glDeleteBuffers(vbo);
		}

		for (Integer texture : textures) {
			GL11.glDeleteTextures(texture);
		}
	}

	/**
	 * Loads an image to a byte buffer. Used when loading textures.
	 * 
	 * @param image
	 *            The image to load to a byte buffer
	 * @return The buffer created from the image or null if the image was null
	 * @throws NullPointerException
	 *             Throws this if the image was null
	 */
	public static ByteBuffer loadToByteBuffer(BufferedImage image) throws NullPointerException {
		int width = image.getWidth();
		int height = image.getHeight();
		int[] pixels = new int[width * height];
		pixels = image.getRGB(0, 0, width, height, null, 0, width);

		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int color = pixels[x + y * width];
				buffer.put((byte) ((color >> 16) & 0xff));
				buffer.put((byte) ((color >> 8) & 0xff));
				buffer.put((byte) (color & 0xff));
				buffer.put((byte) ((color >> 24) & 0xff));
			}
		}
		buffer.flip();
		return buffer;
	}

	/**
	 * Loads the supplied data to a VAO.
	 * 
	 * @param positions
	 *            The positions to load
	 * @param indices
	 *            The indices to load
	 * @param textureCoords
	 *            The texture coords to load
	 * @param normals
	 *            The normals to load
	 * @return The model created
	 */
	public static Model loadToVAO(float[] positions, int[] indices, float[] textureCoords, float[] normals) {
		int vaoID = GL30.glGenVertexArrays();
		System.out.println(vaoID);
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		bindIndicesBuffer(vaoID, indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		GL30.glBindVertexArray(0);
		return new Model(vaoID, indices.length);
	}

	/**
	 * Loads different dimensions of positions to memory.
	 * 
	 * @param positions
	 *            The positions to load
	 * @param textureCoords
	 *            The texture coords to load
	 * @param dimensions
	 *            The dimensions of plane. Ex 3d coords will be 3 and 2d coords are 1
	 * @return The model created
	 */
	public static Model loadToVAO(float[] positions, float[] textureCoords, int dimensions) {
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		storeDataInAttributeList(0, dimensions, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		GL30.glBindVertexArray(0);
		return new Model(vaoID, positions.length / dimensions);
	}

	/**
	 * Loads different dimensions of positions to memory.
	 * 
	 * @param positions
	 *            The positions to load
	 * @param indices
	 *            The indices to load
	 * @param dimensions
	 *            The dimensions of plane. Ex 3d coords will be 3 and 2d coords are 1
	 * @return The model created
	 */
	public static Model loadToVAO(float[] positions, int[] indices, int dimensions) {
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		bindIndicesBuffer(vaoID, indices);
		storeDataInAttributeList(0, dimensions, positions);
		GL30.glBindVertexArray(0);
		return new Model(vaoID, indices.length);
	}

	/**
	 * Loads different dimensions of positions to memory.
	 * 
	 * @param positions
	 *            The positions to load
	 * @param dimensions
	 *            The dimensions of plane. Ex 3d coords will be 3 and 2d coords are 1
	 * @return The model created
	 */
	public static Model loadToVAO(float[] positions, int dimensions) {
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		storeDataInAttributeList(0, dimensions, positions);
		GL30.glBindVertexArray(0);
		return new Model(vaoID, positions.length / dimensions);
	}

	/**
	 * Creates an empty VBO that can be used to dynamically update with.
	 * 
	 * @param floatCount
	 *            The number of floats inside
	 * @return The VBO created
	 */
	public static int createEmptyVBO(int floatCount) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4, GL15.GL_DYNAMIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vboID;
	}

	/**
	 * Stores an instanced attribute into the supplied VBO.
	 * 
	 * @param vao
	 *            The VAO to load the VBO into
	 * @param vbo
	 *            The VBO that will hold the data
	 * @param attributeNumber
	 *            The position this will be held at
	 * @param dataSize
	 *            The size of the data
	 * @param instancedDataLength
	 *            The length of each point of data.
	 * @param offset
	 *            The offset of the data
	 */
	public static void storeInstancedDataInAttributeList(int vao, int vbo, int attributeNumber, int dataSize, int instancedDataLength, int offset) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL30.glBindVertexArray(vao);
		GL20.glVertexAttribPointer(attributeNumber, dataSize, GL11.GL_FLOAT, false, instancedDataLength * 4, offset * 4);
		GL33.glVertexAttribDivisor(attributeNumber, 1);
		GL30.glBindVertexArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	/**
	 * Updates VBO data.
	 * 
	 * @param vbo
	 *            The VBO that is being updated
	 * @param data
	 *            The data that is going to replace the old data
	 * @param buffer
	 *            The buffer to put the data into
	 */
	public static void updateVboData(int vbo, float[] data, FloatBuffer buffer) {
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity() * 4, GL15.GL_DYNAMIC_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	/**
	 * Stores data in an attribute list. Uses the currently bound VAO.
	 * 
	 * @param attributeNumber
	 *            The position this will be held at
	 * @param dataSize
	 *            The size of the data
	 * @param data
	 *            The data to put into the list
	 */
	public static int storeDataInAttributeList(int attributeNumber, int dataSize, float[] data) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = Buffers.storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, dataSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vboID;
	}

	private static void bindIndicesBuffer(int vao, int[] indices) {
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL30.glBindVertexArray(vao);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = Buffers.storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL30.glBindVertexArray(0);
	}
}