package de.katzenpapst.proceduralgalaxy.gui;

import java.awt.event.ActionListener;

/**
 * Q&D class for buttons on the celestial screen
 * @author katzenpapst
 *
 */
public abstract class CelestialScreenButton {
	
	public String caption = "";
	public static final int defaultWidth = 74;
	public static final int defaultHeight = 11;
	public int x = 0; 
	public int y = 0; 
	
	
	public CelestialScreenButton(String buttonCaption) {
		caption = buttonCaption;
	}
	
	abstract public void doAction();
	
	public boolean isWithin(int mouseX, int mouseY) {
		return (mouseX >= x && mouseX <= x+defaultWidth &&
				mouseY >= y && mouseY <= y+defaultHeight); 
	
	}
}
