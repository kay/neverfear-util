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
package org.neverfear.util;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

/**
 * @author doug@neverfear.org
 *
 */
public class HexDumpTest {

	private final String outputWithColumn32;
	private final String outputWithHeaderAndColumn32;
	private final String outputWithHeaderAndColumn32OffsetOneLengthTen;

	private static String readResourceAsString(final String resource) throws IOException {
		final InputStream is = HexDumpTest.class.getResourceAsStream(resource);
		final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		final StringBuilder builder = new StringBuilder();
		final char[] chrBuffer = new char[1024];
		int readCount;
		while ((readCount = reader.read(chrBuffer)) != -1) {
			builder.append(chrBuffer, 0, readCount);
		}
		return builder.toString();
	}

	public HexDumpTest()
			throws IOException {
		this.outputWithColumn32 = readResourceAsString("/hex-32-full");
		this.outputWithHeaderAndColumn32 = readResourceAsString("/hex-32-with-header-full");
		this.outputWithHeaderAndColumn32OffsetOneLengthTen = readResourceAsString("/hex-32-with-header-offset-one-length-ten");
	}

	private static byte[] generateBytes(final int count) {
		final byte[] bytes = new byte[count];
		byte b = 0;
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = b;
			if (b == Byte.MAX_VALUE) {
				b = Byte.MIN_VALUE;
			} else {
				b++;
			}
		}
		return bytes;
	}

	@Test
	public void givenNoHeaders_andThirtyTwoColumns_whenPrint_expectTableWithoutHeaders_andThirtyTwoColumns()
			throws Exception {
		// Given
		final HexDump subject = new HexDump(32, false);
		final byte[] binary = generateBytes(0x101);
		final StringBuilder builder = new StringBuilder();

		// When
		subject.print(builder, binary);

		// Then
		assertEquals(this.outputWithColumn32, builder.toString());
	}

	@Test
	public void givenIncludeHeaders_andThirtyTwoColumns_whenPrint_expectTableWithHeaders_andThirtyTwoColumns()
			throws Exception {
		// Given
		final HexDump subject = new HexDump(32, true);
		final byte[] binary = generateBytes(0x101);
		final StringBuilder builder = new StringBuilder();

		// When
		subject.print(builder, binary);

		// Then
		assertEquals(this.outputWithHeaderAndColumn32, builder.toString());
	}

	@Test
	public void givenIncludeHeaders_andThirtyTwoColumns_whenPrintOffsetOneLengthTen_expectTableWithHeaders_andThirtyTwoColumns_andNineEntries()
			throws Exception {
		// Given
		final HexDump subject = new HexDump(32, true);
		final byte[] binary = generateBytes(0x101);
		final StringBuilder builder = new StringBuilder();

		// When
		subject.print(builder, binary, 1, 10);

		// Then
		assertEquals(this.outputWithHeaderAndColumn32OffsetOneLengthTen, builder.toString());
	}
}
