/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
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
package org.spout.engine.filesystem.versioned;

import gnu.trove.procedure.TShortObjectProcedure;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.spout.api.Spout;
import org.spout.api.component.type.BlockComponent;
import org.spout.api.datatable.ManagedHashMap;
import org.spout.api.geo.cuboid.ChunkSnapshot.BlockComponentSnapshot;
import org.spout.api.material.ComplexMaterial;
import org.spout.api.material.block.BlockFullState;
import org.spout.api.util.NBTMapper;
import org.spout.api.util.StringMap;
import org.spout.api.util.hashing.ByteTripleHashed;
import org.spout.api.util.hashing.NibbleQuadHashed;
import org.spout.api.util.hashing.SignedTenBitTripleHashed;
import org.spout.api.util.sanitation.SafeCast;
import org.spout.api.util.typechecker.TypeChecker;
import org.spout.engine.SpoutEngine;
import org.spout.engine.filesystem.ChunkDataForRegion;
import org.spout.engine.world.SpoutChunk;
import org.spout.engine.world.SpoutChunk.PopulationState;
import org.spout.engine.world.SpoutChunkSnapshot;
import org.spout.engine.world.SpoutRegion;
import org.spout.engine.world.SpoutWorld;
import org.spout.engine.world.dynamic.DynamicBlockUpdate;
import org.spout.nbt.ByteArrayTag;
import org.spout.nbt.ByteTag;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.IntArrayTag;
import org.spout.nbt.IntTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.LongTag;
import org.spout.nbt.ShortTag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

public class ChunkFiles {
	
	private static final TypeChecker<List<? extends CompoundTag>> checkerListCompoundTag = TypeChecker.tList(CompoundTag.class);
	
	public static final byte CHUNK_VERSION = 1;
	
