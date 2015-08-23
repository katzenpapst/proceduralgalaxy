package de.katzenpapst.proceduralgalaxy.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Maps;

import cpw.mods.fml.client.FMLClientHandler;
import de.katzenpapst.proceduralgalaxy.ProceduralGalaxy;
import de.katzenpapst.proceduralgalaxy.network.SimplePacketPG;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;

import java.lang.reflect.*;
// @todo rename this class to something like CelestialScreenBase, I will definitely use this for the other guis, too
public class GuiObservatory extends GuiCelestialSelection {
	
	protected ArrayList<CelestialScreenButton> buttons;
	protected int btnInitialOffset = 0;
	
	
	
	// fuck people who use private on everyfuckingeverything so that you have to reimplement stuff
    protected static int BORDER_WIDTH = 0;
    protected static int BORDER_EDGE_WIDTH = 0;
    
    @Override
    public void initGui()
    {
    	super.initGui();
    	// I'm looking at you, micdoodle8    	
    	GuiObservatory.BORDER_WIDTH = this.width / 65;
    	GuiObservatory.BORDER_EDGE_WIDTH = GuiObservatory.BORDER_WIDTH / 4;  
    	
    	buttons = new ArrayList<CelestialScreenButton>();
    	// add my buttons
    	final GuiObservatory me = this;
    	CelestialScreenButton createSunBtn = new CelestialScreenButton(GCCoreUtil.translate("gui.message.solarsystem.create.name").toUpperCase())
    			{
					
					@Override
					public void doAction() {
						ProceduralGalaxy.instance.getChannelHandler().sendToServer(
							new SimplePacketPG(
								SimplePacketPG.EnumSimplePacketPG.S_GENERATE_SOLAR_SYSTEM, 
								new Object[] { 
										FMLClientHandler.instance().getClient().thePlayer.getGameProfile().getId()
								}
							)
						);
						/*
						ProceduralGalaxy.instance.createNewSolarSystem();
						try {
							me.reInitGui();
						} catch (Exception e) {
							System.out.print("Asploded");
						}*/
					}
				};
		
    	// add them to the array in the REVERSE order they should appear in
		buttons.add(createSunBtn);
    }
    
    public void reInitGui() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    	Map<CelestialBody, Integer> newTicks = Maps.newHashMap();
    	
    	
    	for (Planet planet : GalaxyRegistry.getRegisteredPlanets().values())
        {
            newTicks.put(planet, 0);
        }

        for (Moon moon : GalaxyRegistry.getRegisteredMoons().values())
        {
            newTicks.put(moon, 0);
        }

        for (Satellite satellite : GalaxyRegistry.getRegisteredSatellites().values())
        {
            newTicks.put(satellite, 0);
        }
        
        Field cbTicks = this.getClass().getSuperclass().getDeclaredField("celestialBodyTicks");
    	cbTicks.setAccessible(true);
    	cbTicks.set(this, newTicks);
    	// cbTicks.set(super, newTicks);

    }

	public GuiObservatory(List<CelestialBody> possibleBodies) {
		// just hardcode mapmode to true here, that might help
		super(true, possibleBodies);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void drawButtons(int mousePosX, int mousePosY) {
		// hmm, this seems to be "redraw" rather than "create initially"
		super.drawButtons(mousePosX, mousePosY);
		// GL11.glColor4f(0.0F, 1.0F, 0.0F, 1);
		
		// now do my buttons
		// hmm, just put them at the bottom
		int rightBorder = width - GuiObservatory.BORDER_WIDTH - GuiObservatory.BORDER_EDGE_WIDTH;
		int lowerBorder = height - (GuiObservatory.BORDER_WIDTH + GuiObservatory.BORDER_EDGE_WIDTH)  - CelestialScreenButton.defaultHeight;
		
		for(int i=0;i<buttons.size();i++) {
			// doing it like this because I need the i
			GL11.glColor4f(0.0F, 1.0F, 0.0F, 1);
			CelestialScreenButton btn = buttons.get(i);
			
			int buttonOffset = btnInitialOffset + i*CelestialScreenButton.defaultHeight;
			// update these values here. they are used in mouseClicked
			btn.x=rightBorder - CelestialScreenButton.defaultWidth;
			btn.y=lowerBorder - buttonOffset;
			this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain0);
		    this.drawTexturedModalRect(
		    		btn.x, 
		    		btn.y, 
		    		CelestialScreenButton.defaultWidth, 
		    		CelestialScreenButton.defaultHeight, 0, 392, 148, 22, true, false);
		    String str = btn.caption;
		    this.fontRendererObj.drawString(str, 
		    		rightBorder - 40 - fontRendererObj.getStringWidth(str) / 2, 
		    		lowerBorder - (buttonOffset-1), ColorUtil.to32BitColor(255, 255, 255, 255));
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		// now I have to figure out if the click happened within one of my buttons
		for(int i=0;i<buttons.size();i++) {
			
			CelestialScreenButton btn = buttons.get(i); 
			
			
			if(btn.isWithin(x, y)) {
				// yes, this button
				btn.doAction();
				return;
			}
		    
		}
	}
	
	
}
