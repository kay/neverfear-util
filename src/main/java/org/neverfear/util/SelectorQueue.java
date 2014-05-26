package org.neverfear.util;

import static java.lang.Thread.interrupted;
import static java.lang.Thread.yield;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 * Simplifies {@link Selector} interaction such that a user can just take the
 * next {@link SelectionKey} one after another.
 * 
 * @author doug@neverfear.org
 * 
 */
public final class SelectorQueue {

	private final Selector selector;
	private Iterator<SelectionKey> current = null;

	public SelectorQueue(final Selector selector) {
		super();
		this.selector = selector;
	}

	public SelectionKey take() throws IOException, InterruptedException {
		if (this.current == null || !this.current.hasNext()) {

			while (this.selector.select() == 0) {
				if (interrupted()) {
					throw new InterruptedException();
				}

				yield();
			}

			this.current = this.selector.selectedKeys()
					.iterator();
			assert this.current.hasNext();
		}

		final SelectionKey key = this.current.next();
		this.current.remove();
		return key;
	}
}