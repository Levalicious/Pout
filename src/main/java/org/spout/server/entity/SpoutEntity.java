/*
 * This file is part of Spout (http://www.spout.org/).
 *
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
package org.spout.server.entity;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.spout.api.Game;
import org.spout.api.Spout;
import org.spout.api.collision.CollisionHelper;
import org.spout.api.collision.CollisionModel;
import org.spout.api.collision.CollisionVolume;
import org.spout.api.datatable.DatatableTuple;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.entity.PlayerController;
import org.spout.api.entity.Position;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.Region;
import org.spout.api.geo.discrete.Point;
import org.spout.api.geo.discrete.Pointm;
import org.spout.api.geo.discrete.atomic.AtomicPoint;
import org.spout.api.geo.discrete.atomic.Transform;
import org.spout.api.inventory.Inventory;
import org.spout.api.io.store.simple.MemoryStore;
import org.spout.api.math.*;
import org.spout.api.model.Model;
import org.spout.api.player.Player;
import org.spout.api.util.StringMap;
import org.spout.api.util.concurrent.OptimisticReadWriteLock;
import org.spout.server.SpoutChunk;
import org.spout.server.SpoutRegion;
import org.spout.server.SpoutServer;
import org.spout.server.SpoutWorld;
import org.spout.server.datatable.SpoutDatatableMap;
import org.spout.server.datatable.value.SpoutDatatableBool;
import org.spout.server.datatable.value.SpoutDatatableFloat;
import org.spout.server.datatable.value.SpoutDatatableInt;
import org.spout.server.datatable.value.SpoutDatatableObject;
import org.spout.server.player.SpoutPlayer;
import org.spout.server.util.thread.snapshotable.SnapshotableBoolean;

public class SpoutEntity implements Entity {

	private static final long serialVersionUID = 1L;
	public final static int NOTSPAWNEDID = -1;
	private final static Transform DEAD = new Transform(new Point(null, 0, 0, 0), new Quaternion(0F, 0F, 0F, 0F), new Vector3(0, 0, 0));
	// TODO - needs to have a world based version too?
	public static final StringMap entityStringMap = new StringMap(null, new MemoryStore<Integer>(), 0, Short.MAX_VALUE);
	private final OptimisticReadWriteLock lock = new OptimisticReadWriteLock();
	private final Transform transform = new Transform();
	private Transform lastTransform  = transform;
	private EntityManager entityManager;
	private EntityManager entityManagerLive;
	private Controller controller = null;
	private Controller controllerLive = null;
	private Chunk chunk;
	private Chunk chunkLive;
	private boolean justSpawned = true;
	private final AtomicInteger viewDistanceLive = new AtomicInteger();
	private int viewDistance;
	public int id = NOTSPAWNEDID;
	Model model;
	CollisionModel collision;
	SpoutDatatableMap map;
	boolean observer = false;
	Thread owningThread = null;
	Vector3m offset = new Vector3m();
	float pitch, yaw, roll;

	public SpoutEntity(SpoutServer server, Transform transform, Controller controller, int viewDistance) {
		this.transform.set(transform);


		map = new SpoutDatatableMap();
		viewDistanceLive.set(viewDistance);
		this.viewDistance = viewDistance;


		if (controller != null) {
			this.controller = controller;
			setController(controller);
		}

	}

	public SpoutEntity(SpoutServer server, Transform transform, Controller controller) {
		this(server, transform, controller, 64);
	}

	public SpoutEntity(SpoutServer server, Point point, Controller controller) {
		this(server, new Transform(point, Quaternion.identity, Vector3.ONE), controller);
	}

	/**
	 * @param dt milliseconds since the last tick
	 */
	public void onTick(float dt) {
		lastTransform = transform.copy();
		Vector3 ang = this.transform.getRotation().getAxisAngles();
		pitch = ang.getZ();
		yaw = ang.getY();
		roll = ang.getX();

		if (controller != null && controller.getParent() != null && !isDead()) {
			controller.onTick(dt);
		}
		this.rotate(roll, 1, 0, 0);
		this.rotate(yaw, 0, 1, 0);
		this.rotate(pitch, 0, 0, 1);
	}

	/**
	 * Called when the tick is finished and collisions need to be resolved and
	 * move events fired
	 */
	public void resolve() {
		//Don't need to do collisions if we have no collision volume
		if(this.collision == null) return;

		//Move the collision volume to the new postion
		this.collision.setPosition(this.transform.getPosition());

		//Resolve Collisions Here
		final Pointm location = this.transform.getPosition();
		List<CollisionVolume> colliding = ((SpoutWorld)this.getWorld()).getCollidingObject(this.collision);

		offset.set(this.lastTransform.getPosition());
		offset.subtract(this.transform.getPosition());
		for (CollisionVolume box : colliding) {
			Vector3 collision = this.collision.resolve(box);
			if (collision != null) {
				collision = collision.subtract(location);
				System.out.println("Collision: " + collision);
				if (collision.getX() != 0F) {
					offset.setX(collision.getX());
				}
				if (collision.getY() != 0F) {
					offset.setY(collision.getY());
				}
				if (collision.getZ() != 0F) {
					offset.setZ(collision.getZ());
				}
			}
		}




		location.add(offset);
		Point old = this.getPosition();
		this.setPosition(location);

		//Check to see if we should fire off a Move event
	}




