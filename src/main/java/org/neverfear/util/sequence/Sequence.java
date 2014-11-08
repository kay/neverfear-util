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

/**
 * Represents a sequence of long integers where a sequence is defined as an
 * ascending series of values. The exact semantics of a sequence is defined by
 * the implementor. This means they may have gaps between, and may begin at
 * values other than zero.
 * 
 * @author doug@neverfear.org
 * 
 */
public interface Sequence {

	/**
	 * Gets the next sequence number.
	 * 
	 * @return
	 * @throws SequenceException if for some reason there is no next value, for
	 *         instance if the next value would be out of range, or some
	 *         dependent resource was unavailable.
	 */
	long next() throws SequenceException;
}
