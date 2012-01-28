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
package org.spout.api.util.set.concurrent;

import gnu.trove.TCollections;
import gnu.trove.set.hash.TLongHashSet;

import org.spout.api.util.set.TInt21HashSet;

/**
 * A synchronized version of the {@link TInt21HashSet}.
 *
 * This set is backed by a read/write lock synchronised set.
 */
public class TSyncInt21HashSet extends TInt21HashSet {
	public TSyncInt21HashSet() {
		super(TCollections.synchronizedSet(new TLongHashSet()));
	}

	public TSyncInt21HashSet(int capacity) {
		super(TCollections.synchronizedSet(new TLongHashSet(capacity)));
	}
}
