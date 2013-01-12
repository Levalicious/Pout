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
package org.spout.api.material.source;

import org.spout.api.material.Material;

/**
 * Represents a {@link DataSource} which also contains and stores {@link Material} data.
 */
public interface MaterialSource extends DataSource {
	/**
	 * Gets the material
	 * 
	 * @return the material
	 */
	public Material getMaterial();

	/**
	 * Gets if the material contained is the same one of the materials specified<br>
	 * If a parent material is supplied, sub-materials will evaluate true with it as well<br><br>
	 * 
	 * For example:<br>
	 * - sub.isMaterial(sub) == True<br>
	 * - parent.isMaterial(parent) == True<br>
	 * - sub.isMaterial(parent) == True<br>
	 * - parent.isMaterial(sub) == False<br><br>
	 * 
	 * If sub-materials should not evaluate true to its parent materials, use {@link equals()} instead
	 * 
	 * @param materials to evaluate against
	 * @return True if one of the materials equal this material
	 */
	public boolean isMaterial(Material... materials);
}
