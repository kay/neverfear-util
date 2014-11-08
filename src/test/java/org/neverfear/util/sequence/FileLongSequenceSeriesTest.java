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
package org.neverfear.util.sequence;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

/**
 * @author doug@neverfear.org
 * 
 */
public class FileLongSequenceSeriesTest
	extends AbstractSequenceSeriesTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Override
	protected Sequence create() throws Exception {
		final FileLongSequence sequence = new FileLongSequence(this.temporaryFolder.newFile());
		sequence.open();
		return sequence;
	}

	@Test
	public void givenUnopened_whenNext_expectSequenceException() throws Exception {
		// Given
		try (final FileLongSequence sequence = new FileLongSequence(this.temporaryFolder.newFile())) {

			// Then
			this.expectedException.expect(SequenceException.class);
			this.expectedException.expectMessage("File is not opened");

			// When
			sequence.next();
		}
	}

	@Test
	public void givenTakenOneThousandSequences_whenCreateNewSequence_expectNextValueIsOneThousand()
			throws Exception {
		// Given
		final File sequenceFile = this.temporaryFolder.newFile();
		try (final FileLongSequence oldInstance = new FileLongSequence(sequenceFile)) {
			oldInstance.open();
			for (int i = 0; i < 1000; i++) {
				oldInstance.next();
			}
		}

		// When
		final long value;
		try (final FileLongSequence sequence = new FileLongSequence(sequenceFile)) {
			sequence.open();
			value = sequence.next();
		}

		// Then
		assertEquals(1000, value);
	}

}
