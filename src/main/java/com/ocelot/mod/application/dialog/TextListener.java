package com.ocelot.mod.application.dialog;

/**
 * Called for use of when a button is pressed.
 * 
 * @author Ocelot5836
 */
public interface TextListener {

	/**
	 * Called when component is clicked
	 *
	 * @param mouseButton
	 *            the mouse button used to click
	 * @param inputText
	 *            The text that was inside the box when the dialog was closed
	 */
	void onClick(int mouseX, int mouseY, int mouseButton, String inputText);
}