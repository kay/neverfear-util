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

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import org.junit.*;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.neverfear.util.sequence.api.Sequence;

import java.io.Closeable;

import static org.junit.Assert.assertEquals;

/**
 * @author doug@neverfear.org
 */
@RunWith(Theories.class)
public class SequenceSeriesAcceptanceTest {

    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

    @DataPoints
    public static Sequence[] generatedDataPoints() throws Exception {
        return new Sequence[]{
                createSingleThreadedLongSequence(),
                createAtomicLongSequence(),
                createFileLongSequence(),
                createConcurrentFileLongSequence()
        };
    }

    private static AtomicLongSequence createAtomicLongSequence() {
        return new AtomicLongSequence();
    }

    private static SingleThreadedLongSequence createSingleThreadedLongSequence() {
        return new SingleThreadedLongSequence();
    }

    private static FileLongSequence createFileLongSequence() throws Exception {
        final FileLongSequence sequence = new FileLongSequence(temporaryFolder.newFile());
        sequence.open();
        return sequence;
    }

    private static ConcurrentFileLongSequence createConcurrentFileLongSequence() throws Exception {
        final ConcurrentFileLongSequence sequence = new ConcurrentFileLongSequence(temporaryFolder.newFile());
        sequence.open();
        return sequence;
    }

    @Theory
    public void oneHundredThousandConsecutiveValues(Sequence sequence) throws Exception {
        try {
            for (long i = 0; i < 100_000; i++) {
                assertEquals(i, sequence.next());
            }

        } finally {
            if (sequence instanceof Closeable) {
                ((Closeable) sequence).close();
            }
        }
    }

}
