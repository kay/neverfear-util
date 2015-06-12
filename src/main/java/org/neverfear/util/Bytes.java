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

/**
 * @author doug@neverfear.org
 *
 */
public final class Bytes {

	private static final int LONG_BYTE_SIZE = Long.SIZE / Byte.SIZE;
	private static final int INT_BYTE_SIZE = Integer.SIZE / Byte.SIZE;

	private Bytes() {}

	/**
	 * Reverses the byte ordering of a long
	 * 
	 * @param value
	 * @return
	 * @see Long#reverseBytes(long)
	 */
	public static long swapByteEndianness(final long value) {
		return Long.reverseBytes(value);
	}

	/**
	 * Reverses the byte ordering of an integer
	 * 
	 * @param value
	 * @return
	 * @see Integer#reverseBytes(int)
	 * 
	 */
	public static int swapByteEndianness(final int value) {
		return Integer.reverseBytes(value);
	}

	/**
	 * Convert an integer into a byte array in little endian byte ordering
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] intToLittleEndianBytes(final int value) {
		return intToLittleEndianBytes(new byte[INT_BYTE_SIZE], 0, value);
	}

	/**
	 * Convert an integer into a byte array in little endian byte ordering
	 * 
	 * @param value
	 * @param bytes a buffer to write the bytes into
	 * @param offset an offset in the buffer to start writing
	 * @return
	 */
	public static byte[] intToLittleEndianBytes(final byte[] bytes, final int offset, final int value) {
		checkArrayForInt(bytes, offset);

		int val = value;
		bytes[offset] = (byte) (val & 0xFF);
		val >>= 8;
		bytes[offset + 1] = (byte) (val & 0xFF);
		val >>= 8;
		bytes[offset + 2] = (byte) (val & 0xFF);
		val >>= 8;
		bytes[offset + 3] = (byte) (val & 0xFF);

		return bytes;
	}

	/**
	 * Convert a byte array in little endian byte order to an integer
	 * 
	 * @param bytes must be at least 4 bytes
	 * @return
	 * @throws IllegalArgumentException if the array does not have sufficient
	 *         space
	 */
	public static int littleEndianBytesToInt(final byte[] bytes) throws IllegalArgumentException {
		return littleEndianBytesToInt(bytes, 0);
	}

	/**
	 * Convert a byte array in little endian byte order to an integer
	 * 
	 * @param bytes must be at least 4 bytes
	 * @param offset initial offset to read the int from
	 * @return
	 * @throws IllegalArgumentException if the array does not have sufficient
	 *         space
	 */
	public static int littleEndianBytesToInt(final byte[] bytes, final int offset) throws IllegalArgumentException {
		checkArrayForInt(bytes, offset);

		int value = 0;
		value |= bytes[offset + 3] & 0xFF;
		value <<= 8;
		value |= bytes[offset + 2] & 0xFF;
		value <<= 8;
		value |= bytes[offset + 1] & 0xFF;
		value <<= 8;
		value |= bytes[offset] & 0xFF;
		return value;
	}

	/**
	 * Convert an integer to a byte array in big endian byte ordering
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] intToBigEndianBytes(final int value) {
		return intToBigEndianBytes(new byte[INT_BYTE_SIZE], 0, value);
	}

	/**
	 * Convert an integer to a byte array in big endian byte ordering
	 * 
	 * @param value
	 * @param bytes a buffer to write the bytes into
	 * @param offset an offset in the buffer to start writing
	 * @return
	 */
	public static byte[] intToBigEndianBytes(final byte[] bytes, final int offset, final int value) {
		checkArrayForInt(bytes, offset);

		int val = value;
		bytes[offset + 3] = (byte) (val & 0xFF);
		val >>= 8;
		bytes[offset + 2] = (byte) (val & 0xFF);
		val >>= 8;
		bytes[offset + 1] = (byte) (val & 0xFF);
		val >>= 8;
		bytes[offset] = (byte) (val & 0xFF);
		return bytes;
	}

	/**
	 * Convert a byte array in big endian byte order to an integer
	 * 
	 * @param bytes must be at least 4 bytes
	 * @return
	 * @throws IllegalArgumentException if the array does not have sufficient
	 *         space
	 */
	public static int bigEndianBytesToInt(final byte[] bytes) throws IllegalArgumentException {
		return bigEndianBytesToInt(bytes, 0);
	}

	/**
	 * Convert a byte array in big endian byte order to an integer
	 * 
	 * @param bytes must be at least 4 bytes
	 * @param offset initial offset to read the int from
	 * @return
	 * @throws IllegalArgumentException if the array does not have sufficient
	 *         space
	 */
	public static int bigEndianBytesToInt(final byte[] bytes, final int offset) throws IllegalArgumentException {
		checkArrayForInt(bytes, offset);

		int value = 0;
		value |= bytes[offset] & 0xFF;
		value <<= 8;
		value |= bytes[offset + 1] & 0xFF;
		value <<= 8;
		value |= bytes[offset + 2] & 0xFF;
		value <<= 8;
		value |= bytes[offset + 3] & 0xFF;
		return value;
	}

