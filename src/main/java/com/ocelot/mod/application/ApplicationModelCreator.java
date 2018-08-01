package com.ocelot.mod.application;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.Layout.Background;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.ocelot.api.utils.GuiUtils;
import com.ocelot.api.utils.TextureUtils;
import com.ocelot.mod.Mod;
import com.ocelot.mod.application.component.ComponentModelArea;
import com.ocelot.mod.application.component.Cube;
import com.ocelot.mod.application.component.MenuBar;
import com.ocelot.mod.application.component.MenuBarButton;
import com.ocelot.mod.application.component.MenuBarButtonDivider;
import com.ocelot.mod.application.component.MenuBarItem;
import com.ocelot.mod.application.component.Model;
import com.ocelot.mod.application.layout.LayoutCubeUI;
import com.ocelot.mod.application.task.TaskNotificationCopiedJson;
import com.ocelot.mod.lib.Lib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

/**
 * <em><b>Copyright (c) 2018 Ocelot5836.</b></em>
 * 
 * <br>
 * </br>
 * 
 * The main application class for the Minecraft Model Creator.
 * 
 * @author Ocelot5836
 */
public class ApplicationModelCreator extends Application {

	public static final String MODEL_CREATOR_SAVE_VERSION = "1.0";

	private static ApplicationModelCreator app;
	private static boolean running;

	private Camera camera;

	private Layout mainLayout;
	private MenuBar menuBar;
	private ComponentModelArea modelArea;
	private LayoutCubeUI cubeUI;

	private Cube selectedCube;

