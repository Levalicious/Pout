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
package org.spout.api.util.map.concurrent;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class AtomicByteArrayTest {
	private final static int LENGTH = 10000;
	private AtomicByteArray array = new AtomicByteArray(LENGTH);
	private byte[] arrayData;
	private int[] arrayIndex;

	@Before
	public void setUp() {
		Random rand = new Random();

		arrayData = new byte[LENGTH];
		arrayIndex = new int[LENGTH];

		for (int i = 0; i < LENGTH; i++) {
			arrayData[i] = (byte) rand.nextInt();
			arrayIndex[i] = i;
		}

		shuffle(arrayIndex);
	}

	private void shuffle(int[] deck) {
		Random rand = new Random();

		for (int placed = 0; placed < deck.length; placed++) {
			int remaining = deck.length - placed;
			int newIndex = rand.nextInt(remaining);
			swap(deck, placed, newIndex + placed);
		}
	}

	private void swap(int[] array, int i1, int i2) {
		int temp = array[i1];
		array[i1] = array[i2];
		array[i2] = temp;
	}

	@Test
	public void testArray() {
		Random rand = new Random();

		for (int i = 0; i < LENGTH; i++) {
			int index = arrayIndex[i];
			array.set(index, arrayData[index]);
		}

		for (int i = 0; i < LENGTH; i++) {
			assertTrue("Array data mismatch", array.get(i) == arrayData[i]);
		}

		for (int i = 0; i < LENGTH; i++) {
			compareAndSetTrue(rand.nextInt(LENGTH), (byte) rand.nextInt());
			compareAndSetFalse(rand.nextInt(LENGTH), (byte) rand.nextInt());
		}

		for (int i = 0; i < LENGTH; i++) {
			assertTrue("Array data mismatch after compare and set updates", array.get(i) == arrayData[i]);
		}
	}

	private void compareAndSetTrue(int index, byte value) {
		assertTrue("Compare and set attempt failed, expected value incorrect", array.compareAndSet(index, arrayData[index], value));
		arrayData[index] = value;
	}

	private void compareAndSetFalse(int index, byte value) {
		assertTrue("Compare and set attempt succeeded when it should have failed", !array.compareAndSet(index, (byte) (1 + arrayData[index]), value));
	}
}
