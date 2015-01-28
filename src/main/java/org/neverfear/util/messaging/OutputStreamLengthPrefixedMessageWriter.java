package org.neverfear.util.messaging;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamLengthPrefixedMessageWriter
	extends LengthPrefixedMessageWriter {

	public static final int DEFAULT_SIZE = 1024;

	private final OutputStream outputStream;

	public OutputStreamLengthPrefixedMessageWriter(final OutputStream outputStream) {
		this(outputStream,
				DEFAULT_SIZE);
	}

	public OutputStreamLengthPrefixedMessageWriter(final OutputStream outputStream, final int size) {
		super(size);
		this.outputStream = outputStream;
	}

	@Override
	protected void doWrite(final byte[] message) throws IOException {
		this.outputStream.write(message);
	}

	@Override
	public void close() throws IOException {
		this.outputStream.close();
	}

}
