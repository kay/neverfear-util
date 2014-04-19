package org.neverfear.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

/**
 * <pre>
 *         +---+
 *   +---->| B +-----+
 *   |     +---+     v
 * +-+-+           +---+
 * | A +---------->| C |
 * +---+           +---+
 * </pre>
 * 
 * @author doug@neverfear.org
 * 
 */
public class LeafFirstOrderer<T extends Dependant<T>> {

	public List<T> order(final Collection<T> objects) {
		final List<T> ordered = new ArrayList<>();
		final Deque<T> visited = new ArrayDeque<>();

		for (final T obj : objects) {
			order(ordered, visited, obj);
		}
		return ordered;
	}

	private void order(final List<T> ordered, final Deque<T> visited, final T node) {
		if (visited.contains(node)) {
			visited.push(node);
			throw new IllegalArgumentException("Circular dependency detected at node "
					+ node + " using path " + visited);
		}

		visited.push(node);
		try {
			if (ordered.contains(node)) {
				/*
				 * Already visited this node. This is different than a circle
				 * and can occur legitimately in two principle scenarios:
				 * 
				 * 1) In a dependency graph with a shared dependency (i.e. one
				 * that is reachable through two paths
				 * 
				 * 2) When this method is called with multiple root nodes but
				 * the same ordered collection
				 */
				return;
			}

			final int index = visited.size();
			for (final T dependency : node.dependencies()) {
				order(ordered, visited, dependency);
			}
			visited.remove(index);

			ordered.add(node);
		} finally {
			visited.pop();
		}
	}
}
