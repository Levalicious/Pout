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
package org.spout.api.model.animation;

import org.spout.api.math.Matrix;
import org.spout.api.resource.Resource;

public class Animation extends Resource{

	private String name; //Debug
	
	private int id;
	
	private final int frame;
	
	private final float delay;
	
	private final BoneTransform [][]frames;

	public Animation(Skeleton skeleton, int frame, float delay){
		frames = new BoneTransform[skeleton.getBoneSize()][frame];
		this.frame = frame;
		this.delay = delay;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getDelay() {
		return delay;
	}

	public void setBoneTransform(int bone, int frame, BoneTransform transform){
		if(frames[bone][frame] != null)
			throw new IllegalStateException("This bone transform is already define");
		
		frames[bone][frame] = transform;
	}

	public int getFrame() {
		return frame;
	}

	public BoneTransform getBoneTransform(int bone, int frame) {
		return frames[bone][frame];
	}

	/*public void dumbAnimation(String str) {
		System.out.println(str + "Animation : " + id);

		int i = 0;
		for(BoneTransform bt : transforms){
			System.out.println(str + "  " + i + " : " + bt.toString());
			i++;
		}
	}*/

}
