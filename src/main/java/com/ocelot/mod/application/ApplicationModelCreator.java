package com.ocelot.mod.application;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.lwjgl.util.vector.Vector3f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mrcrayfish.device.MrCrayfishDeviceMod;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Dialog.ResponseHandler;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.Layout.Background;
import com.mrcrayfish.device.api.app.Notification;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.ocelot.api.geometry.Camera;
import com.ocelot.api.geometry.Cube;
import com.ocelot.api.geometry.Face;
import com.ocelot.api.geometry.Model;
import com.ocelot.api.geometry.ModelData;
import com.ocelot.api.utils.GuiUtils;
import com.ocelot.api.utils.Lib;
import com.ocelot.api.utils.NamedBufferedImage;
import com.ocelot.api.utils.TextureUtils;
import com.ocelot.mod.ModelCreator;
import com.ocelot.mod.Usernames;
import com.ocelot.mod.application.component.ComponentModelArea;
import com.ocelot.mod.application.component.MenuBar;
import com.ocelot.mod.application.component.MenuBarItem;
import com.ocelot.mod.application.component.MenuBarItemButton;
import com.ocelot.mod.application.component.MenuBarItemDivider;
import com.ocelot.mod.application.layout.LayoutCubeUI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;

/**
 * The main application class for the Minecraft Model Creator.
 * 
 * @author Ocelot5836
 */
public class ApplicationModelCreator extends Application {

	public static final String MODEL_CREATOR_SAVE_VERSION = ApplicationModelCreatorSaveFormatter.MODEL_CREATOR_SAVE_VERSION_11;

	private static ApplicationModelCreator app;
	private static boolean enableTransparency;
	private static boolean fastRender;

	private List<NamedBufferedImage> loadedImages;
	private Camera camera;

	private Layout mainLayout;
	private MenuBar menuBar;
	private ComponentModelArea modelArea;
	private LayoutCubeUI cubeUI;

	private Cube selectedCube;

