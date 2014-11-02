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

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import org.neverfear.util.Dependant;

/**
 * @author doug@neverfear.org
 * 
 */
public class DependencyTreePrinter {

	public <T extends Dependant<T>> void print(final PrintStream writer, final T root) {
		print(writer, Arrays.asList(root));
	}

	public <T extends Dependant<T>> void print(final PrintStream writer, final Collection<T> roots) {
		final LinkedList<Boolean> parents = new LinkedList<Boolean>();
		for (final T root : roots) {
			print(root, writer, parents, 0, 1);
		}
	}

	private static <T extends Dependant<T>> void print(final Dependant<T> root, final PrintStream writer,
			final Deque<Boolean> parents,
			final int childIndex, final int childCount) {
		final boolean last = childIndex + 1 == childCount;
		final boolean siblings = childCount > 1;

		for (final Boolean line : parents) {
			if (line.booleanValue()) {
				writer.print('|');
			} else {
				writer.print(' ');
			}

			writer.print(' ');
		}

		// if (last && !parents.isEmpty()) {
		// writer.print('\\');
		// } else {
		writer.print('+');
		// }
		writer.print('-');
		writer.println(root);

		final Collection<T> dependencies = root.dependencies();
		final Iterator<T> iterator = dependencies.iterator();
		parents.addLast(siblings && !last);

		int index = 0;
		while (iterator.hasNext()) {
			final Dependant<T> dependency = iterator.next();
			print(dependency, writer, parents, index, dependencies.size());
			index++;
		}
		parents.removeLast();
	}
}
