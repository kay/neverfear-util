package org.neverfear.util;

import java.util.concurrent.TimeUnit;

public final class StopWatch {

	private final TimeDevice timeDevice;

	private long start = Long.MIN_VALUE;

	public StopWatch(final TimeDevice timeDevice) {
		super();
		this.timeDevice = timeDevice;
	}

	public TimeDevice timeDevice() {
		return this.timeDevice;
	}

	/**
	 * Start timing from the point at which this is called.
	 */
	public void start() {
		this.start = this.timeDevice.read();
	}

	/**
	 * Acts as a split lap time.
	 * 
	 * @return the time elapsed since {@link #start()} was called in the
	 *         {@link TimeUnit}s of the {@link TimeDevice}
	 * @throws IllegalStateException if not started
	 */
	public long lap() {
		return lap(this.timeDevice.timeUnit());
	}

	/**
	 * Acts as a split lap time.
	 * 
	 * @return same as {@link #lap()} but the value is in the passed units
	 * @throws IllegalStateException if not started
	 */
	public long lap(final TimeUnit timeUnit) {
		final long now = this.timeDevice.read();
		if (this.start == Long.MIN_VALUE) {
			throw new IllegalStateException("not started");
		}
		final long duration = now - this.start;
		return timeUnit.convert(duration, this.timeDevice.timeUnit());
	}

	/**
	 * Takes a reading and restarts the timer from zero.
	 * 
	 * @return the duration of the last run
	 * @throws IllegalStateException if not started
	 */
	public long restart() {
		final long duration = lap();
		start();
		return duration;
	}

	/**
	 * Takes a reading and restarts the timer from zero.
	 * 
	 * @return same as {@link #restart()} but the value is in the passed units
	 * @throws IllegalStateException if not started
	 */
	public long restart(final TimeUnit timeUnit) {
		final long duration = lap(timeUnit);
		start();
		return duration;
	}

	/**
	 * Reset the recorded times to their initial values.
	 */
	public void reset() {
		this.start = Long.MIN_VALUE;
	}

}
