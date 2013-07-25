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
package org.spout.api.util.set;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import org.spout.api.util.hashing.ByteTripleHashed;

/**
 * A hash set that uses three 8 bit integers as key, backed by a int trove hashset.
 */
public class TByteTripleHashSet extends ByteTripleHashed {
	protected final TIntSet set;

	public TByteTripleHashSet() {
		set = new TIntHashSet(100);
	}

	public TByteTripleHashSet(int capacity) {
		set = new TIntHashSet(capacity);
	}

	public TByteTripleHashSet(TIntSet set) {
		this.set = set;
	}

	public boolean add(int key1, int key2, int key3) {
		int key = key(key1, key2, key3);
		return set.add(key);
	}

	public boolean contains(int key1, int key2, int key3) {
		int key = key(key1, key2, key3);
		return set.contains(key);
	}

	public void clear() {
		set.clear();
	}

	public boolean isEmpty() {
		return set.isEmpty();
	}

	public TIntIterator iterator() {
		return set.iterator();
	}

	public boolean remove(int key1, int key2, int key3) {
		int key = key(key1, key2, key3);
		return set.remove(key);
	}

	public int size() {
		return set.size();
	}

	public int[] toArray() {
		return set.toArray();
	}
}
