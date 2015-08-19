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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.neverfear.util.sequence.api.SequenceException;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * @author doug@neverfear.org
 */
public class ConcurrentFileLongSequenceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void givenUnopened_whenNext_expectSequenceException() throws Exception {
        // Given
        try (final ConcurrentFileLongSequence sequence = new ConcurrentFileLongSequence(this.temporaryFolder.newFile())) {

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
        try (final ConcurrentFileLongSequence oldInstance = new ConcurrentFileLongSequence(sequenceFile)) {
            oldInstance.open();
            for (int i = 0; i < 1000; i++) {
                oldInstance.next();
            }
        }

        // When
        final long value;
        try (final ConcurrentFileLongSequence sequence = new ConcurrentFileLongSequence(sequenceFile)) {
            sequence.open();
            value = sequence.next();
        }

        // Then
        assertEquals(1000, value);
    }

}