	@Override
	public void init(@Nullable NBTTagCompound intent) {
		running = true;
		app = this;

		camera = new Camera(new Vector3f(-5 * 8, -12 * 8, -8 * 8));

		mainLayout = new Layout(362, 164);
		mainLayout.setBackground(new Background() {
			@Override
			public void render(Gui gui, Minecraft mc, int x, int y, int width, int height, int mouseX, int mouseY, boolean windowActive) {
				gui.drawRect(x, y, x + width - (int) (width * (1 - 0.75)) - 1, y + height, 0xffeaeaed);
				gui.drawRect(x + width - (int) (width * (1 - 0.75)) - 1, y, x + width, y + height, 0xffdddde4);
			}
		});

		menuBar = new MenuBar(0, 0, mainLayout.width, 10);
		menuBar.setColor(0xffdddde4);
		menuBar.setBorderColor(0xffdddde4);

		{
			MenuBarItem menuBarFile = new MenuBarItem("File");
			menuBarFile.setTextPadding(6);
			menuBarFile.setTextColor(0xff000000);
			menuBarFile.setColor(0x00ffffff);
			menuBarFile.setBorderColor(0x00ffffff);
			menuBarFile.setHighlightColor(0xffc9c9c9);
			menuBarFile.setHighlightBorderColor(0xffc9c9c9);

			{
				MenuBarButton fileNew = new MenuBarButton("New", Icons.FILE);
				fileNew.setClickListener((mouseX, mouseY, mouseButton) -> {
					List<Cube> cubes = modelArea.getCubes();
					if (!cubes.isEmpty()) {
						ApplicationModelCreator.getApp().removeAllCubes();
					} else {
						Dialog.Confirmation confirmation = new Dialog.Confirmation(I18n.format("dialog.confirmation.save"));
						confirmation.setPositiveListener((mouseX1, mouseY1, mouseButton1) -> {
							ApplicationModelCreator.getApp().saveProjectToFile(cubes);
						});
						confirmation.setNegativeListener((mouseX2, mouseY2, mouseButton2) -> {
							ApplicationModelCreator.getApp().removeAllCubes();
						});
					}
				});
				menuBarFile.add(fileNew);

				menuBarFile.add(new MenuBarButtonDivider());

				MenuBarButton fileLoadProject = new MenuBarButton("Load Project", Icons.FOLDER);
				fileLoadProject.setClickListener((mouseX, mouseY, mouseButton) -> {
					Dialog.OpenFile openDialog = new Dialog.OpenFile(this);
					openDialog.setResponseHandler((success, file) -> {
						if (success) {
							Dialog.Confirmation confirmation = new Dialog.Confirmation(I18n.format("dialog.confirmation.save"));
							confirmation.setPositiveListener((mouseX1, mouseY1, mouseButton1) -> {
								ApplicationModelCreator.getApp().saveProjectToFile(modelArea.getCubes());
								loadProjectFromFile(file);
							});
							confirmation.setNegativeListener((mouseX2, mouseY2, mouseButton2) -> {
								loadProjectFromFile(file);
							});
						} else {
							openErrorDialog(I18n.format("dialog.error.fail_file_open", file != null ? file.getName() : "Null"));
						}
						return true;
					});
					openDialog(openDialog);
				});
				menuBarFile.add(fileLoadProject);

				MenuBarButton fileSaveProject = new MenuBarButton("Save Project", Icons.SAVE);
				fileSaveProject.setClickListener((mouseX, mouseY, mouseButton) -> {
					saveProjectToFile(modelArea.getCubes());
				});
				menuBarFile.add(fileSaveProject);

				menuBarFile.add(new MenuBarButtonDivider());

				MenuBarButton fileImportJson = new MenuBarButton("Import JSON", Icons.IMPORT);
				// fileImportJson.setClickListener((mouseX, mouseY, mouseButton) -> {
				// });
				menuBarFile.add(fileImportJson);

				MenuBarButton fileExportJson = new MenuBarButton("Export JSON", Icons.EXPORT);
				fileExportJson.setClickListener((mouseX, mouseY, mouseButton) -> {
					StringSelection json = new StringSelection(ApplicationModelCreator.createModelJson(modelArea.getCubes()));
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(json, null);
					TaskManager.sendTask(new TaskNotificationCopiedJson());
				});
				menuBarFile.add(fileExportJson);

				menuBarFile.add(new MenuBarButtonDivider());

				MenuBarButton fileSetTexturePath = new MenuBarButton("Set Texture Path", Icons.PICTURE);
				// fileExportJson.setClickListener((mouseX, mouseY, mouseButton) -> {
				// });
				menuBarFile.add(fileSetTexturePath);

				menuBarFile.add(new MenuBarButtonDivider());

				MenuBarButton fileExit = new MenuBarButton("Exit", Icons.POWER_OFF);
				fileExit.setClickListener((mouseX, mouseY, mouseButton) -> {
					Laptop.getSystem().closeApplication(this.getInfo());
				});
				menuBarFile.add(fileExit);
			}

			menuBar.add(menuBarFile);

			MenuBarItem menuBarOptions = new MenuBarItem("Options");
			menuBarOptions.setTextPadding(6);
			menuBarOptions.setTextColor(0xff000000);
			menuBarOptions.setColor(0x00ffffff);
			menuBarOptions.setBorderColor(0x00ffffff);
			menuBarOptions.setHighlightColor(0xffc9c9c9);
			menuBarOptions.setHighlightBorderColor(0xffc9c9c9);
			menuBar.add(menuBarOptions);

			{
				MenuBarButton optionsToggleTransparency = new MenuBarButton("Toggle Transparency", Icons.ARROW_RIGHT) {
					@Override
					public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, int buttonsWidth, int buttonsHeight, float partialTicks) {
						if (this.isVisible()) {
							this.setHovered(GuiUtils.isMouseInside(x, y, buttonsWidth, this.getHeight(), mouseX, mouseY));

							int contentWidth = 13 + Lib.getDefaultTextWidth(this.getText());
							int contentX = (int) Math.ceil((this.getWidth() - contentWidth) / 2.0);

							TextureUtils.bindTexture(Mod.MOD_ID, "textures/app/icons.png");
							GlStateManager.pushMatrix();
							GlStateManager.translate(0.5, 0.5, 0);
							RenderUtil.drawRectWithTexture(x, y + this.getHeight() / 2 - 5, 20, 0, 10, 10, 20, 20, 200, 200);
							GlStateManager.popMatrix();

							int textY = (this.getHeight() - mc.fontRenderer.FONT_HEIGHT) / 2;
							int textOffsetX = 13;
							int textColor = !this.isEnabled() ? this.getDisabledTextColor() : (this.isHovered() ? this.getHighlightedTextColor() : this.getTextColor());
							mc.fontRenderer.drawString(this.getText(), x + textOffsetX, y + textY + 1, textColor, false);
						}
					}
				};
				menuBarOptions.add(optionsToggleTransparency);
			}

			MenuBarItem menuBarScreeenshot = new MenuBarItem("Screeenshot");
			menuBarScreeenshot.setTextPadding(6);
			menuBarScreeenshot.setTextColor(0xff000000);
			menuBarScreeenshot.setColor(0x00ffffff);
			menuBarScreeenshot.setBorderColor(0x00ffffff);
			menuBarScreeenshot.setHighlightColor(0xffc9c9c9);
			menuBarScreeenshot.setHighlightBorderColor(0xffc9c9c9);
			menuBarScreeenshot.setVisible(false);
			menuBar.add(menuBarScreeenshot);

			MenuBarItem menuBarMore = new MenuBarItem("More");
			menuBarMore.setTextPadding(6);
			menuBarMore.setTextColor(0xff000000);
			menuBarMore.setColor(0x00ffffff);
			menuBarMore.setBorderColor(0x00ffffff);
			menuBarMore.setHighlightColor(0xffc9c9c9);
			menuBarMore.setHighlightBorderColor(0xffc9c9c9);
			menuBar.add(menuBarMore);
		}

