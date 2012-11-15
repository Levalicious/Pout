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
package org.spout.api.gui;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import org.spout.api.gui.component.ControlComponent;
import org.spout.api.plugin.Plugin;
import org.spout.api.tickable.BasicTickable;

public class Screen extends BasicTickable implements Container {
	private HashMap<Widget, Plugin> widgets = new LinkedHashMap<Widget, Plugin>();
	private Widget focussedWidget = null;
	private boolean takesInput = true;
	private boolean grabsMouse = true;

	@Override
	public Set<Widget> getWidgets() {
		return widgets.keySet();
	}

	@Override
	public void attachWidget(Plugin plugin, Widget widget) {
		widgets.put(widget, plugin);
		widget.setScreen(this);
	}

	@Override
	public void removeWidget(Widget widget) {
		widgets.remove(widget);
	}

	@Override
	public void removeWidgets(Widget... widgets) {
		for (Widget widget : widgets) {
			removeWidget(widget);
		}
	}

	@Override
	public void removeWidgets(Plugin plugin) {
		//TODO
	}

	public Widget getFocussedWidget() {
		return focussedWidget;
	}

	public void setFocussedWidget(Widget focussedWidget) {
		if (focussedWidget.has(ControlComponent.class)) {
			if (this.focussedWidget != null && this.focussedWidget != focussedWidget) {
				this.focussedWidget.onFocusLost();
			}
			this.focussedWidget = focussedWidget;
		} else {
			throw new IllegalStateException("Can only focus controls, add a ControlComponent to your widget!");
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
