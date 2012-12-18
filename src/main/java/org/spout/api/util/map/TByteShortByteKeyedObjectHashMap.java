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
package org.spout.api.util.map;

import java.util.Collection;

import org.spout.api.util.hashing.ByteShortByteHashed;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;

/**
 * A simplistic map that supports a 3 bytes for keys, using a trove int int
 * hashmap in the backend.
 *
 */
public class TByteShortByteKeyedObjectHashMap<K> extends ByteShortByteHashed {
	protected final TIntObjectHashMap<K> map;

	public TByteShortByteKeyedObjectHashMap() {
		map = new TIntObjectHashMap<K>(100);
	}

	public TByteShortByteKeyedObjectHashMap(int capacity) {
		map = new TIntObjectHashMap<K>(capacity);
	}

	public K put(int key1, int key2, int key3, K value) {
		int key = key(key1, key2, key3);
		return map.put(key, value);
	}

	public K get(int key1, int key2, int key3) {
		int key = key(key1, key2, key3);
		return map.get(key);
	}

	public boolean containsKey(int key1, int key2, int key3) {
		int key = key(key1, key2, key3);
		return map.containsKey(key);
	}

	public void clear() {
		map.clear();
	}

	public boolean containsValue(int val) {
		return map.containsValue(val);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public TIntObjectIterator<K> iterator() {
		return map.iterator();
	}

	public TIntSet keySet() {
		return map.keySet();
	}

	public int[] keys() {
		return map.keys();
	}

	public K remove(int key1, int key2, int key3) {
		int key = key(key1, key2, key3);
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public Collection<K> valueCollection() {
		return map.valueCollection();
	}

	@SuppressWarnings("unchecked")
	public K[] values() {
		return (K[]) map.values();
	}
}
