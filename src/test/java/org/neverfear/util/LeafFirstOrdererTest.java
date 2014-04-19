package org.neverfear.util;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class LeafFirstOrdererTest {

	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	private interface TestDependant
		extends Dependant<TestDependant> {

	}

	private LeafFirstOrderer<TestDependant> subject;

	@Before
	public void before() {
		this.subject = new LeafFirstOrderer<>();
	}

	private static TestDependant createInstance(final String name, final TestDependant... dependencies) {
		final TestDependant node = mock(TestDependant.class, name);
		when(node.dependencies()).thenReturn(new HashSet<>(asList(dependencies)));
		return node;
	}

	/**
	 * <pre>
	 *         +---+
	 *   +---->| B +-----+
	 *   |     +---+     v
	 * +-+-+           +---+
	 * | A +---------->| C |
	 * +---+           +---+
	 * </pre>
	 * 
	 * Should become: C B A
	 * 
	 * C is both a direct dependency of A and an indirect dependency via B
	 */
	@Test
	public void givenDependencyGraph_whenOrderWithCollectionContainingAllNodesInTheTree_expectCThenBThenA() {
		/*
		 * Given
		 */
		final TestDependant nodeC = createInstance("C");
		final TestDependant nodeB = createInstance("B", nodeC);
		final TestDependant nodeA = createInstance("A", nodeB, nodeC);

		/*
		 * When
		 */
		// Operands intentionally passed in an order to cause problems
		final List<TestDependant> ordered = this.subject.order(asList(nodeA, nodeC, nodeB));

		/*
		 * Then
		 */
		Assert.assertEquals("All nodes are expected to be in the resultant list",
			new HashSet<>(asList(nodeA, nodeB, nodeC)),
			new HashSet<>(ordered));

		Assert.assertEquals("C must be before B: " + ordered, nodeC, ordered.get(0));
		Assert.assertEquals("B must be before A:" + ordered, nodeB, ordered.get(1));
		Assert.assertEquals("A is the last node:" + ordered, nodeA, ordered.get(2));
	}

	/**
	 * <pre>
	 *         +---+
	 *   +---->| B +-----+
	 *   |     +---+     v
	 * +-+-+           +---+
	 * | A +---------->| C |
	 * +---+           +---+
	 * </pre>
	 * 
	 * Should become: C B A
	 * 
	 * C is both a direct dependency of A and an indirect dependency via B
	 * 
	 * The root node is not first, this test shows that it handles subsequent
	 * discoveries correctly.
	 */
	@Test
	public void givenDependencyGraph_whenOrderWithCollectionContainingAllNodesInTheTreeAndRootNodeIsNotFirst_expectCThenBThenA() {
		/*
		 * Given
		 */
		final TestDependant nodeC = createInstance("C");
		final TestDependant nodeB = createInstance("B", nodeC);
		final TestDependant nodeA = createInstance("A", nodeB, nodeC);

		/*
		 * When
		 */
		final List<TestDependant> ordered = this.subject.order(asList(nodeB, nodeA, nodeC));

		/*
		 * Then
		 */
		Assert.assertEquals("All nodes are expected to be in the resultant list",
			new HashSet<>(asList(nodeA, nodeB, nodeC)),
			new HashSet<>(ordered));

		Assert.assertEquals("C must be before B: " + ordered, nodeC, ordered.get(0));
		Assert.assertEquals("B must be before A:" + ordered, nodeB, ordered.get(1));
		Assert.assertEquals("A is the last node:" + ordered, nodeA, ordered.get(2));
	}

	/**
	 * <pre>
	 *         +---+
	 *   +---->| B +-----+
	 *   |     +---+     |
	 *   |               v
	 * +-+-+           +---+
	 * | A |           | D |
	 * +-+-+           +---+
	 *   |               ^
	 *   |     +---+     |
	 *   +---->| C +-----+
	 *         +---+
	 * </pre>
	 * 
	 * May become D C B A or D B C A.
	 * 
	 * D is the indirect dependency that is shared by B and C
	 */
	@Test
	public void givenSharedIndirectDependency_whenOrder_expectDBeforeBOrCThenBAndCInAnyOrderThenA() {
		/*
		 * Given
		 */
		final TestDependant nodeD = createInstance("D");
		final TestDependant nodeC = createInstance("C", nodeD);
		final TestDependant nodeB = createInstance("B", nodeD);
		final TestDependant nodeA = createInstance("A", nodeB, nodeC);

		/*
		 * When
		 */
		final List<TestDependant> ordered = this.subject.order(asList(nodeA, nodeB, nodeC, nodeD));

		/*
		 * Then
		 */
		Assert.assertEquals("All nodes are expected to be in the resultant list",
			new HashSet<>(asList(nodeA, nodeB, nodeC, nodeD)),
			new HashSet<>(ordered));

		Assert.assertEquals("D must be before B and C", nodeD, ordered.get(0));
		Assert.assertTrue("C must be after D", ordered.indexOf(nodeD) < ordered.indexOf(nodeC));
		Assert.assertTrue("B must be after D", ordered.indexOf(nodeD) < ordered.indexOf(nodeB));
		Assert.assertEquals("A is the last node", nodeA, ordered.get(3));
	}

	/**
	 * <pre>
	 *         +---+
	 *   +---->| B +-----+
	 *   |     +---+     v
	 * +-+-+           +---+
	 * | A +---------->| C |
	 * +---+           +---+
	 * </pre>
	 * 
	 * Should become: C B A
	 * 
	 * C is both a direct dependency of A and an indirect dependency via B
	 */
	@Test
	public void givenDirectDependencyIsAlsoAnIndirectDependency_whenOrder_expectCThenBThenA() {
		/*
		 * Given
		 */
		final TestDependant nodeC = createInstance("C");
		final TestDependant nodeB = createInstance("B", nodeC);
		final TestDependant nodeA = createInstance("A", nodeB, nodeC);

		/*
		 * When
		 */
		final List<TestDependant> ordered = this.subject.order(asList(nodeA, nodeB, nodeC));

		/*
		 * Then
		 */
		Assert.assertEquals("All nodes are expected to be in the resultant list",
			new HashSet<>(asList(nodeA, nodeB, nodeC)),
			new HashSet<>(ordered));

		Assert.assertEquals("C must be before B: " + ordered, nodeC, ordered.get(0));
		Assert.assertEquals("B must be before A:" + ordered, nodeB, ordered.get(1));
		Assert.assertEquals("A is the last node:" + ordered, nodeA, ordered.get(2));
	}

	/**
	 * <pre>
	 * +---+   +---+   +---+
	 * | A +-->| B +-->| C |
	 * +---+   +---+   +---+
	 * </pre>
	 * 
	 * Should become: C B A
	 */
	@Test
	public void givenSequentialDependencyGraph_whenOrder_expectOrderedInReverse() {
		/*
		 * Given
		 */
		final TestDependant nodeC = createInstance("C");
		final TestDependant nodeB = createInstance("B", nodeC);
		final TestDependant nodeA = createInstance("A", nodeB);

		/*
		 * When
		 */
		final List<TestDependant> ordered = this.subject.order(asList(nodeA, nodeB, nodeC));

		/*
		 * Then
		 */
		Assert.assertEquals("All nodes are expected to be in the resultant list",
			new HashSet<>(asList(nodeA, nodeB, nodeC)),
			new HashSet<>(ordered));

		Assert.assertEquals("C must be before B: " + ordered, nodeC, ordered.get(0));
		Assert.assertEquals("B must be before A: " + ordered, nodeB, ordered.get(1));
		Assert.assertEquals("A is the last node: " + ordered, nodeA, ordered.get(2));
	}

	/**
	 * <pre>
	 *         +---+
	 *   +---->| B |
	 *   |     +---+
	 * +-+-+
	 * | A |
	 * +-+-+
	 *   |     +---+
	 *   +---->| C |
	 *         +---+
	 * </pre>
	 * 
	 * May become C B A or B C A
	 */
	@Test
	public void givenForkedDependencies_whenOrder_expectAFirstThenBOrCSecondAndThird() {
		/*
		 * Given
		 */
		final TestDependant nodeC = createInstance("C");
		final TestDependant nodeB = createInstance("B");
		final TestDependant nodeA = createInstance("A", nodeB, nodeC);

		/*
		 * When
		 */
		final List<TestDependant> ordered = this.subject.order(asList(nodeA, nodeB, nodeC));

		/*
		 * Then
		 */
		Assert.assertEquals("All nodes are expected to be in the resultant list",
			new HashSet<>(asList(nodeA, nodeB, nodeC)),
			new HashSet<>(ordered));

		final int indexA = ordered.indexOf(nodeA);
		final int indexB = ordered.indexOf(nodeB);
		final int indexC = ordered.indexOf(nodeC);

		Assert.assertTrue("C must be before B: " + ordered, indexB < indexA);
		Assert.assertTrue("B must be before A: " + ordered, indexC < indexA);
		Assert.assertEquals("A is the last node: " + ordered, nodeA, ordered.get(2));
	}

	/**
	 * <pre>
	 *         +---+
	 *   +---->| B |
	 *   |     +-+-+
	 * +-+-+     |
	 * | A |<----+
	 * +-+-+
	 * </pre>
	 * 
	 * Illegal
	 */
	@Test
	public void givenDirectCircle_whenOrder_expectIllegalArgumentException() {
		/*
		 * Given
		 */
		final TestDependant nodeB = createInstance("B");
		final TestDependant nodeA = createInstance("A", nodeB);
		// Complete the circle
		when(nodeB.dependencies()).thenReturn(new HashSet<>(asList(nodeA, nodeB)));

		/*
		 * Then
		 */
		this.expectedException.expect(IllegalArgumentException.class);

		/*
		 * When
		 */
		this.subject.order(asList(nodeA));
	}

	/**
	 * <pre>
	 *         +---+
	 *   +---->| B +-----+
	 *   |     +---+     v
	 * +-+-+           +---+
	 * | A |<----------+ C |
	 * +---+           +---+
	 * </pre>
	 * 
	 * Illegal
	 */
	@Test
	public void givenIndirectCircle_whenOrder_expectIllegalArgumentExceptionContainingOffendingPath() {
		/*
		 * Given
		 */
		final TestDependant nodeC = createInstance("C");
		final TestDependant nodeB = createInstance("B", nodeC);
		final TestDependant nodeA = createInstance("A", nodeB);
		// Complete the circle
		when(nodeC.dependencies()).thenReturn(new HashSet<>(asList(nodeA)));

		/*
		 * Then
		 */
		this.expectedException.expect(IllegalArgumentException.class);
		this.expectedException.expectMessage("path [A, C, B, A]");

		/*
		 * When
		 */
		this.subject.order(asList(nodeA, nodeB, nodeC));
	}

}
