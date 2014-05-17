package org.neverfear.test.util;

import java.util.concurrent.TimeUnit;

import org.neverfear.util.TimeDevice;

public class TestTimeDevice
	extends TimeDevice {

	private long value = 0;

	public TestTimeDevice(final TimeUnit timeUnit) {
		super(timeUnit);
	}

	@Override
	public long read() {
		return this.value;
	}

	/**
	 * Set the current time value to be read from {@link #read()}
	 * 
	 * @param value
	 */
	public void setValue(final long value) {
		this.value = value;
	}
}
