package com.ocelot.api.geometry;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import net.minecraft.client.renderer.GlStateManager;

public class Camera {

	private int lastMouseX;
	private int lastMouseY;

	private Vector3f origin;

	private float distanceFromCenter;
	private float angleAroundCenter;
	private float pitch;

	private Vector3f lastPosition = new Vector3f();
	private Vector3f position = new Vector3f();
	private Vector3f renderPosition = new Vector3f();

	private Vector3f lastRotation = new Vector3f();
	private Vector3f rotation = new Vector3f();
	private Vector3f renderRotation = new Vector3f();

	public Camera(Vector3f origin) {
		this(origin, new Vector3f(), new Vector3f());
	}

	public Camera(Vector3f origin, Vector3f position, Vector3f rotation) {
		this.origin = origin;

		this.lastPosition = new Vector3f(position);
		this.position = new Vector3f(position);
		this.renderPosition = new Vector3f();

		this.lastRotation = new Vector3f(rotation);
		this.rotation = new Vector3f(rotation);
		this.renderRotation = new Vector3f();

		this.distanceFromCenter = 0;
		this.angleAroundCenter = 135;
		this.pitch = -45;
	}

	public void update() {
		this.lastPosition.set(this.position);
		this.lastRotation.set(this.rotation);
		
		// this.calculateCameraPosition((float) (distanceFromCenter * Math.cos(Math.toRadians(pitch))), (float) (distanceFromCenter * Math.sin(Math.toRadians(pitch))));
		this.rotation.x = this.pitch;
		this.rotation.y = 180 - this.angleAroundCenter;

		this.position.set(this.origin.x, this.origin.y + distanceFromCenter, this.origin.z);
	}

	public void transform(float partialTicks) {
		this.translate(partialTicks);
		this.rotate(partialTicks);
	}

	public void translate(float partialTicks) {
		this.renderPosition.set(this.lastPosition.x + (this.position.x - this.lastPosition.x) * partialTicks, this.lastPosition.y + (this.position.y - this.lastPosition.y) * partialTicks, this.lastPosition.z + (this.position.z - this.lastPosition.z) * partialTicks);
		GlStateManager.translate(-this.renderPosition.x, -this.renderPosition.y, -this.renderPosition.z);
	}

	public void rotate(float partialTicks) {
		this.renderRotation.set(this.lastRotation.x + (this.rotation.x - this.lastRotation.x) * partialTicks, this.lastRotation.y + (this.rotation.y - this.lastRotation.y) * partialTicks, this.lastRotation.z + (this.rotation.z - this.lastRotation.z) * partialTicks);
		GlStateManager.rotate(this.renderRotation.x, 1, 0, 0);
		GlStateManager.rotate(this.renderRotation.y, 0, 1, 0);
		GlStateManager.rotate(this.renderRotation.z, 0, 0, 1);
	}

	private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
		float theta = this.angleAroundCenter;
		float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
		float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
		this.position.set(this.origin.x - offsetX, this.origin.y + verticalDistance, this.origin.z - offsetZ);
	}

	public void handleMouseDrag(int mouseX, int mouseY, int mouseButton) {		
		if (mouseButton == 1) {
			pitch += mouseY - lastMouseY;
			angleAroundCenter -= mouseX - lastMouseX;
			this.updateMousePosition(mouseX, mouseY);
		}
	}

	public void handleMouseScroll(int mouseX, int mouseY, boolean direction) {
		distanceFromCenter += Mouse.getDWheel() * 0.05f;
	}
	
	public void updateMousePosition(int mouseX, int mouseY) {
		lastMouseX = mouseX;
		lastMouseY = mouseY;
	}

	public Vector3f getOrigin() {
		return origin;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3f getLastPosition() {
		return lastPosition;
	}

	public Vector3f getRenderPosition() {
		return renderPosition;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public Vector3f getLastRotation() {
		return lastRotation;
	}

	public Vector3f getRenderRotation() {
		return renderRotation;
	}
}