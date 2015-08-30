package org.neverfear.util.sequence.api;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RangeSequenceTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private RangeSequence zeroToFive;
    private RangeSequence oneToFive;

    @Before
    public void before() {
        this.zeroToFive = new RangeSequence(0, 5);
        this.oneToFive = new RangeSequence(1, 5);
    }

    @Test
    public void givenOneToFive_whenGetLower_expectOne() {
        // Given
        // When
        long lower = this.oneToFive.lower();

        // When
        assertEquals(1L, lower);
    }

    @Test
    public void givenOneToFive_whenGetUpper_expectFive() {
        // Given
        // When
        long upper = this.oneToFive.upper();

        // When
        assertEquals(5L, upper);
    }

    @Test
    public void givenZeroToFive_andZeroClaimed_whenNextOnce_expectZero() {
        // Given
        // When
        long seq = this.zeroToFive.next();

        // Then
        assertEquals(0L, seq);
    }

    @Test
    public void givenZeroToFive_andZeroClaimed_whenNextFiveTimes_expectZeroThenOneThenTwoThenThreeThenFour() {
        assertEquals(0L, this.zeroToFive.next());
        assertEquals(1L, this.zeroToFive.next());
        assertEquals(2L, this.zeroToFive.next());
        assertEquals(3L, this.zeroToFive.next());
        assertEquals(4L, this.zeroToFive.next());
    }

    @Test
    public void givenZeroToFive_andFiveClaimed_whenNextOnceMore_expectSequenceException() {
        // Given
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();

        // Then
        expectedException.expect(SequenceException.class);

        // When
        this.zeroToFive.next();
    }

    @Test
    public void givenZeroToFive_andSixClaimed_whenNextOnceMore_expectSequenceException() {
        // Given
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        try {
            this.zeroToFive.next();
            fail();
        } catch (SequenceException ignored) {
        }

        // Then
        expectedException.expect(SequenceException.class);

        // When
        this.zeroToFive.next();
    }

    @Test
    public void givenZeroToFive_andZeroClaimed_whenIsExhausted_expectFalse() {
        // Given
        // When
        boolean exhausted = this.zeroToFive.isExhausted();

        // Then
        assertFalse(exhausted);
    }

    @Test
    public void givenZeroToFive_andZeroClaimed_whenHasNext_expectTrue() {
        // Given
        // When
        boolean actual = this.zeroToFive.hasNext();

        // Then
        assertTrue(actual);
    }

    @Test
    public void givenZeroToFive_andFourClaimed_whenIsExhausted_expectFalse() {
        // Given
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();

        // When
        boolean exhausted = this.zeroToFive.isExhausted();

        // Then
        assertFalse(exhausted);
    }
    @Test
    public void givenZeroToFive_andFourClaimed_whenHasNext_expectTrue() {
        // Given
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();

        // When
        boolean actual = this.zeroToFive.hasNext();

        // Then
        assertTrue(actual);
    }

    @Test
    public void givenZeroToFive_andFiveClaimed_whenIsExhausted_expectTrue() {
        // Given
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();

        // When
        boolean exhausted = this.zeroToFive.isExhausted();

        // Then
        assertTrue(exhausted);
    }

    @Test
    public void givenZeroToFive_andFiveClaimed_whenHasNext_expectFalse() {
        // Given
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();

        // When
        boolean actual = this.zeroToFive.hasNext();

        // Then
        assertFalse(actual);
    }

    @Test
    public void givenZeroToFive_andSixClaimed_whenIsExhausted_expectTrue() {
        // Given
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        try {
            this.zeroToFive.next();
            fail();
        } catch (SequenceException ignored) {
        }

        // When
        boolean exhausted = this.zeroToFive.isExhausted();

        // Then
        assertTrue(exhausted);
    }

    @Test
    public void givenZeroToFive_andZeroClaimed_whenRemaining_expectFive() {
        // Given
        // When
        long remaining = this.zeroToFive.remaining();

        // Then
        assertEquals(5L, remaining);
    }

    @Test
    public void givenZeroToFive_andFourClaimed_whenRemaining_expectOne() {
        // Given
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();

        // When
        long remaining = this.zeroToFive.remaining();

        // Then
        assertEquals(1L, remaining);
    }

    @Test
    public void givenZeroToFive_andFiveClaimed_whenRemaining_expectZero() {
        // Given
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();

        // When
        long remaining = this.zeroToFive.remaining();

        // Then
        assertEquals(0L, remaining);
    }

    @Test
    public void givenZeroToFive_andSixClaimed_whenRemaining_expectZero() {
        // Given
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        try {
            this.zeroToFive.next();
            fail();
        } catch (SequenceException ignored) {
        }

        // When
        long remaining = this.zeroToFive.remaining();

        // Then
        assertEquals(0L, remaining);
    }

    @Test
    public void givenZeroToFive_andZeroClaimed_whenSize_expectFive() {
        // Given
        // When
        long size = this.zeroToFive.size();

        // Then
        assertEquals(5L, size);
    }

    @Test
    public void givenZeroToFive_andOneClaimed_whenSize_expectFive() {
        // Given
        this.zeroToFive.next();

        // When
        long size = this.zeroToFive.size();

        // Then
        assertEquals(5L, size);
    }

    @Test
    public void givenZeroToFive_andSixClaimed_whenSize_expectZero() {
        // Given
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        this.zeroToFive.next();
        try {
            this.zeroToFive.next();
            fail();
        } catch (SequenceException ignored) {
        }

        // When
        long size = this.zeroToFive.size();

        // Then
        assertEquals(5L, size);
    }

    @Test
    public void givenOneToFive_andZeroClaimed_whenNextOnce_expectOne() {
        // Given
        // When
        long seq = this.oneToFive.next();

        // Then
        assertEquals(1L, seq);
    }

    @Test
    public void givenOneToFive_andZeroClaimed_whenToString_expectFirstEqualsOne_andCursorEqualsOne_andLastEqualsFive() {
        // Given
        // When
        String actual = this.oneToFive.toString();

        // Then
        assertEquals("range 1 <= 1 < 5", actual);
    }
    @Test
    public void givenOneToFive_andOneClaimed_whenToString_expectFirstEqualsOne_andCursorEqualsTwo_andLastEqualsFive() {
        // Given
        this.oneToFive.next();

        // When
        String actual = this.oneToFive.toString();

        // Then
        assertEquals("range 1 <= 2 < 5", actual);
    }
    @Test
    public void givenOneToFive_andFiveClaimed_whenToString_expectFirstEqualsOne_andCursorEqualsFive_andLastEqualsFive() {
        // Given
        this.oneToFive.next();

        // When
        String actual = this.oneToFive.toString();

        // Then
        assertEquals("range 1 <= 2 < 5", actual);
    }
}
