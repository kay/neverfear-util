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
package org.neverfear.test.util.logging.logback;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assume.assumeTrue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.hamcrest.CoreMatchers;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.rules.ExternalResource;
import org.slf4j.ILoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * This rule adds itself as a Logback {@link Appender} before the test method or
 * class and records all log events matching a specific filter.
 * 
 * @author doug@neverfear.org
 * 
 */
public class LogbackLogs
	extends ExternalResource {

	private static final ILoggerFactory LOGGER_FACTORY = StaticLoggerBinder.getSingleton()
			.getLoggerFactory();

	private static class NamedLoggerSupplier
		implements Supplier<Logger> {

		private final String name;

		public NamedLoggerSupplier(final String name) {
			this.name = name;
		}

		@Override
		public Logger get() {
			final org.slf4j.Logger slf4jLogger = LOGGER_FACTORY.getLogger(this.name);
			assumeTrue("Logback not in use", slf4jLogger instanceof Logger);
			return (Logger) slf4jLogger;
		}

	}

	private final BlockingQueue<ILoggingEvent> events = new LinkedBlockingQueue<>();
	private final List<ILoggingEvent> seen;
	private final List<ILoggingEvent> readOnlyList;

	private final Supplier<Logger> loggerSupplier;

	private final QueueAppender appender;

	public LogbackLogs() {
		this(CoreMatchers.anything(),
				new NamedLoggerSupplier(Logger.ROOT_LOGGER_NAME));
	}

	private LogbackLogs(final Matcher<? super ILoggingEvent> filter,
			final Supplier<Logger> loggerSupplier) {

		final LinkedList<ILoggingEvent> list = new LinkedList<>();
		this.seen = list;
		this.readOnlyList = Collections.unmodifiableList(this.seen);
		this.loggerSupplier = loggerSupplier;
		this.appender = new QueueAppender(this.events,
				filter);
	}

	public LogbackLogs filter(final Matcher<? super ILoggingEvent> filter) {
		return new LogbackLogs(filter,
				this.loggerSupplier);
	}

	public LogbackLogs logger(final String name) {
		return new LogbackLogs(this.appender.filter,
				new NamedLoggerSupplier(name));
	}

	public LogbackLogs logger(final Class<?> clazz) {
		return logger(clazz.getName());
	}

	public ILoggingEvent latest() {
		final List<ILoggingEvent> list = list();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(list.size() - 1);
		}
	}

	public List<ILoggingEvent> list() {
		this.events.drainTo(this.seen);
		return this.readOnlyList;
	}

	public List<String> listMessages() {
		final List<ILoggingEvent> list = list();
		return Lists.newArrayList(Iterables.transform(list, new Function<ILoggingEvent, String>() {

			@Override
			public String apply(final ILoggingEvent input) {
				return input.getFormattedMessage();
			}
		}));
	}

	public List<ILoggingEvent> findAll(final Matcher<? super ILoggingEvent> matcher) {
		final List<ILoggingEvent> filtered = new LinkedList<>();
		for (final ILoggingEvent event : list()) {
			if (matcher.matches(event)) {
				filtered.add(event);
			}
		}
		return filtered;
	}

	public ILoggingEvent findFirst(final Matcher<? super ILoggingEvent> matcher) {
		for (final ILoggingEvent event : list()) {
			if (matcher.matches(event)) {
				return event;
			}
		}
		return null;
	}

	public void awaitMatch(final Matcher<? super ILoggingEvent> matcher) throws InterruptedException {
		while (findFirst(matcher) == null) {
			if (Thread.interrupted()) {
				throw new InterruptedException();
			}
			Thread.yield();
		}
	}

	@Override
	protected void before() {
		final Logger logger = this.loggerSupplier.get();
		this.appender.setContext(logger.getLoggerContext());
		this.appender.start();
		logger.addAppender(this.appender);
	}

	@Override
	protected void after() {
		final Logger logger = this.loggerSupplier.get();
		this.appender.stop();
		logger.detachAppender(this.appender);
	}

	public static FeatureMatcher<ILoggingEvent, String> formattedMessage(final Matcher<? super String> matcher) {
		return new FeatureMatcher<ILoggingEvent, String>(matcher, "formatted log message", "formatted log message") {

			@Override
			protected String featureValueOf(final ILoggingEvent actual) {
				return actual.getFormattedMessage();
			}
		};
	}

	public static FeatureMatcher<ILoggingEvent, String> formattedMessage(final String expected) {
		return formattedMessage(equalTo(expected));
	}

	public static Matcher<ILoggingEvent> atLevel(final Matcher<? super Level> matcher) {
		return new FeatureMatcher<ILoggingEvent, Level>(matcher, "log level", "log level") {

			@Override
			protected Level featureValueOf(final ILoggingEvent actual) {
				return actual.getLevel();
			}
		};
	}

	public static Matcher<ILoggingEvent> atLevel(final Level level) {
		return atLevel(equalTo(level));
	}

}