	public static SpoutChunk loadChunk(SpoutRegion r, int x, int y, int z, InputStream dis, ChunkDataForRegion dataForRegion) {
		SpoutChunk chunk = null;
		NBTInputStream is = null;

		try {
			if (dis == null) {
				//The inputstream is null because no chunk data exists
				Spout.getLogger().info("No input stream for chunk");
				return chunk;
			}

			is = new NBTInputStream(dis, false);
			CompoundTag chunkTag = (CompoundTag) is.readTag();
			CompoundMap map = chunkTag.getValue();

			byte version = SafeCast.toByte(NBTMapper.toTagValue(map.get("version")), (byte) -1);

			if (version == -1) {
				Spout.getLogger().info("Version = -1 for chunk");
				return null;
			}
			
			if (version > CHUNK_VERSION) {
				Spout.getLogger().log(Level.SEVERE, "Chunk version " + version + " exceeds maximum allowed value of " + CHUNK_VERSION);
				return null;
			} else if (version < CHUNK_VERSION) {
				// TODO - Add conversion code here
				Spout.getLogger().log(Level.SEVERE, "Outdated Chunk version " + version);
				return null;
			}
			
			// Add conversion code here

			return loadChunk(r, x, y, z, dataForRegion, map, version);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ignore) {
				}
			}
		}
		return chunk;
	}
	
	public static SpoutChunk loadChunk(SpoutRegion r, int x, int y, int z, ChunkDataForRegion dataForRegion, CompoundMap map, int version) throws IOException {
		
		SpoutChunk chunk = null;
		
		int cx = r.getChunkX() + x;
		int cy = r.getChunkY() + y;
		int cz = r.getChunkZ() + z;

		//Convert world block ids to engine material ids
		SpoutWorld world = r.getWorld();
		StringMap global = ((SpoutEngine) Spout.getEngine()).getEngineItemMap();
		StringMap itemMap = world.getItemMap();

		byte[] skyLight = SafeCast.toByteArray(NBTMapper.toTagValue(map.get("skyLight")), null);
		byte[] blockLight = SafeCast.toByteArray(NBTMapper.toTagValue(map.get("blockLight")), null);
		byte[] extraData = SafeCast.toByteArray(NBTMapper.toTagValue(map.get("extraData")), null);

		ManagedHashMap extraDataMap = new ManagedHashMap();
		extraDataMap.deserialize(extraData);

		boolean skipScan = false;

		byte populationState = SafeCast.toGeneric(map.get("populationState"), new ByteTag("", PopulationState.POPULATED.getId()), ByteTag.class).getValue();
		boolean lightStable = SafeCast.toByte(NBTMapper.toTagValue(map.get("lightStable")), (byte) 0) != 0;

		int[] palette = SafeCast.toIntArray(NBTMapper.toTagValue(map.get("palette")), null);
		if (palette == null) {
			short[] blocks = SafeCast.toShortArray(NBTMapper.toTagValue(map.get("blocks")), null);
			short[] data = SafeCast.toShortArray(NBTMapper.toTagValue(map.get("data")), null);
			for (int i = 0; i < blocks.length; i++) {
				blocks[i] = (short) itemMap.convertTo(global, blocks[i]);
			}
			chunk = new SpoutChunk(r.getWorld(), r, cx, cy, cz, PopulationState.byID(populationState), blocks, data, skyLight, blockLight, extraDataMap, lightStable);
		} else {
			int blockArrayWidth = SafeCast.toInt(NBTMapper.toTagValue(map.get("packedWidth")), -1);
			int[] variableWidthBlockArray = SafeCast.toIntArray(NBTMapper.toTagValue(map.get("packedBlockArray")), null);

			if (palette.length > 0) {
				convertArray(palette, itemMap, global);
				skipScan = componentSkipCheck(palette);
			} else {
				convertArray(variableWidthBlockArray, itemMap, global);
				skipScan = componentSkipCheck(variableWidthBlockArray);
			}
			chunk = new SpoutChunk(r.getWorld(), r, cx, cy, cz, PopulationState.byID(populationState), palette, blockArrayWidth, variableWidthBlockArray, skyLight, blockLight, extraDataMap, lightStable);
		}

		CompoundMap entityMap = SafeCast.toGeneric(NBTMapper.toTagValue(map.get("entities")), (CompoundMap) null, CompoundMap.class);
		EntityFiles.loadEntities(r, entityMap, dataForRegion.loadedEntities);

		List<? extends CompoundTag> updateList = checkerListCompoundTag.checkTag(map.get("dynamic_updates"));
		loadDynamicUpdates(updateList, dataForRegion.loadedUpdates);

		List<? extends CompoundTag> componentsList = checkerListCompoundTag.checkTag(map.get("block_components"), null);

		//Load Block components
		//This is a three-part process
		//1.) Scan the blocks and add them to the chunk map
		//2.) Load the datatables associated with the block components
		//3.) Attach the components
		if (!skipScan) {
			chunk.blockComponentScan();
		}
		//Load data associated with block components
		loadBlockComponents(chunk, componentsList);
		//Attach block components
		chunk.getBlockComponents().forEachEntry(new AttachComponentProcedure());
		return chunk;
	}
	
	public static void saveChunk(SpoutWorld world, SpoutChunkSnapshot snapshot, List<DynamicBlockUpdate> blockUpdates, OutputStream dos) {
		CompoundMap chunkTags = new CompoundMap();

		//Switch block ids from engine material ids to world specific ids
		StringMap global = ((SpoutEngine) Spout.getEngine()).getEngineItemMap();
		StringMap itemMap = world.getItemMap();

		int[] palette = snapshot.getPalette();
		int[] packetBlockArray = snapshot.getPackedBlockArray();
		int packedWidth = snapshot.getPackedWidth();

		if (palette.length > 0) {
			convertArray(palette, global, itemMap);
		} else {
			convertArray(packetBlockArray, global, itemMap);
		}

		chunkTags.put(new ByteTag("version", CHUNK_VERSION));
		chunkTags.put(new ByteTag("format", (byte) 0));
		chunkTags.put(new IntTag("x", snapshot.getX()));
		chunkTags.put(new IntTag("y", snapshot.getY()));
		chunkTags.put(new IntTag("z", snapshot.getZ()));
		chunkTags.put(new ByteTag("populationState", snapshot.getPopulationState().getId()));
		chunkTags.put(new ByteTag("lightStable", snapshot.isLightStable()));
		chunkTags.put(new IntArrayTag("palette", palette));
		chunkTags.put(new IntTag("packedWidth", packedWidth));
		chunkTags.put(new IntArrayTag("packedBlockArray", packetBlockArray));
		chunkTags.put(new ByteArrayTag("skyLight", snapshot.getSkyLight()));
		chunkTags.put(new ByteArrayTag("blockLight", snapshot.getBlockLight()));
		chunkTags.put(new CompoundTag("entities", EntityFiles.saveEntities(snapshot.getEntities())));
		chunkTags.put(saveDynamicUpdates(blockUpdates));
		chunkTags.put(saveBlockComponents(snapshot.getBlockComponents()));
		chunkTags.put(new ByteArrayTag("extraData", snapshot.getDataMap().serialize()));

		CompoundTag chunkCompound = new CompoundTag("chunk", chunkTags);

		NBTOutputStream os = null;
		try {
			os = new NBTOutputStream(dos, false);
			os.writeTag(chunkCompound);
		} catch (IOException e) {
			Spout.getLogger().log(Level.SEVERE, "Error saving chunk {" + snapshot.getX() + ", " + snapshot.getY() + ", " + snapshot + "}", e);
		}

		world.getItemMap().save();
	}
	
	private static void convertArray(int[] fullState, StringMap from, StringMap to) {
		for (int i = 0; i < fullState.length; i++) {
			short newId = (short) from.convertTo(to, BlockFullState.getId(fullState[i]));
			short oldData = BlockFullState.getData(fullState[i]);
			fullState[i] = BlockFullState.getPacked(newId, oldData);
		}
	}

	private static boolean componentSkipCheck(int[] fullState) {
		for (int i = 0; i < fullState.length; i++) {
			if (BlockFullState.getMaterial(fullState[i]) instanceof ComplexMaterial) {
				return false;
			}
		}
		return true;
	}
	
	private static CompoundTag saveBlockComponent(BlockComponentSnapshot snapshot) {
		if (!snapshot.getData().isEmpty()) {
			byte[] data = snapshot.getData().serialize();

			if (data != null && data.length > 0) {
				CompoundMap map = new CompoundMap();
				short packed = NibbleQuadHashed.key(snapshot.getX(), snapshot.getY(), snapshot.getZ(), 0);
				map.put(new ShortTag("packed", packed));
				map.put(new ByteArrayTag("data", data));

				return new CompoundTag("block_component_" + packed, map);
			}
		}

		return null;
	}

	private static ListTag<CompoundTag> saveDynamicUpdates(List<DynamicBlockUpdate> updates) {
		List<CompoundTag> list = new ArrayList<CompoundTag>(updates.size());

		for (DynamicBlockUpdate update : updates) {
			CompoundTag tag = saveDynamicUpdate(update);
			if (tag != null) {
				list.add(tag);
			}
		}

		return new ListTag<CompoundTag>("dynamic_updates", CompoundTag.class, list);
	}

	private static void loadBlockComponents(SpoutChunk chunk, List<? extends CompoundTag> list) {
		if (list == null) {
			return;
		}

		for (CompoundTag compoundTag : list) {
			CompoundMap map = compoundTag.getValue();
			short packed = (Short) map.get("packed").getValue();
			ByteArrayTag data = (ByteArrayTag) map.get("data");

			BlockComponent component = chunk.getBlockComponents().get(packed);
			if (component != null) {
				try {
					component.getOwner().getData().deserialize(data.getValue());
				} catch (IOException e) {
					Spout.getLogger().log(Level.SEVERE, "Unhandled exception deserializing block component data", e);
				}
			}
		}
	}
	
	private static ListTag<CompoundTag> saveBlockComponents(List<BlockComponentSnapshot> components) {
		List<CompoundTag> list = new ArrayList<CompoundTag>(components.size());

		for (BlockComponentSnapshot snapshot : components) {
			CompoundTag tag = saveBlockComponent(snapshot);
			if (tag != null) {
				list.add(tag);
			}
		}
		return new ListTag<CompoundTag>("block_components", CompoundTag.class, list);
	}

	private static CompoundTag saveDynamicUpdate(DynamicBlockUpdate update) {
		CompoundMap map = new CompoundMap();

		map.put(new IntTag("packedv2", update.getPacked()));
		map.put(new LongTag("nextUpdate", update.getNextUpdate()));
		map.put(new IntTag("data", update.getData()));

		return new CompoundTag("update", map);
	}

	private static void loadDynamicUpdates(List<? extends CompoundTag> list, List<DynamicBlockUpdate> loadedUpdates) {
		if (list == null) {
			return;
		}

		for (CompoundTag compoundTag : list) {
			DynamicBlockUpdate update = loadDynamicUpdate(compoundTag);
			if (update == null) {
				continue;
			}

			loadedUpdates.add(update);
		}
	}

	private static DynamicBlockUpdate loadDynamicUpdate(CompoundTag compoundTag) {
		final CompoundMap map = compoundTag.getValue();
		int packed = SafeCast.toInt(NBTMapper.toTagValue(map.get("packedv2")), -1);
		if (packed == -1) {
			packed = SafeCast.toInt(NBTMapper.toTagValue(map.get("packed")), -1);
			if (packed < 0) {
				return null;
			} else {
				int x = 0xFF & ByteTripleHashed.key1(packed);
				int y = 0xFF & ByteTripleHashed.key2(packed);
				int z = 0xFF & ByteTripleHashed.key3(packed);
				packed = SignedTenBitTripleHashed.key(x, y, z);
			}
		}
		final long nextUpdate = SafeCast.toLong(NBTMapper.toTagValue(map.get("nextUpdate")), -1L);
		if (nextUpdate < 0) {
			return null;
		}

		final int data = SafeCast.toInt(NBTMapper.toTagValue(map.get("data")), 0);
		return new DynamicBlockUpdate(packed, nextUpdate, data);
	}
	
	private static class AttachComponentProcedure implements TShortObjectProcedure<BlockComponent> {
		@Override
		public boolean execute(short a, BlockComponent b) {
			try {
				b.onAttached();
			} catch (Exception e) {
				Spout.getLogger().log(Level.SEVERE, "Unhandled exception attaching block component", e);
			}
			return true;
		}
	}
	
}
