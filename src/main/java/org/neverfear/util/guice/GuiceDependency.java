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

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

final class GuiceDependency
	implements Dependant<GuiceDependency> {

	/**
	 * 
	 */
	private static final IsSingleton IS_SINGLETON_VISITOR = new IsSingleton();
	private final Collection<GuiceDependency> dependencies = new HashSet<>();
	private final Binding<?> binding;

	public GuiceDependency(final Binding<?> binding) {
		super();
		this.binding = binding;
	}

	public Key<?> getKey() {
		return this.binding.getKey();
	}

	public Binding<?> getBinding() {
		return this.binding;
	}

	public boolean isSingleton() {
		return this.binding.acceptScopingVisitor(IS_SINGLETON_VISITOR);
	}

	@Override
	public Collection<GuiceDependency> dependencies() {
		return Collections.unmodifiableCollection(this.dependencies);
	}

	@Override
	public String toString() {
		final TypeLiteral<?> typeLiteral = getKey().getTypeLiteral();
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
		result = prime * result + ((this.binding == null) ? 0 : this.binding.hashCode());
		result = prime * result + ((this.dependencies == null) ? 0 : this.dependencies.hashCode());
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
		if (this.binding == null) {
			if (other.binding != null) {
				return false;
			}
		} else if (!this.binding.equals(other.binding)) {
			return false;
		}
		if (this.dependencies == null) {
			if (other.dependencies != null) {
				return false;
			}
		} else if (!this.dependencies.equals(other.dependencies)) {
			return false;
		}
		return true;
	}

}