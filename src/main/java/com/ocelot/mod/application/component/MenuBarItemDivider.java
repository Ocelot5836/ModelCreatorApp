package com.ocelot.mod.application.component;

import java.awt.Color;

import com.mrcrayfish.device.core.Laptop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class MenuBarItemDivider implements IMenuBarItemComponent {

	private int padding;
	private int color;

	public MenuBarItemDivider() {
		this(1);
	}

	public MenuBarItemDivider(int padding) {
		this.color = Color.GRAY.getRGB();
		this.padding = padding;
	}
	
	@Override
	public void handleTick() {		
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, int buttonsWidth, int buttonsHeight, float partialTicks) {
		GlStateManager.pushMatrix();
		Gui.drawRect(x + 2, y, x + buttonsWidth - 2, y + 1, this.color);
		GlStateManager.popMatrix();
	}

	@Override
	public void renderOverlay(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive) {
	}

	@Override
	public boolean handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		return false;
	}
	
	@Override
	public void deselect() {		
	}

	@Override
	public int getWidth() {
		return 0;
	}

	public int getHeight() {
		return 2;
	}

	@Override
	public int getPadding() {
		return padding;
	}

	@Override
	public boolean isHovered() {
		return false;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public void setColor(Color color) {
		this.setColor(color.getRGB());
	}

	public void setColor(int color) {
		this.color = color;
	}
}