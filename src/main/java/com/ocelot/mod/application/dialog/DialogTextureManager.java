package com.ocelot.mod.application.dialog;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;
import com.mrcrayfish.device.Reference;
import com.mrcrayfish.device.api.ApplicationManager;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.ScrollableLayout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.object.AppInfo;
import com.ocelot.api.utils.TextureUtils;
import com.ocelot.mod.application.ApplicationModelCreator;
import com.ocelot.mod.lib.Lib;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.util.Constants;

public class DialogTextureManager extends Dialog {

	private static final Map<BufferedImage, ResourceLocation> TEXTURE_LOCATIONS = Maps.<BufferedImage, ResourceLocation>newHashMap();

	private CloseListener closeListener;
	private Layout layoutMain;

	private Button buttonApply;
	private Button buttonImport;
	private Button buttonClose;

	private ScrollableLayout textures;

	private BufferedImage selectedImage;
	private Image selectedImageComponent;

	@Override
	public void init(@Nullable NBTTagCompound intent) {
		this.setTitle("Texture Manager");
		layoutMain = new Layout(150, 100);

		buttonApply = new Button(0, layoutMain.height - 20, layoutMain.width / 3, 20, "Apply");
		buttonApply.setClickListener((mouseX, mouseY, mouseButton) -> {
			if (selectedImage != null) {
				this.close();
			}
		});
		layoutMain.addComponent(buttonApply);

		buttonImport = new Button(buttonApply.left + layoutMain.width / 3, layoutMain.height - 20, layoutMain.width / 3, 20, "Import");

		buttonImport.setClickListener((mouseX, mouseY, mouseButton) -> {
			Dialog.Input inputTextureLocation = new Dialog.Input("Please Input the texture location you wish to use.") {
				@Override
				public void init(@Nullable NBTTagCompound intent) {
					super.init(intent);
					this.defaultLayout.height -= 5;
					super.init(intent);

					Button buttonOpenPixelPainterFile = new Button(5, this.getHeight() - 40, this.getWidth() - 10, 16, "Open Pixel Painter File", Icons.PICTURE);
					buttonOpenPixelPainterFile.setClickListener((mouseX, mouseY, mouseButton) -> {
						Dialog.OpenFile openFile = new Dialog.OpenFile(ApplicationModelCreator.getApp());
						openFile.setFilter((file) -> {
							AppInfo pixelPainterId = ApplicationManager.getApplication(Reference.MOD_ID + ".pixel_painter");
							return file.getOpeningApp() == null ? true : file.getOpeningApp().equals(pixelPainterId.getFormattedId());
						});
						openFile.setResponseHandler((success, file) -> {
							AppInfo pixelPainterId = ApplicationManager.getApplication(Reference.MOD_ID + ".pixel_painter");
							if (!StringUtils.isNullOrEmpty(file.getOpeningApp())) {
								if (file.getOpeningApp().equals(pixelPainterId.getFormattedId())) {
									NBTTagCompound data = file.getData();
									if (data.hasKey("Pixels", Constants.NBT.TAG_INT_ARRAY) && data.hasKey("Resolution", Constants.NBT.TAG_INT)) {
										int[] pixels = data.getIntArray("Pixels");
										int resolution = data.getInteger("Resolution");
										BufferedImage image = new BufferedImage(resolution, resolution, BufferedImage.TYPE_INT_ARGB);
										for (int y = 0; y < resolution; y++) {
											for (int x = 0; x < resolution; x++) {
												image.setRGB(x, y, pixels[x + y * resolution]);
											}
										}

										return addImage(image);
									}
								}
							}
							return false;
						});
						openDialog(openFile);
					});
					super.addComponent(buttonOpenPixelPainterFile);
				}
			};
			inputTextureLocation.setResponseHandler((success, input) -> {
				if (StringUtils.isNullOrEmpty(input))
					return false;
				String[] splitInput = ResourceLocation.splitObjectName(input);
				ResourceLocation location;

				if (splitInput.length > 1) {
					location = new ResourceLocation(splitInput[0], "textures/" + splitInput[1] + ".png");
				} else {
					location = new ResourceLocation("textures/" + splitInput[0] + ".png");
				}

				try {
					addImage(Lib.loadImageE(location));
					return true;
				} catch (IOException e) {
					return false;
				}
			});
			this.openDialog(inputTextureLocation);
		});

		layoutMain.addComponent(buttonImport);

		buttonClose = new Button(buttonImport.left + layoutMain.width / 3, layoutMain.height - 20, layoutMain.width / 3, 20, "Close");
		buttonClose.setClickListener((mouseX, mouseY, mouseButton) -> {
			this.close();
		});
		layoutMain.addComponent(buttonClose);

		List<BufferedImage> images = ApplicationModelCreator.getApp().getLoadedImages();
		int maxCols = 2;

		textures = new ScrollableLayout(0, 0, layoutMain.width, Math.max(layoutMain.height - 20, (int) Math.ceil((float) images.size() / 2f) * (layoutMain.width / maxCols)), layoutMain.height - 20) {
			@Override
			public void init() {
				this.clear();
				for (int i = 0; i < images.size(); i++) {
					BufferedImage image = images.get(i);
					int x = (i % maxCols) * (layoutMain.width / maxCols);
					int y = (i / maxCols) * (layoutMain.width / maxCols);
					final ResourceLocation location = TextureUtils.createBufferedImageTexture(image);

					TEXTURE_LOCATIONS.put(image, location);
					CustomImage c = new CustomImage(DialogTextureManager.this, x, y, layoutMain.width / maxCols, layoutMain.width / maxCols, 0, 0, image.getWidth(), image.getHeight(), image.getWidth(), image.getHeight(), location) {
						@Override
						protected void handleMouseClick(int mouseX, int mouseY, int mouseButton) {
							if (this.hovered) {
								for (BufferedImage image : TEXTURE_LOCATIONS.keySet()) {
									ResourceLocation tempLocation = TEXTURE_LOCATIONS.get(image);
									if (tempLocation == location) {
										selectedImage = image;
										selectedImageComponent = this;
										return;
									}
								}
							}
						}
					};
					textures.addComponent(c);
				}
			}

			@Override
			public void init(Layout layout) {
				this.init();
			}
		};

		layoutMain.addComponent(textures);

		this.setLayout(layoutMain);
	}

	@Override
	public void onClose() {
		super.onClose();
		if (closeListener != null) {
			closeListener.onClose();
		}
		TEXTURE_LOCATIONS.clear();
	}

	private boolean addImage(BufferedImage image) {		
		if (image == null || image.getWidth() != image.getHeight() || Math.sqrt(image.getWidth()) != 4 || Math.sqrt(image.getHeight()) != 4)
			return false;

		List<BufferedImage> images = ApplicationModelCreator.getApp().getLoadedImages();
		boolean imageIsCopy = false;
		int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

		for (int i = 0; i < images.size(); i++) {
			int[] imagePixels = images.get(i).getRGB(0, 0, images.get(i).getWidth(), images.get(i).getHeight(), null, 0, images.get(i).getWidth());

			if (Arrays.equals(pixels, imagePixels)) {
				imageIsCopy = true;
				break;
			}
		}
		if (!imageIsCopy) {
			images.add(image);
			textures.init();
		}
		return true;
	}

	public BufferedImage getSelectedImage() {
		return selectedImage;
	}

	public Image getSelectedImageComponent() {
		return selectedImageComponent;
	}

	public void setCloseListener(CloseListener closeListener) {
		this.closeListener = closeListener;
	}
}