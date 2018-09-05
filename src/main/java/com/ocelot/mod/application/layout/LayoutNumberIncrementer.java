package com.ocelot.mod.application.layout;

import com.mrcrayfish.device.api.app.Icons;
import com.mrcrayfish.device.api.app.Layout;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.listener.ChangeListener;
import com.ocelot.api.utils.NumberHelper;

public class LayoutNumberIncrementer extends Layout {

	private float previousValue;
	private float value;
	private ChangeListener<Float> changeListener;

	private Button up;
	private Button down;
	private TextField display;

	public LayoutNumberIncrementer(int left, int top, int width, int height, float baseValue) {
		super(left, top, width, height);
		this.changeListener = null;
		this.setPrivate(baseValue);
	}

	@Override
	public void init() {
		int buttonHeight = (int) Math.round(((double) this.height - 16.0) / 2.0);

		up = new Button(0, 0, this.width, buttonHeight, Icons.ARROW_UP);
		up.setClickListener((mouseX, mouseY, mouseButton) -> {
			if (mouseButton == 0) {
				this.add(0.5f);
				this.updateText();
			}
		});
		this.addComponent(up);

		down = new Button(0, this.height - buttonHeight, this.width, buttonHeight, Icons.ARROW_DOWN);
		down.setClickListener((mouseX, mouseY, mouseButton) -> {
			if (mouseButton == 0) {
				this.sub(0.5f);
				this.updateText();
			}
		});
		this.addComponent(down);

		display = new TextField(0, buttonHeight, this.width);
		display.setKeyListener((c) -> {
			this.setPrivate((float) NumberHelper.parseEquation(display.getText()));
			return false;
		});

		this.setPrivate(this.value);
		this.updateText();
		this.addComponent(display);
	}

	@Override
	public void init(Layout layout) {
		this.init();
	}

	@Override
	public void handleTick() {
		super.handleTick();
	}

	public void updateText() {
		if (this.isInitialized()) {
			String s = Float.toString(this.value);
			if (s.endsWith(".0")) {
				this.display.setText(s);
				s = Integer.toString((int) this.value);
			}
			this.display.setText(s);
		}
	}

	public void add(float value) {
		this.setPrivate(this.value + value);
	}

	public void sub(float value) {
		this.setPrivate(this.value - value);
	}

	private void setPrivate(float value) {
		this.previousValue = this.value;
		this.value = value;

		if (this.changeListener != null) {
			this.changeListener.onChange(this.previousValue, this.value);
		}
	}

	public void set(float value) {
		this.previousValue = this.value;
		this.value = value;
		this.updateText();

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