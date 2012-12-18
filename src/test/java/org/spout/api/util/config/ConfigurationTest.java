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
package org.spout.api.util.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import org.spout.api.exception.ConfigurationException;

public class ConfigurationTest {
	private MapConfiguration config;

	@Before
	public void setUp() throws ConfigurationException {
		config = createConfiguration();
		config.load();
	}

	public Map<Object, Object> getConfigMap() {
		Map<Object, Object> newData = new HashMap<Object, Object>();
		newData.put("string-type", "someString");
		newData.put("int-type", 45);
		Map<Object, Object> testNested = new HashMap<Object, Object>();
		testNested.put("bar", "baz");
		newData.put("foo", testNested);
		return newData;
	}

	public MapConfiguration createConfiguration() {
		return new MapConfiguration(getConfigMap());
	}

	@Test
	public void testLoadSave() throws ConfigurationException {
		config.load();
		config.save();
		assertEquals(getConfigMap(), config.getMap());
	}

	@Test
	public void testGetNode() {
		ConfigurationNode node = config.getNode("string-type");
		assertEquals("someString", node.getValue());
		node = config.getNode("foo.bar");
		assertEquals("baz", node.getValue());
	}

	@Test
	public void testGetNewNode() {
		ConfigurationNode node = config.getNode("unknown.node");
		assertTrue(node != null);
		assertEquals(null, node.getValue());
		assertFalse(node.isAttached());
		assertEquals(null, node.getParent());
	}

	private static final String TEST_PATH = "another.unknown.node";
	private static final String TEST_VALUE = "Never gonna give you up!";

	@Test
	public void testSetNewNode() {
		ConfigurationNode node = config.getNode(TEST_PATH);
		assertEquals(null, node.getValue());
		node.setValue(TEST_VALUE);
		assertEquals(TEST_VALUE, node.getString());
		assertEquals(node, config.getNode(TEST_PATH));
		assertEquals(TEST_VALUE, config.getNode(TEST_PATH).getString());

	}

	@Test
	public void testPathSeparator() {
		String actualValue = config.getNode("foo", "bar").getString();
		String value = config.getNode("foo.bar").getString();
		assertEquals(actualValue, value);
		config.setPathSeparator("/");
		value = config.getNode("foo/bar").getString();
		assertEquals(actualValue, value);
		config.setPathSeparator(".");
	}
}
