/*
 * This file is part of SpoutAPI.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
package org.spout.api.datatable.value;

import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.junit.Test;

public class DatatableSerializableTest {
	@Test
	public void testDataSubject() throws UnsupportedEncodingException {
		DatatableSerializable s1 = new DatatableSerializable(77);

		FakeObject1 f1 = new FakeObject1(21);

		s1.set(f1);

		byte[] compressed = s1.compress();

		DatatableSerializable s2 = new DatatableSerializable(77);

		s2.decompress(compressed);

		FakeObject1 f2 = (FakeObject1)s2.get();

		assertTrue("Object was not correctly recovered after compression/decompression", f2.x == f1.x);

		assertTrue("Unable to modify class name in serialized string", searchAndReplace(compressed, "FakeObject1", "FakeObject2"));

		s2.decompress(compressed);

		FakeObject2 f3 = (FakeObject2)s2.get();

		assertTrue("FakeObject2 was not correctly recovered after modification of the byte stream", f3.x == f1.x);

		assertTrue("Valid object returned true for isUnknownClass", !s2.isUnknownClass());

		assertTrue("Unable to modify class name in serialized string", searchAndReplace(compressed, "FakeObject2", "FakeObject3"));

		s2.decompress(compressed);

		Serializable f4 = s2.get();

		assertTrue("Null object not returned when attempting to deserialize a non-existant class", f4 == null);

		assertTrue("Unknown object returned false for isUnknownClass", s2.isUnknownClass());

		byte[] recompressed = s2.compress();

		assertTrue("Byte array mis-match when compressing an unknown class", Arrays.equals(recompressed, compressed));

		assertTrue("Unable to modify class name in serialized string", searchAndReplace(compressed, "FakeObject3", "FakeObject1"));

		FakeObject1 f5 = (FakeObject1)s2.get();

		assertTrue("Object was not correctly recovered after compression/decompression", f5.x == f1.x);

		s2.set(null);

		assertTrue("Null object returned true for isUnknownClass", !s2.isUnknownClass());

		assertTrue("Compare and set returned true when expected did not match", !s2.compareAndSet(f5, null));

		assertTrue("Compare and set returned true when expected did not match", !s2.compareAndSet(f5, f5));

		assertTrue("Compare and set returned false when expected matched", s2.compareAndSet(null, f5));

		assertTrue("Compare and set returned true when expected did not match", !s2.compareAndSet(null, f5));

		assertTrue("Compare and set returned false when expected matched", s2.compareAndSet(f5, f5));

		assertTrue("Compare and set returned false when expected matched", s2.compareAndSet(f5, null));

		assertTrue("Datatable has unexpected value", s2.get() == null);

		assertTrue("Compare and set returned false when expected matched", s2.compareAndSet(null, f5));

		assertTrue("Datatable has unexpected value", s2.get() == f5);
	}

	/*
	 * Used to modify the byte array generated by the object stream.  By changing the class name, it is possible to 
	 * emulate the effects of an object stream corresponding to an unknown class.
	 * 
	 * This can occur when a plugin specific class is saved to a DatatableSerializable.  The DatatableSerializable stores the object as a byte array.
	 */
	private boolean searchAndReplace(byte[] buf, String o, String n) throws UnsupportedEncodingException {
		byte[] oldArray = o.getBytes("UTF-8");
		byte[] newArray = n.getBytes("UTF-8");

		if (oldArray.length != newArray.length) {
			throw new IllegalArgumentException("Old and new arrays must be equal lengths");
		}
		for (int i = 0; i < buf.length; i++) {
			if (i + oldArray.length > buf.length) {
				return false;
			}

			boolean match = true;
			for (int j = 0; j < oldArray.length; j++) {
				if (buf[i + j] != oldArray[j]) {
					match = false;
					break;
				}
			}

			if (match) {
				for (int j = 0; j < oldArray.length; j++) {
					buf[i + j] = newArray[j];
				}
				return true;
			}
		}
		return false;
	}

	private static class FakeObject1 implements Serializable {
		private static final long serialVersionUID = 1L;

		private long x;

		public FakeObject1(long x) {
			this.x = x;
		}
	}

	private static class FakeObject2 implements Serializable {
		private static final long serialVersionUID = 1L;

		private long x;

		@SuppressWarnings("unused")
		public FakeObject2(long x) {
			this.x = x;
		}
	}
}
