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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * @author doug@neverfear.org
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BytesTest {

	private static final byte X12 = 0x12;
	private static final byte X34 = 0x34;
	private static final byte X56 = 0x56;
	private static final byte X78 = 0x78;
	private static final byte X9A = (byte) 0x9A;
	private static final byte XBC = (byte) 0xBC;
	private static final byte XDE = (byte) 0xDE;
	private static final byte XF0 = (byte) 0xF0;

	private static final long BIG_ENDIAN_LONG_VALUE = 0x123456789ABCDEF0L;
	private static final long LITTLE_ENDIAN_LONG_VALUE = 0xF0DEBC9A78563412L;
	private static final byte[] BIG_ENDIAN_LONG_BYTES = {
			X12, X34, X56, X78, X9A, XBC, XDE, XF0
	};
	private static final byte[] BIG_ENDIAN_LONG_BYTES_WITH_LEADING_BYTE = {
			0x00, X12, X34, X56, X78, X9A, XBC, XDE, XF0
	};
	private static final byte[] LITTLE_ENDIAN_LONG_BYTES = {
			XF0, XDE, XBC, X9A, X78, X56, X34, X12
	};
	private static final byte[] LITTLE_ENDIAN_LONG_BYTES_WITH_LEADING_BYTE = {
			0x00, XF0, XDE, XBC, X9A, X78, X56, X34, X12
	};

	private static final int BIG_ENDIAN_INT_VALUE = 0x12345678;
	private static final int LITTLE_ENDIAN_INT_VALUE = 0x78563412;
	private static final byte[] BIG_ENDIAN_INT_BYTES = {
			X12, X34, X56, X78
	};
	private static final byte[] BIG_ENDIAN_INT_BYTES_WITH_LEADING_BYTE = {
			0x00, X12, X34, X56, X78
	};
	private static final byte[] LITTLE_ENDIAN_INT_BYTES = {
			X78, X56, X34, X12
	};
	private static final byte[] LITTLE_ENDIAN_INT_BYTES_WITH_LEADING_BYTE = {
			0x00, X78, X56, X34, X12
	};

	@Test
	public void givenLongInBigEndian_whenSwapEndianness_expectLongInLittleEndian() throws Exception {
		// Given & When
		final long actual = Bytes.swapByteEndianness(BIG_ENDIAN_LONG_VALUE);
		// Then
		assertEquals(LITTLE_ENDIAN_LONG_VALUE, actual);
	}

	@Test
	public void givenIntInBigEndian_whenSwapEndianness_expectIntInLittleEndian() throws Exception {
		// Given & When
		final int actual = Bytes.swapByteEndianness(BIG_ENDIAN_INT_VALUE);
		// Then
		assertEquals(LITTLE_ENDIAN_INT_VALUE, actual);
	}

	@Test
	public void givenLongInLittleEndian_whenSwapEndianness_expectLongInBigEndian() throws Exception {
		// Given & When
		final long actual = Bytes.swapByteEndianness(LITTLE_ENDIAN_LONG_VALUE);
		// Then
		assertEquals(BIG_ENDIAN_LONG_VALUE, actual);
	}

	@Test
	public void givenIntInLittleEndian_whenSwapEndianness_expectIntInBigEndian() throws Exception {
		// Given & When
		final int actual = Bytes.swapByteEndianness(LITTLE_ENDIAN_INT_VALUE);
		// Then
		assertEquals(BIG_ENDIAN_INT_VALUE, actual);
	}

	@Test
	public void givenIntValue_whenIntToBigEndianBytes_expectBigEndianByteArray() {
		// Given
		// When
		final byte[] actual = Bytes.intToBigEndianBytes(BIG_ENDIAN_INT_VALUE);

		// Then
		assertArrayEquals(BIG_ENDIAN_INT_BYTES, actual);
	}

	@Test
	public void givenIntValue_andByteArray_whenIntToBigEndianBytes_expectBigEndianByteArrayWithLeadingByte() {
		// Given
		// When
		final byte[] actual = Bytes.intToBigEndianBytes(new byte[5], 1, BIG_ENDIAN_INT_VALUE);

		// Then
		assertArrayEquals(BIG_ENDIAN_INT_BYTES_WITH_LEADING_BYTE, actual);
	}

	@Test
	public void givenBigEndianByteArray_whenBigEndianByteToInt_expectIntValue() {
		// Given
		// When
		final int actual = Bytes.bigEndianBytesToInt(BIG_ENDIAN_INT_BYTES);

		// Then
		assertEquals(BIG_ENDIAN_INT_VALUE, actual);
	}

	@Test
	public void givenBigEndianByteArrayWithLeadingByte_whenBigEndianByteToIntFromOffsetOne_expectIntValue() {
		// Given
		// When
		final int actual = Bytes.bigEndianBytesToInt(BIG_ENDIAN_INT_BYTES_WITH_LEADING_BYTE, 1);

		// Then
		assertEquals(BIG_ENDIAN_INT_VALUE, actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void givenShortByteArray_whenBigEndianByteToInt_expectIllegalArgumentException() {
		Bytes.bigEndianBytesToInt(new byte[3]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void givenShortByteArray_whenBigEndianByteToIntFromOffsetOne_expectIllegalArgumentException() {
		Bytes.bigEndianBytesToInt(new byte[4], 1);
	}

	@Test
	public void givenIntValue_whenIntToLittleEndianBytes_expectLittleEndianByteArray() {
		// Given
		// When
		final byte[] actual = Bytes.intToLittleEndianBytes(BIG_ENDIAN_INT_VALUE);

		// Then
		assertArrayEquals(LITTLE_ENDIAN_INT_BYTES, actual);
	}

	@Test
	public void givenIntValue_andByteArray_whenIntToLittleEndianBytesFromOffsetOne_expectLittleEndianByteArrayWithLeadingByte() {
		// Given
		// When
		final byte[] actual = Bytes.intToLittleEndianBytes(new byte[5], 1, BIG_ENDIAN_INT_VALUE);

		// Then
		assertArrayEquals(LITTLE_ENDIAN_INT_BYTES_WITH_LEADING_BYTE, actual);
	}

	@Test
	public void givenLittleEndianByteArray_whenLittleEndianByteToInt_expectIntValue() {
		// Given
		// When
		final int actual = Bytes.littleEndianBytesToInt(LITTLE_ENDIAN_INT_BYTES);

		// Then
		assertEquals(BIG_ENDIAN_INT_VALUE, actual);
	}

	@Test
	public void givenLittleEndianByteArrayWithLeadingByte_whenLittleEndianByteToIntFromOffsetOne_expectIntValue() {
		// Given
		// When
		final int actual = Bytes.littleEndianBytesToInt(LITTLE_ENDIAN_INT_BYTES_WITH_LEADING_BYTE, 1);

		// Then
		assertEquals(BIG_ENDIAN_INT_VALUE, actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void givenShortByteArray_whenLittleEndianByteToInt_expectIllegalArgumentException() {
		Bytes.littleEndianBytesToInt(new byte[3]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void givenShortByteArray_whenLittleEndianByteToIntFromOffsetOne_expectIllegalArgumentException() {
		Bytes.littleEndianBytesToInt(new byte[4], 1);
	}

	@Test
	public void givenLongValue_whenLongToBigEndianBytes_expectBigEndianByteArray() {
		// Given
		// When
		final byte[] actual = Bytes.longToBigEndianBytes(BIG_ENDIAN_LONG_VALUE);

		// Then
		assertArrayEquals(BIG_ENDIAN_LONG_BYTES, actual);
	}

	@Test
	public void givenLongValue_andByteArray_whenLongToBigEndianBytesFromOffsetOne_expectBigEndianByteArrayWithLeadingByte() {
		// Given
		// When
		final byte[] actual = Bytes.longToBigEndianBytes(new byte[9], 1, BIG_ENDIAN_LONG_VALUE);

		// Then
		assertArrayEquals(BIG_ENDIAN_LONG_BYTES_WITH_LEADING_BYTE, actual);
	}

	@Test
	public void givenBigEndianByteArray_whenBigEndianByteToLong_expectLongValue() {
		// Given
		// When
		final long actual = Bytes.bigEndianBytesToLong(BIG_ENDIAN_LONG_BYTES);

		// Then
		assertEquals(BIG_ENDIAN_LONG_VALUE, actual);
	}

	@Test
	public void givenBigEndianByteArrayWithLeadingByte_whenBigEndianByteToLongFromOffsetOne_expectLongValue() {
		// Given
		// When
		final long actual = Bytes.bigEndianBytesToLong(BIG_ENDIAN_LONG_BYTES_WITH_LEADING_BYTE, 1);

		// Then
		assertEquals(BIG_ENDIAN_LONG_VALUE, actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void givenShortByteArray_whenBigEndianByteToLong_expectIllegalArgumentException() {
		Bytes.bigEndianBytesToLong(new byte[7]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void givenShortByteArray_whenBigEndianByteToLongFromOffsetOne_expectIllegalArgumentException() {
		Bytes.bigEndianBytesToLong(new byte[8], 1);
	}

	@Test
	public void givenLongValue_whenLongToLittleEndianBytes_expectLittleEndianByteArray() {
		// Given
		// When
		final byte[] actual = Bytes.longToLittleEndianBytes(BIG_ENDIAN_LONG_VALUE);

		// Then
		assertArrayEquals(LITTLE_ENDIAN_LONG_BYTES, actual);
	}

	@Test
	public void givenLongValue_andByteBuffer_whenLongToLittleEndianBytesFromOffsetOne_expectLittleEndianByteArrayWithLeadingByte() {
		// Given
		// When
		final byte[] actual = Bytes.longToLittleEndianBytes(new byte[9], 1, BIG_ENDIAN_LONG_VALUE);

		// Then
		assertArrayEquals(LITTLE_ENDIAN_LONG_BYTES_WITH_LEADING_BYTE, actual);
	}

	@Test
	public void givenLittleEndianByteArray_whenLittleEndianByteToLong_expectLongValue() {
		// Given
		// When
		final long actual = Bytes.littleEndianBytesToLong(LITTLE_ENDIAN_LONG_BYTES);

		// Then
		assertEquals(BIG_ENDIAN_LONG_VALUE, actual);
	}

	@Test
	public void givenLittleEndianByteArrayWithLeadingByte_whenLittleEndianByteToLongFromOffsetOne_expectLongValue() {
		// Given
		// When
		final long actual = Bytes.littleEndianBytesToLong(LITTLE_ENDIAN_LONG_BYTES_WITH_LEADING_BYTE, 1);

		// Then
		assertEquals(BIG_ENDIAN_LONG_VALUE, actual);
	}

	@Test(expected = IllegalArgumentException.class)
	public void givenShortByteArray_whenLittleEndianByteToLong_expectIllegalArgumentException() {
		Bytes.littleEndianBytesToLong(new byte[7]);
	}

	@Test(expected = IllegalArgumentException.class)
	public void givenShortByteArray_whenLittleEndianByteToLongFromOffsetOne_expectIllegalArgumentException() {
		Bytes.littleEndianBytesToLong(new byte[8], 1);
	}
}
