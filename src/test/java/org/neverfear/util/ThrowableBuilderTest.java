package org.neverfear.util;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.neverfear.test.util.Matchers.suppressed;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ThrowableBuilderTest {

	private static final IOException EX1 = new IOException();
	private static final IOException EX2 = new IOException();

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private ThrowableBuilder<IOException> builder;

	@Before
	public void before() {
		this.builder = ThrowableBuilder.create();
	}

	@Test
	public void givenEmptyBuilder_whenGet_expectNull() {
		assertNull(this.builder.get());
	}

	@Test
	public void givenOneAdded_whenGet_expectAddedException() {
		this.builder.add(EX1);

		assertEquals(EX1, this.builder.get());
	}

	@Test
	public void givenTwoAdded_whenGet_expectAddedException_andSecondExceptionSuppressed() {
		this.builder.add(EX1);
		this.builder.add(EX2);

		assertEquals(EX1, this.builder.get());
		assertEquals(EX2, this.builder.get()
				.getSuppressed()[0]);
	}

	@Test
	public void givenEmptyBuilder_whenThrowIfSet_expectNothing() throws IOException {
		this.builder.throwIfSet();
	}

	@Test
	public void givenOneAdded_whenThrowIfSet_expectAddedException() throws IOException {
		this.builder.add(EX1);
		this.expectedException.expect(equalTo(EX1));
		this.builder.throwIfSet();
	}

	@Test
	public void givenTwoAdded_whenThrowIfSet_expectAddedException_andSecondExceptionSuppressed() throws IOException {
		this.builder.add(EX1);
		this.builder.add(EX2);

		this.expectedException.expect(allOf(equalTo(EX1), suppressed(EX2)));

		this.builder.throwIfSet();
	}

	@Test
	public void givenAdded_whenClear_expectExceptionUnset() {
		this.builder.add(EX1);
		this.builder.clear();
		assertNull(this.builder.get());
	}
}
