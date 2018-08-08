package com.ocelot.mod.application.component;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.core.Laptop;
import com.ocelot.api.utils.GuiUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class MenuBarItem implements Iterable<IMenuBarButton> {

	private boolean visible;
	private int width;
	private int height;
	private String text;

	private String[] tooltip;
	private int tooltipDelay;
	private int tooltipTick;
	private boolean hovered;

	private int color;
	private int borderColor;
	private int highlightColor;
	private int highlightBorderColor;
	private int textColor;
	private int menuColor;

	private ClickListener clickListener;
	private List<IMenuBarButton> buttons;
	private int buttonsWidth;
	private int buttonsHeight;

	public MenuBarItem(String text) {
		this.text = text;
		this.visible = true;
		this.setTextPadding(2);

		this.tooltip = new String[0];
		this.tooltipDelay = 20;
		this.tooltipTick = 0;
		this.hovered = false;

		this.color = Color.GRAY.getRGB();
		this.borderColor = Color.DARK_GRAY.getRGB();
		this.highlightColor = Color.LIGHT_GRAY.getRGB();
		this.highlightBorderColor = Color.GRAY.getRGB();
		this.textColor = Color.WHITE.getRGB();
		this.menuColor = Color.LIGHT_GRAY.getRGB();

		this.clickListener = null;
		this.buttons = new ArrayList<IMenuBarButton>();
		this.buttonsWidth = 0;
	}

	/**
	 * Called each time the menu bar ticks. (20 times per second)
	 */
	public void handleTick() {
		if (this.visible) {
			tooltipTick = hovered ? tooltipTick + 1 : 0;
		}
		
		for(IMenuBarButton button : this.buttons) {
			button.handleTick();
		}
	}

	/**
	 * The main render loop. This is where you draw your component.
	 * 
	 * @param laptop
	 *            a Laptop instance
	 * @param mc
	 *            a Minecraft instance
	 * @param mouseX
	 *            the current x position of the mouse
	 * @param mouseY
	 *            the current y position of the mouse
	 * @param windowActive
	 *            if the window is active (at front)
	 * @param partialTicks
	 *            percentage passed in-between two ticks
	 */
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, boolean selected, float partialTicks) {
		/** Update whether or not this component is hovered */
		this.hovered = GuiUtils.isMouseInside(x, y, this.width, this.height, mouseX, mouseY) || (selected && GuiUtils.isMouseInside(x, y + this.height, this.buttonsWidth, this.buttonsHeight, mouseX, mouseY));

		/** The border */
		Gui.drawRect(x, y, x + this.width, y + this.height, (this.hovered || selected) ? this.highlightBorderColor : this.borderColor);

		/** The inside */
		Gui.drawRect(x + 1, y + 1, x + this.width - 1, y + this.height - 1, (this.hovered || selected) ? this.highlightColor : this.color);

		/** The text inside */
		mc.fontRenderer.drawString(this.text, x + this.width / 2 - mc.fontRenderer.getStringWidth(this.text) / 2, y + this.height / 2 - mc.fontRenderer.FONT_HEIGHT / 2, this.textColor, false);
	}

	/**
	 * The overlay render loop. Renders over the top of the main render loop.
	 * 
	 * @param laptop
	 *            a Laptop instance
	 * @param mc
	 *            a Minecraft instance
	 * @param mouseX
	 *            the current x position of the mouse
	 * @param mouseY
	 *            the current y position of the mouse
	 * @param windowActive
	 *            if the window is active (at front)
	 */
	public void renderOverlay(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive, boolean selected) {
		if (this.hovered && this.tooltip.length > 0 && tooltipTick >= tooltipDelay) {
			laptop.drawHoveringText(Arrays.asList(this.tooltip), mouseX, mouseY);
		}
	}

	/**
	 * The main render loop for the buttons. This is where you draw the buttons for this component.
	 * 
	 * @param laptop
	 *            a Laptop instance
	 * @param mc
	 *            a Minecraft instance
	 * @param mouseX
	 *            the current x position of the mouse
	 * @param mouseY
	 *            the current y position of the mouse
	 * @param windowActive
	 *            if the window is active (at front)
	 * @param partialTicks
	 *            percentage passed in-between two ticks
	 */
	public void renderButtons(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
		Gui.drawRect(x, y + this.height, x + this.buttonsWidth + 2, y + this.height + this.buttonsHeight, this.menuColor);
		int currentHeight = this.height;
		for (IMenuBarButton button : this.buttons) {
			button.render(laptop, mc, x + 1, y + currentHeight + button.getPadding(), mouseX, mouseY, windowActive, this.buttonsWidth, this.buttonsHeight, partialTicks);
			currentHeight += button.getHeight() + button.getPadding() * 2;
		}
	}

	/**
	 * The overlay render loop for the buttons. Renders over the top of the main render loop.
	 * 
	 * @param laptop
	 *            a Laptop instance
	 * @param mc
	 *            a Minecraft instance
	 * @param mouseX
	 *            the current x position of the mouse
	 * @param mouseY
	 *            the current y position of the mouse
	 * @param windowActive
	 *            if the window is active (at front)
	 */
	public void renderButtonsOverlay(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive) {
		for (IMenuBarButton button : this.buttons) {
			button.renderOverlay(laptop, mc, mouseX, mouseY, windowActive);
		}
	}

	public boolean handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		if (this.hovered && this.clickListener != null) {
			this.clickListener.onClick(mouseX, mouseY, mouseButton);
		}

		for (IMenuBarButton button : this.buttons) {
			if (button.isHovered() && button.handleMouseClick(mouseX, mouseY, mouseButton)) {
				return true;
			}
		}
		return false;
	}

	private void updateMenuSize() {
		this.buttonsHeight = 0;
		for (IMenuBarButton button : this.buttons) {
			if (button.getWidth() > this.buttonsWidth) {
				this.buttonsWidth = button.getWidth();
			}
			this.buttonsHeight += button.getHeight() + button.getPadding() * 2;
		}
	}

	public void add(IMenuBarButton item) {
		if (item != null) {
			this.buttons.add(item);
			updateMenuSize();
		}
	}

	public void addAll(Collection<? extends IMenuBarButton> items) {
		this.buttons.addAll(items);
		updateMenuSize();
	}

	public void sort(Comparator<IMenuBarButton> comparator) {
		Collections.sort(this.buttons, comparator);
	}

	public void clear() {
		this.buttons.clear();
	}

	public int size() {
		return this.buttons.size();
	}

	@Override
	public Iterator<IMenuBarButton> iterator() {
		return this.buttons.iterator();
	}

	public void deselect() {
		this.hovered = false;
		for (IMenuBarButton button : this.buttons) {
			button.deselect();
		}
	}

	public boolean isVisible() {
		return visible;
	}

	public int getWidth() {
		return width;
	}

	public String getName() {
		return text;
	}

	public int getColor() {
		return color;
	}

	public int getNameColor() {
		return textColor;
	}

	public int getHighlightColor() {
		return highlightColor;
	}

	public String[] getTooltip() {
		return tooltip;
	}

	public boolean isHovered() {
		return hovered;
	}

	public int getTooltipDelay() {
		return tooltipDelay;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setTextPadding(int padding) {
		this.width = Minecraft.getMinecraft().fontRenderer.getStringWidth(text) + padding;
	}

	protected void setHeight(int height) {
		this.height = height;
	}

	public void setName(String name) {
		this.text = name;
	}

	public void setColor(Color color) {
		this.setColor(color.getRGB());
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setBorderColor(Color borderColor) {
		this.setBorderColor(borderColor.getRGB());
	}

	public void setBorderColor(int borderColor) {
		this.borderColor = borderColor;
	}

	public void setHighlightColor(Color highlightColor) {
		this.setHighlightColor(highlightColor.getRGB());
	}

	public void setHighlightColor(int highlightColor) {
		this.highlightColor = highlightColor;
	}

	public void setHighlightBorderColor(Color highlightBorderColor) {
		this.setHighlightBorderColor(highlightBorderColor.getRGB());
	}

	public void setHighlightBorderColor(int highlightBorderColor) {
		this.highlightBorderColor = highlightBorderColor;
	}

	public void setMenuColor(Color menuColor) {
		this.setMenuColor(menuColor.getRGB());
	}

	public void setMenuColor(int menuColor) {
		this.menuColor = menuColor;
	}

	public void setNameColor(Color textColor) {
		this.setTextColor(textColor.getRGB());
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public void setTooltip(String... tooltipLines) {
		this.tooltip = tooltipLines;
	}

	public void setTooltipDelay(int tooltipDelay) {
		this.tooltipDelay = tooltipDelay;
	}

	public void setClickListener(ClickListener clickListener) {
		this.clickListener = clickListener;
	}
}