//REGION: Accessors
	@Override
	public void translate(Vector3 amount) {
		//Check if this thread is the calling thread
		if(this.owningThread != Thread.currentThread()){
			if(Spout.getGame().debugMode()) throw new IllegalAccessError("Tried to translate from another thread!");
			return;
		}
		this.transform.getPosition().add(amount);
	}

	@Override
	public void translate(float x, float y, float z) {
		if(this.owningThread != Thread.currentThread()){
			if(Spout.getGame().debugMode()) throw new IllegalAccessError("Tried to translate from another thread!");
			return;
		}
		this.transform.getPosition().add(x,y,z);
	}

	@Override
	public void rotate(float ang, float x, float y, float z) {
		if(this.owningThread != Thread.currentThread()){
			if(Spout.getGame().debugMode()) throw new IllegalAccessError("Tried to Rotate from another thread!");
			return;
		}
		this.transform.getRotation().rotate(ang,x,y,z);
	}

	@Override
	public void rotate(Quaternion rot) {
		if(this.owningThread != Thread.currentThread()){
			if(Spout.getGame().debugMode()) throw new IllegalAccessError("Tried to Rotate from another thread!");
			return;
		}
		this.transform.getRotation().multiply(rot);
	}

	@Override
	public void scale(Vector3 amount) {
		if(this.owningThread != Thread.currentThread()){
			if(Spout.getGame().debugMode()) throw new IllegalAccessError("Tried to scale from another thread!");
			return;
		}
		this.transform.getScale().multiply(amount);
	}

	@Override
	public void scale(float x, float y, float z) {
		if(this.owningThread != Thread.currentThread()){
			if(Spout.getGame().debugMode()) throw new IllegalAccessError("Tried to scale from another thread!");
			return;
		}
		this.transform.getScale().multiply(x,y,z);
	}


	@Override
	public Point getPosition() {
		return transform.getPosition();
	}

	@Override
	public Quaternion getRotation() {
		return transform.getRotation();
	}

	@Override
	public Vector3 getScale() {
		return this.transform.getScale();
	}

	@Override
	public void setPosition(Point position) {
		if(this.owningThread != Thread.currentThread()){
			if(Spout.getGame().debugMode()) throw new IllegalAccessError("Tried to set position from another thread!");
			return;
		}
		this.transform.setPosition(position);
	}

	@Override
	public void setRotation(Quaternion rotation) {
		if(this.owningThread != Thread.currentThread()){
			if(Spout.getGame().debugMode()) throw new IllegalAccessError("Tried to set rotation from another thread!");
			return;
		}
		this.transform.setRotation(rotation);
	}

	@Override
	public void setScale(Vector3 scale) {
		if(this.owningThread != Thread.currentThread()){
			if(Spout.getGame().debugMode()) throw new IllegalAccessError("Tried to set scale from another thread!");
			return;
		}
		this.transform.setScale(scale);
	}


	@Override
	public void roll(float ang) {
		if(this.owningThread != Thread.currentThread()){
			if(Spout.getGame().debugMode()) throw new IllegalAccessError("Tried to roll from another thread!");
			return;
		}
		roll += ang;
	}

	@Override
	public void pitch(float ang) {
		if(this.owningThread != Thread.currentThread()){
			if(Spout.getGame().debugMode()) throw new IllegalAccessError("Tried to pitch from another thread!");
			return;
		}
		pitch += ang;
	}

	@Override
	public void yaw(float ang) {
		if(this.owningThread != Thread.currentThread()){
			if(Spout.getGame().debugMode()) throw new IllegalAccessError("Tried to yaw from another thread!");
			return;
		}
		yaw += ang;
	}

	@Override
	public float getPitch() {
		return pitch;
	}

	@Override
	public float getYaw() {
		return yaw;
	}

	@Override
	public float getRoll() {
		return roll;
	}

	@Override
	public void setPitch(float ang) {
		if(this.owningThread != Thread.currentThread()){
			if(Spout.getGame().debugMode()) throw new IllegalAccessError("Tried to set pitch from another thread!");
			return;
		}
		pitch = ang;
	}

	@Override
	public void setRoll(float ang) {
		if(this.owningThread != Thread.currentThread()){
			if(Spout.getGame().debugMode()) throw new IllegalAccessError("Tried to set scale from another thread!");
			return;
		}
		roll = ang;
	}

	@Override
	public void setYaw(float ang) {
		if(this.owningThread != Thread.currentThread()){
			if(Spout.getGame().debugMode()) throw new IllegalAccessError("Tried to set scale from another thread!");
			return;
		}
		yaw = ang;
	}

