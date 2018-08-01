package com.ocelot.mod.application.component;

import java.awt.Color;
import java.util.Arrays;

import com.mrcrayfish.device.api.app.IIcon;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.ocelot.api.utils.GuiUtils;
import com.ocelot.api.utils.TextureUtils;
import com.ocelot.mod.lib.Lib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

public class MenuBarButton implements IMenuBarButton {

	private boolean visible;
	private boolean enabled;
	private boolean hovered;
	private int width;
	private int height;
	private int padding;
	private boolean explicitSize;

	private String text;
	private String[] tooltip;
	private int tooltipDelay;
	private int tooltipTick;

	private ResourceLocation iconResource;
	private int iconU, iconV;
	private int iconWidth, iconHeight;
	private int iconSourceWidth;
	private int iconSourceHeight;

	private int textColor;
	private int highlightedTextColor;
	private int disabledTextColor;

	private ClickListener clickListener = null;

	public MenuBarButton(String text, ResourceLocation iconResource, int iconU, int iconV, int iconWidth, int iconHeight) {
		this(text);
		this.setIcon(iconResource, iconU, iconV, iconWidth, iconHeight);
	}

	public MenuBarButton(String text, IIcon icon) {
		this(text);
		this.setIcon(icon);
	}

	public MenuBarButton(String text) {
		this.text = text;
		this.tooltip = new String[0];
		this.tooltipDelay = 20;
		this.tooltipTick = 0;
		this.visible = true;
		this.enabled = true;
		this.hovered = false;
		this.padding = 0;
		this.width = Lib.getDefaultTextWidth(text) + this.padding;
		this.height = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
		this.explicitSize = false;
		this.textColor = 14737632;
		this.highlightedTextColor = 16777120;
		this.disabledTextColor = 10526880;
	}

	protected void handleTick() {
		if (this.visible) {
			tooltipTick = hovered ? tooltipTick++ : 0;
		}
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, int buttonsWidth, int buttonsHeight, float partialTicks) {
		if (this.visible) {
			this.hovered = GuiUtils.isMouseInside(x, y, buttonsWidth, this.height, mouseX, mouseY);

			int contentWidth = (iconResource != null ? iconWidth : 0) + Lib.getDefaultTextWidth(text);
			if (iconResource != null && !StringUtils.isNullOrEmpty(text))
				contentWidth += 3;
			int contentX = (int) Math.ceil((width - contentWidth) / 2.0);

			if (this.iconResource != null) {
				TextureUtils.bindTexture(this.iconResource);
				GlStateManager.color(1, 1, 1);
				RenderUtil.drawRectWithTexture(x, y + this.height / 2 - this.iconHeight / 2, iconU, iconV, iconWidth, iconHeight, iconWidth, iconHeight, iconSourceWidth, iconSourceHeight);
			}

			if (!StringUtils.isNullOrEmpty(this.text)) {
				int textY = (height - mc.fontRenderer.FONT_HEIGHT) / 2;
				int textOffsetX = iconResource != null ? iconWidth + 3 : 0;
				int textColor = !this.enabled ? this.disabledTextColor : (this.hovered ? this.highlightedTextColor : this.textColor);
				mc.fontRenderer.drawString(text, x + textOffsetX, y + textY, textColor, false);
			}
		}
	}

	@Override
	public void renderOverlay(Laptop laptop, Minecraft mc, int mouseX, int mouseY, boolean windowActive) {
		if (this.hovered && this.tooltip.length >= 0 && tooltipTick >= tooltipDelay) {
			laptop.drawHoveringText(Arrays.asList(this.tooltip), mouseX, mouseY);
		}
	}

	@Override
	public boolean handleMouseClick(int mouseX, int mouseY, int mouseButton) {
		if (this.clickListener != null) {
			this.clickListener.onClick(mouseX, mouseY, mouseButton);
			return true;
		}
		return false;
	}

	@Override
	public void deselect() {
		this.hovered = false;
	}

	public void removeIcon() {
		this.iconResource = null;
		updateSize();
	}

	private void updateSize() {
		if (explicitSize)
			return;
		int height = padding * 2;
		int width = padding * 2;

		if (iconResource != null) {
			width += iconWidth;
			height += iconHeight;
		}

		if (text != null) {
			width += Lib.getDefaultTextWidth(text);
			height = 16;
		}

		if (iconResource != null && text != null) {
			width += 3;
			height = iconHeight + padding * 2;
		}

		this.width = width;
		this.height = height;
	}

	public boolean isVisible() {
		return visible;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public boolean isHovered() {
		return hovered;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getPadding() {
		return padding;
	}

	public String getText() {
		return text;
	}

	public ResourceLocation getIconResource() {
		return iconResource;
	}

	public int getIconU() {
		return iconU;
	}

	public int getIconV() {
		return iconV;
	}

	public int getIconWidth() {
		return iconWidth;
	}

	public int getIconHeight() {
		return iconHeight;
	}

	public int getIconSourceWidth() {
		return iconSourceWidth;
	}

	public int getIconSourceHeight() {
		return iconSourceHeight;
	}

	public int getTooltipDelay() {
		return tooltipDelay;
	}
	
	public int getTextColor() {
		return textColor;
	}
	
	public int getHighlightedTextColor() {
		return highlightedTextColor;
	}
	
	public int getDisabledTextColor() {
		return disabledTextColor;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setHovered(boolean hovered) {
		this.hovered = hovered;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		this.explicitSize = true;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setTooltip(String... tooltipLines) {
		this.tooltip = tooltipLines;
	}

	public void setTooltipDelay(int tooltipDelay) {
		this.tooltipDelay = tooltipDelay;
	}

	public void setTooltipTick(int tooltipTick) {
		this.tooltipTick = tooltipTick;
	}

	public void setIcon(ResourceLocation iconResource, int iconU, int iconV, int iconWidth, int iconHeight) {
		this.iconU = iconU;
		this.iconV = iconV;
		this.iconResource = iconResource;
		this.iconWidth = iconWidth;
		this.iconHeight = iconHeight;
		this.iconSourceWidth = 256;
		this.iconSourceHeight = 256;
		updateSize();
	}

	public void setIcon(IIcon icon) {
		this.iconU = icon.getU();
		this.iconV = icon.getV();
		this.iconResource = icon.getIconAsset();
		this.iconWidth = icon.getIconSize();
		this.iconHeight = icon.getIconSize();
		this.iconSourceWidth = icon.getGridWidth() * icon.getIconSize();
		this.iconSourceHeight = icon.getGridHeight() * icon.getIconSize();
		updateSize();
	}

	public void setTextColor(Color textColor) {
		this.setTextColor(textColor.getRGB());
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public void setHighlightedTextColor(Color highlightedTextColor) {
		this.setHighlightedTextColor(highlightedTextColor.getRGB());
	}

	public void setHighlightedTextColor(int highlightedTextColor) {
		this.highlightedTextColor = highlightedTextColor;
	}

	public void setDisabledTextColor(Color disabledTextColor) {
		this.setDisabledTextColor(disabledTextColor.getRGB());
	}

	public void setDisabledTextColor(int disabledTextColor) {
		this.disabledTextColor = disabledTextColor;
	}

	public void setClickListener(ClickListener clickListener) {
		this.clickListener = clickListener;
	}
}