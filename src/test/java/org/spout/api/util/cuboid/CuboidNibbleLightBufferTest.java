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
package org.spout.api.util.cuboid;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

public class CuboidNibbleLightBufferTest {
	
	private final int SIZE = 16;
	private final int HALF_SIZE = 16;
	private final int LOOPS = 10;
	
	@Test
	public void copyTest() {
		
		Random r = new Random();
		
		for (int c = 0; c < LOOPS; c++) {
			int bx = r.nextInt();
			int by = r.nextInt();
			int bz = r.nextInt();
			
			int sx = r.nextInt(SIZE) + SIZE;
			int sy = r.nextInt(SIZE) + SIZE;
			int sz = r.nextInt(SIZE) + SIZE;
			
			int vol = sx * sy * sz;
			if ((vol | 1) == vol) {
				sx = sx & (~1);
			}
			CuboidNibbleLightBuffer dest = new CuboidNibbleLightBuffer(bx, by, bz, sx, sy, sz);
			
			int bx2 = bx + r.nextInt(HALF_SIZE);
			int by2 = by + r.nextInt(HALF_SIZE);
			int bz2 = bz + r.nextInt(HALF_SIZE);
			
			int sx2 = r.nextInt(HALF_SIZE) + HALF_SIZE;
			int sy2 = r.nextInt(HALF_SIZE) + HALF_SIZE;
			int sz2 = r.nextInt(HALF_SIZE) + HALF_SIZE;
			
			int vol2 = sx2 * sy2 * sz2;
			if ((vol2 | 1) == vol2) {
				sx2 = sx2 & (~1);
			}
			
			CuboidNibbleLightBuffer src = new CuboidNibbleLightBuffer(bx2, by2, bz2, sx2, sy2, sz2);
			
			byte[][][] values = new byte[sx2][sy2][sz2];

			for (int x = bx2; x < bx2 + sx2; x++) {
				for (int y = by2; y < by2 + sy2; y++) {
					for (int z = bz2; z < bz2 + sz2; z++) {
						byte value = (byte) (r.nextInt() & 0xf);
						values[x - bx2][y - by2][z - bz2] = value;
						src.set(x, y, z, value);
					}
				}
			}
			
			for (int x = bx2; x < bx2 + sx2; x++) {
				for (int y = by2; y < by2 + sy2; y++) {
					for (int z = bz2; z < bz2 + sz2; z++) {
						byte value = values[x - bx2][y - by2][z - bz2];
						assertTrue("value mismatch in setting up src buffer " + (x - bx2) + ", " + (y - by2) + ", " + (z - bz2) + ", got " + src.get(x, y, z) + ", exp " + value, value == src.get(x, y, z));
					}
				}
			}
			
			dest.write(src);
			
			for (int x = bx2; x < bx2 + sx2; x++) {
				for (int y = by2; y < by2 + sy2; y++) {
					for (int z = bz2; z < bz2 + sz2; z++) {
						if (x >= (bx + sx) || y >= (by + sy) || z >= (bz + sz)) {
							continue;
						}
						byte value = values[x - bx2][y - by2][z - bz2];
						assertTrue("value mismatch after copy " + (x - bx2) + ", " + (y - by2) + ", " + (z - bz2) + ", got " + dest.get(x, y, z) + ", exp " + value, value == dest.get(x, y, z));
					}
				}
			}
			
			for (int x = bx; x < bx + sx; x++) {
				for (int y = by; y < by + sy; y++) {
					for (int z = bz; z < bz + sz; z++) {
						if (x < (bx2 + sx2) && x >= bx2 && y < (by2 + sy2) && y >= by2 && z < (bz2 + sz2) && z >= bz2) {
							continue;
						}
						assertTrue("Dest buffer changed outside source buffer " + (x - bx) + ", " + (y - by) + ", " + (z - bz) + ", got " + dest.get(x, y, z) + ", exp " + 0, 0 == dest.get(x, y, z));
					}
				}
			}

		}
		
	}
}
