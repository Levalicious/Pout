package org.getspout.commons.geo.cuboid;

import org.getspout.commons.geo.point.Point;
import org.getspout.commons.math.Vector3;

/**
 * Represents a Cubic volume that is located somewhere in a world.
 */
public class Cube extends Cuboid {
	
	public Cube(Point base, double size) {
		super(base, new Vector3(size, size, size));
	}

}
