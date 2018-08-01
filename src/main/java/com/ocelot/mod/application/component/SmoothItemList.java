package com.ocelot.mod.application.component;

import java.awt.Color;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mrcrayfish.device.api.app.Component;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Spinner;
import com.mrcrayfish.device.api.app.listener.ItemClickListener;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.util.GLHelper;
import com.mrcrayfish.device.util.GuiHelper;
import com.ocelot.api.utils.GuiUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.NonNullList;

public class SmoothItemList<E> extends Component implements Iterable<E> {

	protected int width;
	protected int height;
	protected double lastOffset;
	protected double offset;
	protected int selected = -1;

	protected boolean showScrollbar = true;
	protected boolean resized = false;
	protected boolean initialized = false;
	protected boolean loading = false;
	protected double scrollSpeed = 1;

	protected List<E> items = NonNullList.create();
	protected ListItemRenderer<E> renderer = null;
	protected ItemClickListener<E> itemClickListener = null;

	protected Layout layoutLoading;

	protected int textColor = Color.WHITE.getRGB();
	protected int backgroundColor = Color.GRAY.getRGB();
	protected int borderColor = Color.BLACK.getRGB();
	protected int innerBorderColor = Color.DARK_GRAY.getRGB();
	private static final int LOADING_BACKGROUND = new Color(0F, 0F, 0F, 0.5F).getRGB();

	private Comparator<E> sorter = null;

	/**
	 * Default constructor for the item list. Should be noted that the height is determined by how many visible items there are.
	 * 
	 * @param left
	 *            how many pixels from the left
	 * @param top
	 *            how many pixels from the top
	 * @param width
	 *            width of the list
	 * @param height
	 *            height of the list
	 */
	public SmoothItemList(int left, int top, int width, int height) {
		this(left, top, width, height, false);
	}

	public SmoothItemList(int left, int top, int width, int height, boolean showScrollbar) {
		super(left, top);
		this.width = width;
		this.height = height;
		this.showScrollbar = showScrollbar;
	}

