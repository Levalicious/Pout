package org.getspout.api.geo.cuboid;

import org.getspout.api.geo.World;
import org.getspout.api.geo.discrete.Point;

/**
 * Represents a cube with an edge length of 1.
 */
public class Block extends Cube {

	private final static double EDGE = 1.0;

	public Block(World world, double x, double y, double z) {
		super(new Point(world, x, y, z), EDGE);
	}
}
