/*
 * Copyright 2014 doug@neverfear.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neverfear.test.util;

import static org.hamcrest.CoreMatchers.equalTo;

import java.util.Iterator;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Matches if both matchers (or values) are present in an iterable and the first
 * matcher (or value) is before the second.
 * 
 * @author doug@neverfear.org
 * 
 */
public class IsBefore<T>
	extends BaseMatcher<Iterable<T>> {

	private final Matcher<T> first;
	private final Matcher<T> second;

	public IsBefore(final T firstValue, final T secondValue) {
		this(equalTo(firstValue),
				equalTo(secondValue));
	}

	public IsBefore(final Matcher<T> first, final Matcher<T> second) {
		super();
		this.first = first;
		this.second = second;
	}

	@Override
	public void describeMismatch(final Object item, final Description description) {
		final Iterable<T> iterable = (Iterable<T>) item;
		final Iterator<T> it = iterable.iterator();
		if (iteratorMatchesFirstBeforeSecond(it)) {
			assert !iteratorContainsMatch(it, this.second) : "should not get here because this if true then this is not a mismatch";
			description.appendDescriptionOf(this.first)
					.appendText(" was found but ")
					.appendDescriptionOf(this.second)
					.appendText(" is not in ")
					.appendValue(item);

		} else {
			if (iteratorContainsMatch(it, this.first)) {
				description.appendDescriptionOf(this.second)
						.appendText(" is before ")
						.appendDescriptionOf(this.first);
			} else {
				description.appendDescriptionOf(this.first)
						.appendText(" is not in ")
						.appendValue(item);
			}
		}
	}

	@Override
	public boolean matches(final Object argument) {
		final Iterable<T> iterable = (Iterable<T>) argument;
		final Iterator<T> it = iterable.iterator();
		if (iteratorMatchesFirstBeforeSecond(it)) {
			return iteratorContainsMatch(it, this.second);
		}
		return false;
	}

	@Override
	public void describeTo(final Description description) {
		description.appendDescriptionOf(this.first)
				.appendText(" before ")
				.appendDescriptionOf(this.second);
	}

	private boolean iteratorMatchesFirstBeforeSecond(final Iterator<T> it) {
		while (it.hasNext()) {
			final T value = it.next();
			if (this.first.matches(value)) {
				return true;
			} else if (this.second.matches(value)) {
				return false;
			}
		}
		return false;
	}

	private static <T> boolean iteratorContainsMatch(final Iterator<T> it, final Matcher<T> matcher) {
		while (it.hasNext()) {
			final T value = it.next();
			if (matcher.matches(value)) {
				return true;
			}
		}
		return false;
	}

}
