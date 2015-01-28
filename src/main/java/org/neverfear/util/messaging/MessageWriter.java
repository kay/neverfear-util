package org.neverfear.util.messaging;

import java.io.Closeable;
import java.io.IOException;

public interface MessageWriter
	extends Closeable {

	void write(final byte[] payload) throws IOException;

}