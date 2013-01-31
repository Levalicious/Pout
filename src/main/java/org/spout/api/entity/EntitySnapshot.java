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
package org.spout.api.entity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.spout.api.component.Component;
import org.spout.api.datatable.ManagedHashMap;
import org.spout.api.datatable.SerializableMap;
import org.spout.api.geo.discrete.Transform;

public class EntitySnapshot {
	private final WeakReference<Entity> entity;
	private final int entityId;
	private final UUID uniqueId;
	private final Transform location;
	private final String worldName;
	private final UUID worldId;
	private final SerializableMap dataMap;
	private final int viewDistance;
	private final boolean observer;
	private final boolean savable;
	private final List<Class<? extends Component>> components;

	public EntitySnapshot(Entity e) {
		if (e.isRemoved()) {
			throw new IllegalArgumentException("Can not take a snapshot of a removed entity");
		}
		this.entity = new WeakReference<Entity>(e);
		this.entityId = e.getId();
		this.uniqueId = e.getUID();
		this.location = e.getScene().getTransform();
		this.worldName = e.getWorld().getName();
		this.worldId = e.getWorld().getUID();
		this.viewDistance = e.getViewDistance();
		this.observer = e.isObserver();
		this.savable = e.isSavable();
		if (e.getData().size() > 0) {
			this.dataMap = e.getData().deepCopy();
		} else {
			this.dataMap = new ManagedHashMap();
		}
		components = new ArrayList<Class<? extends Component>>();
		for (Component c : e.values()) {
			if (c.isDetachable()) {
				this.components.add(c.getClass());
			}
		}
	}

	public Entity getReference() {
		return entity.get();
	}

	public final int getId() {
		return entityId;
	}

	public final UUID getUID() {
		return uniqueId;
	}

	public final Transform getTransform() {
		return location;
	}

	public final UUID getWorldUID() {
		return worldId;
	}

	public String getWorldName() {
		return worldName;
	}

	public final SerializableMap getDataMap() {
		return dataMap;
	}

	public int getViewDistance() {
		return viewDistance;
	}

	public boolean isObserver() {
		return observer;
	}

	public boolean isSavable() {
		return savable;
	}

	public List<Class<? extends Component>> getComponents() {
		return components;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
