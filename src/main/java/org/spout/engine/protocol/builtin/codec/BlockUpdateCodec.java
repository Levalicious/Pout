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
package org.spout.engine.protocol.builtin.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.spout.api.protocol.MessageCodec;
import org.spout.engine.protocol.builtin.message.BlockUpdateMessage;

/**
 *
 */
public class BlockUpdateCodec extends MessageCodec<BlockUpdateMessage> {
	public BlockUpdateCodec() {
		super(BlockUpdateMessage.class, 0x07);
	}

	@Override
	public ChannelBuffer encode(BlockUpdateMessage message) {
		ChannelBuffer buffer = ChannelBuffers.buffer(17);
		buffer.writeInt(message.getX());
		buffer.writeInt(message.getY());
		buffer.writeInt(message.getZ());
		buffer.writeShort(message.getType());
		buffer.writeShort(message.getData());
		return buffer;
	}

	@Override
	public BlockUpdateMessage decode(ChannelBuffer buffer) {
		final int x = buffer.readInt();
		final int y = buffer.readInt();
		final int z = buffer.readInt();
		final short type = buffer.readShort();
		final short data = buffer.readShort();
		return new BlockUpdateMessage(x, y, z, type, data);
	}
}
