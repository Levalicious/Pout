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
package org.spout.api.data;

import static org.junit.Assert.*;

import org.junit.Test;

import org.spout.api.event.server.RetrieveDataEvent;
import org.spout.api.geo.World;

public class DataSubjectTest implements DataSubject {
	private final RetrieveDataEvent event = new RetrieveDataEvent(null, this, "foo.bar");

	@Test
	public void testDataSubject() {
		String node = "foo.bar";
		event.setResult(20);
		assertEquals(getData(node).getInt(), 20);
		event.setResult(20L);
		assertEquals(getData(node).getLong(), 20L);
		event.setResult(20.0d);
		assertEquals(getData(node).getDouble(), 20.0, 0d);
		event.setResult(true);
		assertEquals(getData(node).getBoolean(), true);
		event.setResult("baz");
		assertEquals(getData(node).getString(), "baz");
	}

	@Override
	public ValueHolder getData(String node) {
		return event.getResult();
	}

	@Override
	public ValueHolder getData(World world, String node) {
		return event.getResult();
	}

	@Override
	public boolean hasData(String node) {
		return false;
	}

	@Override
	public boolean hasData(World world, String node) {
		return false;
	}

	@Override
	public String getName() {
		return "DataSubjectTest";
	}
}
