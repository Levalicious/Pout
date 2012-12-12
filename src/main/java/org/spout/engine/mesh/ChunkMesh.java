/*
 * This file is part of Spout.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
 * Spout is licensed under the SpoutDev License Version 1.
 *
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Spout is distributed in the hope that it will be useful,
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
package org.spout.engine.mesh;

import gnu.trove.list.array.TFloatArrayList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.geo.cuboid.ChunkSnapshotModel;
import org.spout.api.geo.discrete.Point;
import org.spout.api.material.BlockMaterial;
import org.spout.api.material.Material;
import org.spout.api.material.block.BlockFace;
import org.spout.api.math.Vector3;
import org.spout.api.model.mesh.MeshFace;
import org.spout.api.model.mesh.OrientedMesh;
import org.spout.api.model.mesh.OrientedMeshFace;
import org.spout.api.model.mesh.Vertex;
import org.spout.api.render.RenderMaterial;
import org.spout.api.render.effect.SnapshotMesh;
import org.spout.api.util.bytebit.ByteBitSet;
import org.spout.engine.renderer.BatchVertexRenderer;
import org.spout.engine.renderer.BufferContainer;
import org.spout.engine.world.SpoutChunkSnapshotModel;

/**
 * Represents a mesh for a chunk.
 */
public class ChunkMesh{

	private HashMap<RenderMaterial, BufferContainer> meshs = new HashMap<RenderMaterial, BufferContainer>();

	private SpoutChunkSnapshotModel chunkModel;
	private ChunkSnapshot center;
	private final World world;
	private final int chunkX,chunkY,chunkZ;
	private boolean isUnloaded = false;
	private boolean first = false;

	/**
	 * Time of the used SpoutChunkSnapshotModel generation
	 * To benchmark purpose
	 */
	private final long time;

	public ChunkMesh(SpoutChunkSnapshotModel chunkModel) {
		this.chunkModel = chunkModel;
		first = chunkModel.isFirst();

		world = chunkModel.getWorld();

		chunkX = chunkModel.getX();
		chunkY = chunkModel.getY();
		chunkZ = chunkModel.getZ();

		time = chunkModel.getTime();
	}

	public int getChunkX(){
		return chunkX;
	}

	public int getChunkY(){
		return chunkY;
	}

	public int getChunkZ(){
		return chunkZ;
	}

	public void update(){
		if(chunkModel.isUnload()){
			isUnloaded = true;
			return;
		}

		center = chunkModel.getCenter();

		//Update mesh vertex and light
		updateBlock();
		updateLight();

		// Free memory
		chunkModel = null;
		center = null;
	}

	/**
	 * Updates the mesh.
	 */
	private void updateBlock() {
		for (int x = center.getBase().getBlockX(); x < center.getBase().getBlockX() + Chunk.BLOCKS.SIZE; x++) {
			for (int y = center.getBase().getBlockY(); y < center.getBase().getBlockY() + Chunk.BLOCKS.SIZE; y++) {
				for (int z = center.getBase().getBlockZ(); z < center.getBase().getBlockZ() + Chunk.BLOCKS.SIZE; z++) {
					generateBlockVertices(chunkModel,x, y, z);
				}
			}
		}
	}

	/**
	 * Updates the mesh light, the block vertices MUST BE done before
	 */
	private void updateLight() {
		if(chunkModel.isUnload())
			throw new IllegalStateException("ChunkSnapshotModel with Unload state can't be used to compute light");

		for(BufferContainer container : meshs.values()){

			TFloatArrayList vertexBuffer = (TFloatArrayList) container.getBuffers().get(BatchVertexRenderer.VERTEX_LAYER);
			
			/*
			 * Use a shader light (2) and skylight (4)
			 * 
			 * WE NEED TO USE 2 BECAUSE WE DON'T USE COLOR
			 * OPENGL 2 NEED TO USE LAYOUT IN THE ORDER
			 * WE CAN'T USE 3 IF 2 ISN'T USED
			 * 
			 * One float per vertice
			 * file://Vanilla/resources/shaders/terrain.120.vert 
			 * file://Vanilla/resources/shaders/terrain.330.vert
			 */
			
			TFloatArrayList lightBuffer = (TFloatArrayList) container.getBuffers().get(BatchVertexRenderer.COLOR_LAYER);
			TFloatArrayList skylightBuffer = (TFloatArrayList) container.getBuffers().get(4);

			if(lightBuffer==null){
				lightBuffer = new TFloatArrayList(vertexBuffer.size() / 4);
				container.setBuffers(BatchVertexRenderer.COLOR_LAYER, lightBuffer);
			}
			
			if(skylightBuffer==null){
				skylightBuffer = new TFloatArrayList(vertexBuffer.size() / 4);
				container.setBuffers(4, skylightBuffer);
			}

			for(int i = 0; i < vertexBuffer.size();){
				float x = vertexBuffer.get(i++);
				float y = vertexBuffer.get(i++);
				float z = vertexBuffer.get(i++);
				i++; // w component

				//TODO : Create a buffer for each light registred by plugin

				generateLightOnVertices( chunkModel, x, y, z, lightBuffer, skylightBuffer);
			}
		}
	}

