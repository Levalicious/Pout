/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.resources.server;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.spout.api.render.Texture;

public class ServerTexture extends Texture {

	public ServerTexture(Color[] colors, int width, int height){
		super(colors, width, height);
	}

	public ServerTexture(BufferedImage baseImage) {
		super(Texture.convertFromIntArray(baseImage.getRGB(0, 0, baseImage.getWidth(), baseImage.getHeight(), null, 0, baseImage.getWidth())), baseImage.getWidth(), baseImage.getHeight());
	}
	
	@Override
	public Texture subTexture(int x, int y, int w, int h) {
		throw new UnsupportedOperationException("TODO: Reimplement this");
	}

	@Override
	public void writeGPU() { }

	@Override
	public void bind() { }

	@Override
	public boolean isLoaded() {
		return false;
	}
}
