package org.neverfear.util.messaging;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public final class SocketChannelLengthPrefixedMessageReader
	extends LengthPrefixedMessageReader {

	public static final int DEFAULT_SIZE = 1024;

	private final SocketChannel channel;

	public SocketChannelLengthPrefixedMessageReader(final SocketChannel channel) {
		this(channel,
				DEFAULT_SIZE);
	}

	public SocketChannelLengthPrefixedMessageReader(final SocketChannel channel, final int size) {
		super(size);
		this.channel = channel;
	}

	@Override
	protected int fillBuffer(final ByteBuffer buffer) throws IOException {
		return this.channel.read(buffer);
	}

	@Override
	public void close() throws IOException {
		this.channel.close();
	}

	public static void main(final String... strings) throws Exception {
		final ServerSocketChannel serv = ServerSocketChannel.open();
		serv.bind(new InetSocketAddress(7896));

		final SocketChannel client = SocketChannel.open(new InetSocketAddress(7896));
		final SocketChannel rem = serv.accept();

		final MessageReader codec = new SocketChannelLengthPrefixedMessageReader(rem,
				6);

		final MessageWriter writer = new LengthPrefixedMessageWriter(1024) {

			@Override
			protected void doWrite(final byte[] message) throws IOException {
				final ByteBuffer byteBuffer = ByteBuffer.wrap(message);
				client.write(byteBuffer);
			}

			@Override
			public void close() throws IOException {
				client.close();
			}
		};

		/**
		 * Scenarios:
		 * <ul>
		 * <li>Message is spread over two buffers ([4 byte prefix]"He" is in
		 * first buffer, "llo" is in second)
		 * <li>When you start with a partial buffer containing some of the
		 * previous message, and some of yours ("llo"[3 bytes of 4 byte prefix])
		 * <li>When your prefix is shared across two buffers [1 byte of 4 byte
		 * prefix]"Pickl"
		 * <li>When the buffer contains exactly the right amount of bytes to
		 * complete a message "es!" and ":-D"
		 * <li>When the buffer contains more space than a message ":-("
		 * </ul>
		 */
		for (final String s : new String[] {
				"Hello", "World", "Pickles!", ":-D", ":-("
		}) {
			writer.write(s.getBytes());
		}

		System.out.println(new String(codec.readMessage()));
		System.out.println(new String(codec.readMessage()));
		System.out.println(new String(codec.readMessage()));
		System.out.println(new String(codec.readMessage()));
		System.out.println(new String(codec.readMessage()));

	}

}