		modelArea = new ComponentModelArea(0, menuBar.getHeight(), (int) (mainLayout.width * 0.75), mainLayout.height - menuBar.getHeight(), camera);

		cubeUI = new LayoutCubeUI(modelArea.left + modelArea.width, menuBar.getHeight(), mainLayout.width - modelArea.width, mainLayout.height - menuBar.getHeight());

		mainLayout.addComponent(modelArea);
		mainLayout.addComponent(menuBar);
		mainLayout.addComponent(cubeUI);

		setCurrentLayout(this.mainLayout);
	}

	@Override
	public boolean handleFile(File file) {
		return loadProjectFromFile(file);
	}

	@Override
	public void onTick() {
		super.onTick();
		this.camera.update();
	}

	@Override
	public void handleMouseDrag(int mouseX, int mouseY, int mouseButton) {
		super.handleMouseDrag(mouseX, mouseY, mouseButton);
		this.camera.handleMouseDrag(mouseX, mouseY, mouseButton);
	}

	@Override
	public void handleMouseScroll(int mouseX, int mouseY, boolean direction) {
		super.handleMouseScroll(mouseX, mouseY, direction);
		this.camera.handleMouseScroll(mouseX, mouseY, direction);
	}

	@Override
	public void handleKeyTyped(char character, int code) {
		super.handleKeyTyped(character, code);
		if (Mod.isDebug() && code == Keyboard.KEY_Y) {
			String json = ApplicationModelCreator.createModelJson(this.modelArea.getCubes());
			System.out.println("\n\n" + json + "\n");
		}
	}

	@Override
	public void load(NBTTagCompound nbt) {
	}

	@Override
	public void save(NBTTagCompound nbt) {
	}

	@Override
	public void onClose() {
		running = false;
		this.modelArea.cleanUp();
	}

	public void addCube(float x, float y, float z, float sizeX, float sizeY, float sizeZ, float rotationX, float rotationY, float rotationZ) {
		modelArea.addCube(x, y, z, sizeX, sizeY, sizeZ, rotationX, rotationY, rotationZ);
		cubeUI.updateCubes(modelArea.getCubes());
	}

	public void addCube(Cube cube) {
		modelArea.addCube(cube);
		cubeUI.updateCubes(modelArea.getCubes());
	}

	public void removeAllCubes() {
		modelArea.getCubes().clear();
		cubeUI.updateCubes(modelArea.getCubes());
	}

	public void removeCube(int index) {
		modelArea.removeCube(index);
		cubeUI.updateCubes(modelArea.getCubes());
	}

	public static String createModelJson(List<Cube> cubes) {
		Model model = new Model(cubes);
		Gson gson = new GsonBuilder().registerTypeAdapter(Model.class, new Model.Serializer()).setPrettyPrinting().create();
		return gson.toJson(model);
	}

	public static void saveProjectToFile(List<Cube> cubes) {
		NBTTagCompound data = new NBTTagCompound();
		data.setString("version", MODEL_CREATOR_SAVE_VERSION);

		NBTTagList cubesList = new NBTTagList();
		for (Cube cube : cubes) {
			cubesList.appendTag(cube.serializeNBT());
		}
		data.setTag("cubes", cubesList);

		Dialog.SaveFile saveDialog = new Dialog.SaveFile(ApplicationModelCreator.getApp(), data);
		ApplicationModelCreator.getApp().openDialog(saveDialog);
	}

	public static boolean loadProjectFromFile(File file) {
		NBTTagCompound data = file.getData();
		if (data.hasKey("version", Constants.NBT.TAG_STRING)) {
			String version = data.getString("version");
			if (MODEL_CREATOR_SAVE_VERSION.equalsIgnoreCase(version)) {
				ApplicationModelCreator.getApp().removeAllCubes();
				if (data.hasKey("cubes", Constants.NBT.TAG_LIST)) {
					NBTTagList list = data.getTagList("cubes", Constants.NBT.TAG_COMPOUND);
					for (NBTBase base : list) {
						if (base instanceof NBTTagCompound) {
							ApplicationModelCreator.getApp().addCube(Cube.fromTag((NBTTagCompound) base));
						}
					}
				}
				return true;
			} else {
				openErrorDialog(I18n.format("dialog.error.wrong_version", version));
				return false;
			}
		} else {
			openErrorDialog(I18n.format("dialog.error.wrong_version", "Unknown"));
			return false;
		}
	}

	public static void openErrorDialog(String content) {
		openMessageDialog("Error", content);
	}

	public static void openMessageDialog(String title, String content) {
		Dialog.Message error = new Dialog.Message(content);
		error.setTitle(title);
		ApplicationModelCreator.getApp().openDialog(error);
	}

	public static ApplicationModelCreator getApp() {
		return app;
	}

	public static boolean isRunning() {
		return running;
	}
}