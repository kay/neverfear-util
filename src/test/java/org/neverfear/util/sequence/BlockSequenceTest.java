/*
 * Copyright 2014 doug@neverfear.org
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
package org.neverfear.util.sequence;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.neverfear.util.sequence.BlockSequence.MinimumPolicy;

/**
 * @author doug@neverfear.org
 * 
 */
public class BlockSequenceTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private BlockSequenceAllocator allocator;
	private BlockSequence subject;

	@Before
	public void before() {
		this.allocator = spy(new InMemoryBlockSequenceAllocator(3));
		this.subject = new BlockSequence(this.allocator,
				new MinimumPolicy(2));
	}

	@Test
	public void givenAllocatorReturnsEmpty_whenNext_throwsIllegalStateException() {
		// Given
		this.allocator = spy(new InMemoryBlockSequenceAllocator(0));
		this.subject = new BlockSequence(this.allocator,
				new MinimumPolicy(2));

		// Then
		this.expectedException.expect(SequenceException.class);
		this.expectedException.expectMessage("Range exhausted");

		// When
		this.subject.next();
	}

	@Test
	public void givenAllocatorReturnsBlockSmallerThanMinimum_whenNext_expectAllocatesAgain() {
		// Given
		this.allocator = spy(new InMemoryBlockSequenceAllocator(1));
		this.subject = new BlockSequence(this.allocator,
				new MinimumPolicy(2));

		// When
		final long first = this.subject.next();
		final long second = this.subject.next();

		// Then
		assertEquals(0, first);
		assertEquals(1, second);
		verify(this.allocator, times(2)).allocate();
	}

	@Test
	public void givenTenInvocations_whenNext_expectTen() {
		// Given
		for (int i = 0; i < 10; i++) {
			assertEquals(i, this.subject.next());
		}

		// When
		final long value = this.subject.next();

		// Then
		assertEquals(10, value);
	}

	@Test
	public void givenInitial_whenNext_expectAllocates_andReturnsZero() throws Exception {
		// Given
		// When
		final long value = this.subject.next();

		// Then
		verify(this.allocator).allocate();

		// And
		assertEquals(0, value);
	}

	@Test
	public void givenAllocated_andHasOneRemaining_whenNext_expectAllocates_andReturnsTwoFromOriginalAllocation()
			throws Exception {
		// Given
		this.subject.next();
		this.subject.next();

		// When
		final long value = this.subject.next();

		// Then
		verify(this.allocator, times(2)).allocate();

		// And
		assertEquals(2, value);
	}

	@Test
	public void givenAllocated_andHasTwoRemaining_whenNext_expectNoAdditionalAllocation_andReturnsOneFromOriginalAllocation()
			throws Exception {
		// Given
		this.subject.next();

		// When
		final long value = this.subject.next();

		// Then
		verify(this.allocator).allocate();

		// And
		assertEquals(1, value);
	}

	@Test
	public void givenAllocated_andHasZeroRemaining_whenNext_expectAllocates_andReturnsThreeFromSecondAllocation()
			throws Exception {
		// Given
		this.subject.next();
		this.subject.next();
		this.subject.next();

		// When
		final long value = this.subject.next();

		// Then
		verify(this.allocator, times(2)).allocate();

		// And
		assertEquals(3, value);
	}
}
