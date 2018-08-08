package com.ocelot.mod.application.component;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.core.Laptop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

// MrCrayfish, if you ever want to add this component in any way, just let me know so I can add more documentation. -Ocelot5836
public class MenuBar extends Component implements Iterable<MenuBarItem> {

	private MenuBarItem selectedItem;

	private int width;
	private int height;
	private int itemPadding;
	private List<MenuBarItem> items;

	private int color;
	private int borderColor;

	public MenuBar(int left, int top, int width, int height) {
		super(left, top);
		this.width = width;
		this.height = height;
		this.itemPadding = 0;
		this.items = new ArrayList<MenuBarItem>();
		this.color = Color.DARK_GRAY.getRGB();
		this.borderColor = Color.BLACK.getRGB();
	}

	@Override
	protected void handleTick() {
		for (MenuBarItem item : this.items) {
			item.handleTick();
		}
	}

	@Override
	protected void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
		if (this.visible) {
			Gui.drawRect(x, y, x + this.width, y + this.height, this.borderColor);
			Gui.drawRect(x + 1, y + 1, x + this.width - 1, y + this.height - 1, this.color);

			int currentX = 0;
			for (MenuBarItem item : this.items) {
				if (item.isVisible()) {
					item.render(laptop, mc, x + currentX, y, mouseX, mouseY, windowActive, item == this.selectedItem, partialTicks);
					currentX += item.getWidth() + this.itemPadding;
				}
			}

			if (this.selectedItem != null) {
				currentX = 0;
				for (MenuBarItem item : this.items) {
					if (item.isVisible()) {
						if (item == this.selectedItem) {
							this.selectedItem.renderButtons(laptop, mc, x + currentX, y, mouseX, mouseY, windowActive, partialTicks);
						}
						currentX += item.getWidth() + this.itemPadding;
					}
				}
			}
		}
	}

	@Override
	protected void renderOverlay(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive) {
		if (this.visible) {
			for (MenuBarItem item : this.items) {
				if (item.isVisible()) {
					item.renderOverlay(laptop, mc, mouseX, mouseY, windowActive, item == this.selectedItem);
				}
			}

			if (this.selectedItem != null) {
				this.selectedItem.renderButtonsOverlay(laptop, mc, mouseX, mouseY, windowActive);
			}
		}
	}

	@Override
	protected void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		if (this.selectedItem != null && this.selectedItem.handleMouseClick(mouseX, mouseY, mouseButton)) {
			this.deselect();
			return;
		}

		this.selectedItem = null;
		for (MenuBarItem item : this.items) {
			if (item.isHovered()) {
				item.handleMouseClick(mouseX, mouseY, mouseButton);
				this.selectedItem = item;
				break;
			}
		}
	}

	public void deselect() {
		this.selectedItem.deselect();
		this.selectedItem = null;
	}

	public void add(MenuBarItem item) {
		if (item != null) {
			item.setHeight(this.height);
			this.items.add(item);
		}
	}

	public void addAll(Collection<? extends MenuBarItem> items) {
		this.items.addAll(items);
	}

	public void sort(Comparator<MenuBarItem> comparator) {
		Collections.sort(this.items, comparator);
	}

	public void clear() {
		this.items.clear();
	}

	public int size() {
		return this.items.size();
	}

	@Override
	public Iterator<MenuBarItem> iterator() {
		return this.items.iterator();
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getItemPadding() {
		return itemPadding;
	}

	public int getColor() {
		return color;
	}

	public int getBorderColor() {
		return borderColor;
	}

	public MenuBarItem getSelectedItem() {
		return selectedItem;
	}

	public void setItemPadding(int itemPadding) {
		this.itemPadding = itemPadding;
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
}