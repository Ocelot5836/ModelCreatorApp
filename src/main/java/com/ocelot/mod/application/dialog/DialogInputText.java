package com.ocelot.mod.application.dialog;

import java.awt.Color;

import javax.annotation.Nullable;

import com.mrcrayfish.device.api.app.Dialog;
import com.mrcrayfish.device.api.app.Layout.Background;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.Text;
import com.mrcrayfish.device.api.app.component.TextArea;
import com.mrcrayfish.device.api.app.listener.ClickListener;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;

public class DialogInputText extends Dialog {
	
	private TextListener positiveListener;
	private String message;
	
	private Button buttonPositive;
	private TextArea textArea;
	private Text text;
	
	public DialogInputText(String message, int width, int height) {
		this.message = message;
		defaultLayout.width = width;
		defaultLayout.height = height;
	}

	@Override
	public void init(@Nullable NBTTagCompound intent)
	{
		super.init(intent);
		
		defaultLayout.setBackground(new Background()
		{
			@Override
			public void render(Gui gui, Minecraft mc, int x, int y, int width, int height, int mouseX, int mouseY, boolean windowActive)
			{
				Gui.drawRect(x, y, x + width, y + height, Color.LIGHT_GRAY.getRGB());
			}
		});
		
		text = new Text(message, 5, 5, defaultLayout.width - 10);
		this.addComponent(text);
		
		buttonPositive = new Button(getWidth() - 41, getHeight() - 20, I18n.format("gui.done"));
		buttonPositive.setSize(36, 16);
		buttonPositive.setClickListener((mouseX, mouseY, mouseButton) ->
		{
            if(positiveListener != null)
            {
                positiveListener.onClick(mouseX, mouseY, mouseButton, textArea.getText());
            }
            close();
        });
		this.addComponent(buttonPositive);
		
		textArea = new TextArea(5, 5 + text.getHeight(), getWidth() - 10, getHeight() - 25);
		this.addComponent(textArea);
	}
	
	public void setPositiveListener(TextListener positiveListener) {
		this.positiveListener = positiveListener;
	}
}