package com.ocelot.mod.lib;

import java.util.Map;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.google.common.collect.Maps;
import com.ocelot.mod.game.core.gfx.Camera;

public class Maths {

	private static final Map<String, Matrix4f> TRANSFORMATION_MATRICES = Maps.<String, Matrix4f>newHashMap();
	private static final Vector3f translation = new Vector3f();

	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f rotation, float scale) {
		return createTransformationMatrix(translation.x, translation.y, translation.z, rotation.x, rotation.y, rotation.z, scale);
	}

	public static Matrix4f createTransformationMatrix(float x, float y, float z, float rx, float ry, float rz, float scale) {
		Matrix4f matrix = TRANSFORMATION_MATRICES.get(x + "," + y + "," + z + "," + rx + "," + ry + "," + rz + "," + scale);
		if (matrix == null) {
			matrix = new Matrix4f();
			matrix.setIdentity();
			translation.set(x, y, z);
			Matrix4f.translate(translation, matrix, matrix);
			Matrix4f.rotate((float) Math.toRadians(rx), new Vector3f(1, 0, 0), matrix, matrix);
			Matrix4f.rotate((float) Math.toRadians(ry), new Vector3f(0, 1, 0), matrix, matrix);
			Matrix4f.rotate((float) Math.toRadians(rz), new Vector3f(0, 0, 1), matrix, matrix);
			Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
		}
		return matrix;
	}

	public static Matrix4f createViewMatrix(Camera camera) {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Vector3f rotation = camera.getRenderRotation();
		Matrix4f.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1), viewMatrix, viewMatrix);
		Vector3f cameraPos = camera.getRenderPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}

	public static Matrix4f createProjectionMatrix(float aspectRatio, float fov, float near, float far) {
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(fov / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = far - near;

		Matrix4f m = new Matrix4f();
		m.m00 = x_scale;
		m.m11 = y_scale;
		m.m22 = -((far + near) / frustum_length);
		m.m23 = -1;
		m.m32 = -((2 * near * far) / frustum_length);
		m.m33 = 0;
		return m;
	}

	public static Vector4f multiply(Matrix4f matrix, Vector4f vector) {
		return matrix.transform(matrix, vector, vector);
	}
	
	public static void cleanUp() {
		TRANSFORMATION_MATRICES.clear();
	}
}