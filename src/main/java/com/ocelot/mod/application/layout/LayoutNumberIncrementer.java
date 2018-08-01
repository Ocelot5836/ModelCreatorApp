package com.ocelot.mod.application.layout;

import java.util.regex.Pattern;

import org.apache.commons.lang3.math.NumberUtils;

import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.listener.ChangeListener;

import net.minecraft.client.gui.GuiScreen;

public class LayoutNumberIncrementer extends Layout {

	private static final Pattern PATTERN = Pattern.compile("^({1,10})$");

	private float previousValue;
	private float value;
	private ChangeListener<Float> changeListener;

	private Button up;
	private Button down;
	private TextField display;

	public LayoutNumberIncrementer(int left, int top, int width, int height, float baseValue) {
		super(left, top, width, height);
		this.changeListener = null;
		this.set(baseValue);
	}

	@Override
	public void init() {
		int buttonHeight = (int) Math.round(((double) this.height - 16.0) / 2.0);

		up = new Button(0, 0, this.width, buttonHeight, Icons.ARROW_UP);
		up.setClickListener((mouseX, mouseY, mouseButton) -> {
			if (mouseButton == 0) {
				if (GuiScreen.isShiftKeyDown()) {
					this.add(0.1f);
				} else {
					this.add(1f);
				}
			}
		});
		this.addComponent(up);

		down = new Button(0, this.height - buttonHeight, this.width, buttonHeight, Icons.ARROW_DOWN);
		down.setClickListener((mouseX, mouseY, mouseButton) -> {
			if (mouseButton == 0) {
				if (GuiScreen.isShiftKeyDown()) {
					this.sub(0.1f);
				} else {
					this.sub(1f);
				}
			}
		});
		this.addComponent(down);

		display = new TextField(0, buttonHeight, this.width);
		display.setKeyListener((c) -> {
			if (NumberUtils.isCreatable(display.getText())) {
				this.set(Float.parseFloat(display.getText()));
			}
			return false;
		});
		this.set(this.value);
		this.addComponent(display);
	}

	@Override
	public void init(Layout layout) {
		this.init();
	}

	@Override
	public void handleTick() {
		super.handleTick();
		if (NumberUtils.isCreatable(display.getText())) {
			this.set(Float.parseFloat(display.getText()));
		}
	}

	public void add(float value) {
		this.set(this.value + value / 2);
	}

	public void sub(float value) {
		this.set(this.value - value / 2);
	}

	public void set(float value) {
		this.previousValue = this.value;
		this.value = value;

		if (this.isInitialized()) {
			String st = Float.toString(this.value);
			if (st.endsWith(".0")) {
				this.display.setText(st);
				st = Integer.toString((int) this.value);
			}
			this.display.setText(st);
		}

		if (this.changeListener != null) {
			this.changeListener.onChange(this.previousValue, this.value);
		}
	}

	public void setChangeListener(ChangeListener<Float> listener) {
		this.changeListener = listener;
	}

	public float getValue() {
		return this.value;
	}
}