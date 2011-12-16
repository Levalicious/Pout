package org.getspout.server.net.codec;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import org.getspout.server.msg.TimeMessage;

public final class TimeCodec extends MessageCodec<TimeMessage> {
	public TimeCodec() {
		super(TimeMessage.class, 0x04);
	}

	@Override
	public TimeMessage decode(ChannelBuffer buffer) throws IOException {
		return new TimeMessage(buffer.readLong());
	}

	@Override
	public ChannelBuffer encode(TimeMessage message) throws IOException {
		ChannelBuffer buffer = ChannelBuffers.buffer(8);
		buffer.writeLong(message.getTime());
		return buffer;
	}
}
