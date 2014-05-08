package org.neverfear.test.util;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.mockito.internal.matchers.Equality;

public final class CausedByMatcher
	extends BaseMatcher<Throwable> {

	private final Throwable cause;

	public CausedByMatcher(final Throwable cause) {
		this.cause = cause;
	}

	@Override
	public boolean matches(final Object argument) {
		final Throwable throwable = (Throwable) argument;
		final Throwable actual = throwable.getCause();
		return Equality.areEqual(this.cause, actual);
	}

	@Override
	public void describeTo(final Description description) {
		description.appendValue(this.cause);
	}
}