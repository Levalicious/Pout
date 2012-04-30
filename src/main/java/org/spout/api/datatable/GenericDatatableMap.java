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
package org.spout.api.datatable;

import gnu.trove.impl.sync.TSynchronizedIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.spout.api.datatable.value.DatatableNil;
import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.util.StringMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GenericDatatableMap implements DatatableMap {
	private static final StringMap ROOT_STRING_MAP = new StringMap(null, new MemoryStore<Integer>(), 0, Short.MAX_VALUE);
	private final StringMap stringmap;
	TSynchronizedIntObjectMap<DatatableTuple> map = new TSynchronizedIntObjectMap<DatatableTuple>(new TIntObjectHashMap<DatatableTuple>());

	private final DatatableNil niltype = new DatatableNil();

	public static StringMap getStringMap() {
		return ROOT_STRING_MAP;
	}

	public GenericDatatableMap() {
		stringmap = ROOT_STRING_MAP;
	}

	@Override
	public void set(DatatableTuple value) {
		set(value.hashCode(), value);
	}

	@Override
	public void set(String key, DatatableTuple value) {
		setRaw(stringmap.register(key), value);
	}

	@Override
	public void set(int key, DatatableTuple value) {
		if (stringmap.getString(key) == null) {
			throw new IllegalArgumentException("Key " + key + " does not have a matching string");
		} else {
			setRaw(key, value);
		}
	}
	
	private void setRaw(int key, DatatableTuple value) {
		value.setKey(key);
		map.put(key, value);
	}

	@Override
	public DatatableTuple get(String key) {
		int intKey = getIntKey(key);
		DatatableTuple value = map.get(intKey);
		if (value == null) {
			return niltype;
		} else {
			return value;
		}
	}

	@Override
	public byte[] compress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void decompress(byte[] compressedData) {
		// TODO Auto-generated method stub
	}

	@Override
	public void output(OutputStream out) throws IOException {

		// Should include table with
		// int -> String mappings
		// All entries in the map as
		// int -> DatatableObjects
	}

	public void input(InputStream in) throws IOException {

	}

	@Override
	public boolean contains(String key) {
		int intKey = getIntKey(key);
		return map.containsKey(intKey);
	}
	
	@Override
	public boolean contains(int key) {
		return map.containsKey(key);
	}

	@Override
	public int getIntKey(String key) {
		return stringmap.register(key);
	}

	@Override
	public String getStringKey(int key) {
		return stringmap.getString(key);
	}

	@Override
	public DatatableTuple get(int key) {
		return map.get(key);
	}
}
