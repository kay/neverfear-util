package org.neverfear.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.neverfear.test.util.TestTimeDevice;

public class StopWatchTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private TestTimeDevice timeDevice;
	private StopWatch subject;

	@Before
	public void before() {
		this.timeDevice = new TestTimeDevice(TimeUnit.MILLISECONDS);
		this.subject = new StopWatch(this.timeDevice);
	}

	@Test
	public void givenTimeDevice_whenGetTimeDevice_expectSameTimeDevice() {
		assertEquals(this.timeDevice, this.subject.timeDevice());
	}

	@Test
	public void givenNotStarted_whenLap_expectIllegalStateException() {
		this.expectedException.expect(IllegalStateException.class);

		this.subject.lap();

		fail("Should not have gotten here");
	}

	@Test
	public void givenNotStarted_whenLapWithTimeUnit_expectIllegalStateException() {
		this.expectedException.expect(IllegalStateException.class);

		this.subject.lap(TimeUnit.DAYS);

		fail("Should not have gotten here");
	}

	@Test
	public void givenNotStarted_whenRestart_expectIllegalStateException() {
		this.expectedException.expect(IllegalStateException.class);

		this.subject.restart();

		fail("Should not have gotten here");
	}

	@Test
	public void givenNotStarted_whenRestartWithTimeUnit_expectIllegalStateException() {
		this.expectedException.expect(IllegalStateException.class);

		this.subject.restart(TimeUnit.DAYS);

		fail("Should not have gotten here");
	}

	@Test
	public void givenStarted_whenRestart_expectIllegalStateException() {
		/*
		 * Given
		 */
		final long expected = 123;
		this.subject.start();
		this.timeDevice.setValue(expected);

		/*
		 * When
		 */
		final long actual = this.subject.restart();

		/*
		 * Then
		 */
		assertEquals(expected, actual);
	}

	@Test
	public void givenStarted_whenRestartWithTimeUnit_expectIllegalStateException() {
		/*
		 * Given
		 */
		final long expected = 123;
		this.subject.start();
		this.timeDevice.setValue(expected);

		/*
		 * When
		 */
		final long actual = this.subject.restart(TimeUnit.MICROSECONDS);

		/*
		 * Then
		 */
		assertEquals(expected * 1000, actual);
	}

	@Test
	public void givenStarted_thenReset_whenStart_expectIllegalStateException() {
		/*
		 * Given
		 */
		this.subject.start();
		this.subject.reset();

		/*
		 * Then
		 */
		this.expectedException.expect(IllegalStateException.class);

		/*
		 * When
		 */
		this.subject.lap();

		fail("Should not have gotten here");
	}

	@Test
	public void givenStarted_whenLap_expectDifferenceBetweenTimePoints() {
		/*
		 * Given
		 */
		final long expected = 123;
		final int init = 20;
		this.timeDevice.setValue(init);
		this.subject.start();
		this.timeDevice.setValue(init + expected);
		/*
		 * When
		 */
		final long actual = this.subject.lap();

		/*
		 * Then
		 */
		assertEquals(expected, actual);
	}

	@Test
	public void givenStarted_whenLapWithTimeUnit_expectDifferenceBetweenTimePointsInTargetTimeUnit() {
		/*
		 * Given
		 */
		final long interval = 123;
		final int init = 20;
		this.timeDevice.setValue(init);
		this.subject.start();
		this.timeDevice.setValue(init + interval);

		/*
		 * When
		 */
		final long actual = this.subject.lap(TimeUnit.MICROSECONDS);

		/*
		 * Then
		 */
		assertEquals(interval * 1000, actual);
	}
}
