package org.neverfear.util.process;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.neverfear.util.TimeDevice;

public final class ProcessUtil {
	private static final TimeDevice DEVICE = TimeDevice.ticker();

	public static InputStream tryOpenStreamOfProcess(Process process, File redirectFile, long timeout,
			TimeUnit unit) throws FileNotFoundException {
		long now = DEVICE.read(TimeUnit.NANOSECONDS);
		long deadline = now + unit.toNanos(timeout);
		while (DEVICE.read(TimeUnit.NANOSECONDS) < deadline) {
			try {
				FileInputStream fin = new FileInputStream(redirectFile);
				PipedFileInputStream in = new PipedFileInputStream(fin, () -> process.isAlive());
				return in;
			} catch (FileNotFoundException e) {
				Thread.yield();
				continue;
			}
		}
		throw new FileNotFoundException("File was not found within the deadline");
	}
}
