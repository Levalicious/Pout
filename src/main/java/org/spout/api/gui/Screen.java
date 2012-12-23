/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * SpoutAPI is licensed under the Spout License Version 1.
 *
 * SpoutAPI is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * SpoutAPI is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.api.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.spout.api.plugin.Plugin;
import org.spout.api.tickable.BasicTickable;

public class Screen extends BasicTickable implements Container {
	private HashMap<Widget, Plugin> widgets = new LinkedHashMap<Widget, Plugin>();
	private Widget focussedWidget = null;
	private boolean takesInput = true;
	private boolean grabsMouse = true;

	@Override
	public List<Widget> getWidgets() {
		return Collections.unmodifiableList(new ArrayList<Widget>(widgets.keySet()));
	}

	@Override
	public void attachWidget(Plugin plugin, Widget widget) {
		widget.setScreen(this);
		widgets.put(widget, plugin);
	}

	@Override
	public void removeWidget(Widget widget) {
		widgets.remove(widget);
		cleanupWidget(widget);
	}

	@Override
	public void removeWidgets(Widget... widgets) {
		for (Widget widget : widgets) {
			removeWidget(widget);
		}
	}

	@Override
	public void removeWidgets() {
		Iterator<Widget> i = getWidgets().iterator();
		while (i.hasNext()) {
			cleanupWidget(i.next());
			i.remove();
		}
	}
	
	private void cleanupWidget(Widget widget) {
		widget.setScreen(null);
		if (widget == focussedWidget) {
			focussedWidget = null;
			widget.onFocusLost();
		}
	}

	@Override
	public void removeWidgets(Plugin plugin) {
		Iterator<Widget> i = getWidgets().iterator();
		while (i.hasNext()) {
			Widget widget = i.next();
			if (widgets.get(widget).equals(plugin)) {
				i.remove();
				cleanupWidget(widget);
			}
		}
	}

	@Override
	public void onTick(float dt) {
		for (Widget w : widgets.keySet()) {
			w.tick(dt);
		}
	}

	@Override
	public boolean canTick() {
		return true;
	}

	public Widget getFocusedWidget() {
		return focussedWidget;
	}
	
	public void setFocus(Widget newFocus) {
		setFocus(newFocus, FocusReason.PROGRAMMED);
	}
	
	public void setFocus(Widget newFocus, FocusReason reason) {
		if (widgets.containsKey(newFocus)) {			
			if (focussedWidget != newFocus) {
				Widget oldFocus = focussedWidget;
				focussedWidget = newFocus;
				if (oldFocus != null) {
					oldFocus.onFocusLost();
				}
				if (newFocus != null) {
					newFocus.onFocus(reason);
				}
			}
		}
	}

	public boolean grabsMouse() {
		return grabsMouse;
	}

	public void setGrabsMouse(boolean grabsMouse) {
		this.grabsMouse = grabsMouse;
	}

	/**
	 * @returns if this screen should receive mouse and keyboard input. Default is true
	 */
	public boolean takesInput() {
		return takesInput;
	}

	public void setTakesInput(boolean takesInput) {
		this.takesInput = takesInput;
	}
}
