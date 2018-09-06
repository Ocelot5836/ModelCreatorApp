package com.ocelot.mod.application.dialog;

import java.awt.Color;

import javax.annotation.Nullable;

import com.mrcrayfish.device.api.app.Application;
import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Spinner;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.programs.gitweb.component.GitWebFrame;
import com.mrcrayfish.device.programs.gitweb.layout.TextLayout;
import com.mrcrayfish.device.programs.system.layout.StandardLayout;

import net.minecraft.client.gui.Gui;
import net.minecraft.nbt.NBTTagCompound;

public class DialogGitweb extends Dialog {

	private Layout layoutBrowser;

	private GitWebFrame webFrame;

	private Application app;
	private String site;

	public DialogGitweb(Application app, String site) {
		this.app = app;
		this.site = site;
	}

	@Override
	public void init(@Nullable NBTTagCompound intent) {
		layoutBrowser = new StandardLayout("GitWeb", 362, 240, app, null);
		layoutBrowser.setBackground((gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
			Color color = new Color(Laptop.getSystem().getSettings().getColorScheme().getItemBackgroundColor());
			Gui.drawRect(x, y + 21, x + width, y + 164, Color.GRAY.getRGB());
		});

		webFrame = new GitWebFrame(app, 0, 0, 362, 164);
		webFrame.loadWebsite(this.site);
		layoutBrowser.addComponent(webFrame);

		this.setLayout(layoutBrowser);
	}
}