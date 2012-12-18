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
package org.spout.api.protocol;

import org.spout.api.inventory.Inventory;
import org.spout.api.inventory.ItemStack;

/**
 * An implementation of NetworkSynchronizer that doesn't do anything
 * used for when a NetworkSynchronizer has not been set
 */
public class NullNetworkSynchronizer extends NetworkSynchronizer {
	public NullNetworkSynchronizer(Session session) {
		super(session, 0);
	}

	public void onSlotSet(Inventory inventory, int slot, ItemStack item) {
	}

	public void updateAll(Inventory inventory, ItemStack[] slots) {
	}

	@Override
	public void finalizeTick() {
	}

	@Override
	public void preSnapshot() {
	}

}