//


	@Override
	public int getId() {
		while (true) {
			int seq = lock.readLock();
			int lid = this.id;
			if (lock.readUnlock(seq)) {
				return lid;
			}
		}
	}

	public void setId(int id) {
		int seq = lock.writeLock();
		try {
			this.id = id;
		} finally {
			lock.writeUnlock(seq);
		}
	}

	@Override
	public Controller getController() {
		while (true) {
			int seq = lock.readLock();
			Controller lcontroller = controllerLive;
			if (lock.readUnlock(seq)) {
				return lcontroller;
			}
		}
	}

	public Controller getPrevController() {
		return controller;
	}

	@Override
	public void setController(Controller controller) {

		if (controller != null){

			controller.attachToEntity(this);
		}
		int seq = lock.writeLock();
		try {
			controllerLive = controller;
		} finally {
			lock.writeUnlock(seq);
		}
		if (controller != null) {
			if(controller instanceof PlayerController) setObserver(true);
			controller.onAttached();
		}
	}


	// TODO - make actually atomic, rather than just threadsafe
	@Override
	public boolean kill() {
		int seq = lock.writeLock();
		boolean alive = true;
		try {
			AtomicPoint p = transform.getPosition();
			alive = p.getWorld() != null;
			transform.set(DEAD);
			chunkLive = null;
			entityManagerLive = null;
		} finally {
			lock.writeUnlock(seq);
		}
		return alive;
	}

	@Override
	public boolean isDead() {
		while (true) {
			int seq = transform.readLock();
			boolean dead = id != NOTSPAWNEDID && transform.getPosition().getWorld() == null;
			if (transform.readUnlock(seq)) {
				return dead;
			}
		}
	}

	// TODO - needs to be made thread safe
	@Override
	public void setModel(Model model) {
		this.model = model;
	}

	// TODO - needs to be made thread safe
	@Override
	public Model getModel() {
		return model;
	}

	// TODO - needs to be made thread safe
	@Override
	public void setCollision(CollisionModel model) {

		collision = model;
		if(collision != null) collision.setPosition(this.transform.getPosition());

	}

	// TODO - needs to be made thread safe
	@Override
	public CollisionModel getCollision() {
		return collision;
	}



	@Override
	public boolean isSpawned() {
		return id != NOTSPAWNEDID;
	}



	@Override
	public void finalizeRun() {
		if (entityManager != null) {
			if (entityManager != entityManagerLive || controller != controllerLive) {
				SpoutRegion r = (SpoutRegion)chunk.getRegion();
				r.removeEntity(this);
				if (entityManagerLive == null) {
					controller.onDeath();
					if (controller instanceof PlayerController) {
						Player p = ((PlayerController) controller).getPlayer();
						((SpoutPlayer) p).getNetworkSynchronizer().onDeath();
					}
				}
			}
		}
		if (entityManagerLive != null) {
			if (entityManager != entityManagerLive || controller != controllerLive) {
				entityManagerLive.allocate(this);
			}
		}
		if (chunkLive != chunk) {
			if (chunkLive != null) {
				((SpoutChunk) chunkLive).addEntity(this);
			}
			if (chunk != null && chunk.isLoaded()) {
				((SpoutChunk) chunk).removeEntity(this);
			}
			if (chunkLive == null) {
				if (chunk != null && chunk.isLoaded()) {
					((SpoutChunk) chunk).removeEntity(this);
				}
				if (entityManagerLive != null) {
					entityManagerLive.deallocate(this);
				}
			}
		}
	}

	public void copyToSnapshot() {
		chunk = chunkLive;
		if (entityManager != entityManagerLive) {
			entityManager = entityManagerLive;
		}
		controller = controllerLive;
		justSpawned = false;
		viewDistance = viewDistanceLive.get();

	}

	@Override
	public Chunk getChunk() {
		World world = getWorld();
		if (world == null) {
			return null;
		} else {
			return world.getChunkFromBlock(MathHelper.floor(getPosition().getX()), MathHelper.floor(getPosition().getY()), MathHelper.floor(getPosition().getZ()));
		}
	}

	public Chunk getChunkLive() {
		while (true) {
			int seq = lock.readLock();
			Point position = transform.getPosition();
			World w = position.getWorld();
			if (w == null) {
				if (lock.readUnlock(seq)) {
					return null;
				}
			} else {
				Chunk c = w.getChunk(position, true);
				if (lock.readUnlock(seq)) {
					return c;
				}
			}
		}
	}

	@Override
	public Region getRegion() {
		World world = getWorld();
		if (world == null) {
			return null;
		} else {
			return world.getRegionFromBlock(MathHelper.floor(getPosition().getX()), MathHelper.floor(getPosition().getY()), MathHelper.floor(getPosition().getZ()));
		}
	}

	public Region getRegionLive() {
		while (true) {
			int seq = lock.readLock();
			Point position = transform.getPosition();
			World world = position.getWorld();
			if (world == null && lock.readUnlock(seq)) {
				return null;
			} else {
				Region r = world.getRegion(position, true);
				if (lock.readUnlock(seq)) {
					return r;
				}
			}
		}
	}

	@Override
	public World getWorld() {
		return transform.getPosition().getWorld();
	}

	@Override
	public boolean is(Class<? extends Controller> clazz) {
		return clazz.isAssignableFrom(getController().getClass());
	}

	// TODO - datatable and atomics
	@Override
	public void setData(String key, int value) {
		int ikey = map.getKey(key);
		map.set(ikey, new SpoutDatatableInt(ikey, value));
	}

	@Override
	public void setData(String key, float value) {
		int ikey = map.getKey(key);
		map.set(ikey, new SpoutDatatableFloat(ikey, value));
	}

	@Override
	public void setData(String key, boolean value) {
		int ikey = map.getKey(key);
		map.set(ikey, new SpoutDatatableBool(ikey, value));

	}

	@Override
	public void setData(String key, Serializable value) {
		int ikey = map.getKey(key);
		map.set(ikey, new SpoutDatatableObject(ikey, value));
	}

	@Override
	public DatatableTuple getData(String key) {
		return map.get(key);
	}

    @Override
	public boolean hasData(String key) {
		return map.contains(key);
	}

	private int inventorySize;
	private Inventory inventory;

	@Override
	public int getInventorySize() {
		return inventorySize;
	}

	@Override
	public void setInventorySize(int newsize) {
		if (inventorySize == newsize) {
			return;
		}
		inventorySize = newsize;
		if (getInventory().getSize() != inventorySize) {
			inventory = null;
			setData("inventory", null);
		}
	}

	@Override
	public Inventory getInventory() {
		if (getInventorySize() <= 0) {
			return null;
		}
		if (inventory == null) {

			if (!hasData("inventory")) {
				inventory = controllerLive == null ? new Inventory(getInventorySize()) : controllerLive.createInventory(getInventorySize());
				setData("inventory", inventory);
			} else {
				inventory = (Inventory) getData("inventory").get();
			}
		}
		return inventory;
	}

	@Override
	public void onSync() {
		//Forward to controller for now, but we may want to do some sync logic here for the entitiy.
		controller.onSync();
		//TODO - this might not be needed, if it is, it needs to send to the network synchronizer for players
	}

	public boolean justSpawned() {
		return justSpawned;
	}

	@Override
	public void setViewDistance(int distance) {
		viewDistanceLive.set(distance);
	}

	@Override
	public int getViewDistance() {
		return viewDistanceLive.get();
	}

	public int getPrevViewDistance() {
		return viewDistance;
	}


	@Override
	public void setObserver(boolean obs) {
		this.observer = obs;

	}

	@Override
	public boolean isObserver() {
		return observer;
	}




	public void setOwningThread(Thread thread){
		this.owningThread = thread;
	}
}
