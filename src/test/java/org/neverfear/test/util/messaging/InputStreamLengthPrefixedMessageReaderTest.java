/*
 * Copyright 2015 doug@neverfear.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neverfear.test.util.messaging;

import static org.junit.Assert.assertArrayEquals;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.neverfear.util.messaging.InputStreamLengthPrefixedMessageReader;
import org.neverfear.util.messaging.OutputStreamLengthPrefixedMessageWriter;

/**
 * @author doug@neverfear.org
 * 
 */
public class InputStreamLengthPrefixedMessageReaderTest {

	@Rule
	public TestRule timeout = new DisableOnDebug(Timeout.seconds(2));

	private InputStream inputStream;
	private PipedOutputStream writeToInputStream;
	private InputStreamLengthPrefixedMessageReader subject;

	private OutputStreamLengthPrefixedMessageWriter writer;

	@Before
	public void before() throws Exception {
		this.writeToInputStream = new PipedOutputStream();
		this.inputStream = new PipedInputStream(this.writeToInputStream);
		this.subject = new InputStreamLengthPrefixedMessageReader(this.inputStream, 50);
		this.writer = new OutputStreamLengthPrefixedMessageWriter(this.writeToInputStream);
	}

	@Test
	public void givenOneMessages_whenRead_expectReturnPayloadWithoutTheLengthPrefix() throws Exception {
		// Given
		final byte[] payload = new byte[] {
				0, 0, 0, 11, 10, 9, 8, 0, 16, -67, -73, -74, -107, -77, 41
		};
		this.writeToInputStream.write(payload);

		// When
		final byte[] message1 = this.subject.readMessage();

		// Then
		assertArrayEquals(new byte[] {
				10, 9, 8, 0, 16, -67, -73, -74, -107, -77, 41
		}, message1);
	}

	@Test
	public void givenOneLargeMessage_whenRead_expectReturnPayloadWithoutTheLengthPrefix() throws Exception {
		// Given
		final byte[] payload = new byte[100];
		for (int i = 0; i < 100; i++) {
			payload[i] = (byte) i;
		}
		this.writer.write(payload);

		// When
		final byte[] message1 = this.subject.readMessage();

		// Then
		assertArrayEquals(payload, message1);
	}

	@Test
	public void givenTwoMessages_whenRead_expectReadBothMessagesBack() throws Exception {
		// Given
		final byte[] payload = new byte[] {
				0, 0, 0, 11, 10, 9, 8, 0, 16, -67, -73, -74, -107, -77, 41,
				0, 0, 0, 20, 18, 18, 8, 0, 16, -67, -73, -74, -107, -77, 41, 24, -57, -39, -76, -59, -114, -127, -38, 1
		};
		this.writeToInputStream.write(payload);

		// When
		final byte[] message1 = this.subject.readMessage();
		final byte[] message2 = this.subject.readMessage();

		// Then
		assertArrayEquals(new byte[] {
				10, 9, 8, 0, 16, -67, -73, -74, -107, -77, 41
		}, message1);

		assertArrayEquals(new byte[] {
				18, 18, 8, 0, 16, -67, -73, -74, -107, -77, 41, 24, -57, -39, -76, -59, -114, -127, -38, 1
		}, message2);
	}

	@Test
	public void givenReadFirstMessageIsLarge_andSecondMessageIsSmall_whenReadSecondMessage_expectCorrectlyReads()
			throws Exception {
		// Given
		this.writeToInputStream.write(new byte[] {
				0, 0, 0, 20, 18, 18, 8, 0, 16, -67, -73, -74, -107, -77, 41, 24, -57, -39, -76, -59, -114, -127, -38, 1
		});
		this.subject.readMessage();

		this.writeToInputStream.write(new byte[] {
				0, 0, 0, 11, 10, 9, 8, 0, 16, -67, -73, -74, -107, -77, 41
		});

		// When
		final byte[] message2 = this.subject.readMessage();

		// Then
		assertArrayEquals(new byte[] {
				10, 9, 8, 0, 16, -67, -73, -74, -107, -77, 41
		}, message2);
	}
}
