package com.ocelot.mod.application.layout;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.util.vector.Vector4f;

import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.ScrollableLayout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.CheckBox;
import com.mrcrayfish.device.api.app.component.ComboBox;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.RadioGroup;
import com.mrcrayfish.device.api.app.component.Slider;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.renderer.ItemRenderer;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.ocelot.api.geometry.Cube;
import com.ocelot.api.utils.NamedBufferedImage;
import com.ocelot.api.utils.TextureUtils;
import com.ocelot.mod.ModelCreator;
import com.ocelot.mod.application.ApplicationModelCreator;
import com.ocelot.mod.application.component.SmoothItemList;
import com.ocelot.mod.application.dialog.DialogTextureManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumFacing;

public class LayoutCubeUI extends Layout {

	private Cube cube;
	private EnumFacing.Axis selectedAxis;
	private float cubeRotation;
	private EnumFacing selectedFace;
	private NamedBufferedImage copiedTexture;
	private Vector4f copiedTextureCoords;

	private TextField cubesSearch;
	private List<Cube> cubesCopy;
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

	private ComboBox.List<EnumFacing.Axis> axisSelection;
	private Slider rotation;

	private ComboBox.List<EnumFacing> faceSelection;
	private Button faceImage;
	private Button faceDeleteImage;
	private Button faceCopyImage;
	private Button facePasteImage;
	private LayoutNumberIncrementer faceU;
	private LayoutNumberIncrementer faceV;
	private LayoutNumberIncrementer faceWidth;
	private LayoutNumberIncrementer faceHeight;
	private Slider faceRotation;
	private CheckBox faceCull;
	private CheckBox faceFill;
	private CheckBox faceEnable;
	private CheckBox faceAutoUV;

	public LayoutCubeUI(int left, int top, int width, int height) {
		super(left, top, width, height);
		this.cube = null;
		this.selectedAxis = EnumFacing.Axis.X;
		this.cubeRotation = 0;
		this.selectedFace = EnumFacing.DOWN;
		this.copiedTexture = null;
		this.copiedTextureCoords = new Vector4f();
	}

