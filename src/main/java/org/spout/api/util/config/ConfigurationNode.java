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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.spout.api.data.ValueHolder;
import org.spout.api.data.ValueHolderBase;
import org.spout.api.util.config.serialization.Serialization;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigurationNode extends AbstractConfigurationNodeSource implements ValueHolder {
	private Object value;
	private boolean attached;
	private WeakReference<ConfigurationNodeSource> parent = new WeakReference<ConfigurationNodeSource>(null);

	private final String[] path;
	private final ValueHolderBase valueHolder = new ValueHolderBase(this);

	public ConfigurationNode(Configuration config, String[] path, Object value) {
		super(config);
		this.path = path;
		if (value != null) {
			setValue(value);
		}
	}

	// Delegated methods

	@Override
	public boolean getBoolean() {
		return valueHolder.getBoolean();
	}

	@Override
	public boolean getBoolean(boolean def) {
		return valueHolder.getBoolean(def);
	}

	@Override
	public byte getByte() {
		return valueHolder.getByte();
	}

	@Override
	public byte getByte(byte def) {
		return valueHolder.getByte(def);
	}

	@Override
	public float getFloat() {
		return valueHolder.getFloat();
	}

	@Override
	public float getFloat(float def) {
		return valueHolder.getFloat(def);
	}

	@Override
	public short getShort() {
		return valueHolder.getShort();
	}

	@Override
	public short getShort(short def) {
		return valueHolder.getShort(def);
	}

	@Override
	public int getInt() {
		return valueHolder.getInt();
	}

	@Override
	public int getInt(int def) {
		return valueHolder.getInt(def);
	}

	@Override
	public long getLong() {
		return valueHolder.getLong();
	}

	@Override
	public long getLong(long def) {
		return valueHolder.getLong(def);
	}

	@Override
	public double getDouble() {
		return valueHolder.getDouble();
	}

	@Override
	public double getDouble(double def) {
		return valueHolder.getDouble(def);
	}

	@Override
	public String getString() {
		return valueHolder.getString();
	}

	@Override
	public String getString(String def) {
		return valueHolder.getString(def);
	}

	@Override
	public <T> T getTypedValue(Class<T> type) {
		return valueHolder.getTypedValue(type);
	}

	@Override
	public <T> T getTypedValue(Class<T> type, T def) {
		return valueHolder.getTypedValue(type, def);
	}

	@Override
	public Object getTypedValue(Type type) {
		return valueHolder.getTypedValue(type);
	}

	@Override
	public Object getTypedValue(Type type, Object def) {
		return valueHolder.getTypedValue(type, def);
	}

	@Override
	public List<?> getList() {
		return valueHolder.getList();
	}

	@Override
	public List<?> getList(List<?> def) {
		return valueHolder.getList(def);
	}

	@Override
	public List<String> getStringList() {
		return valueHolder.getStringList();
	}

	@Override
	public List<String> getStringList(List<String> def) {
		return valueHolder.getStringList(def);
	}

	@Override
	public List<Integer> getIntegerList() {
		return valueHolder.getIntegerList();
	}

	@Override
	public List<Integer> getIntegerList(List<Integer> def) {
		return valueHolder.getIntegerList(def);
	}

	@Override
	public List<Double> getDoubleList() {
		return valueHolder.getDoubleList();
	}

	@Override
	public List<Double> getDoubleList(List<Double> def) {
		return valueHolder.getDoubleList(def);
	}

	@Override
	public List<Boolean> getBooleanList() {
		return valueHolder.getBooleanList();
	}

	@Override
	public List<Boolean> getBooleanList(List<Boolean> def) {
		return valueHolder.getBooleanList(def);
	}

	// Actual value access

	@Override
	public Object getValue() {
		return getValue(null);
	}

	@Override
	public Object getValue(Object def) {
		if (hasChildren()) {
			return getValues();
		}

		if (value != null) {
			return value;
		}

		if (def != null && getConfiguration().doesWriteDefaults()) {
			setValue(def);
		}

		return def;
	}

	/**
	 * Sets the configuration's value
	 *
	 * @param value The value to set
	 * @return The previous value of the configuration
	 */
	public Object setValue(Object value) {
		if (value instanceof ValueHolder) {
			value = ((ValueHolder) value).getValue();
		}
		checkAdded();
		Object old = this.getValue();
		if (value instanceof Map<?, ?>) {
			this.value = null;
			removeChildren();
			for (Map.Entry<?, ?> entry : ((Map<?, ?>)value).entrySet()) {
				addChild(createConfigurationNode(ArrayUtils.add(getPathElements(), entry.getKey().toString()), entry.getValue()));
			}
		} else {
			if (value != null) {
				removeChildren();
			}
			this.value = value;
		}
		return old;
	}

	/**
	 * Sets the
	 * @param type
	 * @param value
	 * @return
	 */
	public Object setValue(Type type, Object value) {
		return setValue(Serialization.serialize(type, value));
	}

	// Util methods to make sure stuff is connected properly

	/**
	 * Detach all this node's children, making sure to remove associations both ways
	 */
	private void removeChildren() {
		for (ConfigurationNode node : children.values()) {
			detachChild(node);
		}
		children.clear();
	}

	protected void checkAdded() {
		if (!isAttached()) {
			getConfiguration().setNode(this);
		}
	}

	public void remove() {
		if (isAttached()) {
			getParent().removeChild(this);
		}
		this.value = null;
	}

	/**
	 * Return whether a ConfigurationNode is attached to any configuration
	 * @return if this node is attached to any configuration
	 */
	public boolean isAttached() {
		return attached;
	}

	protected void setAttached(boolean value) {
		this.attached = value;
	}

	public ConfigurationNodeSource getParent() {
		return parent.get();
	}

	protected void setParent(ConfigurationNodeSource parent) {
		if (parent == this) {
			throw new IllegalArgumentException("Attempted circular inheritance between child" + getPath() + " and parent.");
		}
		Set<ConfigurationNodeSource> visited = new HashSet<ConfigurationNodeSource>();
		ConfigurationNodeSource oldParent = getParent();
		while (oldParent != null) {
			if (visited.contains(oldParent)) {
				throw new IllegalArgumentException("Attempted circular inheritance between child " + getPath() + " and parent " +
						(oldParent instanceof ConfigurationNode ? ((ConfigurationNode) oldParent).getPath() : "root") + ".");
			}
			visited.add(oldParent);
			oldParent = oldParent instanceof ConfigurationNode ? ((ConfigurationNode) oldParent).getParent() : null;
		}
		this.parent = new WeakReference<ConfigurationNodeSource>(parent);
	}

	@Override
	public ConfigurationNode addChild(ConfigurationNode node) {
		checkAdded();
		return super.addChild(node);
	}

	/**
	 * @return The path, joined by the {@link AbstractConfiguration#getPathSeparator()} of the attached configuration
	 * @see #getPathElements
	 */
	public String getPath() {
		return StringUtils.join(getPathElements(), getConfiguration().getPathSeparator());
	}

	/**
	 * @return the elements of this node's path, unjoined
	 */
	@Override
	public String[] getPathElements() {
		return path;
	}
}
