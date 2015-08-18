package org.neverfear.util.sequence;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neverfear.util.sequence.api.RangeSequence;

import static org.junit.Assert.assertEquals;

public class InMemoryBlockSequenceAllocatorTest {
    private InMemoryBlockSequenceAllocator subject;

    @Before
    public void before() {
        this.subject = new InMemoryBlockSequenceAllocator(3);
    }

    @Test
    public void givenRangeLengthOfThree_whenAllocate_expectThreeEntries_andLowerEqualsZero_andUpperEqualsThree() {
        // Given
        // When
        RangeSequence rangeSequence = this.subject.allocate();

        // Then
        assertEquals(3L, rangeSequence.size());
        assertEquals(0L, rangeSequence.lower());
        assertEquals(3L, rangeSequence.upper());
    }

    @Test
    public void givenRangeLengthOfThree_andAllocatedFirstBlock_whenAllocateSecondBlock_expectThreeEntries_andLowerEqualsThree_andUpperEqualsSix() {
        // Given
        this.subject.allocate();

        // When
        RangeSequence rangeSequence = this.subject.allocate();

        // Then
        assertEquals(3L, rangeSequence.size());
        assertEquals(3L, rangeSequence.lower());
        assertEquals(6L, rangeSequence.upper());
    }
}
