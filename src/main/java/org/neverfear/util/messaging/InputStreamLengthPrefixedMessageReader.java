package org.neverfear.util.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class InputStreamLengthPrefixedMessageReader
	extends LengthPrefixedMessageReader {

	public static final int DEFAULT_SIZE = 1024;

	private final byte[] byteArray;
	private final InputStream inputStream;

	public InputStreamLengthPrefixedMessageReader(final InputStream inputStream) {
		this(inputStream,
				DEFAULT_SIZE);
	}

	public InputStreamLengthPrefixedMessageReader(final InputStream inputStream, final int size) {
		super(size);
		this.inputStream = inputStream;
		this.byteArray = new byte[size];
	}

	@Override
	protected int fillBuffer(final ByteBuffer buffer) throws IOException {
		final int readCount = this.inputStream.read(this.byteArray);
		if (readCount > 0) {
			buffer.put(this.byteArray, 0, readCount);
		}
		return readCount;
	}

	@Override
	public void close() throws IOException {
		this.inputStream.close();
	}
}
