package com.ocelot.mod.application.dialog;

import java.awt.image.BufferedImage;

import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.core.Laptop;
import com.ocelot.api.utils.GuiUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class CustomImage extends Image {

	private DialogTextureManager dialog;

	protected boolean hovered;

	public CustomImage(DialogTextureManager dialog, int left, int top, int componentWidth, int componentHeight, int imageU, int imageV, int imageWidth, int imageHeight, int sourceWidth, int sourceHeight, ResourceLocation resource) {
		super(left, top, componentWidth, componentHeight, imageU, imageV, imageWidth, imageHeight, sourceWidth, sourceHeight, resource);
		this.dialog = dialog;
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
		super.render(laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);
		hovered = GuiUtils.isMouseInside(x, y, this.componentWidth, this.componentHeight, mouseX, mouseY);
		if (this == dialog.getSelectedImageComponent()) {
			Gui.drawRect(x, y, x+this.componentWidth, y+this.componentHeight, 0x77ffffff);
		}
	}
}