	@Override
	public void init(@Nullable NBTTagCompound intent) {
		app = this;
		enableTransparency = false;
		fastRender = false;

		loadedImages = new ArrayList<NamedBufferedImage>();
		camera = new Camera(new Vector3f(-5 * 8, -12 * 8, -8 * 8));

		mainLayout = new Layout(362, 164);
		mainLayout.setBackground(new Background() {
			@Override
			public void render(Gui gui, Minecraft mc, int x, int y, int width, int height, int mouseX, int mouseY, boolean windowActive) {
				gui.drawRect(x, y, x + width - (int) (width * (1 - 0.75)) - 1, y + height, 0xffeaeaed);
				gui.drawRect(x + width - (int) (width * (1 - 0.75)) - 1, y, x + width, y + height, 0xffdddde4);
				mc.fontRenderer.drawString(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".version", ModelCreator.VERSION, MODEL_CREATOR_SAVE_VERSION), x + 2, y + 12, 0xffdddde4, false);
				if (fastRender) {
					mc.fontRenderer.drawString(TextFormatting.RED + I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".fastRender"), x + 2, y + 22, 0xffffffff, false);
				}
			}
		});

		menuBar = new MenuBar(0, 0, mainLayout.width, 10);
		menuBar.setColor(0xffdddde4);
		menuBar.setBorderColor(0xffdddde4);

		{
			MenuBarItem menuBarFile = new MenuBarItem(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".file"));
			menuBarFile.setTextPadding(6);
			menuBarFile.setTextColor(0xff000000);
			menuBarFile.setColor(0x00ffffff);
			menuBarFile.setBorderColor(0x00ffffff);
			menuBarFile.setHighlightColor(0xffc9c9c9);
			menuBarFile.setHighlightBorderColor(0xffc9c9c9);

			{
				MenuBarItemButton fileNew = new MenuBarItemButton(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".new"), Icons.NEW_FILE);
				fileNew.setTooltip(TextFormatting.GRAY + I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".tooltip.new"));
				fileNew.setClickListener((mouseX, mouseY, mouseButton) -> {
					List<Cube> cubes = this.modelArea.getCubes();
					if (cubes.isEmpty()) {
						clearProject();
					} else {
						Dialog.Confirmation confirmation = new Dialog.Confirmation(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".dialog.confirmation.save"));
						confirmation.setPositiveListener((mouseX1, mouseY1, mouseButton1) -> {
							saveProjectToFile(cubes, this.modelArea.hasAmbientOcclusion(), this.modelArea.getParticle());
						});
						confirmation.setNegativeListener((mouseX2, mouseY2, mouseButton2) -> {
							clearProject();
						});
						openDialog(confirmation);
					}
				});
				menuBarFile.add(fileNew);

				menuBarFile.add(new MenuBarItemDivider());

				MenuBarItemButton fileLoadProject = new MenuBarItemButton(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".load"), Icons.FOLDER);
				fileLoadProject.setTooltip(TextFormatting.GRAY + I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".tooltip.load"));
				fileLoadProject.setClickListener((mouseX, mouseY, mouseButton) -> {
					Dialog.OpenFile openDialog = new Dialog.OpenFile(this);
					openDialog.setFilter(this);
					openDialog.setResponseHandler((success, file) -> {
						if (success) {
							if (this.modelArea.getCubes().isEmpty()) {
								loadProjectFromFile(file);
							} else {
								Dialog.Confirmation confirmation = new Dialog.Confirmation(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".dialog.confirmation.save"));
								confirmation.setPositiveListener((mouseX1, mouseY1, mouseButton1) -> {
									saveProjectToFile(this.modelArea.getCubes(), this.modelArea.hasAmbientOcclusion(), this.modelArea.getParticle(), (success1, file1) -> {
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
							openErrorDialog(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".dialog.confirmation.fail_file_open", file != null ? file.getName() : "Null"));
						}
						return success;
					});
					openDialog(openDialog);
				});
				menuBarFile.add(fileLoadProject);
				MenuBarItemButton fileSaveProject = new MenuBarItemButton(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".save"), Icons.SAVE);
				fileSaveProject.setTooltip(TextFormatting.GRAY + I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".tooltip.save"));
				fileSaveProject.setClickListener((mouseX, mouseY, mouseButton) -> {
					saveProjectToFile(this.modelArea.getCubes(), this.modelArea.hasAmbientOcclusion(), this.modelArea.getParticle());
				});
				menuBarFile.add(fileSaveProject);

				MenuBarItemButton fileCopyProject = new MenuBarItemButton(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".copy"), Icons.FILE);
				fileCopyProject.setTooltip(TextFormatting.GRAY + I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".tooltip.copy"));
				fileCopyProject.setClickListener((mouseX, mouseY, mouseButton) -> {

					NBTTagCompound data = new NBTTagCompound();
					{
						data.setString("version", MODEL_CREATOR_SAVE_VERSION);

						NBTTagList cubesList = new NBTTagList();
						for (Cube cube : modelArea.getCubes()) {
							cubesList.appendTag(cube.serializeNBT());
						}
						data.setTag("cubes", cubesList);

						data.setBoolean("ambientOcclusion", modelArea.hasAmbientOcclusion());
						if (modelArea.getParticle() != null) {
							data.setTag("particle", modelArea.getParticle().serializeNBT());
						}

						NBTTagList texturesList = new NBTTagList();
						for (NamedBufferedImage image : loadedImages) {
							texturesList.appendTag(image.serializeNBT());
						}
						data.setTag("textures", texturesList);
					}

					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(String.valueOf(data)), null);
					MrCrayfishDeviceMod.proxy.showNotification(new Notification(Icons.COPY, TextFormatting.BOLD + I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".copy.title"), I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".copy.desc")).toTag());
				});
				menuBarFile.add(fileCopyProject);

				menuBarFile.add(new MenuBarItemDivider());

				MenuBarItemButton fileImportJson = new MenuBarItemButton(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".import"), Icons.IMPORT);
				fileImportJson.setTooltip(TextFormatting.GRAY + I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".tooltip.not_added"), 150);
				fileImportJson.setEnabled(false);
				// fileImportJson.setClickListener((mouseX, mouseY, mouseButton) -> {
				// });
				menuBarFile.add(fileImportJson);

				MenuBarItemButton fileExportJson = new MenuBarItemButton(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".export"), Icons.EXPORT);
				fileExportJson.setTooltip(TextFormatting.GRAY + I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".tooltip.export"));
				fileExportJson.setClickListener((mouseX, mouseY, mouseButton) -> {
					Dialog.Input input = new Dialog.Input(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".input_json"));
					input.setResponseHandler(new ResponseHandler<String>() {
						@Override
						public boolean onResponse(boolean success, String input) {
							if (success) {
								if (!StringUtils.isNullOrEmpty(input)) {
									String json = ApplicationModelCreator.createModelJson(modelArea.getCubes(), input, modelArea.hasAmbientOcclusion(), modelArea.getParticle());
									java.io.File jsonFile = ApplicationModelCreator.this.saveToDisc(json, input);

									try {
										Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(jsonFile.getParentFile().getCanonicalPath()), null);
									} catch (Exception e) {
										e.printStackTrace();
									}

									MrCrayfishDeviceMod.proxy.showNotification(new Notification(Icons.EXPORT, TextFormatting.BOLD + I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".export.title"), I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".export.desc")).toTag());
									return true;
								}
							}
							return false;
						}
					});
					openDialog(input);
				});
				menuBarFile.add(fileExportJson);

				menuBarFile.add(new MenuBarItemDivider());

				MenuBarItemButton fileSetTexturePath = new MenuBarItemButton(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".set_tex_path"), Icons.PICTURE);
				fileSetTexturePath.setTooltip(TextFormatting.GRAY + I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".tooltip.not_added"), 150);
				fileSetTexturePath.setEnabled(false);
				// fileSetTexturePath.setClickListener((mouseX, mouseY, mouseButton) -> {
				// });
				menuBarFile.add(fileSetTexturePath);

				menuBarFile.add(new MenuBarItemDivider());

				MenuBarItemButton fileExit = new MenuBarItemButton(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".exit"), Icons.POWER_OFF);
				fileExit.setTooltip(TextFormatting.GRAY + I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".tooltip.broken"), 150);
				fileExit.setEnabled(false);
				// fileExit.setTooltip(TextFormatting.GRAY + I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".tooltip.exit"));
				// fileExit.setClickListener((mouseX, mouseY, mouseButton) -> {
				// if (modelArea.getCubes().isEmpty()) {
				// Laptop.getSystem().closeApplication(this.getInfo());
				// } else {
				// Dialog.Confirmation confirmation = new Dialog.Confirmation(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".dialog.confirmation.save"));
				// confirmation.setPositiveListener((mouseX1, mouseY1, mouseButton1) -> {
				// saveProjectToFile(modelArea.getCubes(), loadedImages, modelArea.hasAmbientOcclusion(), modelArea.getParticle(), (success1, file1) -> {
				// Laptop.getSystem().closeApplication(this.getInfo());
				// return true;
				// });
				// });
				// confirmation.setNegativeListener((mouseX1, mouseY1, mouseButton1) -> {
				// Laptop.getSystem().closeApplication(this.getInfo());
				// });
				// openDialog(confirmation);
				// }
				// });
				menuBarFile.add(fileExit);
			}

			menuBar.add(menuBarFile);

			MenuBarItem menuBarOptions = new MenuBarItem(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".options"));
			menuBarOptions.setTextPadding(6);
			menuBarOptions.setTextColor(0xff000000);
			menuBarOptions.setColor(0x00ffffff);
			menuBarOptions.setBorderColor(0x00ffffff);
			menuBarOptions.setHighlightColor(0xffc9c9c9);
			menuBarOptions.setHighlightBorderColor(0xffc9c9c9);
			menuBar.add(menuBarOptions);

			{
				MenuBarItemButton optionsToggleTransparency = new MenuBarItemButton(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".transparency.toggle"), Icons.ARROW_RIGHT) {
					@Override
					public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, int buttonsWidth, int buttonsHeight, float partialTicks) {
						if (this.isVisible()) {
							this.setHovered(GuiUtils.isMouseInside(x, y, buttonsWidth, this.getHeight(), mouseX, mouseY));

							int contentWidth = 13 + Lib.getDefaultTextWidth(this.getText());
							int contentX = (int) Math.ceil((this.getWidth() - contentWidth) / 2.0);

							TextureUtils.bindTexture(ModelCreator.MOD_ID, "textures/app/icons.png");
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
				optionsToggleTransparency.setTooltip(TextFormatting.GRAY + I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".tooltip.transparency.toggle"), 150);
				optionsToggleTransparency.setClickListener((mouseX, mouseY, mouseButton) -> {
					enableTransparency = !enableTransparency;
				});
				menuBarOptions.add(optionsToggleTransparency);

				MenuBarItemButton optionToggleFastRender = new MenuBarItemButton(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".fast_render.toggle"), Icons.WRENCH);
				optionToggleFastRender.setTooltip(TextFormatting.GRAY + I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".tooltip.fast_render.toggle"), 150);
				optionToggleFastRender.setClickListener((mouseX, mouseY, mouseButton) -> {
					fastRender = !fastRender;
					this.markDirty();
				});
				menuBarOptions.add(optionToggleFastRender);
			}

			MenuBarItem menuBarScreeenshot = new MenuBarItem(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".screenshot"));
			menuBarScreeenshot.setTextPadding(6);
			menuBarScreeenshot.setTextColor(0xff000000);
			menuBarScreeenshot.setColor(0x00ffffff);
			menuBarScreeenshot.setBorderColor(0x00ffffff);
			menuBarScreeenshot.setHighlightColor(0xffc9c9c9);
			menuBarScreeenshot.setHighlightBorderColor(0xffc9c9c9);
			menuBarScreeenshot.setVisible(false);
			menuBar.add(menuBarScreeenshot);

			MenuBarItem menuBarMore = new MenuBarItem(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".more"));
			menuBarMore.setTextPadding(6);
			menuBarMore.setTextColor(0xff000000);
			menuBarMore.setColor(0x00ffffff);
			menuBarMore.setBorderColor(0x00ffffff);
			menuBarMore.setHighlightColor(0xffc9c9c9);
			menuBarMore.setHighlightBorderColor(0xffc9c9c9);
			menuBar.add(menuBarMore);

			{
				MenuBarItemButton moreExamples = new MenuBarItemButton(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".examples"), Icons.FILE);
				moreExamples.setTooltip(TextFormatting.GRAY + I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".tooltip.not_added"), 150);
				moreExamples.setClickListener((mouseX, mouseY, mouseButton) -> {
					Dialog.Message dialog = new Dialog.Message("Visit\n" + TextFormatting.DARK_BLUE + "modelcreator.app/pages/models" + TextFormatting.RESET + "\nfor examples. (This will be removed in a future) update");
					this.openDialog(dialog);
				});
				menuBarMore.add(moreExamples);

				menuBarMore.add(new MenuBarItemDivider());

				MenuBarItemButton moreGithub = new MenuBarItemButton(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".github")) {
					@Override
					public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean windowActive, int buttonsWidth, int buttonsHeight, float partialTicks) {
						if (this.isVisible()) {
							this.setHovered(GuiUtils.isMouseInside(x, y, buttonsWidth, this.getHeight(), mouseX, mouseY));

							int contentWidth = 13 + Lib.getDefaultTextWidth(this.getText());
							int contentX = (int) Math.ceil((this.getWidth() - contentWidth) / 2.0);

							TextureUtils.bindTexture(ModelCreator.MOD_ID, "textures/app/icons.png");
							GlStateManager.pushMatrix();
							GlStateManager.translate(0.5, 0.5, 0);
							RenderUtil.drawRectWithTexture(x, y + this.getHeight() / 2 - 6, 40, 0, 10, 10, 20, 20, 200, 200);
							GlStateManager.popMatrix();

							int textY = (this.getHeight() - mc.fontRenderer.FONT_HEIGHT) / 2;
							int textOffsetX = 13;
							int textColor = !this.isEnabled() ? this.getDisabledTextColor() : (this.isHovered() ? this.getHighlightedTextColor() : this.getTextColor());
							mc.fontRenderer.drawString(this.getText(), x + textOffsetX, y + textY, textColor, false);
						}
					}
				};
				moreGithub.setTooltip(TextFormatting.GRAY + I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".tooltip.github"), 150);
				moreGithub.setClickListener((mouseX, mouseY, mouseButton) -> {
					try {
						URI githubURL = new URI("https://github.com/Ocelot5836/ModelCreatorApp");
						Desktop.getDesktop().browse(githubURL);
					} catch (Exception e) {
						openErrorDialog(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".dialog.project.open.fail", "https://github.com/Ocelot5836/ModelCreatorApp"));
						e.printStackTrace();
					}
				});
				menuBarMore.add(moreGithub);

				MenuBarItemButton moreSubmitABug = new MenuBarItemButton(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".submit_bug"), Icons.ERROR);
				moreSubmitABug.setTooltip(TextFormatting.GRAY + I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".tooltip.submit_bug"), 150);
				moreSubmitABug.setClickListener((mouseX, mouseY, mouseButton) -> {
					try {
						URI githubURL = new URI("https://github.com/Ocelot5836/ModelCreatorApp/issues/new");
						Desktop.getDesktop().browse(githubURL);
					} catch (Exception e) {
						openErrorDialog(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".dialog.project.open.fail", "https://github.com/Ocelot5836/ModelCreatorApp/issues/new"));
						e.printStackTrace();
					}
				});
				menuBarMore.add(moreSubmitABug);
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

		openMessageDialog(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".welcome.title"), I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".welcome.info"));
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
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks) {
		super.render(laptop, mc, x, y, mouseX, mouseY, active, partialTicks);
	}

	@Override
	public void load(NBTTagCompound nbt) {
		fastRender = nbt.getBoolean("fastRender");
	}

	@Override
	public void save(NBTTagCompound nbt) {
		nbt.setBoolean("fastRender", fastRender);
	}

	@Override
	public void onClose() {
		clearProject();
		this.modelArea.dispose();
		this.mainLayout.clear();
		this.cubeUI.clear();
		this.selectedCube = null;
	}

	public List<NamedBufferedImage> getLoadedImages() {
		return loadedImages;
	}

	public void updateCubes(List<Cube> cubes) {
		this.modelArea.updateCubes(cubes);
		this.cubeUI.updateCubes(cubes);
	}

	public void addCube(float x, float y, float z, float sizeX, float sizeY, float sizeZ, float rotationX, float rotationY, float rotationZ) {
		this.modelArea.addCube(x, y, z, sizeX, sizeY, sizeZ, rotationX, rotationY, rotationZ);
		this.cubeUI.updateCubes(this.modelArea.getCubes());
	}

	public void addCube(Cube cube) {
		this.modelArea.addCube(cube);
		this.cubeUI.updateCubes(this.modelArea.getCubes());
	}

	public void removeAllCubes() {
		this.modelArea.clear();
		this.cubeUI.updateCubes(this.modelArea.getCubes());
	}

	public void removeCube(int index) {
		this.modelArea.removeCube(index);
		this.cubeUI.updateCubes(this.modelArea.getCubes());
	}

	public void setAmbientOcclusion(boolean ambientOcclusion) {
		this.modelArea.setAmbientOcclusion(ambientOcclusion);
		this.cubeUI.setAmbientOcclusion(ambientOcclusion);
	}

	public void setParticle(NamedBufferedImage image) {
		this.modelArea.setParticle(image);
		this.cubeUI.setParticle(image);
	}

	private java.io.File saveToDisc(String json, String jsonName) {
		java.io.File folder = new java.io.File(Minecraft.getMinecraft().mcDataDir, ModelCreator.MOD_ID + "-export/" + jsonName);
		java.io.File jsonFile = new java.io.File(folder, jsonName + ".json");
		try {
			if (!folder.exists()) {
				folder.mkdirs();
			}
			jsonFile.createNewFile();

			FileOutputStream stream = new FileOutputStream(jsonFile);
			IOUtils.write(json, stream, Charset.defaultCharset());
			stream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonFile;
	}

	public static void clearProject() {
		ApplicationModelCreator.getApp().removeAllCubes();
		ApplicationModelCreator.getApp().setParticle(null);
		ApplicationModelCreator.getApp().loadedImages.clear();
		Face.clearCache();
	}

	public static boolean addImage(ResourceLocation location, BufferedImage image) {
		if (image == null || image.getWidth() != image.getHeight())
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

	public static void loadModelJson(String json) {
		List<Cube> cubes = ApplicationModelCreator.getApp().modelArea.getCubes();

		if (cubes.isEmpty()) {
			clearProject();
		} else {
			Dialog.Confirmation confirmation = new Dialog.Confirmation(I18n.format("app." + ApplicationModelCreator.getApp().getInfo().getFormattedId() + ".dialog.confirmation.save"));
			confirmation.setPositiveListener((mouseX1, mouseY1, mouseButton1) -> {
				saveProjectToFile(cubes, ApplicationModelCreator.getApp().modelArea.hasAmbientOcclusion(), ApplicationModelCreator.getApp().modelArea.getParticle());
			});
			confirmation.setNegativeListener((mouseX2, mouseY2, mouseButton2) -> {
				clearProject();
			});
			ApplicationModelCreator.getApp().openDialog(confirmation);
		}

		Gson gson = new GsonBuilder().registerTypeAdapter(Model.class, new Model.Deserializer()).create();
		Model model = gson.fromJson(json, Model.class);
	}

	public static String createModelJson(List<Cube> cubes, String jsonName, boolean ambientOcclusion, NamedBufferedImage particle) {
		Model model = new Model(jsonName, new ModelData(cubes, ambientOcclusion, particle));
		Gson gson = new GsonBuilder().registerTypeAdapter(Model.class, new Model.Serializer()).setPrettyPrinting().create();
		return "{\n  \"_comment\": \"" + I18n.format("app.mca.mc.json.comment", Usernames.getOcelot5836Username(), "https://mrcrayfish.com/tools?id=mc") + "\"," + gson.toJson(model).substring(1);
	}

	public static void saveProjectToFile(List<Cube> cubes, boolean ambientOcclusion, NamedBufferedImage particle) {
		saveProjectToFile(cubes, ambientOcclusion, particle, null);
	}

	public static void saveProjectToFile(List<Cube> cubes, boolean ambientOcclusion, NamedBufferedImage particle, @Nullable ResponseHandler<File> responseHandler) {
		NBTTagCompound data = new NBTTagCompound();

		data.setString("version", MODEL_CREATOR_SAVE_VERSION);
		data.setTag("modelData", new ModelData(cubes, ambientOcclusion, particle).serializeNBT());

		Dialog.SaveFile saveDialog = new Dialog.SaveFile(ApplicationModelCreator.getApp(), data);
		saveDialog.setResponseHandler(responseHandler);
		ApplicationModelCreator.getApp().openDialog(saveDialog);
	}

	public static boolean loadProjectFromFile(File file) {
		NBTTagCompound data = file.getData();

		if (data.hasKey("version", Constants.NBT.TAG_STRING)) {
			String version = data.getString("version");

			if (MODEL_CREATOR_SAVE_VERSION.equalsIgnoreCase(version)) {
				loadModelData(new ModelData(data.getCompoundTag("modelData")));
				return true;
			} else if (ApplicationModelCreatorSaveFormatter.MODEL_CREATOR_SAVE_VERSION_10.equals(version)) {
				NBTTagCompound newData = ApplicationModelCreatorSaveFormatter.convert10to11(data);
				loadModelData(new ModelData(newData.getCompoundTag("modelData")));

				openConfirmation(I18n.format("app.omca.mc.dialog.convert"), I18n.format("app.omca.mc.dialog.project.can_convert", version, ApplicationModelCreatorSaveFormatter.MODEL_CREATOR_SAVE_VERSION_11), (mouseX, mouseY, mouseButton) -> {
					file.setData(newData);
				}, null);

				return true;
			} else {
				openErrorDialog(I18n.format("app.mca.mc.dialog.project.wrong_version", version));
				return false;
			}
		} else {
			openErrorDialog(I18n.format("app.mca.mc.dialog.project.wrong_version", "Unknown"));
			return false;
		}
	}

	private static void loadModelData(ModelData data) {
		ApplicationModelCreator.getApp().removeAllCubes();
		ApplicationModelCreator.getApp().loadedImages.clear();

		NamedBufferedImage[] textures = data.getTextures();
		for (NamedBufferedImage texture : textures) {
			ApplicationModelCreator.getApp().addImage(texture.getLocation(), texture.getImage());
		}

		Cube[] cubes = data.getCubes();
		for (Cube cube : cubes) {
			ApplicationModelCreator.getApp().addCube(cube);
		}

		ApplicationModelCreator.getApp().setAmbientOcclusion(data.isAmbientOcclusion());

		if (data.getParticle() != null) {
			ApplicationModelCreator.getApp().setParticle(data.getParticle());
		}
	}

	public static void openErrorDialog(String content) {
		openMessageDialog(I18n.format("app.mca.mc.dialog.error"), content);
	}

	public static void openMessageDialog(String title, String content) {
		Dialog.Message message = new Dialog.Message(content);
		message.setTitle(title);
		ApplicationModelCreator.getApp().openDialog(message);
	}

	public static void openConfirmation(String title, String content, @Nullable ClickListener positiveListener, @Nullable ClickListener negativeListener) {
		Dialog.Confirmation confirmation = new Dialog.Confirmation(content);
		confirmation.setTitle(title);
		confirmation.setPositiveListener(positiveListener);
		confirmation.setNegativeListener(negativeListener);
		ApplicationModelCreator.getApp().openDialog(confirmation);
	}

	public static ApplicationModelCreator getApp() {
		return app;
	}

	public static boolean isTransparencyEnabled() {
		return enableTransparency;
	}

	public static boolean isFastRender() {
		return fastRender;
	}
}