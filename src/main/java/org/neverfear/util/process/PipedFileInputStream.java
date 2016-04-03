package org.neverfear.util.process;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.function.BooleanSupplier;

final class PipedFileInputStream extends FilterInputStream {
	private static final int EOF = -1;
	private final BooleanSupplier condition;

	protected PipedFileInputStream(InputStream in, BooleanSupplier condition) {
		super(in);
		this.condition = condition;
	}

	@Override
	public int read() throws IOException {
		return super.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		while (!Thread.interrupted()) {
			int length = super.read(b, off, len);
			if (length != EOF) {
				return length;
			} else if (!condition.getAsBoolean()) {
				return EOF;
			}
		}
		throw new InterruptedIOException();
	}

}
