/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * SpoutAPI is licensed under the SpoutDev License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.api.component.components;

import java.awt.Color;

import org.spout.api.Client;
import org.spout.api.Spout;
import org.spout.api.entity.Player;
import org.spout.api.gui.Screen;
import org.spout.api.gui.Widget;
import org.spout.api.gui.component.RenderPartsHolderComponent;
import org.spout.api.gui.render.RenderPart;
import org.spout.api.math.Rectangle;
import org.spout.api.meta.SpoutMetaPlugin;
import org.spout.api.plugin.CommonPluginManager;
import org.spout.api.plugin.Platform;
import org.spout.api.render.SpoutRenderMaterials;
/**
 * DebugHUD for Spout (client). Its purpose, by default, is to render debug developer info when a client player
 * presses F3. The internal debug screen is exposed so plugins can add their own widgets to this.
 *
 * I'll take a moment to briefly explain some of the more confusing methods found in this class (namely
 * how setSprite and setSource works).
 *
 * Firstly you must understand how Rectangle works in-regards to drawing onscreen. Here is some things to remember.
 * Ex. setSprite(new Rectangle(x, y, width, height))
 *
 * X = Where to start drawing X-wise. -1 is western bounds of the screen, 1 is eastern bounds of the screen.
 * Y = Where to start drawing Y-wise. -1 is bottom bounds of the screen, 1 is top bounds of the screen.
 * Width = The width of the sprite. Negative values will drawn leftwards, positive values draw rightwards.
 * Height = The height of the sprite. Negative values will drawn downwards, positive values draw upwards.
 *
 * Secondly you must understand how setSource works. Its almost the same but we are now looking at grabbing a certain texture
 * from a sprite sheet (texture atlas). Its the same concept with rectangles: choose where to start with x, y and then the width and height
 * of the image to grab.
 *
 * Ex. You have a sprite sheet with images (think terrain.png) that is 256 x 256. You want to grab a specific texture from it. You would
 * take the texture and use an application like paint to find the exact pixel this image starts at. It comes down to basic coordinate planes.
 * In short, you want the "origin" to start at within the image, it is the top left corner of the pixel where it starts drawing (both x and y).
 * The following demonstrates how you would perceive a sprite sheet to find the appropriate values.
 *
 *              0
 *              ^
 *              |
 *              |
 * 0 <----------|----------> 256
 *              |
 *              |
 *              v
 *             256
 *
 * In our example, lets say our image starts at 120x and is at the top of the sprite sheet so 0y. Now its a matter of finding out the width and height
 * of the image. An application like paint allows you to select an image and find out its width and height. We will assume its a 16x16. Here is the
 * final code line to grab the image:
 *
 * setSource(new Rectangle(120f/256f, 0f, 16f/256f, 16f/256f));
 *
 * Finally some tricks I've found. I'll add on to this as I further complete this component...
 * - If you want to just draw a rectangle with a solid color on the screen, setSprite(new Color(r, g, b, a)) to whatever you like and
 *   setSource(new Rectangle(0f, 0f, 0f, 0f)). This will render your entire sprite with the color you chose!
 */
public class DebugHUDComponent extends EntityComponent {
	// The Internal Spout-dummy plugin
	private final SpoutMetaPlugin plugin;
	// The debug HUD screen
	private final Screen screen;
	// The widget attachment that will hold the pop-out display
	private final Widget debug;

	public DebugHUDComponent() {
		plugin = ((CommonPluginManager) Spout.getPluginManager()).getMetaPlugin();
		screen = new Screen();
		debug = new Widget();
	}

	@Override
	public void onAttached() {
		if (!(getOwner() instanceof Player)) {
			throw new IllegalStateException("May only attach this component to players!");
		}
		if (Spout.getPlatform() != Platform.CLIENT) {
			throw new IllegalStateException("This component is only attached to clients!");
		}
		init();
	}

	public void open() {
		((Client) Spout.getEngine()).getScreenStack().openScreen(screen);
	}

	public void close() {
		((Client) Spout.getEngine()).getScreenStack().closeScreen(screen);
	}

	public void reset() {
		screen.removeWidgets();
		init();
	}

	public Screen getScreen() {
		return screen;
	}

	/**
	 * Constructs the default Spout debug HUD
	 */
	private void init() {
		screen.setGrabsMouse(false);
		screen.setTakesInput(false);
		final RenderPartsHolderComponent debugBar = debug.add(RenderPartsHolderComponent.class);














		//The pop-out bar
		final RenderPart bar = new RenderPart();
		bar.setRenderMaterial(SpoutRenderMaterials.GUI_COLOR);
		bar.setColor(new Color(0f, 0f, 0f, 0.6f)); //Black with opacity of 40%
		bar.setSprite(new Rectangle(-1f, 1f, 2f, -0.06f));
		bar.setSource(new Rectangle(0f, 0f, 0f, 0f));
		debugBar.add(bar, 0);

		//The pop-out extension for coordinate display
		final RenderPart barExt = new RenderPart();
		barExt.setRenderMaterial(SpoutRenderMaterials.GUI_COLOR);
		barExt.setColor(new Color(0f, 0f, 0f, 0.6f)); //Black with opacity of 40%
		barExt.setSprite(new Rectangle(-1f, 0.94f, 0.25f, -0.06f));
		barExt.setSource(new Rectangle(0f, 0f, 0f, 0f));
		debugBar.add(barExt, 1);

		//Finally attach widget so we can draw
		screen.attachWidget(plugin, debug);
	}
}
