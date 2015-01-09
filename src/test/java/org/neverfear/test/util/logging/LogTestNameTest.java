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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runners.MethodSorters;
import org.neverfear.test.util.logging.logback.LogbackLogs;

import ch.qos.logback.classic.Level;

/**
 * @author doug@neverfear.org
 * 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LogTestNameTest {

	@FixMethodOrder(MethodSorters.NAME_ASCENDING)
	public static abstract class AbstractLogTestNameTest {

		protected abstract LogTestName get();

		@Test
		public void logAtInfoWithoutArguments() {
			get().info("without args");
		}

		@Test
		public void logAtInfoWithArguments() {
			get().info("with {} arg(s)", 1);
		}
	}

	public static class WithClassRule
		extends AbstractLogTestNameTest {

		@ClassRule
		public static LogTestName subject = new LogTestName(LogTestNameTest.class);

		@Override
		protected LogTestName get() {
			return subject;
		}

	}

	public static class WithInstanceRule
		extends AbstractLogTestNameTest {

		@Rule
		public LogTestName subject = new LogTestName(LogTestNameTest.class);

		@Override
		protected LogTestName get() {
			return this.subject;
		}

	}

	@Rule
	public final LogbackLogs info = new LogbackLogs()
			.logger(LogTestNameTest.class)
			.filter(LogbackLogs.atLevel(Level.INFO));

	@Test
	public void givenInstanceRule_whenRunAnyTest_expectLogsPrefixedWithTestMethodName_andLogsSuffixedWithTestMethodName_andOutputFromTestSandwichedBetween() {
		runTest(WithInstanceRule.class, "logAtInfoWithoutArguments");
		assertThat(this.info.listMessages(),
			contains(
				"[logAtInfoWithoutArguments(org.neverfear.test.util.logging.LogTestNameTest$WithInstanceRule)] starting",
				"[logAtInfoWithoutArguments(org.neverfear.test.util.logging.LogTestNameTest$WithInstanceRule)] without args",
				"[logAtInfoWithoutArguments(org.neverfear.test.util.logging.LogTestNameTest$WithInstanceRule)] finished"));
	}

	@Test
	public void givenInstanceRule_whenLogAtInfoWithArguments_expectLogsPrefixedWithTestMethodName_and() {
		runTest(WithInstanceRule.class, "logAtInfoWithArguments");
		assertThat(this.info.listMessages(),
			hasItem("[logAtInfoWithArguments(org.neverfear.test.util.logging.LogTestNameTest$WithInstanceRule)] with 1 arg(s)"));
	}

	@Test
	public void givenInstanceRule_whenLogAtInfoWithoutArguments_expectLogsPrefixedWithTestMethodName() {
		runTest(WithInstanceRule.class, "logAtInfoWithoutArguments");
		assertThat(this.info.listMessages(),
			hasItem("[logAtInfoWithoutArguments(org.neverfear.test.util.logging.LogTestNameTest$WithInstanceRule)] without args"));
	}

	@Test
	public void givenClassRule_whenRunAnyTest_expectLogsPrefixedWithTestClassName_andLogsSuffixedWithTestClassName_andOutputFromAllTestsSandwichedBetween() {
		runTest(WithClassRule.class);
		assertThat(this.info.listMessages(),
			contains(
				"[org.neverfear.test.util.logging.LogTestNameTest$WithClassRule] starting",
				"[org.neverfear.test.util.logging.LogTestNameTest$WithClassRule] with 1 arg(s)",
				"[org.neverfear.test.util.logging.LogTestNameTest$WithClassRule] without args",
				"[org.neverfear.test.util.logging.LogTestNameTest$WithClassRule] finished"));
	}

	private static Result runTest(final Class<?> testClass) {
		final Request methodRequest = Request.aClass(testClass);

		final Result result = new JUnitCore().run(methodRequest);
		assertTrue(String.valueOf(result.getFailures()), result.wasSuccessful());
		return result;
	}

	private static Result runTest(final Class<?> testClass, final String testName) {
		final Request methodRequest = Request.method(testClass, testName);

		final Result result = new JUnitCore().run(methodRequest);
		assertTrue(String.valueOf(result.getFailures()), result.wasSuccessful());
		return result;
	}

}
