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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.neverfear.util.Dependant;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;

final class GuiceDependency
	implements Dependant<GuiceDependency> {

	private final Collection<GuiceDependency> dependencies = new HashSet<>();
	private final Key<?> key;

	public GuiceDependency(final Key<?> key) {
		super();
		this.key = key;
	}

	public Key<?> getKey() {
		return this.key;
	}

	@Override
	public Collection<GuiceDependency> dependencies() {
		return Collections.unmodifiableCollection(this.dependencies);
	}

	@Override
	public String toString() {
		final TypeLiteral<?> typeLiteral = this.key.getTypeLiteral();
		if (!this.dependencies.isEmpty()) {
			return typeLiteral + " depends  on " + this.dependencies;
		} else {
			return String.valueOf(typeLiteral);
		}
	}

	void addDependency(final GuiceDependency dependency) {
		this.dependencies.add(dependency);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.dependencies == null) ? 0 : this.dependencies.hashCode());
		result = prime * result + ((this.key == null) ? 0 : this.key.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final GuiceDependency other = (GuiceDependency) obj;
		if (this.dependencies == null) {
			if (other.dependencies != null) {
				return false;
			}
		} else if (!this.dependencies.equals(other.dependencies)) {
			return false;
		}
		if (this.key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!this.key.equals(other.key)) {
			return false;
		}
		return true;
	}

}