	/**
	 * Convert a long to a byte array in little endian byte ordering
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] longToLittleEndianBytes(final long value) {
		return longToLittleEndianBytes(new byte[LONG_BYTE_SIZE], 0, value);
	}

	/**
	 * Convert a long to a byte array in little endian byte ordering
	 * 
	 * @param value
	 * @param bytes a buffer to write the bytes into
	 * @param offset an offset in the buffer to start writing
	 * @return
	 */
	public static byte[] longToLittleEndianBytes(final byte[] bytes, final int offset, final long value) {
		long val = value;
		bytes[offset] = (byte) (val & 0xFF);
		val >>= 8;
		bytes[offset + 1] = (byte) (val & 0xFF);
		val >>= 8;
		bytes[offset + 2] = (byte) (val & 0xFF);
		val >>= 8;
		bytes[offset + 3] = (byte) (val & 0xFF);
		val >>= 8;
		bytes[offset + 4] = (byte) (val & 0xFF);
		val >>= 8;
		bytes[offset + 5] = (byte) (val & 0xFF);
		val >>= 8;
		bytes[offset + 6] = (byte) (val & 0xFF);
		val >>= 8;
		bytes[offset + 7] = (byte) (val & 0xFF);
		return bytes;
	}

	/**
	 * Convert a byte array in little endian byte ordering to a long
	 * 
	 * @param bytes must be at least 8 bytes
	 * @return
	 * @throws IllegalArgumentException if the array does not have sufficient
	 *         space
	 */
	public static long littleEndianBytesToLong(final byte[] bytes) throws IllegalArgumentException {
		return littleEndianBytesToLong(bytes, 0);
	}

	/**
	 * Convert a byte array in little endian byte ordering to a long
	 * 
	 * @param bytes must be at least 8 bytes
	 * @param offset initial offset to read the long from
	 * @return
	 * @throws IllegalArgumentException if the array does not have sufficient
	 *         space
	 */
	public static long littleEndianBytesToLong(final byte[] bytes, final int offset) throws IllegalArgumentException {
		checkArrayForLong(bytes, offset);

		long value = 0L;
		value |= bytes[offset + 7] & 0xFF;
		value <<= 8;
		value |= bytes[offset + 6] & 0xFF;
		value <<= 8;
		value |= bytes[offset + 5] & 0xFF;
		value <<= 8;
		value |= bytes[offset + 4] & 0xFF;
		value <<= 8;
		value |= bytes[offset + 3] & 0xFF;
		value <<= 8;
		value |= bytes[offset + 2] & 0xFF;
		value <<= 8;
		value |= bytes[offset + 1] & 0xFF;
		value <<= 8;
		value |= bytes[offset] & 0xFF;
		return value;
	}

	/**
	 * Convert a long to a byte array in big endian byte ordering
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] longToBigEndianBytes(final long value) {
		return longToBigEndianBytes(new byte[LONG_BYTE_SIZE], 0, value);
	}

	/**
	 * Convert a long to a byte array in big endian byte ordering
	 * 
	 * @param value
	 * @param bytes a buffer to write the bytes into
	 * @param offset an offset in the buffer to start writing
	 * @return
	 */
	public static byte[] longToBigEndianBytes(final byte[] bytes, final int offset, final long value) {
		long val = value;
		bytes[offset + 7] = (byte) (val & 0xFF);
		val >>= 8;
		bytes[offset + 6] = (byte) (val & 0xFF);
		val >>= 8;
		bytes[offset + 5] = (byte) (val & 0xFF);
		val >>= 8;
		bytes[offset + 4] = (byte) (val & 0xFF);
		val >>= 8;
		bytes[offset + 3] = (byte) (val & 0xFF);
		val >>= 8;
		bytes[offset + 2] = (byte) (val & 0xFF);
		val >>= 8;
		bytes[offset + 1] = (byte) (val & 0xFF);
		val >>= 8;
		bytes[offset] = (byte) (val & 0xFF);
		return bytes;
	}

	/**
	 * Convert a byte array in big endian byte order to a long
	 * 
	 * @param bytes
	 * @return
	 * @throws IllegalArgumentException if the array does not have sufficient
	 *         space
	 */
	public static long bigEndianBytesToLong(final byte[] bytes) throws IllegalArgumentException {
		return bigEndianBytesToLong(bytes, 0);
	}

	/**
	 * Convert a byte array in big endian byte order to a long
	 * 
	 * @param bytes
	 * @param offset initial offset to read the long from
	 * @return
	 * @throws IllegalArgumentException if the array does not have sufficient
	 *         space
	 */
	public static long bigEndianBytesToLong(final byte[] bytes, final int offset) throws IllegalArgumentException {
		checkArrayForLong(bytes, offset);

		long value = 0L;
		value |= bytes[offset] & 0xFF;
		value <<= 8;
		value |= bytes[offset + 1] & 0xFF;
		value <<= 8;
		value |= bytes[offset + 2] & 0xFF;
		value <<= 8;
		value |= bytes[offset + 3] & 0xFF;
		value <<= 8;
		value |= bytes[offset + 4] & 0xFF;
		value <<= 8;
		value |= bytes[offset + 5] & 0xFF;
		value <<= 8;
		value |= bytes[offset + 6] & 0xFF;
		value <<= 8;
		value |= bytes[offset + 7] & 0xFF;
		return value;
	}

	private static void checkArrayForLong(final byte[] bytes, final int offset) {
		if (bytes.length < LONG_BYTE_SIZE + offset) {
			throw new IllegalArgumentException("byte array has insufficient space");
		}
	}

	private static void checkArrayForInt(final byte[] bytes, final int offset) {
		if (bytes.length < INT_BYTE_SIZE + offset) {
			throw new IllegalArgumentException("byte array has insufficient space");
		}
	}
}
