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
package org.spout.api.event.player;

import org.spout.api.entity.Player;
import org.spout.api.event.Cancellable;
import org.spout.api.event.HandlerList;
import org.spout.api.input.Keyboard;

/**
 * Event is called when a key is pressed/held/or released from the client
 */
public class PlayerKeyEvent extends PlayerEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private final Keyboard key;
	private final String rawCommand;
	private final boolean pressed;
	private final char c;

	public PlayerKeyEvent(Player p, Keyboard key, boolean pressed, String rawCommand, char c) {
		super(p);
		this.key = key;
		this.pressed = pressed;
		this.rawCommand = rawCommand;
		this.c = c;
	}

	/**
	 * Returns the char of the key event.
	 *
	 * @return character
	 */
	public char getChar() {
		return c;
	}

	/**
	 * Returns the key that triggered this event.
	 * @return The keyboard key.
	 */
	public Keyboard getKey() {
		return key;
	}

	/**
	 * Determines if the key was pressed.
	 * @return True if pressed, false if released
	 */
	public boolean isPressed() {
		return pressed;
	}

	/**
	 * Gets the raw command bound to this key.
	 * @return The raw command bound to the key
	 */
	public String getRawCommand() {
		return rawCommand;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		super.setCancelled(cancelled);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
