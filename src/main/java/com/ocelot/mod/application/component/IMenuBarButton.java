package com.ocelot.mod.application.component;

import com.mrcrayfish.device.core.Laptop;

import net.minecraft.client.Minecraft;

/**
 * <em><b>Copyright (c) 2018 Ocelot5836.</b></em>
 * 
 * <br>
 * </br>
 * 
 * A very abstract button that can be added to a menu bar.
 * 
 * @author Ocelot5836
 */
public interface IMenuBarButton {

	/**
	 * Renders the button.
	 * 
	 * @param laptop
	 *            The laptop instance
	 * @param mc
	 *            A minecraft instance
	 * @param x
	 *            The x position to render at
	 * @param y
	 *            The y position to render at
	 * @param mouseX
	 *            The x position of the mouse
	 * @param mouseY
	 *            The y position of the mouse
	 * @param windowActive
	 *            Whether or not the window is on top
	 * @param buttonsWidth
	 *            The width of the list of buttons
	 * @param buttonsHeight
	 *            The height of the list of buttons
	 * @param partialTicks
	 *            The percentage from last update and next update
	 */
	void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, int buttonsWidth, int buttonsHeight, float partialTicks);

	/**
	 * Renders the button.
	 * 
	 * @param laptop
	 *            The laptop instance
	 * @param mc
	 *            A minecraft instance
	 * @param mouseX
	 *            The x position of the mouse
	 * @param mouseY
	 *            The y position of the mouse
	 * @param windowActive
	 *            Whether or not the window is on top
	 */
	void renderOverlay(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive);

	/**
	 * Called each time the mouse is pressed.
	 * 
	 * @param mouseX
	 *            The x position of the mouse
	 * @param mouseY
	 *            The y position of the mouse
	 * @param mouseButton
	 *            The button the mouse is using
	 * @return Whether or not the list should close when the mouse is pressed
	 */
	boolean handleMouseClick(int mouseX, int mouseY, int mouseButton);

	/**
	 * @return The width of this button
	 */
	int getWidth();

	/**
	 * @return The height of this button
	 */
	int getHeight();

	/**
	 * @return The padding for this button
	 */
	int getPadding();

	/**
	 * @return Whether or not this button is hovered
	 */
	boolean isHovered();
}