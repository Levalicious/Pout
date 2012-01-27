/*
 * This file is part of SpoutAPI (http://www.spout.org/).
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
package org.spout.api.gui;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.spout.api.gui.WidgetAttr.Position;
import org.spout.api.packet.PacketUtil;
import org.spout.api.util.Color;

/**
 * The Spout implementation of the default Chat Bar.
 *
 * This provides extra abilities above the default version.
 */
public class ChatBar extends AbstractWidget implements Widget {

	private int cursorX = 4, cursorY = 240;
	protected Color textColor = new Color(1F, 1F, 1F);

	/**
	 * Package-private constructor.
	 */
	ChatBar() {
		setDirty(false);
		setUID(2);
		setAttr(WidgetAttr.POSITION, Position.ABSOLUTE);
		setAttr(WidgetAttr.LEFT, 2);
		setAttr(WidgetAttr.BOTTOM, 2);
		setAttr(WidgetAttr.WIDTH, 425);
		setAttr(WidgetAttr.HEIGHT, 12);
	}

	@Override
	public int getNumBytes() {
		return super.getNumBytes() + 13;
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		setCursorX(input.readInt());
		setCursorY(input.readInt());
		setTextColor(PacketUtil.readColor(input));
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		output.writeInt(getCursorX());
		output.writeInt(getCursorY());
		PacketUtil.writeColor(output, getTextColor());
	}

	@Override
	public WidgetType getType() {
		return WidgetType.CHATBAR;
	}

	/**
	 * Gets the x position that the cursor starts at when typing chat
	 * @return cursor x
	 */
	public int getCursorX() {
		return cursorX;
	}

	/**
	 * Sets the x position that the cursor starts at when typing 
	 * @param x position to set
	 * @return this
	 */
	public ChatBar setCursorX(int x) {
		cursorX = x;
		return this;
	}

	/**
	 * Gets the y position that the cursor starts at when typing chat
	 * @return cursor y
	 */
	public int getCursorY() {
		return cursorY;
	}

	/**
	 * Sets the y position that the cursor starts at when typing 
	 * @param y position to set
	 * @return this
	 */
	public ChatBar setCursorY(int y) {
		cursorY = y;
		return this;
	}

	/**
	 * Gets the default color of the text for the chat bar
	 * @return default text color
	 */
	public Color getTextColor() {
		return textColor;
	}

	/**
	 * Sets the default color of the text for the chat bar
	 * @param color to set
	 * @return this
	 */
	public ChatBar setTextColor(Color color) {
		textColor = color;
		return this;
	}

	public void render() {
	}

	@Override
	public int getVersion() {
		return super.getVersion() + 2;
	}
}
