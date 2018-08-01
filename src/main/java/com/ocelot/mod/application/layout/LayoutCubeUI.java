package com.ocelot.mod.application.layout;

import java.awt.Color;
import java.util.List;

import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.ScrollableLayout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.ocelot.api.utils.TextureUtils;
import com.ocelot.mod.Mod;
import com.ocelot.mod.application.ApplicationModelCreator;
import com.ocelot.mod.application.component.Cube;
import com.ocelot.mod.application.component.SmoothItemList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class LayoutCubeUI extends Layout {

	private Cube cube;

	private SmoothItemList<Cube> cubes;

	private ScrollableLayout options;

	private Button addCube;
	private Button copyCube;
	private Button deleteCube;
	private TextField cubeName;

	private LayoutNumberIncrementer positionX;
	private LayoutNumberIncrementer positionY;
	private LayoutNumberIncrementer positionZ;

	private LayoutNumberIncrementer sizeX;
	private LayoutNumberIncrementer sizeY;
	private LayoutNumberIncrementer sizeZ;

	public LayoutCubeUI(int left, int top, int width, int height) {
		super(left, top, width, height);
		this.cube = null;
	}

	@Override
	public void init(Layout layout) {
		cubes = new SmoothItemList<>(0, 0, this.width, layout.height / 3);
		cubes.setBorderColor(new Color(164, 164, 164));
		cubes.setInnerBorderColor(new Color(255, 255, 255, 0));
		cubes.setScrollSpeed(5);
		cubes.setItemClickListener((e, index, mouseButton) -> {
			cube = cubes.getItems().get(index);
			this.updateCube(cube);
		});
		cubes.setListItemRenderer(new ListItemRenderer<Cube>(12) {
			@Override
			public void render(Cube cube, Gui gui, Minecraft mc, int x, int y, int width, int height, boolean selected) {
				Gui.drawRect(x, y, x + width, y + height, selected ? 0xffd2d2d2 : 0xffffffff);
				RenderUtil.drawStringClipped(cube.getName().trim(), x + 3, y + 3, width - 6, 0xff000000, false);
			}
		});
		this.addComponent(cubes);

		int buttonPadding = 1;
		int buttonWidth = (this.width - buttonPadding) / 3;
		int buttonHeight = 16;

		addCube = new Button(0 * (buttonWidth + buttonPadding), cubes.top + cubes.getHeight(), buttonWidth, buttonHeight, "") {
			@Override
			public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, float partialTicks) {
				super.render(laptop, mc, x, y, mouseX, mouseY, windowActive, partialTicks);

				int contentWidth = 10;
				int contentX = (int) Math.ceil((width - contentWidth) / 2.0);
				TextureUtils.bindTexture(Mod.MOD_ID, "textures/app/icons.png");
				RenderUtil.drawRectWithTexture(x + contentX, y + 3, 0, 0, 10, 10, 20, 20, 200, 200);
			}
		};
		addCube.setToolTip("New Element", "Adds another element to the workspace");
		addCube.setClickListener((mouseX, mouseY, mouseButton) -> {
			ApplicationModelCreator.getApp().addCube(0, 0, 0, 1, 1, 1, 0, 0, 0);
			this.updateCube(cubes.getSelectedItem());
		});
		this.addComponent(addCube);

		deleteCube = new Button(1 * (buttonWidth + buttonPadding), cubes.top + cubes.getHeight(), buttonWidth, buttonHeight, Icons.TRASH);
		deleteCube.setClickListener((mouseX, mouseY, mouseButton) -> {
			if (cube != null) {
				ApplicationModelCreator.getApp().removeCube(cubes.getSelectedIndex());
				this.updateCube(cubes.getSelectedItem());
			}
		});
		this.addComponent(deleteCube);

		copyCube = new Button(2 * (buttonWidth + buttonPadding), cubes.top + cubes.getHeight(), buttonWidth, buttonHeight, Icons.COPY);
		copyCube.setClickListener((mouseX, mouseY, mouseButton) -> {
			if (cube != null) {
				ApplicationModelCreator.getApp().addCube(cube.copy());
				cubes.setSelectedIndex(cubes.size() - 1);
				this.updateCube(cubes.getSelectedItem());
			}
		});
		this.addComponent(copyCube);

		cubeName = new TextField(0, cubes.top + buttonHeight + cubes.getHeight(), this.width);
		cubeName.setBorderColor(new Color(164, 164, 164));
		cubeName.setBackgroundColor(Color.WHITE);
		cubeName.setTextColor(Color.BLACK);
		cubeName.setKeyListener((c) -> {
			if (!cubeName.getText().trim().isEmpty() && cube != null) {
				cube.setName(cubeName.getText());
			}
			return false;
		});
		this.addComponent(cubeName);

		options = new ScrollableLayout(0, cubes.top + buttonHeight + cubes.getHeight() + 16, this.width, 85, this.height - (cubes.top + buttonHeight + cubes.getHeight() + 16));

		positionX = new LayoutNumberIncrementer(2 + (options.width / 3 - 3) * 0, 2, options.width / 3 - 4, 40, 0);
		options.addComponent(positionX);

		positionY = new LayoutNumberIncrementer(2 + (options.width / 3 - 3) * 1, 2, options.width / 3 - 4, 40, 0);
		options.addComponent(positionY);

		positionZ = new LayoutNumberIncrementer(2 + (options.width / 3 - 3) * 2, 2, options.width / 3 - 4, 40, 0);
		options.addComponent(positionZ);

		sizeX = new LayoutNumberIncrementer(2 + (options.width / 3 - 3) * 0, positionX.height + 3, options.width / 3 - 4, 40, 0);
		options.addComponent(sizeX);

		sizeY = new LayoutNumberIncrementer(2 + (options.width / 3 - 3) * 1, positionY.height + 3, options.width / 3 - 4, 40, 0);
		options.addComponent(sizeY);

		sizeZ = new LayoutNumberIncrementer(2 + (options.width / 3 - 3) * 2, positionZ.height + 3, options.width / 3 - 4, 40, 0);
		options.addComponent(sizeZ);

		this.addComponent(options);
	}

	@Override
	public void handleTick() {
		super.handleTick();
		this.cubeName.setVisible(cube != null);
		this.copyCube.setEnabled(cube != null);
		this.deleteCube.setEnabled(cube != null);
		this.options.setVisible(cube != null);

		if (this.cube != null) {
			this.cube.setPosition(this.positionX.getValue(), this.positionY.getValue(), this.positionZ.getValue());
			this.cube.setSize(this.sizeX.getValue(), this.sizeY.getValue(), this.sizeZ.getValue());
		}
	}

	public void updateCube(Cube cube) {
		this.cube = cube;
		this.cubeName.setText(String.valueOf(cube));
		if (this.cube != null) {
			this.positionX.set(this.cube.getPosition().x);
			this.positionY.set(this.cube.getPosition().y);
			this.positionZ.set(this.cube.getPosition().z);
			
			this.sizeX.set(this.cube.getSize().x);
			this.sizeY.set(this.cube.getSize().y);
			this.sizeZ.set(this.cube.getSize().z);
		}
	}

	public void updateCubes(List<Cube> cubes) {
		this.cubes.setItems(cubes);
	}
}