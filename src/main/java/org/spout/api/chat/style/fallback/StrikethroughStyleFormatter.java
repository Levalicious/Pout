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
package org.spout.api.chat.style.fallback;

import org.spout.api.chat.style.StyleFormatter;

/**
 * Applies the unicode strike-through character to a string
 */
public class StrikethroughStyleFormatter implements StyleFormatter {
	private static final String STRIKETHROUGH_CHAR = "\u0336";
	@Override
	public String format(String text) {
		StringBuilder builder = new StringBuilder(text.length() * 2);
		for (int i = 0; i < text.length(); ++i) {
			builder.append(text.substring(i, i + 1));
			if (Character.isLetter(text.codePointAt(i))) {
				builder.append(STRIKETHROUGH_CHAR);
			}
		}
		return builder.toString();
	}
}
