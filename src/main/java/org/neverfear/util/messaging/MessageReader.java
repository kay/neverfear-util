package org.neverfear.util.messaging;

import java.io.Closeable;
import java.io.IOException;

public interface MessageReader
	extends Closeable {

	/**
	 * Read one complete message. The bytes returned is the payload of each
	 * length prefixed message frame. e.g. if a frame has a length prefix of 5,
	 * then 5 bytes will be returned.
	 * 
	 * Blocks until full message is received.
	 * 
	 * @return the message payload or null if EOF was reached.
	 * @throws IOException
	 */
	byte[] readMessage() throws IOException;

}