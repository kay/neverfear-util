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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neverfear.util.LeafFirstOrderer;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.DependencyAndSource;
import com.google.inject.spi.ProvisionListener;

/**
 * @author doug@neverfear.org
 * 
 */
public class DependencyOrderer
	extends AbstractModule {

	private static final class DependencyTreeListener
		implements ProvisionListener {

		private final Map<Key<?>, GuiceDependency> dependencyByKey = new HashMap<>();

		@Override
		public <T> void onProvision(final ProvisionInvocation<T> provision) {
			final List<DependencyAndSource> dependencyChain = new ArrayList<>(provision.getDependencyChain());
			Collections.reverse(dependencyChain);

			final Binding<T> binding = provision.getBinding();
			final Key<?> bindingKey = binding.getKey();

			Preconditions.checkArgument(dependencyChain.get(0)
					.getDependency()
					.getKey()
					.equals(bindingKey),
				"the dependency chain should go from the binding leading to all dependencies it needs");

			final GuiceDependency currentBinding = getBindingForKey(bindingKey);

			if (dependencyChain.size() >= 2) {
				final Key<?> dependsOnBinding = dependencyChain.get(1)
						.getDependency()
						.getKey();
				final GuiceDependency dependsOnCurrentBinding = this.dependencyByKey.get(dependsOnBinding);
				dependsOnCurrentBinding.addDependency(currentBinding);
			}
		}

		public GuiceDependency get(final Key<?> key) {
			return this.dependencyByKey.get(key);
		}

		private GuiceDependency getBindingForKey(final Key<?> bindingKey) {
			final GuiceDependency currentBinding;
			if (!this.dependencyByKey.containsKey(bindingKey)) {
				currentBinding = new GuiceDependency(bindingKey);
				this.dependencyByKey.put(bindingKey, currentBinding);
			} else {
				currentBinding = this.dependencyByKey.get(bindingKey);
			}
			return currentBinding;
		}

		public Collection<GuiceDependency> values() {
			return this.dependencyByKey.values();
		}
	}

	private final DependencyTreeListener dependencyTree = new DependencyTreeListener();

	public List<GuiceDependency> depthFirst() {
		final LeafFirstOrderer<GuiceDependency> orderer = new LeafFirstOrderer<GuiceDependency>();
		return orderer.order(this.dependencyTree.values());
	}

	public GuiceDependency get(final Key<?> key) {
		return this.dependencyTree.get(key);
	}

	@Override
	protected void configure() {
		this.bindListener(Matchers.any(), this.dependencyTree);
	}
}
