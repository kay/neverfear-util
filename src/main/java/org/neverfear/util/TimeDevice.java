package org.neverfear.util;

import java.util.concurrent.TimeUnit;

public abstract class TimeDevice {

	private static final TimeDevice WALL_CLOCK_TIME_DEVICE = new TimeDevice(TimeUnit.MILLISECONDS) {

		@Override
		public long read() {
			return System.currentTimeMillis();
		}
	};

	private static final TimeDevice TICKER_TIME_DEVICE = new TimeDevice(TimeUnit.NANOSECONDS) {

		@Override
		public long read() {
			return System.nanoTime();
		}
	};

	public static TimeDevice wallClock() {
		return WALL_CLOCK_TIME_DEVICE;
	}

	public static TimeDevice ticker() {
		return TICKER_TIME_DEVICE;
	}

	private final TimeUnit timeUnit;

	public TimeDevice(final TimeUnit timeUnit) {
		super();
		this.timeUnit = timeUnit;
	}

	public abstract long read();

	public final TimeUnit timeUnit() {
		return this.timeUnit;
	}

	public long read(final TimeUnit asUnit) {
		if (asUnit == this.timeUnit) {
			return read();
		} else {
			return asUnit.convert(read(), this.timeUnit);
		}
	}
}
