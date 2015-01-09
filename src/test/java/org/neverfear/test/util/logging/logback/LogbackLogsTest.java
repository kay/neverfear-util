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

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.neverfear.test.util.logging.logback.LogbackLogs.atLevel;
import static org.neverfear.test.util.logging.logback.LogbackLogs.formattedMessage;

import java.util.LinkedList;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * @author doug@neverfear.org
 * 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LogbackLogsTest {

	private static final Logger HOOKED_LOGGER = LoggerFactory.getLogger(LogbackLogsTest.class);
	private static final Logger UNRELATED_LOGGER = LoggerFactory.getLogger(String.class);

	@Rule
	public final LogbackLogs subject = new LogbackLogs()
			.logger(LogbackLogsTest.class)
			.filter(formattedMessage(not(containsString("Sausage"))));

	@Test
	public void givenNothingLogged_whenLatest_expectNull() {
		assertNull(this.subject.latest());
	}

	@Test
	public void givenHookedLogger_andLogAtInfo_whenLatest_expectLastLogLine() throws Exception {
		HOOKED_LOGGER.info("First log line");
		HOOKED_LOGGER.info("Hello {}", "World");

		final ILoggingEvent event = this.subject.latest();
		assertThat(event, formattedMessage("Hello World"));
	}

	@Test
	public void givenHookedLogger_andLogAtInfo_whenList_expectBothLogLines() throws Exception {
		HOOKED_LOGGER.info("One");
		HOOKED_LOGGER.debug("Two");

		final LinkedList<ILoggingEvent> events = new LinkedList<>(this.subject.list());
		assertThat(events.poll(), allOf(formattedMessage("One"), atLevel(Level.INFO)));
		assertThat(events.poll(), allOf(formattedMessage("Two"), atLevel(Level.DEBUG)));
	}

	@Test
	public void givenHookedLogger_whenLogLineContainsFilteredWord_expectFilteredWordNotInOutput() throws Exception {
		HOOKED_LOGGER.info("Breakfast Sausage and Eggs");
		assertThat(this.subject.list(), empty());
	}

	@Test
	public void givenUnrelatedLogger_andLogOnUnrelatedLogger_whenList_expectEmpty() throws Exception {
		UNRELATED_LOGGER.info("Unrelated log lines are unrelated");
		assertThat(this.subject.list(), empty());
	}
}
