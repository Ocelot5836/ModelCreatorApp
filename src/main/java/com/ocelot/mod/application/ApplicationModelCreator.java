package com.ocelot.mod.application;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.Layout.Background;
import com.ocelot.mod.application.component.ComponentModelArea;
import com.ocelot.mod.application.component.Cube;
import com.ocelot.mod.application.component.MenuBar;
import com.ocelot.mod.application.component.MenuBarButton;
import com.ocelot.mod.application.component.MenuBarItem;
import com.ocelot.mod.application.component.Model;
import com.ocelot.mod.application.layout.LayoutCubeUI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;

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

	private static ApplicationModelCreator app;
	private static boolean running;

	private Camera camera;

	private Layout mainLayout;
	private MenuBar menuBar;
	private ComponentModelArea modelArea;
	private LayoutCubeUI cubeUI;

	private Cube selectedCube;

	@Override
	public void init(NBTTagCompound intent) {
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
					System.out.println("s1");
				});
				menuBarFile.add(fileNew);
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

			MenuBarItem menuBarScreeenshot = new MenuBarItem("Screeenshot");
			menuBarScreeenshot.setTextPadding(6);
			menuBarScreeenshot.setTextColor(0xff000000);
			menuBarScreeenshot.setColor(0x00ffffff);
			menuBarScreeenshot.setBorderColor(0x00ffffff);
			menuBarScreeenshot.setHighlightColor(0xffc9c9c9);
			menuBarScreeenshot.setHighlightBorderColor(0xffc9c9c9);
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
		if (code == Keyboard.KEY_Y) {
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

	public void removeCube(int index) {
		modelArea.removeCube(index);
		cubeUI.updateCubes(modelArea.getCubes());
	}

	public static String createModelJson(List<Cube> cubes) {
		Model model = new Model(cubes);
		Gson gson = new GsonBuilder().registerTypeAdapter(Model.class, new Model.Serializer()).setPrettyPrinting().create();
		return gson.toJson(model);
	}

	public static ApplicationModelCreator getApp() {
		return app;
	}

	public static boolean isRunning() {
		return running;
	}
}