package com.ocelot.mod.application;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Dialog.ResponseHandler;
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
import com.ocelot.mod.application.dialog.NamedBufferedImage;
import com.ocelot.mod.application.layout.LayoutCubeUI;
import com.ocelot.mod.application.task.TaskNotificationCopiedJson;
import com.ocelot.mod.lib.Lib;
import com.ocelot.mod.lib.ModelCreatorFileConverter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.Loader;

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

	public static final String MODEL_CREATOR_SAVE_VERSION = ModelCreatorFileConverter.MODEL_CREATOR_SAVE_VERSION_12;

	private static ApplicationModelCreator app;
	private static boolean running;
	private static String jsonName;

	private List<NamedBufferedImage> loadedImages;
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

		loadedImages = new ArrayList<NamedBufferedImage>();
		camera = new Camera(new Vector3f(-5 * 8, -12 * 8, -8 * 8));

		mainLayout = new Layout(362, 164);
		mainLayout.setBackground(new Background() {
			@Override
			public void render(Gui gui, Minecraft mc, int x, int y, int width, int height, int mouseX, int mouseY, boolean windowActive) {
				gui.drawRect(x, y, x + width - (int) (width * (1 - 0.75)) - 1, y + height, 0xffeaeaed);
				gui.drawRect(x + width - (int) (width * (1 - 0.75)) - 1, y, x + width, y + height, 0xffdddde4);
				mc.fontRenderer.drawString("v" + Mod.VERSION + ", Save format v" + MODEL_CREATOR_SAVE_VERSION, x + 2, y + 12, 0xffdddde4, false);
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
						removeAllCubes();
					} else {
						Dialog.Confirmation confirmation = new Dialog.Confirmation(I18n.format("dialog.confirmation.save"));
						confirmation.setPositiveListener((mouseX1, mouseY1, mouseButton1) -> {
							saveProjectToFile(cubes, loadedImages, modelArea.hasAmbientOcclusion(), modelArea.getParticle());
						});
						confirmation.setNegativeListener((mouseX2, mouseY2, mouseButton2) -> {
							removeAllCubes();
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
							if (modelArea.getCubes().isEmpty()) {
								loadProjectFromFile(file);
							} else {
								Dialog.Confirmation confirmation = new Dialog.Confirmation(I18n.format("dialog.confirmation.save"));
								confirmation.setPositiveListener((mouseX1, mouseY1, mouseButton1) -> {
									saveProjectToFile(modelArea.getCubes(), loadedImages, modelArea.hasAmbientOcclusion(), modelArea.getParticle(), (success1, file1) -> {
										if (success1) {
											return loadProjectFromFile(file1);
										}
										return true;
									});

								});
								confirmation.setNegativeListener((mouseX1, mouseY1, mouseButton1) -> {
									loadProjectFromFile(file);
								});
								openDialog(confirmation);
							}
						} else {
							openErrorDialog(I18n.format("dialog.error.fail_file_open", file != null ? file.getName() : "Null"));
						}
						return success;
					});
					openDialog(openDialog);
				});
				menuBarFile.add(fileLoadProject);

				MenuBarButton fileSaveProject = new MenuBarButton("Save Project", Icons.SAVE);
				fileSaveProject.setClickListener((mouseX, mouseY, mouseButton) -> {
					saveProjectToFile(modelArea.getCubes(), loadedImages, modelArea.hasAmbientOcclusion(), modelArea.getParticle());
				});
				menuBarFile.add(fileSaveProject);

				menuBarFile.add(new MenuBarButtonDivider());

				MenuBarButton fileImportJson = new MenuBarButton("Import JSON", Icons.IMPORT);
				// fileImportJson.setClickListener((mouseX, mouseY, mouseButton) -> {
				// });
				menuBarFile.add(fileImportJson);

				MenuBarButton fileExportJson = new MenuBarButton("Export JSON", Icons.EXPORT);
				fileExportJson.setClickListener((mouseX, mouseY, mouseButton) -> {
					Dialog.Input input = new Dialog.Input("Enter the name of the json");
					input.setResponseHandler(new ResponseHandler<String>() {
						@Override
						public boolean onResponse(boolean success, String input) {
							if (success) {
								if (!StringUtils.isNullOrEmpty(input)) {
									jsonName = input;
									String json = ApplicationModelCreator.createModelJson(modelArea.getCubes(), modelArea.hasAmbientOcclusion(), modelArea.getParticle());
									java.io.File jsonFile = ApplicationModelCreator.this.saveToDisc(json);
									Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(jsonFile.getParentFile().getAbsolutePath()), null);
									TaskManager.sendTask(new TaskNotificationCopiedJson());
									return true;
								}
							}
							return false;
						}
					});
					openDialog(input);
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
					if (modelArea.getCubes().isEmpty()) {
						Laptop.getSystem().closeApplication(this.getInfo());
					} else {
						Dialog.Confirmation confirmation = new Dialog.Confirmation(I18n.format("dialog.confirmation.save"));
						confirmation.setPositiveListener((mouseX1, mouseY1, mouseButton1) -> {
							saveProjectToFile(modelArea.getCubes(), loadedImages, modelArea.hasAmbientOcclusion(), modelArea.getParticle(), (success1, file1) -> {
								Laptop.getSystem().closeApplication(this.getInfo());
								return true;
							});
						});
						confirmation.setNegativeListener((mouseX1, mouseY1, mouseButton1) -> {
							Laptop.getSystem().closeApplication(this.getInfo());
						});
						openDialog(confirmation);
					}
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

			{
				MenuBarButton moreExamples = new MenuBarButton("Examples", Icons.FILE);
				menuBarMore.add(moreExamples);

				menuBarMore.add(new MenuBarButtonDivider());
			}
		}

		modelArea = new ComponentModelArea(0, menuBar.getHeight(), (int) (mainLayout.width * 0.75), mainLayout.height - menuBar.getHeight(), camera);

		cubeUI = new LayoutCubeUI(modelArea.left + modelArea.width, menuBar.getHeight(), mainLayout.width - modelArea.width, mainLayout.height - menuBar.getHeight());

		mainLayout.addComponent(modelArea);
		mainLayout.addComponent(menuBar);
		mainLayout.addComponent(cubeUI);

		setCurrentLayout(this.mainLayout);

		setAmbientOcclusion(true);
		setParticle(null);
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
			String json = ApplicationModelCreator.createModelJson(this.modelArea.getCubes(), modelArea.hasAmbientOcclusion(), modelArea.getParticle());
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

	public List<NamedBufferedImage> getLoadedImages() {
		return loadedImages;
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

	public void setAmbientOcclusion(boolean ambientOcclusion) {
		modelArea.setAmbientOcclusion(ambientOcclusion);
		cubeUI.setAmbientOcclusion(ambientOcclusion);
	}

	public void setParticle(NamedBufferedImage image) {
		modelArea.setParticle(image);
		cubeUI.setParticle(image);
	}

	private java.io.File saveToDisc(String json) {
		java.io.File folder = new java.io.File(Loader.instance().getConfigDir(), Mod.MOD_ID + "/export/" + ApplicationModelCreator.getJsonSaveName());
		java.io.File jsonFile = new java.io.File(folder, ApplicationModelCreator.getJsonSaveName() + ".json");
		try {
			if (jsonFile.createNewFile()) {
			} else {
			}

			FileOutputStream stream = new FileOutputStream(jsonFile);
			IOUtils.write(json, stream, Charset.defaultCharset());
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonFile;
	}

	public static boolean addImage(ResourceLocation location, BufferedImage image) {
		if (image == null || image.getWidth() != image.getHeight() || Math.sqrt(image.getWidth()) != 4 || Math.sqrt(image.getHeight()) != 4)
			return false;

		List<NamedBufferedImage> images = ApplicationModelCreator.getApp().getLoadedImages();
		boolean imageIsCopy = false;
		int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

		for (int i = 0; i < images.size(); i++) {
			int[] imagePixels = images.get(i).getImage().getRGB(0, 0, images.get(i).getImage().getWidth(), images.get(i).getImage().getHeight(), null, 0, images.get(i).getImage().getWidth());

			if (Arrays.equals(pixels, imagePixels)) {
				imageIsCopy = true;
				break;
			}
		}
		if (!imageIsCopy) {
			images.add(new NamedBufferedImage(image, location));
		}
		return true;
	}

	public static String createModelJson(List<Cube> cubes, boolean ambientOcclusion, NamedBufferedImage particle) {
		Model model = new Model(cubes, ambientOcclusion, particle);
		Gson gson = new GsonBuilder().registerTypeAdapter(Model.class, new Model.Serializer()).setPrettyPrinting().create();
		return gson.toJson(model);
	}

	public static void saveProjectToFile(List<Cube> cubes, List<NamedBufferedImage> textures, boolean ambientOcclusion, NamedBufferedImage particle) {
		saveProjectToFile(cubes, textures, ambientOcclusion, particle, null);
	}

	public static void saveProjectToFile(List<Cube> cubes, List<NamedBufferedImage> textures, boolean ambientOcclusion, NamedBufferedImage particle, ResponseHandler<File> responseHandler) {
		NBTTagCompound data = new NBTTagCompound();
		data.setString("version", MODEL_CREATOR_SAVE_VERSION);

		NBTTagList cubesList = new NBTTagList();
		for (Cube cube : cubes) {
			cubesList.appendTag(cube.serializeNBT());
		}
		data.setTag("cubes", cubesList);

		data.setBoolean("ambientOcclusion", ambientOcclusion);
		if (particle != null) {
			data.setTag("particle", particle.serializeNBT());
		}

		NBTTagList texturesList = new NBTTagList();
		for (NamedBufferedImage image : textures) {
			texturesList.appendTag(image.serializeNBT());
		}
		data.setTag("textures", texturesList);

		Dialog.SaveFile saveDialog = new Dialog.SaveFile(ApplicationModelCreator.getApp(), data);
		saveDialog.setResponseHandler(responseHandler);
		ApplicationModelCreator.getApp().openDialog(saveDialog);
	}

	public static boolean loadProjectFromFile(File file) {
		NBTTagCompound data = file.getData();
		if (data.hasKey("version", Constants.NBT.TAG_STRING)) {
			String version = data.getString("version");
			if (MODEL_CREATOR_SAVE_VERSION.equalsIgnoreCase(version)) {

				ApplicationModelCreator.getApp().removeAllCubes();
				ApplicationModelCreator.getApp().loadedImages.clear();
				if (data.hasKey("cubes", Constants.NBT.TAG_LIST)) {
					NBTTagList list = data.getTagList("cubes", Constants.NBT.TAG_COMPOUND);
					for (NBTBase base : list) {
						if (base instanceof NBTTagCompound) {
							ApplicationModelCreator.getApp().addCube(Cube.fromTag((NBTTagCompound) base));
						}
					}
				}

				ApplicationModelCreator.getApp().setAmbientOcclusion(data.getBoolean("ambientOcclusion"));
				if (data.hasKey("particle", Constants.NBT.TAG_COMPOUND)) {
					ApplicationModelCreator.getApp().setParticle(NamedBufferedImage.fromTag(data.getCompoundTag("particle")));
				}

				NBTTagList textures = data.getTagList("textures", Constants.NBT.TAG_COMPOUND);
				for (NBTBase base : textures) {
					if (base instanceof NBTTagCompound) {
						NamedBufferedImage image = NamedBufferedImage.fromTag((NBTTagCompound) base);
						ApplicationModelCreator.getApp().addImage(image.getLocation(), image.getImage());
					}
				}

				return true;
			} else {
				if (version.equalsIgnoreCase(ModelCreatorFileConverter.MODEL_CREATOR_SAVE_VERSION_10)) {
					Dialog.Confirmation confirm = new Dialog.Confirmation(I18n.format("dialog.project.can_convert", version, ModelCreatorFileConverter.MODEL_CREATOR_SAVE_VERSION_11));
					confirm.setPositiveListener((mouseX, mouseY, mouseButton) -> {
						file.setData(ModelCreatorFileConverter.convert10To11(file.getData()));
						loadProjectFromFile(file);
					});
					ApplicationModelCreator.getApp().openDialog(confirm);
					return false;
				}

				if (version.equalsIgnoreCase(ModelCreatorFileConverter.MODEL_CREATOR_SAVE_VERSION_11)) {
					Dialog.Confirmation confirm = new Dialog.Confirmation(I18n.format("dialog.project.can_convert", version, ModelCreatorFileConverter.MODEL_CREATOR_SAVE_VERSION_12));
					confirm.setPositiveListener((mouseX, mouseY, mouseButton) -> {
						file.setData(ModelCreatorFileConverter.convert11To12(file.getData()));
						loadProjectFromFile(file);
					});
					ApplicationModelCreator.getApp().openDialog(confirm);
					return false;
				}

				openErrorDialog(I18n.format("dialog.project.wrong_version", version));
				return false;
			}
		} else {
			openErrorDialog(I18n.format("dialog.project.wrong_version", "Unknown"));
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

	public static String getJsonSaveName() {
		return jsonName;
	}
}