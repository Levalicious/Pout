package org.getspout.api.protocol.notch.codec;

import org.getspout.api.protocol.MessageCodec;
import org.getspout.api.protocol.notch.msg.HealthMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;

public final class HealthCodec extends MessageCodec<HealthMessage> {
	public HealthCodec() {
		super(HealthMessage.class, 0x08);
	}

	@Override
	public HealthMessage decode(ChannelBuffer buffer) throws IOException {
		int health = buffer.readShort();
		int food = buffer.readShort();
		float foodSaturation = buffer.readFloat();
		return new HealthMessage(health, food, foodSaturation);
	}

	@Override
	public ChannelBuffer encode(HealthMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(9);
		buffer.writeShort(message.getHealth());
		buffer.writeShort(message.getFood());
		buffer.writeFloat(message.getFoodSaturation());
		return buffer;
	}
}
