package org.neverfear.util.messaging;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class LengthPrefixedMessageWriter
	implements MessageWriter {

	private final ByteArrayOutputStream baos;

	public LengthPrefixedMessageWriter(final int size) {
		this.baos = new ByteArrayOutputStream(size);
	}

	protected abstract void doWrite(final byte[] message) throws IOException;

	@Override
	public synchronized void write(final byte[] payload) throws IOException {
		final DataOutputStream dos = new DataOutputStream(this.baos);
		dos.writeInt(payload.length);
		dos.write(payload);
		final byte[] resultant = this.baos.toByteArray();
		this.baos.reset();
		doWrite(resultant);
	}
}
