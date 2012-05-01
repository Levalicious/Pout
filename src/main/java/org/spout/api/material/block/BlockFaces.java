/*
 * This file is part of SpoutAPI (http://www.spout.org/).
 *
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
package org.spout.api.material.block;

/**
 * Contains several BlockFace constants
 */
public class BlockFaces {
	/**
	 * The [north-east-south-west] faces
	 */
	public static final BlockFace[] NESW = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};

	/**
	 * The [north-south-east-west] faces
	 */
	public static final BlockFace[] NSEW = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

	/**
	 * The [east-west-south-north] faces
	 */
	public static final BlockFace[] EWSN = {BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH};

	/**
	 * The [north-south-west-east] faces
	 */
	public static final BlockFace[] NSWE = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST};

	/**
	 * The [south-west-north-east] faces
	 */
	public static final BlockFace[] SWNE = {BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST};

	/**
	 * The [west-north-east-south] faces
	 */
	public static final BlockFace[] WNES = {BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH};

	/**
	 * The [south-north-east-west] faces
	 */
	public static final BlockFace[] SNEW = {BlockFace.SOUTH, BlockFace.NORTH, BlockFace.EAST, BlockFace.WEST};

	/**
	 * The [north-east-south-west-bottom] faces
	 */
	public static final BlockFace[] NESWB = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.BOTTOM};

	/**
	 * The [east-west-south-north-bottom] faces
	 */
	public static final BlockFace[] EWSNB = {BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.BOTTOM};

	/**
	 * The [north-east-south-west-top] faces
	 */
	public static final BlockFace[] NESWT = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.TOP};

	/**
	 * The [north-east-south-west-bottom-top] faces
	 */
	public static final BlockFace[] NESWBT = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.BOTTOM, BlockFace.TOP};

	/**
	 * The [bottom-top-east-west-north-south] faces
	 */
	public static final BlockFace[] BTEWNS = {BlockFace.BOTTOM, BlockFace.TOP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};

	/**
	 * The [bottom-top-north-south-west-east] faces
	 */
	public static final BlockFace[] BTNSWE = {BlockFace.BOTTOM, BlockFace.TOP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST};

	/**
	 * The [north-south-east-west-bottom] faces
	 */
	public static final BlockFace[] NSEWB = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.BOTTOM};

	/**
	 * The [north-south-west-east-bottom] faces
	 */
	public static final BlockFace[] NSWEB = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST, BlockFace.BOTTOM};

	/**
	 * Gets whether the constant contains the face
	 * @param constant to use
	 * @param face to use
	 * @return True if found, False if not
	 */
	public static boolean contains(BlockFace[] constant, BlockFace face) {
		for (BlockFace bface : constant) {
			if (bface == face) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets the index of the face in the constant given as a short
	 * 
	 * @param constant to use
	 * @param face to get the index of
	 * @param def to return if not found
	 * @return the index in the constant, or the def value if not found
	 */
	public static int indexOf(BlockFace[] constant, BlockFace face, int def) {
		for (int i = 0; i < constant.length; i++) {
			if (constant[i] == face) {
				return i;
			}
		}
		return def;
	}

	/**
	 * Gets the face from a constant at the index given<br>
	 * If the index is out of range, the first or last element is returned
	 * 
	 * @param constant to get from
	 * @param index to get at
	 * @return the BlockFace
	 */
	public static BlockFace get(BlockFace[] constant, int index) {
		if (index < 0) {
			return constant[0];
		} else if (index >= constant.length) {
			return constant[constant.length - 1];
		} else {
			return constant[index];
		}
	}

	/**
	 * Gets the face from a constant at the index given<br>
	 * If the index is out of range, the default is returned
	 * 
	 * @param constant to get from
	 * @param index to get at
	 * @param def if the index is out of range
	 * @return the BlockFace
	 */
	public static BlockFace get(BlockFace[] constant, int index, BlockFace def) {
		if (index < 0 || index >= constant.length) {
			return def;
		} else {
			return constant[index];
		}
	}
}