	/**
	 * Compute the light for one vertex
	 * @param chunkModel
	 * @param x
	 * @param y
	 * @param z
	 * @param lightBuffer 
	 * @return
	 */
	private void generateLightOnVertices(SpoutChunkSnapshotModel chunkModel, float x, float y, float z, TFloatArrayList lightBuffer, TFloatArrayList skylightBuffer) {
		int xi = (int)x;
		int yi = (int)y;
		int zi = (int)z;
		if(chunkModel != null){
			float light = 0;
			float skylight = 0;
			int count = 0;

			//TODO : Make it use each sort of light if plugin can add others lights later

			int xs = (x == xi) ? (xi - 1) : xi;
			int ys = (y == yi) ? (yi - 1) : yi;
			int zs = (z == zi) ? (zi - 1) : zi;

			for (int xx = xs; xx <= xi; xx++) {
				for (int yy = ys; yy <= yi; yy++) {
					for (int zz = zs; zz <= zi; zz++) {
						ChunkSnapshot chunk = chunkModel.getChunkFromBlock(xx, yy, zz);
						BlockMaterial m = chunk.getBlockMaterial(xx, yy, zz);
						if (!m.isOpaque()) {
							light += chunk.getBlockLight(xx, yy, zz);
							skylight += chunk.getBlockSkyLightRaw(xx, yy, zz); //use the SkyLightRaw, the real sky state would be apply by the shader
							count++;
						}
					}
				}
			}

			if (count == 0) {
				count++;
			}

			light /= count;
			skylight /= count;
			light /= 16;
			skylight /= 16;

			//TODO : To replace by 2 byte buffer for Vanilla
			
			lightBuffer.add(light);
			skylightBuffer.add(skylight);
		}else{
			lightBuffer.add(1f);
			lightBuffer.add(1f);
		}
	}

	public List<MeshFace> buildBlock(ChunkSnapshotModel chunkSnapshotModel,Material blockMaterial,
			Vector3 position, boolean toRender[], OrientedMesh mesh) {
		List<MeshFace> meshs = new ArrayList<MeshFace>();
		Vector3 model = new Vector3(position.getFloorX(), position.getFloorY(), position.getFloorZ());
		for(OrientedMeshFace meshFace : mesh){

			if(!meshFace.canRender(toRender))
				continue;

			Iterator<Vertex> it = meshFace.iterator();
			Vertex v1 = new Vertex(it.next());
			Vertex v2 = new Vertex(it.next());
			Vertex v3 = new Vertex(it.next());
			v1.position = v1.position.add(model);
			v2.position = v2.position.add(model);
			v3.position = v3.position.add(model);

			meshs.add(new MeshFace(v1, v2, v3));
		}
		return meshs;
	}

