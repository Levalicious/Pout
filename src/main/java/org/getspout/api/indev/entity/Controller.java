package org.getspout.api.indev.entity;

public abstract class Controller {
	Entity parent;
	public void attachToEntity(Entity e){
		this.parent = e;
	}
	
	public abstract void onAttached();
	public abstract void onTick(float dt);
}
