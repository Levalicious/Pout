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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.spout.api.collision;

import org.spout.api.math.Vector3;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


public class CollisionHelperTest {
	public CollisionHelperTest() {
	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_BoundingBox_BoundingBox() {
		BoundingBox a = new BoundingBox(
			Vector3.create(1, 1, 1),
			Vector3.create(3, 3, 3));
		BoundingBox b = new BoundingBox(
			Vector3.create(2, 2, 2),
			Vector3.create(4, 4, 4));

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check shared points

		a = new BoundingBox(
			Vector3.create(4, 4, 4),
			Vector3.create(6, 6, 6));

		result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check not intersecting

		a = new BoundingBox(
			Vector3.create(6, 6, 6),
			Vector3.create(10, 10, 10));

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_BoundingBox_BoundingSphere() {
		BoundingBox a = new BoundingBox(
			Vector3.create(1, 1, 1),
			Vector3.create(3, 3, 3));

		BoundingSphere b = new BoundingSphere(
			Vector3.create(2, 2, 2),
			15);

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check not intersecting

		b = new BoundingSphere(
			Vector3.create(20, 20, 20),
			2);

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_BoundingBox_Segment() {
		BoundingBox a = new BoundingBox(
			Vector3.create(-2, -2, -2),
			Vector3.create(0, 0, 0));

		Segment b = new Segment(
			Vector3.create(-1, 5, -1),
			Vector3.create(-1, -5, -1));

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check shared endpoint

		b = new Segment(
			Vector3.create(0, 0, 0),
			Vector3.create(2, 2, 2));

		result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check outside

		b = new Segment(
			Vector3.create(1, 1, 1),
			Vector3.create(2, 2, 2));

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_BoundingBox_Plane() {
		BoundingBox a = new BoundingBox(
			Vector3.create(-1, -1, -1),
			Vector3.create(1, 1, 1));

		Plane b = new Plane(
			Vector3.create(0, 0, 0),
			Vector3.Up);

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check slanted plane

		b = new Plane(
			Vector3.create(0, 0, 0),
			Vector3.create(1, 1, 1).normalize());

		result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check non intersecting

		b = new Plane(
			Vector3.create(-2, -2, -2),
			Vector3.Up);

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_BoundingSphere_BoundingSphere() {

		BoundingSphere a = new BoundingSphere(
			Vector3.create(0, 0, 0),
			1);

		BoundingSphere b = new BoundingSphere(
			Vector3.create(0, 1, 0),
			1);

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check overlapping edges

		b = new BoundingSphere(
			Vector3.create(0, 2, 0),
			1);

		result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check not intersecting

		b = new BoundingSphere(
			Vector3.create(0, 3, 0),
			1);

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_BoundingSphere_Segment() {
		BoundingSphere a = new BoundingSphere(
			Vector3.create(1, 1, 1),
			1);

		Segment b = new Segment(
			Vector3.create(1, 2, 1),
			Vector3.create(1, 0, 1));

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check tangent

		b = new Segment(
			Vector3.create(-1, 0, 1),
			Vector3.create(1, 0, 1));

		result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Check not intersecting

		b = new Segment(
			Vector3.create(20, 20, 20),
			Vector3.create(21, 21, 21));

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_BoundingSphere_Ray() {
		BoundingSphere a = new BoundingSphere(
			Vector3.create(0, 0, 0),
			10);

		Ray b = new Ray(
			Vector3.create(0, 10, 0),
			Vector3.Up.scale(-1));

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Start within the sphere

		b = new Ray(
			Vector3.create(0, 0, 0),
			Vector3.create(1, 1, 1).normalize());

		result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Not colliding

		b = new Ray(
			Vector3.create(20, 20, 20),
			Vector3.create(1, 1, 1).normalize());

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_BoundingSphere_Plane() {
		//Edge collision

		BoundingSphere a = new BoundingSphere(
			Vector3.create(1, 1, 1),
			1);

		Plane b = new Plane(
			Vector3.create(0, 0, 0),
			Vector3.Up);

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Non collision

		b = new Plane(
			Vector3.create(-1, -1, -1),
			Vector3.Up);

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_Segment_Segment() {
			Segment a = new Segment(
			Vector3.create(0, 0, 0),
			Vector3.create(0, 10, 0));

		Segment b = new Segment(
			Vector3.create(-5, 5, 0),
			Vector3.create(5, 5, 0));

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Non collision

		b = new Segment(
			Vector3.create(-10, -10, -10),
			Vector3.create(-20, -20, -20));

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_Segment_Plane() {
		Segment a = new Segment(
			Vector3.create(0, -1, 0),
			Vector3.create(0, 1, 0));

		Plane b = new Plane(
			Vector3.create(0, 0, 0),
			Vector3.Up);

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Non intersection

		b = new Plane(
			Vector3.create(0, 2, 0),
			Vector3.Up);

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}

	/**
	 * Test of checkCollision method, of class CollisionHelper.
	 */
	@Test
	public void testCheckCollision_Plane_Plane() {
		Plane a = new Plane(
			Vector3.create(0, 0, 0),
			Vector3.Up);

		Plane b = new Plane(
			Vector3.create(1, 1, 1),
			Vector3.Right);

		boolean result = CollisionHelper.checkCollision(a, b);

		assertTrue(result);

		//Not intersecting (parallel)

		b = new Plane(
			Vector3.create(1, 1, 1),
			Vector3.Up);

		result = CollisionHelper.checkCollision(a, b);

		assertFalse(result);
	}
}
