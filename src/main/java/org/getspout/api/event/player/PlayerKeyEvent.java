/*
 * This file is part of SpoutAPI (http://www.getspout.org/).
 *
 * SpoutAPI is licensed under the SpoutDev license version 1.
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
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://getspout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.getspout.api.event.player;

import org.getspout.api.event.Cancellable;
import org.getspout.api.event.HandlerList;
import org.getspout.api.gui.ScreenType;
import org.getspout.api.keyboard.Keyboard;
import org.getspout.api.player.Player;

public class PlayerKeyEvent extends PlayerEvent implements Cancellable {
	private static HandlerList handlers = new HandlerList();

	private Keyboard key;

	private boolean pressed;

	private ScreenType screenType;

	public PlayerKeyEvent(Player p) {
		super(p);
	}

	public Keyboard getKey() {
		return key;
	}

	public void setKey(Keyboard key) {
		this.key = key;
	}

	public ScreenType getScreenType() {
		return screenType;
	}

	public void setScreenType(ScreenType screenType) {
		this.screenType = screenType;
	}

	public boolean isPressed() {
		return pressed;
	}

	public boolean isReleased() {
		return !pressed;
	}

	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}