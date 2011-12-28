/*
 * Copyright (c) 2011 toi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
*/

package org.getspout.server.util;

import gnu.trove.set.hash.TIntHashSet;

import org.getspout.api.geo.World;
import org.getspout.api.geo.discrete.Point;
import org.getspout.api.geo.discrete.Transform;
import org.getspout.api.math.Vector3;
import org.getspout.server.entity.SpoutEntity;
import org.getspout.unchecked.server.block.BlockID;

/**
 * This class uses an inefficient method to figure out what block a player is
 * looking towards.
 *
 * Originally written by toi. It was ported to WorldEdit and trimmed down by
 * sk89q. Thanks to Raphfrk for optimization of toi's original class. Ported to
 * Spout by zml2008. This classs has come a long way. Treat it nicely.
 *
 * @author toi
 */
public class TargetBlock {
	private World world;
	private int maxDistance;
	private double checkDistance, curDistance;
	private Vector3 targetPos = new Vector3();
	private Vector3 targetPosDouble = new Vector3();
	private Vector3 prevPos = new Vector3();
	private Vector3 offset = new Vector3();
	private TIntHashSet transparentBlocks = null;

	/**
	 * Constructor requiring a player, uses default values
	 *
	 * @param player player to work with
	 */
	public TargetBlock(SpoutEntity player, TIntHashSet transparent) {
		Transform epos = player.getTransform();
		world = epos.getPosition().getWorld();
		setValues(epos.getPosition(), 300, player.getEyeHeight(), 0.2, transparent);
	}

	/**
	 * Constructor requiring a player, max distance and a checking distance
	 *
	 * @param player LocalPlayer to work with
	 * @param maxDistance how far it checks for blocks
	 * @param checkDistance how often to check for blocks, the smaller the more
	 *            precise
	 */
	public TargetBlock(SpoutEntity player, int maxDistance, double checkDistance, TIntHashSet transparent) {
		world = player.getTransform().getPosition().getWorld();
		setValues(player.getLocation(), maxDistance, player.getEyeHeight(), checkDistance, transparent);
	}

	/**
	 * Set the values, all constructors uses this function
	 *
	 * @param loc location of the view
	 * @param maxDistance how far it checks for blocks
	 * @param viewHeight where the view is positioned in y-axis
	 * @param checkDistance how often to check for blocks, the smaller the more
	 *            precise
	 */
	private void setValues(Point loc, int maxDistance, double viewHeight, double checkDistance, TIntHashSet transparent) {
		this.maxDistance = maxDistance;
		this.checkDistance = checkDistance;
		curDistance = 0;
		int xRotation = (int) (loc.getYaw() + 90) % 360;
		int yRotation = (int) loc.getPitch() * -1;

		double h = checkDistance * Math.cos(Math.toRadians(yRotation));

		offset = new Vector3((h * Math.cos(Math.toRadians(xRotation))), (checkDistance * Math.sin(Math.toRadians(yRotation))), (h * Math.sin(Math.toRadians(xRotation))));

		targetPosDouble = loc.add(0, viewHeight, 0).toVector();
		targetPos = targetPosDouble.toBlockVector();
		prevPos = targetPos;
		transparentBlocks = transparent;
		if (transparentBlocks == null) {
			transparentBlocks = new TIntHashSet(new int[] {0});
		}
	}

	/**
	 * Returns any block at the sight. Returns null if out of range or if no
	 * viable target was found. Will try to return the last valid air block it
	 * finds.
	 *
	 * @return Block
	 */
	public Point getAnyTargetBlock() {
		boolean searchForLastBlock = true;
		Point lastBlock = null;
		while (getNextBlock()) {
			if (world.getBlockTypeIdAt(getCurrentBlock()) == BlockID.AIR) {
				if (searchForLastBlock) {
					lastBlock = getCurrentBlock();
					if (lastBlock.getY() <= 0 || lastBlock.getY() >= world.getMaxHeight() - 1) {
						searchForLastBlock = false;
					}
				}
			} else {
				break;
			}
		}
		Point currentBlock = getCurrentBlock();
		return currentBlock != null ? currentBlock : lastBlock;
	}

	/**
	 * Returns the block at the sight. Returns null if out of range or if no
	 * viable target was found
	 *
	 * @return Block
	 */
	public Point getTargetBlock() {
		while (getNextBlock() && world.getBlockTypeIdAt(getCurrentBlock()) == BlockID.AIR) {
			;
		}
		return getCurrentBlock();
	}

	/**
	 * Returns the block at the sight. Returns null if out of range or if no
	 * viable target was found
	 *
	 * @return Block
	 */
	public Point getSolidTargetBlock() {
		while (getNextBlock() && transparentBlocks.contains(world.getBlockTypeIdAt(getCurrentBlock()))) {
			;
		}
		return getCurrentBlock();
	}

	/**
	 * Get next block
	 *
	 * @return next block position
	 */
	public boolean getNextBlock() {
		prevPos = targetPos;
		do {
			curDistance += checkDistance;

			targetPosDouble.add(offset);
			targetPos = targetPosDouble.toBlockVector();
		} while (curDistance <= maxDistance && targetPos.getBlockX() == prevPos.getBlockX() && targetPos.getBlockY() == prevPos.getBlockY() && targetPos.getBlockZ() == prevPos.getBlockZ());

		if (curDistance > maxDistance) {
			return false;
		}

		return true;
	}

	/**
	 * Returns the current block along the line of vision
	 *
	 * @return block position
	 */
	public Point getCurrentBlock() {
		if (curDistance > maxDistance) {
			return null;
		} else {
			return new Point(targetPos, world);
		}
	}

	/**
	 * Returns the previous block in the aimed path
	 *
	 * @return block position
	 */
	public Point getPreviousBlock() {
		return new Point(prevPos, world);
	}
}