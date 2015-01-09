/*
 * Copyright 2015 doug@neverfear.org
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
package org.neverfear.test.util.logging.logback;

import java.util.Queue;

import org.hamcrest.Matcher;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;

/**
 * An {@link Appender} that appends to a {@link Queue}.
 * 
 * @author doug@neverfear.org
 */
final class QueueAppender
	extends AppenderBase<ILoggingEvent> {

	private final Queue<ILoggingEvent> events;
	final Matcher<? super ILoggingEvent> filter;

	public QueueAppender(final Queue<ILoggingEvent> events,
			final Matcher<? super ILoggingEvent> filter) {
		super();
		this.events = events;
		this.filter = filter;
	}

	@Override
	protected void append(final ILoggingEvent eventObject) {
		if (this.filter.matches(eventObject)) {
			this.events.add(eventObject);
		}
	}
}