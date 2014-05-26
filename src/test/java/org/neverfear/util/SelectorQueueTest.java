package org.neverfear.util;

import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SelectorQueueTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private Selector selector;
	private SelectionKey selectionKey1;
	private SelectionKey selectionKey2;
	private SelectionKey selectionKey3;
	private SelectorQueue subject;

	@Before
	public void before() {
		this.selector = mock(Selector.class);
		this.selectionKey1 = mock(SelectionKey.class, "Key 1");
		this.selectionKey2 = mock(SelectionKey.class, "Key 2");
		this.selectionKey3 = mock(SelectionKey.class, "Key 3");
		this.subject = new SelectorQueue(this.selector);
	}

	@Test
	public void givenSelectReturnsOne_andSelectedKeysEmpty_whenTake_expectAssertionError() throws Exception {
		/*
		 * Given
		 */
		when(this.selector.select()).thenReturn(1);
		final Set<SelectionKey> emptySet = Collections.emptySet();
		when(this.selector.selectedKeys()).thenReturn(emptySet);

		/*
		 * Then
		 */
		{
			/*
			 * This may also happen if you've got assertions disabled. Enable it
			 * fool!
			 */
			this.expectedException.handleAssertionErrors()
					.expect(AssertionError.class);
		}

		/*
		 * When
		 */
		this.subject.take();
	}

	@Test
	public void givenSelectReturnsZeroThenTwo_andSelectKeysHasTwoElements_whenTake_expectFirstValue_andFirstValueRemoved()
			throws Exception {
		/*
		 * Given
		 */
		final Set<SelectionKey> original = new HashSet<>(Arrays.asList(this.selectionKey1, this.selectionKey2));
		final Set<SelectionKey> copy = new HashSet<>(original);
		when(this.selector.select()).thenReturn(0, original.size());
		when(this.selector.selectedKeys()).thenReturn(copy);

		/*
		 * When
		 */
		final SelectionKey actual = this.subject.take();

		/*
		 * Then
		 */
		assertTrue("Must be one of the elements in the set", original.contains(actual));
		assertFalse("Expected to be removed from the copy", copy.contains(actual));
	}

	@Test
	public void givenInterrupted_whenTake_expectInterrupted()
			throws Exception {
		/*
		 * Given
		 */
		when(this.selector.select()).thenReturn(0);
		currentThread().interrupt();

		/*
		 * Then
		 */
		this.expectedException.expect(InterruptedException.class);

		/*
		 * When
		 */
		this.subject.take();
	}

	@Test
	public void givenSelectReturnsZeroThenTwo_andSelectKeysHasTwoElements_andTake_whenTake_expectLastValue()
			throws Exception {
		/*
		 * Given
		 */
		final Set<SelectionKey> original = new HashSet<>(Arrays.asList(this.selectionKey1, this.selectionKey2));
		final Set<SelectionKey> copy = new HashSet<>(original);
		when(this.selector.select()).thenReturn(0, original.size());
		when(this.selector.selectedKeys()).thenReturn(copy);

		/*
		 * When
		 */
		this.subject.take();
		final SelectionKey actual = this.subject.take();

		/*
		 * Then
		 */
		assertTrue("Must be one of the elements in the set", original.contains(actual));
		assertFalse("Expected to be removed from the copy", copy.contains(actual));
		assertTrue(copy.isEmpty());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void givenFirstBatchOfTwoKeys_andSecondBatchOfOneKey_whenTakeTwice_andThenTake_expectKey1_then2_then3()
			throws Exception {
		/*
		 * Given
		 */
		final Set<SelectionKey> firstBatch = new HashSet<>(Arrays.asList(this.selectionKey1, this.selectionKey2));
		final Set<SelectionKey> secondBatch = new HashSet<>(Arrays.asList(this.selectionKey3));
		when(this.selector.select()).thenReturn(firstBatch.size(), secondBatch.size());
		when(this.selector.selectedKeys()).thenReturn(firstBatch, secondBatch);

		/*
		 * When
		 */
		this.subject.take();
		this.subject.take();
		final SelectionKey actual = this.subject.take();

		/*
		 * Then
		 */
		assertTrue(firstBatch.isEmpty());
		assertTrue(secondBatch.isEmpty());
		assertEquals(this.selectionKey3, actual);
	}

}
