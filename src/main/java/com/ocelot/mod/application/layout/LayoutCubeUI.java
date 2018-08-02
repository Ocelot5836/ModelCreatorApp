package com.ocelot.mod.application.layout;

import java.awt.Color;
import java.util.List;

import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.ScrollableLayout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.CheckBox;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.RadioGroup;
import com.mrcrayfish.device.api.app.component.Slider;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.ocelot.api.utils.TextureUtils;
import com.ocelot.mod.Mod;
import com.ocelot.mod.application.ApplicationModelCreator;
import com.ocelot.mod.application.component.Cube;
import com.ocelot.mod.application.component.SmoothItemList;
import com.ocelot.mod.application.dialog.DialogTextureManager;
import com.ocelot.mod.application.dialog.NamedBufferedImage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class LayoutCubeUI extends Layout {

	private Cube cube;

	private SmoothItemList<Cube> cubes;

	private ScrollableLayout objectOptions;
	private ScrollableLayout rotationOptions;
	private ScrollableLayout textureOptions;

	private Button addCube;
	private Button copyCube;
	private Button deleteCube;
	private TextField cubeName;

	private RadioGroup modes;
	private CheckBox objectMode;
	private CheckBox rotationMode;
	private CheckBox textureMode;

	private LayoutNumberIncrementer positionX;
	private LayoutNumberIncrementer positionY;
	private LayoutNumberIncrementer positionZ;
	private LayoutNumberIncrementer sizeX;
	private LayoutNumberIncrementer sizeY;
	private LayoutNumberIncrementer sizeZ;
	private CheckBox shade;

	private CheckBox ambientOcclusion;
	private Button particle;

	private Slider rotationX;
	private Slider rotationY;
	private Slider rotationZ;

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
		deleteCube.setToolTip("Remove Element", "Removes the selected element from the workspace");
		deleteCube.setClickListener((mouseX, mouseY, mouseButton) -> {
			if (cube != null) {
				ApplicationModelCreator.getApp().removeCube(cubes.getSelectedIndex());
				this.updateCube(cubes.getSelectedItem());
			}
		});
		this.addComponent(deleteCube);

		copyCube = new Button(2 * (buttonWidth + buttonPadding), cubes.top + cubes.getHeight(), buttonWidth, buttonHeight, Icons.COPY);
		copyCube.setToolTip("Duplicate Element", "Adds a copy of the selected element to the workspace");
		copyCube.setClickListener((mouseX, mouseY, mouseButton) -> {
			if (cube != null) {
				ApplicationModelCreator.getApp().addCube(cube.copy());
				cubes.setSelectedIndex(cubes.size() - 1);
				this.updateCube(cubes.getSelectedItem());
			}
		});
		this.addComponent(copyCube);

		{
			int boxOffset = 20;

			modes = new RadioGroup();

			objectMode = new CheckBox("", boxOffset, cubes.top + buttonHeight + cubes.getHeight() + 1);
			objectMode.setRadioGroup(modes);
			objectMode.setSelected(true);
			this.addComponent(objectMode);

			rotationMode = new CheckBox("", width / 2 - 5, cubes.top + buttonHeight + cubes.getHeight() + 1);
			rotationMode.setRadioGroup(modes);
			this.addComponent(rotationMode);

			textureMode = new CheckBox("", width - 10 - boxOffset, cubes.top + buttonHeight + cubes.getHeight() + 1);
			textureMode.setRadioGroup(modes);
			this.addComponent(textureMode);
		}

		cubeName = new TextField(0, cubes.top + buttonHeight + cubes.getHeight() + 12, this.width);
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

		{
			objectOptions = new ScrollableLayout(0, cubes.top + buttonHeight + cubes.getHeight() + 16 + 12, this.width, 164, this.height - (cubes.top + buttonHeight + cubes.getHeight() + 16 + 12));

			positionX = new LayoutNumberIncrementer(2 + (objectOptions.width / 3 - 3) * 0, 15, objectOptions.width / 3 - 4, 40, 0);
			objectOptions.addComponent(positionX);

			positionY = new LayoutNumberIncrementer(2 + (objectOptions.width / 3 - 3) * 1, 15, objectOptions.width / 3 - 4, 40, 0);
			objectOptions.addComponent(positionY);

			positionZ = new LayoutNumberIncrementer(2 + (objectOptions.width / 3 - 3) * 2, 15, objectOptions.width / 3 - 4, 40, 0);
			objectOptions.addComponent(positionZ);

			Label positionLabel = new Label("Position", 5, 5);
			positionLabel.setTextColor(Color.BLACK);
			positionLabel.setShadow(false);
			objectOptions.addComponent(positionLabel);

			sizeX = new LayoutNumberIncrementer(2 + (objectOptions.width / 3 - 3) * 0, positionX.height + 32, objectOptions.width / 3 - 4, 40, 0);
			objectOptions.addComponent(sizeX);

			sizeY = new LayoutNumberIncrementer(2 + (objectOptions.width / 3 - 3) * 1, positionY.height + 32, objectOptions.width / 3 - 4, 40, 0);
			objectOptions.addComponent(sizeY);

			sizeZ = new LayoutNumberIncrementer(2 + (objectOptions.width / 3 - 3) * 2, positionZ.height + 32, objectOptions.width / 3 - 4, 40, 0);
			objectOptions.addComponent(sizeZ);

			Label sizeLabel = new Label("Size", 5, 60);
			sizeLabel.setTextColor(Color.BLACK);
			sizeLabel.setShadow(false);
			objectOptions.addComponent(sizeLabel);

			shade = new CheckBox("Shade", 5, 118);
			shade.setTextColor(Color.BLACK);
			objectOptions.addComponent(shade);

			ambientOcclusion = new CheckBox("Ambient Occ...", 5, 130);
			ambientOcclusion.setTextColor(Color.BLACK);
			ambientOcclusion.setClickListener((mouseX, mouseY, mouseButton) -> {
				ApplicationModelCreator.getApp().setAmbientOcclusion(this.ambientOcclusion.isSelected());
			});
			objectOptions.addComponent(ambientOcclusion);

			particle = new Button(5, 145, objectOptions.width - 10, 16, "Particle", Icons.PICTURE);
			particle.setClickListener((mouseX, mouseY, mouseButton) -> {
				DialogTextureManager textureManager = new DialogTextureManager();
				textureManager.setCloseListener(() -> {
					if (textureManager.getSelectedImage() != null) {
						ApplicationModelCreator.getApp().setParticle(textureManager.getSelectedImage());
					}
				});
				ApplicationModelCreator.getApp().openDialog(textureManager);
			});
			objectOptions.addComponent(particle);

			this.addComponent(objectOptions);
		}

		{
			rotationOptions = new ScrollableLayout(0, cubes.top + buttonHeight + cubes.getHeight() + 16 + 12, this.width, 72, this.height - (cubes.top + buttonHeight + cubes.getHeight() + 16 + 12));

			rotationX = new Slider(5, 15, rotationOptions.width - 10);
			rotationX.setSlideListener((percentage) -> {
				int realPercentage = (int) (percentage * 8);
				int rotationAmount = 360 / 8;
				int rotation = rotationAmount * realPercentage;
				if (cube != null) {
					cube.getRotation().x = rotation;
				}
			});
			rotationOptions.addComponent(rotationX);

			rotationY = new Slider(5, 35, rotationOptions.width - 10);
			rotationY.setSlideListener((percentage) -> {
				int realPercentage = (int) (percentage * 8);
				int rotationAmount = 360 / 8;
				int rotation = rotationAmount * realPercentage;
				if (cube != null) {
					cube.getRotation().y = rotation;
				}
			});
			rotationOptions.addComponent(rotationY);

			rotationZ = new Slider(5, 55, rotationOptions.width - 10);
			rotationZ.setSlideListener((percentage) -> {
				int realPercentage = (int) (percentage * 8);
				int rotationAmount = 360 / 8;
				int rotation = rotationAmount * realPercentage;
				if (cube != null) {
					cube.getRotation().z = rotation;
				}
			});
			rotationOptions.addComponent(rotationZ);

			Label rotationLabel = new Label("Rotation", 5, 5);
			rotationLabel.setTextColor(Color.BLACK);
			rotationLabel.setShadow(false);
			rotationOptions.addComponent(rotationLabel);

			this.addComponent(rotationOptions);
		}

		{
			textureOptions = new ScrollableLayout(0, cubes.top + buttonHeight + cubes.getHeight() + 16 + 12, this.width, 85, this.height - (cubes.top + buttonHeight + cubes.getHeight() + 16 + 12));

			this.addComponent(textureOptions);
		}
	}

	@Override
	public void handleTick() {
		super.handleTick();
		this.cubeName.setVisible(cube != null);
		this.copyCube.setEnabled(cube != null);
		this.deleteCube.setEnabled(cube != null);
		this.objectOptions.setVisible(cube != null);

		this.objectMode.setVisible(cube != null);
		this.rotationMode.setVisible(cube != null);
		this.textureMode.setVisible(cube != null);

		this.objectOptions.setVisible(cube != null && this.objectMode.isSelected());
		this.rotationOptions.setVisible(cube != null && this.rotationMode.isSelected());
		this.textureOptions.setVisible(cube != null && this.textureMode.isSelected());

		if (this.cube != null) {
			this.cube.setPosition(this.positionX.getValue(), this.positionY.getValue(), this.positionZ.getValue());
			this.cube.setSize(this.sizeX.getValue(), this.sizeY.getValue(), this.sizeZ.getValue());
			this.cube.setShade(this.shade.isSelected());
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

			this.rotationX.setPercentage(this.cube.getRotation().x / 360f);
			this.rotationY.setPercentage(this.cube.getRotation().y / 360f);
			this.rotationZ.setPercentage(this.cube.getRotation().z / 360f);

			this.shade.setSelected(this.cube.shouldShade());
		}
	}

	public void updateCubes(List<Cube> cubes) {
		this.cubes.setItems(cubes);
	}

	public void setAmbientOcclusion(boolean ambientOcclusion) {
		this.ambientOcclusion.setSelected(ambientOcclusion);
	}

	public void setParticle(NamedBufferedImage particle) {
		this.particle.setText(particle == null ? "Particle" : "Particle Set");
	}
}