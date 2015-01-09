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
package org.neverfear.test.util.logging;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This rule automatically logs entry and exit into
 * 
 * @author doug@neverfear.org
 * 
 */
public class LogTestName
	extends TestWatcher {

	private final Logger logger;
	private String testName;

	public LogTestName(final String name) {
		this(LoggerFactory.getLogger(name));
	}

	public LogTestName(final Class<?> cls) {
		this(LoggerFactory.getLogger(cls));
	}

	public LogTestName(final Logger logger) {
		super();
		this.logger = logger;
	}

	private static Object[] prepend(final Object[] array, final Object item) {
		final Object[] result = new Object[array.length + 1];
		result[0] = item;
		System.arraycopy(array, 0, result, 1, array.length);
		return result;
	}

	private static String formatWithTestName(final String format) {
		return "[{}] " + format;
	}

	@Override
	protected void starting(final Description description) {
		this.testName = description.getDisplayName();
		this.logger.info("[{}] starting", this.testName);
	}

	@Override
	protected void finished(final Description description) {
		this.logger.info("[{}] finished", this.testName);
		this.testName = null;
	}

	public void trace(final String msg) {
		this.logger.trace(formatWithTestName(msg), this.testName);
	}

	public void trace(final String format, final Object... arguments) {
		this.logger.trace(formatWithTestName(format), prepend(arguments, this.testName));
	}

	public void debug(final String msg) {
		this.logger.debug(formatWithTestName(msg), this.testName);
	}

	public void debug(final String format, final Object... arguments) {
		this.logger.debug(formatWithTestName(format), prepend(arguments, this.testName));
	}

	public void info(final String msg) {
		this.logger.info(formatWithTestName(msg), this.testName);
	}

	public void info(final String format, final Object... arguments) {
		this.logger.info(formatWithTestName(format), prepend(arguments, this.testName));
	}

	public void warn(final String msg) {
		this.logger.warn(formatWithTestName(msg), this.testName);
	}

	public void warn(final String format, final Object... arguments) {
		this.logger.warn(formatWithTestName(format), prepend(arguments, this.testName));
	}

}
