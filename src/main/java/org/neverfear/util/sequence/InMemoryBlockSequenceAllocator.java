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

import org.neverfear.util.sequence.api.BlockSequenceAllocator;
import org.neverfear.util.sequence.api.RangeSequence;

/**
 * This allocator creates a new range of a certain size on demand. This is only
 * useful as a testing resource as the {@link BlockSequenceAllocator} are
 * intended to carry the burden of I/O.
 * 
 * @author doug@neverfear.org
 * 
 */
public class InMemoryBlockSequenceAllocator
	implements BlockSequenceAllocator {

	private final long length;
	private long index;

	public InMemoryBlockSequenceAllocator(final long length) {
		super();
		this.length = length;
	}

	@Override
	public RangeSequence allocate() throws AllocationException {
		final long firstInclusive = this.index;
		final long lastExclusive = firstInclusive + this.length;

		this.index = lastExclusive;
		return new RangeSequence(
				firstInclusive,
				lastExclusive);
	}

}
