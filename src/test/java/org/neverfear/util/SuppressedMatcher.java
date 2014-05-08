package org.neverfear.util;

import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public final class SuppressedMatcher
	extends BaseMatcher<Throwable> {

	private final Set<? extends Throwable> wanted;

	public SuppressedMatcher(final Throwable wanted) {
		this(new HashSet<>(asList(wanted)));
	}

	public SuppressedMatcher(final Set<? extends Throwable> wanted) {
		this.wanted = wanted;
	}

	@Override
	public boolean matches(final Object argument) {
		final Throwable throwable = (Throwable) argument;
		final Set<Throwable> actual = new HashSet<>(asList(throwable.getSuppressed()));
		return this.wanted.equals(actual);
	}

	@Override
	public void describeTo(final Description description) {
		description.appendText("surpressed exception")
				.appendValue(this.wanted);
	}
}