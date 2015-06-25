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

import static java.lang.System.out;

import java.io.IOException;

/**
 * @author doug@neverfear.org
 *
 */
public final class HexDump {

	private static final char[] digits = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
	};

	private final int columnCount;
	private final boolean includeHeader;

	public HexDump(final int columnCount, final boolean includeHeader) {
		super();
		if (columnCount > 256 || columnCount < 0) {
			throw new IllegalArgumentException();
		}
		this.columnCount = columnCount;
		this.includeHeader = includeHeader;
	}

	private char toHexChar(final int nibble) {
		return digits[nibble];
	}

	private void appendHex(final Appendable appendable, final int value) throws IOException {
		appendHex(appendable, 2, value);
	}

	private void appendHex(final Appendable appendable, final byte value) throws IOException {
		appendable.append(String.format("%02X", value));
	}

	private void appendHex(final Appendable appendable, final int width, final int value) throws IOException {
		appendable.append(String.format("%0" + width + "X", value));
	}

	public Appendable print(final Appendable appendable, final byte[] binary) throws IOException {
		return print(appendable, binary, 0, binary.length);
	}

	/**
	 * Does not generate trailing new line
	 * 
	 * @param appendable
	 * @param binary
	 * @param offset
	 * @param headers
	 * @return
	 * @throws IOException
	 */
	public Appendable print(final Appendable appendable,
			final byte[] binary,
			final int offset,
			final int length) throws IOException {

		int columnWidth = 0;
		if (this.includeHeader) {
			final int binaryLength = length - offset;

			/*
			 * Subtract 1 because we will only print that length if there is at
			 * least 1 byte on the column
			 */
			columnWidth = calculateHexWidth(binaryLength - 1);
			appendable.append("  ");
			for (int i = 0; i < columnWidth; i++) {
				appendable.append(' ');
			}
			appendable.append(" |");
			for (int columnIndex = 0; columnIndex < this.columnCount; columnIndex++) {
				appendable.append(' ');
				appendHex(appendable, columnIndex);
			}
			newLine(appendable);

			appendable.append("--");
			for (int i = 0; i < columnWidth; i++) {
				appendable.append("-");
			}
			appendable.append("-+");
			for (int columnIndex = 0; columnIndex < this.columnCount; columnIndex++) {
				appendable.append("---");
			}
		}

		for (int byteIndex = offset, i = 0; byteIndex < length; byteIndex++, i++) {
			if (i % this.columnCount == 0) {
				if (this.includeHeader) {
					newLine(appendable);
					appendable.append("0x");
					appendHex(appendable, columnWidth, byteIndex);
					appendable.append(" | ");
				} else if (byteIndex != offset) {
					newLine(appendable);
				}
			} else {
				appendable.append(' ');
			}
			final byte b = binary[byteIndex];
			appendHex(appendable, b);
		}
		return appendable;
	}

	private static Appendable newLine(final Appendable appendable) throws IOException {
		appendable.append("\n");
		return appendable;
	}

	/**
	 * Calculates the number of characters needed to print the value.
	 * 
	 * @param value
	 * @return
	 */
	private static int calculateHexWidth(final int value) {
		return (int) (Math.log(value) / Math.log(16)) + 1;
	}

	public static void main(final String[] args) throws Exception {
		out.println((0x1000));
		out.println(calculateHexWidth(0x1000));
		out.println(calculateHexWidth(0xFFFF));
		out.println(calculateHexWidth(0x10000));
		out.println();
		final byte[] bytes = new byte[257];
		byte b = 0;
		for (int i = 0; i < bytes.length; i++) {
			bytes[i] = b;
			if (b == Byte.MAX_VALUE) {
				b = Byte.MIN_VALUE;
			} else {
				b++;
			}
		}
		final HexDump hexDump = new HexDump(32, true);
		final StringBuilder appendable = new StringBuilder();
		hexDump.print(appendable, bytes);
		out.println(appendable);
	}
}
