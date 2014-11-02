package org.neverfear.util.messaging;

import java.io.IOException;

public interface MessageWriter {

	void write(byte[] payload) throws IOException;

}