package org.neverfear.util;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

public class TimeDeviceTest {

	private static final int TIME_IN_SECONDS = 123;
	private TimeDevice subject;

	@Before
	public void before() {
		this.subject = new TimeDevice(TimeUnit.SECONDS) {

			@Override
			public long read() {
				return TIME_IN_SECONDS;
			}
		};
	}

	@Test
	public void givenTicker_whenTimeUnit_expectNanoseconds() {
		/*
		 * Given
		 */
		final TimeDevice ticker = TimeDevice.ticker();

		/*
		 * When
		 */
		final TimeUnit actual = ticker.timeUnit();

		/*
		 * Then
		 */
		assertEquals(TimeUnit.NANOSECONDS, actual);
	}

	@Test
	public void givenTicker_whenReadTwiceSeparatedByOneMillisecond_expectAtLeastOneThousand() throws Exception {
		/*
		 * Given
		 */
		final TimeDevice ticker = TimeDevice.ticker();

		/*
		 * When
		 */
		final long first = ticker.read();
		sleep(1);
		final long second = ticker.read();

		/*
		 * Then
		 */
		final long elapsed = second - first;
		assertTrue(elapsed >= 1000);
	}

	@Test
	public void givenWallClock_whenTimeUnit_expectNanoseconds() {
		/*
		 * Given
		 */
		final TimeDevice wallClock = TimeDevice.wallClock();

		/*
		 * When
		 */
		final TimeUnit actual = wallClock.timeUnit();

		/*
		 * Then
		 */
		assertEquals(TimeUnit.MILLISECONDS, actual);
	}

	@Test
	public void givenWallClock_whenReadTwiceSeparatedByOneMillisecond_expectAtLeastFive() throws Exception {
		/*
		 * Given
		 */
		final TimeDevice wallClock = TimeDevice.wallClock();

		/*
		 * When
		 */
		final long first = wallClock.read();
		sleep(5);
		final long second = wallClock.read();

		/*
		 * Then
		 */
		final long elapsed = second - first;
		assertTrue(elapsed >= 5);
	}

	@Test
	public void givenTimeDeviceMeasuredInSecond_whenTimeUnit_expectSeconds() {
		assertEquals(TimeUnit.SECONDS, this.subject.timeUnit());
	}

	@Test
	public void givenTimeDevice_whenRead_expectFixedTime() {
		final long actual = this.subject.read();
		assertEquals(TIME_IN_SECONDS, actual);
	}

	@Test
	public void givenTimeDevice_whenReadAsMilliseconds_expectTimeRepresentedAsMilliseconds() {
		final long actual = this.subject.read(TimeUnit.MILLISECONDS);
		assertEquals(TIME_IN_SECONDS * 1000, actual);
	}
}
