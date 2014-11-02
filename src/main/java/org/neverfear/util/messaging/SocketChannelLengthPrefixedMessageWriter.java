package org.neverfear.util.messaging;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public final class SocketChannelLengthPrefixedMessageWriter
	extends LengthPrefixedMessageWriter {

	public static final int DEFAULT_SIZE = 1024;

	private final SocketChannel channel;

	public SocketChannelLengthPrefixedMessageWriter(final SocketChannel channel) {
		this(channel,
				DEFAULT_SIZE);
	}

	public SocketChannelLengthPrefixedMessageWriter(final SocketChannel channel, final int size) {
		super(size);
		this.channel = channel;
	}

	@Override
	protected void doWrite(final byte[] message) throws IOException {
		final ByteBuffer buffer = ByteBuffer.wrap(message);
		this.channel.write(buffer);
	}

}
