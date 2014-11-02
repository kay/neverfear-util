package org.neverfear.test.util;

import static java.util.Arrays.asList;

import java.util.HashSet;

public final class Matchers {

	private Matchers() {
		throw new AssertionError();
	}

	public static CausedByMatcher causedBy(final Throwable cause) {
		return new CausedByMatcher(cause);
	}

	public static SuppressedMatcher suppressed(final Throwable... suppressed) {
		return new SuppressedMatcher(new HashSet<Throwable>(asList(suppressed)));
	}

	public static <T> IsBefore<T> isBefore(final T firstValue, final T secondValue) {
		return new IsBefore<T>(firstValue,
				secondValue);
	}
}
