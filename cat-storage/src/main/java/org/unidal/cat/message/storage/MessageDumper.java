package org.unidal.cat.message.storage;

import io.netty.buffer.ByteBuf;

import com.dianping.cat.message.internal.MessageId;

import com.dianping.cat.message.spi.MessageTree;

public interface MessageDumper {
	public void awaitTermination(long timestamp) throws InterruptedException;

	public void process(MessageTree tree);

	public ByteBuf find(MessageId id);
	
	public void initialize(long timestamp);
}
