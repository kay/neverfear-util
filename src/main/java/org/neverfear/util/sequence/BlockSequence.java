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

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author doug@neverfear.org
 * 
 */
public class BlockSequence
	implements Sequence {

	public interface AllocationPolicy {

		boolean shouldAllocate(final long remaining);
	}

	public static class EmptyPolicy
		implements AllocationPolicy {

		@Override
		public boolean shouldAllocate(final long remaining) {
			return remaining == 0;
		}

	}

	public static class MinimumPolicy
		implements AllocationPolicy {

		private final long minimum;

		public MinimumPolicy(final long minimum) {
			super();
			this.minimum = minimum;
		}

		@Override
		public boolean shouldAllocate(final long remaining) {
			return remaining < this.minimum;
		}

	}

	private static final RangeSequence EMPTY = new RangeSequence(0,
			0);

	private final Queue<RangeSequence> allocated = new LinkedList<>();
	private final AllocationPolicy allocationPolicy;

	private final BlockSequenceAllocator allocator;

	private long remaining = 0;
	private RangeSequence current = EMPTY;

	public BlockSequence(final BlockSequenceAllocator allocator, final AllocationPolicy allocationPolicy) {
		super();
		this.allocator = allocator;
		this.allocationPolicy = allocationPolicy;
	}

	private void allocate() throws AllocationException {
		final RangeSequence latest = this.allocator.allocate();
		this.remaining += latest.remaining();
		this.allocated.add(latest);
	}

	public long remaining() {
		return this.remaining;
	}

	@Override
	public synchronized long next() {
		if (this.allocationPolicy.shouldAllocate(this.remaining)) {
			allocate();
		}

		if (this.current.isExhausted()) {
			this.current = this.allocated.poll();
		}

		this.remaining--;
		return this.current.next();
	}

}
