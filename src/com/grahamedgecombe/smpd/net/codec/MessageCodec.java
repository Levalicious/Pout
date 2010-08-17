package com.grahamedgecombe.smpd.net.codec;

import org.jboss.netty.buffer.ChannelBuffer;

import com.grahamedgecombe.smpd.msg.Message;

public abstract class MessageCodec<T extends Message> {

	private final Class<T> clazz;
	private final int opcode;

	public MessageCodec(Class<T> clazz, int opcode) {
		this.clazz = clazz;
		this.opcode = opcode;
	}

	public final Class<T> getType() {
		return clazz;
	}

	public final int getOpcode() {
		return opcode;
	}

	public abstract ChannelBuffer encode(T message);

	public abstract T decode(ChannelBuffer buffer);

}