	/**
	 * Generates the vertices of the given block and adds them to the ChunkMesh.
	 * @param chunkSnapshotModel 
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	private void generateBlockVertices(SpoutChunkSnapshotModel chunkSnapshotModel, int x, int y, int z) {
		BlockMaterial material = center.getBlockMaterial(x, y, z);

		if (material.isInvisible()) {
			return;
		}

		RenderMaterial renderMaterial = material.getModel().getRenderMaterial();

		if( !chunkModel.hasRenderMaterial(renderMaterial) ){
			return;
		}

		Vector3 position = new Vector3(x, y, z);

		boolean toRender[] = new boolean[OrientedMeshFace.shouldRender.length];
		boolean fullyOccluded = true;
		for(int i = 0; i < OrientedMeshFace.shouldRender.length; i++){
			BlockFace face = OrientedMeshFace.shouldRender[i];
			Vector3 facePos = position.add(face.getOffset());
			int x1 = facePos.getFloorX();
			int y1 = facePos.getFloorY();
			int z1 = facePos.getFloorZ();

			BlockMaterial neighbor = chunkModel.getChunkFromBlock(x1, y1, z1).getBlockMaterial(x1, y1, z1);

			if (material.isFaceRendered(face, neighbor)) {
				toRender[i] = true;
				fullyOccluded = false;
			}else{
				toRender[i] = false;
				continue;
			}

			ByteBitSet occlusion = neighbor.getOcclusion(material.getData());

			if (occlusion.get(face.getOpposite())) {
				toRender[i] = false;
				continue;
			}else{
				toRender[i] = true;
				fullyOccluded = false;
			}
		}

		if(fullyOccluded)
			return;

		SnapshotMesh snapshotMesh = new SnapshotMesh(material, chunkSnapshotModel, new Point(position, world), toRender);

		renderMaterial.preMesh(snapshotMesh);
		List<MeshFace> faces = buildBlock(snapshotMesh.getSnapshotModel(), snapshotMesh.getMaterial(),
				snapshotMesh.getPosition(), snapshotMesh.getToRender(), (OrientedMesh)snapshotMesh.getMesh());
		snapshotMesh.setResult(faces);
		renderMaterial.postMesh(snapshotMesh);
		faces = snapshotMesh.getResult();

		if(!faces.isEmpty()){
			BufferContainer container = meshs.get(renderMaterial);

			if(container == null){
				container = new BufferContainer();
				meshs.put(renderMaterial, container);
			}

			TFloatArrayList vertexBuffer = (TFloatArrayList) container.getBuffers().get(BatchVertexRenderer.VERTEX_LAYER);
			TFloatArrayList normalBuffer = (TFloatArrayList) container.getBuffers().get(BatchVertexRenderer.NORMAL_LAYER);
			TFloatArrayList textureBuffer = (TFloatArrayList) container.getBuffers().get(BatchVertexRenderer.TEXTURE0_LAYER);

			if(vertexBuffer==null){
				vertexBuffer = new TFloatArrayList();
				container.setBuffers(BatchVertexRenderer.VERTEX_LAYER, vertexBuffer);
			}

			if(normalBuffer==null){
				normalBuffer = new TFloatArrayList();
				container.setBuffers(BatchVertexRenderer.NORMAL_LAYER, normalBuffer);
			}

			if(textureBuffer==null){
				textureBuffer = new TFloatArrayList();
				container.setBuffers(BatchVertexRenderer.TEXTURE0_LAYER, textureBuffer);
			}

			for (MeshFace meshFace : faces) {
				for (Vertex vert : meshFace) {

					vertexBuffer.add(vert.position.getX());
					vertexBuffer.add(vert.position.getY());
					vertexBuffer.add(vert.position.getZ());
					vertexBuffer.add(1f);

					if(vert.texCoord0 != null){
						textureBuffer.add(vert.texCoord0.getX());
						textureBuffer.add(vert.texCoord0.getY());
					}

					if(vert.normal != null){
						normalBuffer.add(vert.normal.getX());
						normalBuffer.add(vert.normal.getY());
						normalBuffer.add(vert.normal.getZ());
						normalBuffer.add(1f);
					}

					container.element++;
				}
			}
		}
	}

	/**
	 * Checks if the chunk mesh has any vertices.
	 * 
	 * @return
	 */
	public boolean hasVertices() {
		return !meshs.isEmpty();
	}

	@Override
	public String toString() {
		return "ChunkMesh [center=" + center + "]";
	}

	public boolean isUnloaded() {
		return isUnloaded;
	}

	public Map<RenderMaterial, BufferContainer> getMaterialsFaces() {
		return meshs;
	}

	public boolean isFirst() {
		return first;
	}

	public long getTime() {
		return time;
	}

	public World getWorld() {
		return world;
	}

}
