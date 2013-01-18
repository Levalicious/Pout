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
package org.spout.engine.renderer.vertexbuffer;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

public class ComposedFloatBuffer {	
	int usage = GL15.GL_STATIC_DRAW;

	int vboId = -1;

	int []elements;
	int []layout;
	int []offset;

	int stride;

	public static final int FLOAT_SIZE = Float.SIZE / Byte.SIZE;

	public ComposedFloatBuffer(int []elements, int[] layouts){
		if(elements.length != layouts.length)
			throw new IllegalStateException("Number of elements and layout must be same");

		this.elements = elements;
		this.layout = layouts;
		offset = new int[elements.length];

		stride = 0;
		for(int i = 0; i < elements.length; i++){
			offset[i] = stride;
			stride += elements[i] * FLOAT_SIZE;
		}
	}

	public void flush(FloatBuffer buffer){
		if(vboId == -1) vboId = GL15.glGenBuffers();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, usage);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public void bind(){
		if(vboId == -1) throw new IllegalStateException("Cannot bind a vertex buffer without data!");
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);

		for(int i = 0; i < elements.length; i++)
			GL20.glVertexAttribPointer(layout[i], elements[i], GL11.GL_FLOAT, false, stride, offset[i]);
	}

	public void unbind() {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public int[] getElements() {
		return elements;
	}

	public int[] getLayout() {
		return layout;
	}

	public void release() {
		if(vboId == -1) return;

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, 0, usage);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void dispose() {
		if( vboId != -1 ) GL15.glDeleteBuffers(vboId);
	}

	public void finalize() {
		dispose();
	}

}
