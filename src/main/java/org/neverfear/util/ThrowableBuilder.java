package org.neverfear.util;

/**
 * Builder pattern for suppressed exceptions.
 * 
 * Implements a simple way to record every exception that occured in a code unit
 * and have them properly represented in the resultant {@link Throwable}.
 * 
 * <pre>
 * ThrowableBuilder&lt;IOException&gt; builder = ThrowableBuilder.create();
 * for (OutputStream o : openStreams) {
 * 	try {
 * 		o.write(&quot;msg&quot;.getBytes());
 * 	} catch (IOException e) {
 * 		builder.add(e);
 * 	}
 * }
 * builder.throwIfSet();
 * </pre>
 * 
 * This code will throw an IOException where the first failure is the principle
 * exception and subsequent failures are suppressed but recorded in the
 * principle exception.
 * 
 * @author doug@neverfear.org
 * 
 */
public class ThrowableBuilder<T extends Throwable> {

	private T firstException = null;

	/**
	 * Register a new Throwable that matches the constraint T.
	 * 
	 * @param throwable
	 * @return
	 */
	public ThrowableBuilder<T> add(final T throwable) {
		if (this.firstException == null) {
			this.firstException = throwable;
		} else {
			this.firstException.addSuppressed(throwable);
		}
		return this;
	}

	/**
	 * 
	 * @return the current principle exception or null if none added.
	 */
	public T get() {
		return this.firstException;
	}

	/**
	 * If any exceptions were added then an exception is thrown when called.
	 * Otherwise this method returns immediately.
	 * 
	 * @throws T the Throwable to throw.
	 */
	public void throwIfSet() throws T {
		if (this.firstException != null) {
			throw this.firstException;
		}
	}

	/**
	 * Resets the gathered exceptions. This allows you to reuse this builder.
	 */
	public void clear() {
		this.firstException = null;
	}

	public static <T extends Throwable> ThrowableBuilder<T> create() {
		return new ThrowableBuilder<T>();
	}

	public static ThrowableBuilder<Exception> anyException() {
		return new ThrowableBuilder<Exception>();
	}
}