	@Override
	public void init(Layout layout) {
		layoutLoading = new Layout(left, top, getWidth(), getHeight());
		layoutLoading.setVisible(loading);
		layoutLoading.addComponent(new Spinner((layoutLoading.width - 12) / 2, (layoutLoading.height - 12) / 2));
		layoutLoading.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
			Gui.drawRect(x, y, x + width, y + height, LOADING_BACKGROUND);
		});
		layout.addComponent(layoutLoading);

		initialized = true;
	}

	@Override
	protected void handleTick() {
		super.handleTick();
		this.lastOffset = this.offset;
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
		if (this.visible) {
			int size = this.height / this.getCellHeight();

			Gui.drawRect(x + 1, y + 1, x + width - 1, y + height - 1, Color.LIGHT_GRAY.getRGB());

			drawHorizontalLine(x, x + width - 1, y, borderColor);
			drawVerticalLine(x, y, y + height - 1, borderColor);
			drawVerticalLine(x + width - 1, y, y + height - 1, borderColor);
			drawHorizontalLine(x, x + width - 1, y + height - 1, borderColor);

			double yOffset = this.lastOffset + (this.lastOffset - this.offset) * partialTicks;
			GLHelper.pushScissor(x + 1, y + 1, this.width - 2, this.height - 2);
			for (int i = 0; i < this.size(); i++) {
				if (i >= 0) {
					E item = items.get(i);
					if (item != null) {
						int renderX = x;
						int renderY = y + i * (this.getCellHeight() + 1);
						if (renderer != null) {
							renderer.render(item, this, mc, renderX + 1, renderY + 1 + (int) yOffset, width - 1, 1 + this.getCellHeight(), i == selected);
							drawHorizontalLine(renderX + 1, renderX + width - 2, renderY + (int) yOffset, innerBorderColor);
						} else {
							Gui.drawRect(renderX + 1, renderY + 1 + (int) yOffset, renderX + width - 1, renderY + 1 + (int) yOffset + this.getCellHeight(), i != selected ? backgroundColor : Color.DARK_GRAY.getRGB());
							RenderUtil.drawStringClipped(String.valueOf(item), renderX + 3, renderY + 3 + (int) yOffset, width - 6, textColor, true);
							drawHorizontalLine(renderX + 1, renderX + width - 2, renderY + (int) yOffset, innerBorderColor);
						}
					}
				}
			}
			GLHelper.popScissor();

			if (showScrollbar) {
				// TODO get this scrollbar working
				// int scrollbarSize = 4;
				//
				// int maxScrollbarHeight = height - 1;
				// int scrollbarHeight = Math.max(1, ((int) ((float) maxScrollbarHeight * (1f / Math.max(1, (float) this.size() / ((float) height / (float) this.size()))))));
				//
				// double currentScroll = (yOffset / (this.getCellHeight() + 1)) / (items.size() - height / this.getCellHeight());
				//
				// int scrollbarY = (int) (y + 1 + (-currentScroll * (maxScrollbarHeight - scrollbarHeight - 1)));
				//
				// Gui.drawRect(x + width - 2 - scrollbarSize, scrollbarY, x + width - 2, scrollbarY + scrollbarHeight + 1, 0xffe8e8e8);
			}
		}
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		if (!this.visible || !this.enabled || this.loading)
			return;

		int height = this.getCellHeight();
		int yOffset = (int) this.offset;
		if (GuiUtils.isMouseInside(this.xPosition, this.yPosition, this.width, this.height, mouseX, mouseY)) {
			for (int i = 0; i < this.size() && i < items.size(); i++) {
				if (GuiUtils.isMouseInside(xPosition, yPosition + 1 + i * (height + 1) + yOffset, this.width - 2, height, mouseX, mouseY)) {
					if (mouseButton == 0)
						this.selected = i;
					if (itemClickListener != null) {
						itemClickListener.onClick(items.get(i), i, mouseButton);
					}
				}
			}
		}
	}

	@Override
	public void handleMouseScroll(int mouseX, int mouseY, boolean direction) {
		if (!this.visible || !this.enabled || this.loading)
			return;

		int size = this.items.size();
		if (GuiHelper.isMouseInside(mouseX, mouseY, xPosition, yPosition, xPosition + width, yPosition + height)) {
			if (direction) {
				scrollUp();
			} else {
				scrollDown();
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int size() {
		return this.items.size();
	}

	private void scrollUp() {
		for (int i = 0; i < scrollSpeed; i++) {
			if (offset < 0) {
				offset++;
			} else {
				break;
			}
		}
	}

	private void scrollDown() {
		int height = 0;
		height = this.items.size() * (this.getCellHeight() + 1) - this.height + 2;
		for (int i = 0; i < scrollSpeed; i++) {
			if (offset > -height) {
				offset--;
			} else {
				break;
			}
		}
	}

	private void updateScroll() {
		if (getOffset() > items.size()) {
			offset = Math.max(0, items.size() - height / this.getCellHeight());
		}
	}

	private int getOffset() {
		return (int) offset / this.getCellHeight();
	}

	private int getCellHeight() {
		return renderer != null ? renderer.getHeight() : 13;
	}

	/**
	 * Sets the custom item list renderer.
	 * 
	 * @param renderer
	 *            the custom renderer
	 */
	public void setListItemRenderer(ListItemRenderer<E> renderer) {
		this.renderer = renderer;
	}

	/**
	 * Sets the item click listener for when an item is clicked.
	 * 
	 * @param itemClickListener
	 *            the item click listener
	 */
	public void setItemClickListener(ItemClickListener<E> itemClickListener) {
		this.itemClickListener = itemClickListener;
	}

	/**
	 * Appends an item to the list
	 * 
	 * @param e
	 *            the item
	 */
	public void addItem(@Nonnull E e) {
		items.add(e);
		sort();
	}

	/**
	 * Appends an item to the list
	 *
	 * @param newItems
	 *            the items
	 */
	public void setItems(List<E> newItems) {
		items.clear();
		items.addAll(newItems);
		sort();
		if (initialized) {
			offset = 0;
		}
	}

	/**
	 * Removes an item at the specified index
	 * 
	 * @param index
	 *            the index to remove
	 */
	@Nullable
	public E removeItem(int index) {
		if (index >= 0 && index < items.size()) {
			E e = items.remove(index);
			if (index == selected)
				selected = -1;
			if (initialized) {
				updateScroll();
			}
			return e;
		}
		return null;
	}

	/**
	 * Gets the selected item
	 * 
	 * @return the selected item
	 */
	@Nullable
	public E getSelectedItem() {
		if (selected >= 0 && selected < items.size()) {
			return items.get(selected);
		}
		return null;
	}

	/**
	 * Sets the selected item in the list using the index
	 * 
	 * @param index
	 *            the index of the item
	 */
	public void setSelectedIndex(int index) {
		if (index < 0)
			index = -1;
		this.selected = index;
	}

	/**
	 * Gets the selected item's index
	 * 
	 * @return the index
	 */
	public int getSelectedIndex() {
		return selected;
	}

	/**
	 * Gets all items from the list. Do not use this to remove items from the item list, instead use {@link #removeItem(int)} otherwise it will cause scroll issues.
	 * 
	 * @return the items
	 */
	public List<E> getItems() {
		return items;
	}

	/**
	 * Removes all items from the list
	 */
	public void removeAll() {
		this.items.clear();
		this.selected = -1;
		if (initialized) {
			updateScroll();
		}
	}

	/**
	 * Sets the text color for this component
	 * 
	 * @param color
	 *            the text color
	 */
	public void setTextColor(Color color) {
		this.textColor = color.getRGB();
	}

	/**
	 * Sets the background color for this component
	 * 
	 * @param color
	 *            the background color
	 */
	public void setBackgroundColor(Color color) {
		this.backgroundColor = color.getRGB();
	}

	/**
	 * Sets the border color for this component
	 * 
	 * @param color
	 *            the border color
	 */
	public void setBorderColor(Color color) {
		this.borderColor = color.getRGB();
	}

	/**
	 * Sets the inner border color for this component.
	 * 
	 * @param color
	 *            The color of the bars in between the items in the list
	 */
	public void setInnerBorderColor(Color color) {
		this.innerBorderColor = color.getRGB();
	}

	public void setLoading(boolean loading) {
		this.loading = loading;
		if (initialized) {
			layoutLoading.setVisible(loading);
		}
	}

	public void setScrollSpeed(double scrollSpeed) {
		this.scrollSpeed = scrollSpeed;
	}

	/**
	 * Sets the sorter for this item list and updates straight away
	 * 
	 * @param sorter
	 *            the comparator to sort the list by
	 */
	public void sortBy(Comparator<E> sorter) {
		this.sorter = sorter;
		sort();
	}

	/**
	 * Sorts the list
	 */
	public void sort() {
		if (sorter != null) {
			Collections.sort(items, sorter);
		}
	}

	@Override
	public Iterator<E> iterator() {
		return items.iterator();
	}
}
