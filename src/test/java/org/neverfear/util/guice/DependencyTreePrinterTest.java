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
package org.neverfear.util.guice;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.neverfear.util.Dependant;

/**
 * @author doug@neverfear.org
 * 
 */
public class DependencyTreePrinterTest {

	private static class Node
		implements Dependant<Node> {

		private final String id;
		private final Collection<Node> dependencies;

		public Node(final String id, final Node... dependencies) {
			super();
			this.id = id;
			this.dependencies = Arrays.asList(dependencies);
		}

		@Override
		public String toString() {
			return this.id;
		}

		@Override
		public Collection<Node> dependencies() {
			return this.dependencies;
		}
	}

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private static final Node E = new Node("E");
	private static final Node A = new Node("A",
			E);
	private static final Node B = new Node("B");
	private static final Node C = new Node("C",
			A,
			B);
	private static final Node D = new Node("D",
			C,
			A);

	private DependencyTreePrinter subject;

	@Before
	public void before() {
		this.subject = new DependencyTreePrinter();
	}

	@Test
	public void test() {
		final ByteArrayOutputStream out = new ByteArrayOutputStream(0xFFFF);
		final PrintStream writer = new PrintStream(out);
		this.subject.print(writer, Arrays.asList(D, A));
		final String result = new String(out.toByteArray());

		final String expected =
				"+-D" + LINE_SEPARATOR +
						"  +-C" + LINE_SEPARATOR +
						"  | +-A" + LINE_SEPARATOR +
						"  | | +-E" + LINE_SEPARATOR +
						"  | +-B" + LINE_SEPARATOR +
						"  +-A" + LINE_SEPARATOR +
						"    +-E" + LINE_SEPARATOR +
						"+-A" + LINE_SEPARATOR +
						"  +-E" + LINE_SEPARATOR;
		assertEquals(expected, result);
	}
}
