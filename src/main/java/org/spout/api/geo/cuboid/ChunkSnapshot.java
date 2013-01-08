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
package org.spout.api.geo.cuboid;

import java.util.List;

import org.spout.api.component.type.BlockComponent;
import org.spout.api.datatable.SerializableMap;
import org.spout.api.entity.EntitySnapshot;
import org.spout.api.generator.biome.BiomeManager;
import org.spout.api.geo.AreaBlockSource;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.util.cuboid.CuboidLightBuffer;
import org.spout.api.util.thread.annotation.SnapshotRead;

public abstract class ChunkSnapshot extends Cube implements AreaBlockSource {
	/**
	 * Internal size of a side of a chunk
	 */
	public final static int CHUNK_SIZE = 16;

	/**
	 * Number of bits on the side of a chunk
	 */
	public final static int CHUNK_SIZE_BITS = 4;

	/**
	 * Mask to convert a block integer coordinate into the chunk's base
	 */
	public final static int BASE_MASK = -CHUNK_SIZE;

	public ChunkSnapshot(World world, float x, float y, float z) {
		super(new Point(world, x, y, z), CHUNK_SIZE);
	}

	/**
	 * Gets a copy of the raw block ids.
	 * 
	 * @return raw block ids
	 */
	public abstract short[] getBlockIds();

	/**
	 * Gets a copy of the raw block data.
	 * 
	 * @return block data
	 */
	public abstract short[] getBlockData();

	/**
	 * Gets a copy of the raw block light data. <br/><br/> 
	 * 
	 * Light is stored in nibbles, with the first index even, the second odd.
	 * @return raw block light data
	 */
	public abstract byte[] getBlockLight();

	/**
	 * Gets a copy of the raw sky light data. <br/><br/> 
	 * 
	 * Light is stored in nibbles, with the first index even, the second odd.
	 * @return raw skylight data
	 */
	public abstract byte[] getSkyLight();
	
	/**
	 * Gets an array containing a copy of the light data arrays
	 * 
	 * @return
	 */
	public abstract CuboidLightBuffer[] getLightBuffers();

	/**
	 * Gets the region that this chunk is located in.
	 *
	 * @return
	 */
	public abstract Region getRegion();

	/**
	 * Gets an unmodifiable list of all of the entity snapshots
	 * associated with this chunk.
	 *
	 * @return the entities
	 */
	public abstract List<EntitySnapshot> getEntities();

	/**
	 * Gets if this chunk snapshot had already been populated.
	 *
	 * @return if the chunk snapshot was populated.
	 */
	public abstract boolean isPopulated();

	/**
	 * Gets a copy of the biome manager associated with this chunk.
	 * 
	 * @return biome manager
	 */
	@SnapshotRead
	public abstract BiomeManager getBiomeManager();

	/**
	 * A thread-safe copy of the map of data attached to the chunk.
	 *
	 * @return data map
	 */
	public abstract SerializableMap getDataMap();

	/**
	 * Gets a unmodifiable list of all of the block component snapshots
	 * associated with this chunk.
	 * 
	 * @return list of block component snapshots
	 */
	public abstract List<BlockComponentSnapshot> getBlockComponents();

	public static enum SnapshotType {
		/**
		 * Loads no block data (ids, data, skylight, blocklight) in the snapshot
		 */
		NO_BLOCK_DATA,
		/**
		 * Loads only block ids, no block data, skylight, or blocklight
		 */
		BLOCK_IDS_ONLY,
		/**
		 * Loads only block ids and block data, no skylight or blocklight
		 */
		BLOCKS_ONLY,
		/**
		 * Loads only skylight and blocklight, no block ids or block data
		 */
		LIGHT_ONLY,
		/**
		 * Loads block ids, block data, skylight, and blocklight
		 */
		BOTH,
	}

	public static enum EntityType {
		/**
		 * Saves no entity information in the snapshot
		 */
		NO_ENTITIES,
		/**
		 * Saves class and datatable information regarding block components in the snapshot
		 */
		BLOCK_COMPONENTS,
		/**
		 * Saves entity snapshot information
		 */
		ENTITIES,
		/**
		 * Saves both entity and block component information in the snapshot
		 */
		BOTH
	}

	public static enum ExtraData {
		/**
		 * Loads no extra data in the snapshot
		 */
		NO_EXTRA_DATA,
		/**
		 * Loads only extra biome data in the snapshot
		 */
		BIOME_DATA,
		/**
		 * Loads only extra datatable information in the snapshot
		 */
		DATATABLE,
		/**
		 * Loads both biome and datatable information in the snapshot
		 */
		BOTH;
	}

	public static interface BlockComponentSnapshot {
		/**
		 * Gets the world block x-coordinate for this snapshot
		 * 
		 * @return x-coordinate
		 */
		public int getX();

		/**
		 * Gets the world block x-coordinate for this snapshot
		 * 
		 * @return y-coordinate
		 */
		public int getY();

		/**
		 * Gets the world block x-coordinate for this snapshot
		 * 
		 * @return z-coordinate
		 */
		public int getZ();

		/**
		 * Gets the block component class
		 * 
		 * @return block component class
		 */
		public Class<? extends BlockComponent> getComponent();

		/**
		 * Gets a copy of the block component data
		 * 
		 * @return data
		 */
		public SerializableMap getData();
	}
}