	@Override
	public void init(Layout layout) {
		cubesSearch = new TextField(0, 0, this.width);
		cubesSearch.setPlaceholder(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.search_placeholder"));
		cubesSearch.setKeyListener((c) -> {
			this.cubes.setSelectedIndex(-1);
			this.updateCube(null);
			this.cubes.setItems(cubesCopy.stream().filter((cube) -> {
				return StringUtils.containsIgnoreCase(cube.getName(), cubesSearch.getText());
			}).collect(Collectors.toList()));
			return false;
		});
		this.addComponent(cubesSearch);

		cubesCopy = new ArrayList<Cube>();

		cubes = new SmoothItemList<>(0, cubesSearch.top + 16, this.width, layout.height / 3);
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
				TextureUtils.bindTexture(ModelCreator.MOD_ID, "textures/app/icons.png");
				RenderUtil.drawRectWithTexture(x + contentX, y + 3, 0, 0, 10, 10, 20, 20, 200, 200);
			}
		};
		addCube.setToolTip(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.new"), I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.new.tooltip"));
		addCube.setClickListener((mouseX, mouseY, mouseButton) -> {
			ApplicationModelCreator.getApp().addCube(0, 0, 0, 1, 1, 1, 0, 0, 0);
		});
		this.addComponent(addCube);

		deleteCube = new Button(1 * (buttonWidth + buttonPadding), cubes.top + cubes.getHeight(), buttonWidth, buttonHeight, Icons.TRASH);
		deleteCube.setToolTip(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.remove"), I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.remove.tooltip"));
		deleteCube.setClickListener((mouseX, mouseY, mouseButton) -> {
			if (cube != null) {
				ApplicationModelCreator.getApp().removeCube(cubes.getSelectedIndex());
				this.updateCube(cubes.getSelectedItem());
			}
		});
		this.addComponent(deleteCube);

		copyCube = new Button(2 * (buttonWidth + buttonPadding), cubes.top + cubes.getHeight(), buttonWidth, buttonHeight, Icons.COPY);
		copyCube.setToolTip(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.copy"), I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.copy.tooltip"));
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
			objectOptions.setScrollSpeed(10);

			positionX = new LayoutNumberIncrementer(2 + (objectOptions.width / 3 - 3) * 0, 15, objectOptions.width / 3 - 4, 40, 0);
			objectOptions.addComponent(positionX);

			positionY = new LayoutNumberIncrementer(2 + (objectOptions.width / 3 - 3) * 1, 15, objectOptions.width / 3 - 4, 40, 0);
			objectOptions.addComponent(positionY);

			positionZ = new LayoutNumberIncrementer(2 + (objectOptions.width / 3 - 3) * 2, 15, objectOptions.width / 3 - 4, 40, 0);
			objectOptions.addComponent(positionZ);

			Label positionLabel = new Label(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.label.position"), 5, 5);
			positionLabel.setTextColor(Color.BLACK);
			positionLabel.setShadow(false);
			objectOptions.addComponent(positionLabel);

			sizeX = new LayoutNumberIncrementer(2 + (objectOptions.width / 3 - 3) * 0, positionX.height + 32, objectOptions.width / 3 - 4, 40, 0);
			objectOptions.addComponent(sizeX);

			sizeY = new LayoutNumberIncrementer(2 + (objectOptions.width / 3 - 3) * 1, positionY.height + 32, objectOptions.width / 3 - 4, 40, 0);
			objectOptions.addComponent(sizeY);

			sizeZ = new LayoutNumberIncrementer(2 + (objectOptions.width / 3 - 3) * 2, positionZ.height + 32, objectOptions.width / 3 - 4, 40, 0);
			objectOptions.addComponent(sizeZ);

			Label sizeLabel = new Label(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.label.size"), 5, 60);
			sizeLabel.setTextColor(Color.BLACK);
			sizeLabel.setShadow(false);
			objectOptions.addComponent(sizeLabel);

			shade = new CheckBox(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.label.shade"), 5, 118);
			shade.setTextColor(Color.BLACK);
			objectOptions.addComponent(shade);

			ambientOcclusion = new CheckBox(RenderUtil.clipStringToWidth(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.label.ambient_occlusion"), objectOptions.width - 20), 5, 130);
			ambientOcclusion.setTextColor(Color.BLACK);
			ambientOcclusion.setClickListener((mouseX, mouseY, mouseButton) -> {
				ApplicationModelCreator.getApp().setAmbientOcclusion(this.ambientOcclusion.isSelected());
			});
			objectOptions.addComponent(ambientOcclusion);

			particle = new Button(5, 145, objectOptions.width - 10, 16, I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.button.particle"), Icons.PICTURE);
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
			rotationOptions = new ScrollableLayout(0, cubes.top + buttonHeight + cubes.getHeight() + 16 + 12, this.width, 56, this.height - (cubes.top + buttonHeight + cubes.getHeight() + 16 + 12));
			rotationOptions.setScrollSpeed(10);

			axisSelection = new ComboBox.List<EnumFacing.Axis>(5, 15, rotationOptions.width - 10, rotationOptions.width - 15, EnumFacing.Axis.values());
			axisSelection.setSelectedItem(EnumFacing.Axis.X);
			axisSelection.setChangeListener((oldValue, newValue) -> {
				selectedAxis = newValue;
				if (cube != null) {
					updateRotation();
				}
			});
			rotationOptions.addComponent(axisSelection);

			rotation = new Slider(5, 40, rotationOptions.width - 10);
			rotation.setSlideListener((percentage) -> {
				int realRotation = (int) (percentage * 4) - 2;
				float rotationAmount = 45f / 2f;
				float rotation = rotationAmount * (int) (realRotation);
				this.cubeRotation = rotation;
				if (cube != null) {
					updateRotation();
				}
			});
			rotationOptions.addComponent(rotation);

			Label rotationLabel = new Label(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.label.rotation"), 5, 5);
			rotationLabel.setTextColor(Color.BLACK);
			rotationLabel.setShadow(false);
			rotationOptions.addComponent(rotationLabel);

			this.addComponent(rotationOptions);
		}

		{
			textureOptions = new ScrollableLayout(0, cubes.top + buttonHeight + cubes.getHeight() + 16 + 12, this.width, 270, this.height - (cubes.top + buttonHeight + cubes.getHeight() + 16 + 12));
			textureOptions.setScrollSpeed(10);

			Label faceLabel = new Label(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.label.face"), 5, 5);
			faceLabel.setTextColor(Color.BLACK);
			faceLabel.setShadow(false);
			textureOptions.addComponent(faceLabel);

			faceSelection = new ComboBox.List<EnumFacing>(5, 15, textureOptions.width - 10, textureOptions.width - 15, EnumFacing.values());
			faceSelection.setChangeListener((oldValue, newValue) -> {
				selectedFace = newValue;
			});
			faceSelection.setItemRenderer(new ItemRenderer<EnumFacing>() {
				@Override
				public void render(EnumFacing facing, Gui gui, Minecraft mc, int x, int y, int width, int height) {
					mc.fontRenderer.drawString(facing.getName2().substring(0, 1).toUpperCase() + facing.getName2().substring(1), x + 2, y + height / 2 - mc.fontRenderer.FONT_HEIGHT / 2, 0xffffffff, false);
				}
			});
			faceSelection.setListItemRenderer(new ListItemRenderer<EnumFacing>((int) (12 * 0.8)) {
				@Override
				public void render(EnumFacing facing, Gui gui, Minecraft mc, int x, int y, int width, int height, boolean selected) {
					mc.fontRenderer.drawString(facing.getName2().substring(0, 1).toUpperCase() + facing.getName2().substring(1), x + 2, y + 1, 0xffffffff, false);
				}
			});
			textureOptions.addComponent(faceSelection);

			Label textureLabel = new Label("Texture", 5, 35);
			textureLabel.setTextColor(Color.BLACK);
			textureLabel.setShadow(false);
			textureOptions.addComponent(textureLabel);

			faceImage = new Button(5, 45, textureOptions.width - 10, 16, I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.button.texture"), Icons.PICTURE);
			faceImage.setClickListener((mouseX, mouseY, mouseButton) -> {
				DialogTextureManager textureManager = new DialogTextureManager();
				textureManager.setCloseListener(() -> {
					if (textureManager.getSelectedImage() != null) {
						if (this.cube != null) {
							this.cube.textureFace(selectedFace, textureManager.getSelectedImage());
						}
					}
				});
				ApplicationModelCreator.getApp().openDialog(textureManager);
			});
			textureOptions.addComponent(faceImage);

			faceDeleteImage = new Button(5, 65, textureOptions.width - 10, 16, I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.button.clear"), Icons.FORBIDDEN);
			faceDeleteImage.setClickListener((mouseX, mouseY, mouseButton) -> {
				if (this.cube != null) {
					if (GuiScreen.isShiftKeyDown()) {
						for (int i = 0; i < EnumFacing.values().length; i++) {
							this.cube.textureFace(EnumFacing.values()[i], null);
						}
					} else {
						this.cube.textureFace(this.selectedFace, null);
					}
				}
			});
			textureOptions.addComponent(faceDeleteImage);

			faceCopyImage = new Button(5, 85, textureOptions.width - 10, 16, I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.button.copy"), Icons.COPY);
			faceCopyImage.setClickListener((mouseX, mouseY, mouseButton) -> {
				if (this.cube != null) {
					this.copiedTexture = this.cube.getFace(selectedFace).getTexture();
					this.copiedTextureCoords.set(this.cube.getFace(selectedFace).getTextureCoords());
				}
			});
			textureOptions.addComponent(faceCopyImage);

			facePasteImage = new Button(5, 105, textureOptions.width - 10, 16, I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.button.paste"), Icons.CLIPBOARD);
			facePasteImage.setClickListener((mouseX, mouseY, mouseButton) -> {
				if (this.cube != null && this.copiedTexture != null) {
					if (GuiScreen.isShiftKeyDown()) {
						for (int i = 0; i < EnumFacing.values().length; i++) {
							this.cube.getFace(EnumFacing.values()[i]).setTexture(this.copiedTexture, this.copiedTextureCoords);
						}
					} else {
						this.cube.getFace(selectedFace).setTexture(this.copiedTexture, this.copiedTextureCoords);
					}
				}
			});
			textureOptions.addComponent(facePasteImage);

			Label uvLabel = new Label(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.label.uv"), 5, 125);
			uvLabel.setTextColor(Color.BLACK);
			uvLabel.setShadow(false);
			textureOptions.addComponent(uvLabel);

			faceU = new LayoutNumberIncrementer(1, 135, (textureOptions.width - 10) / 4, 40, 0);
			textureOptions.addComponent(faceU);

			faceV = new LayoutNumberIncrementer(1 * (textureOptions.width) / 4, 135, (textureOptions.width - 10) / 4, 40, 0);
			textureOptions.addComponent(faceV);

			faceWidth = new LayoutNumberIncrementer(2 * (textureOptions.width) / 4 - 2, 135, (textureOptions.width - 10) / 4, 40, 16);
			textureOptions.addComponent(faceWidth);

			faceHeight = new LayoutNumberIncrementer(2 + textureOptions.width - (textureOptions.width - 4) / 4 - 8, 135, (textureOptions.width - 10) / 4, 40, 16);
			textureOptions.addComponent(faceHeight);

			Label rotationLabel = new Label(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.label.rotation"), 5, 180);
			rotationLabel.setTextColor(Color.BLACK);
			rotationLabel.setShadow(false);
			textureOptions.addComponent(rotationLabel);

			faceRotation = new Slider(5, 190, textureOptions.width - 13);
			faceRotation.setSlideListener((percentage) -> {
				float rotation = 90 * (int) (percentage * 3.25);
				if (this.cube != null) {
					this.cube.getFace(this.selectedFace).setRotation(rotation);
				}
			});
			textureOptions.addComponent(faceRotation);

			faceCull = new CheckBox(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.label.cull"), 5, 210);
			faceCull.setTextColor(Color.BLACK);
			textureOptions.addComponent(faceCull);

			faceFill = new CheckBox(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.label.fill"), 5, 225);
			faceFill.setTextColor(Color.BLACK);
			textureOptions.addComponent(faceFill);

			faceEnable = new CheckBox(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.label.enable"), 5, 240);
			faceEnable.setTextColor(Color.BLACK);
			textureOptions.addComponent(faceEnable);

			faceAutoUV = new CheckBox(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.label.auto_uv"), 5, 255);
			faceAutoUV.setTextColor(Color.BLACK);
			textureOptions.addComponent(faceAutoUV);

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

			this.cube.setTextureCoords(this.selectedFace, this.faceU.getValue(), this.faceV.getValue(), this.faceWidth.getValue(), this.faceHeight.getValue());

			this.cube.cullFace(this.selectedFace, this.faceCull.isSelected());
			this.cube.setFaceEnabled(this.selectedFace, this.faceEnable.isSelected());
			this.cube.getFace(this.selectedFace).setFill(this.faceFill.isSelected());
			this.cube.getFace(this.selectedFace).setAutoUV(this.faceAutoUV.isSelected());
		}
	}

	private void updateRotation() {
		this.cube.getRotation().set(0, 0, 0);
		switch (selectedAxis) {
		case X:
			this.cube.getRotation().set(this.cubeRotation, 0, 0);
			break;
		case Y:
			this.cube.getRotation().set(0, this.cubeRotation, 0);
			break;
		case Z:
			this.cube.getRotation().set(0, 0, this.cubeRotation);
			break;
		default:
			break;
		}
	}

	public void updateCube(Cube cube) {
		if (cube != null) {
			this.positionX.set(cube.getPosition().x);
			this.positionY.set(cube.getPosition().y);
			this.positionZ.set(cube.getPosition().z);

			this.sizeX.set(cube.getSize().x);
			this.sizeY.set(cube.getSize().y);
			this.sizeZ.set(cube.getSize().z);

			float rotation = this.selectedAxis == EnumFacing.Axis.X ? cube.getRotation().x : this.selectedAxis == EnumFacing.Axis.Y ? cube.getRotation().y : this.selectedAxis == EnumFacing.Axis.Z ? cube.getRotation().z : 0;
			this.rotation.setPercentage(rotation / 2f / 45f + 0.5f);
			cubeRotation = rotation;
			updateRotation();

			this.faceU.set(cube.getFace(this.selectedFace).getTextureCoords().x);
			this.faceV.set(cube.getFace(this.selectedFace).getTextureCoords().y);
			this.faceWidth.set(cube.getFace(this.selectedFace).getTextureCoords().z);
			this.faceHeight.set(cube.getFace(this.selectedFace).getTextureCoords().w);

			// TODO implement these features
			this.faceRotation.setEnabled(false);

			this.faceEnable.setSelected(cube.getFace(this.selectedFace).isEnabled());
			this.faceCull.setSelected(cube.getFace(this.selectedFace).isCullFace());
			this.faceFill.setSelected(cube.getFace(this.selectedFace).isFill());
			this.faceAutoUV.setSelected(cube.getFace(this.selectedFace).isAutoUV());

			this.shade.setSelected(cube.shouldShade());
		}
		this.cube = cube;
		this.cubeName.setText(cube == null ? "" : cube.getName());
	}

	public void updateCubes(List<Cube> cubes) {
		this.cubes.setItems(cubes);
		this.cubesCopy.clear();
		this.cubesCopy.addAll(cubes);
		if (this.cubes.size() == 0) {
			this.updateCube(null);
		}
	}

	public void setAmbientOcclusion(boolean ambientOcclusion) {
		this.ambientOcclusion.setSelected(ambientOcclusion);
	}

	public void setParticle(NamedBufferedImage particle) {
		this.particle.setText(particle == null ? I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.button.particle") : I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".ui.button.particle_set"));
	}
}