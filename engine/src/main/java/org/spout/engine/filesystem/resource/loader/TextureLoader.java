/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011 Spout LLC <http://www.spout.org/>
 * Spout is licensed under the Spout License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Spout is distributed in the hope that it will be useful, but WITHOUT ANY
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
package org.spout.engine.filesystem.resource.loader;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

import org.spout.api.Platform;
import org.spout.api.Spout;
import org.spout.api.render.Texture;
import org.spout.api.resource.ResourceLoader;
import org.spout.engine.filesystem.resource.ClientTexture;
import org.spout.engine.filesystem.resource.ServerTexture;

public class TextureLoader extends ResourceLoader {
	public TextureLoader() {
		super("texture", "texture://Spout/fallbacks/fallback.png");
	}

	@Override
	public Texture load(InputStream in) {
		Texture t = null;
		try {
			BufferedImage image = ImageIO.read(in);
			if (Spout.getPlatform() == Platform.SERVER) {
				t = new ServerTexture(image);
			} else {
				t = new ClientTexture(image);
				t.writeGPU();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return t;
	}
}
