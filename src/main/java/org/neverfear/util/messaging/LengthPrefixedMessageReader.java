package org.neverfear.util.messaging;

import static java.lang.Math.min;

import java.io.EOFException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public abstract class LengthPrefixedMessageReader
	implements MessageReader {

	/**
	 * Used to buffer the byte source.
	 */
	private final ByteBuffer buffer;

	/**
	 * Offset in the current message buffer
	 */
	private transient int offset = 0;

	/**
	 * Buffer used to store the complete message while we read in the buffer.
	 */
	private transient byte[] message = null;

	public LengthPrefixedMessageReader(final int size) {
		this.buffer = ByteBuffer.allocate(size);
	}

	/**
	 * Fill the passed buffer with all available bytes.
	 * 
	 * @param buffer
	 * @return the number of bytes written into the buffer
	 * @throws IOException
	 */
	protected abstract int fillBuffer(final ByteBuffer buffer) throws IOException;

	/**
	 * 
	 * @return the remaining bytes to complete this message.
	 */
	private int remaining() {
		return this.message.length - this.offset;
	}

	/**
	 * Fills the message with all available remaining bytes. If more the message
	 * requires more bytes than available in the buffer, the entire buffer will
	 * be consumed. If the message requires less than or equal to the number of
	 * bytes in the buffer, only that portion will be consumed.
	 */
	private void fillMessage() {
		final int bufferRemaining = this.buffer.remaining();
		final int size = min(bufferRemaining, remaining());
		this.buffer.get(this.message, this.offset, size);
		this.offset += size;
	}

	@Override
	public byte[] readMessage() throws IOException {
		final int remainingInBuffer = this.buffer.capacity() - this.buffer.remaining();
		if (remainingInBuffer < Integer.BYTES) {
			if (fillBuffer(this.buffer) < 0) {
				return null;
			}
		}
		this.buffer.flip();

		final int length = this.buffer.getInt();
		this.message = new byte[length];
		this.offset = 0;

		fillMessage();

		while (remaining() > 0) {
			if (Thread.interrupted()) {
				throw new InterruptedIOException();
			}

			this.buffer.compact();
			if (fillBuffer(this.buffer) < 0) {
				throw new EOFException("EOF reached before message was completely read");
			}
			this.buffer.flip();

			fillMessage();
		}

		/*
		 * Do not need to do flip because we want to write from where we left
		 * off reading after compaction.
		 */
		this.buffer.compact();
		return this.message;
	}

	public static void main(final String... strings) throws Exception {
		final ServerSocketChannel serv = ServerSocketChannel.open();
		serv.bind(new InetSocketAddress(7896));

		final SocketChannel client = SocketChannel.open(new InetSocketAddress(7896));
		final SocketChannel rem = serv.accept();

		final MessageReader codec = new LengthPrefixedMessageReader(6) {

			@Override
			public int fillBuffer(final ByteBuffer buffer) throws IOException {
				return rem.read(buffer);
			}
		};

		final MessageWriter writer = new LengthPrefixedMessageWriter(1024) {

			@Override
			protected void doWrite(final byte[] message) throws IOException {
				final ByteBuffer byteBuffer = ByteBuffer.wrap(message);
				client.write(byteBuffer);
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
