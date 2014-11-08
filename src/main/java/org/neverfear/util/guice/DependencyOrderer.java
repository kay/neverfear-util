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

import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.neverfear.util.LeafFirstOrderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.DependencyAndSource;
import com.google.inject.spi.ProvisionListener;

/**
 * @author doug@neverfear.org
 * 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DependencyOrderer
	extends AbstractModule {

	private static final Logger LOGGER = LoggerFactory.getLogger(DependencyOrderer.class);

	private static final class DependencyTreeListener
		implements ProvisionListener {

		private static final Joiner JOINER = Joiner.on(" --(depends on)--> ");
		private static final Function<DependencyAndSource, TypeLiteral<?>> EXTRACT_KEY = new Function<DependencyAndSource, TypeLiteral<?>>() {

			@Override
			public TypeLiteral<?> apply(final DependencyAndSource input) {
				return input.getDependency()
						.getKey()
						.getTypeLiteral();
			}
		};

		private final Map<Key<?>, GuiceDependency> dependencyByKey = new HashMap<>();

		@Override
		public <T> void onProvision(final ProvisionInvocation<T> provision) {
			final List<DependencyAndSource> dependencyChain = new ArrayList<>(provision.getDependencyChain());
			Collections.reverse(dependencyChain);

			final Binding<T> binding = provision.getBinding();
			final Key<?> bindingKey = binding.getKey();

			logDebugDependencyChain(provision);

			Preconditions.checkArgument(dependencyChain.get(0)
					.getDependency()
					.getKey()
					.equals(bindingKey),
				"the dependency chain should go from the binding leading to all dependencies it needs");

			final GuiceDependency currentBinding = getDependencyBinding(binding);

			if (dependencyChain.size() >= 2) {
				final Key<?> dependsOnBinding = dependencyChain.get(1)
						.getDependency()
						.getKey();
				final GuiceDependency dependsOnCurrentBinding = this.dependencyByKey.get(dependsOnBinding);
				dependsOnCurrentBinding.addDependency(currentBinding);
			}
		}

		private static <T> void logDebugDependencyChain(final ProvisionInvocation<T> provision) {
			if (LOGGER.isDebugEnabled()) {
				final List<DependencyAndSource> dependencyChain = provision.getDependencyChain();
				final Binding<T> binding = provision.getBinding();
				final Key<?> bindingKey = binding.getKey();

				final Collection<TypeLiteral<?>> typeLiterals = Collections2.transform(
					dependencyChain,
					EXTRACT_KEY);
				final String dependencyPath = JOINER.join(typeLiterals);
				LOGGER.debug("{} was provisioned because of {}", bindingKey.getTypeLiteral(), dependencyPath);
			}
		}

		// TODO: generic type would be useful here
		public GuiceDependency getDependency(final Key<?> key) {
			return this.dependencyByKey.get(key);
		}

		private GuiceDependency getDependencyBinding(final Binding<?> binding) {
			final Key<?> bindingKey = binding.getKey();
			final GuiceDependency currentBinding;
			if (!this.dependencyByKey.containsKey(bindingKey)) {
				currentBinding = new GuiceDependency(binding);
				this.dependencyByKey.put(binding.getKey(), currentBinding);
			} else {
				currentBinding = this.dependencyByKey.get(binding.getKey());
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

	public GuiceDependency getDependency(final Key<?> key) {
		return this.dependencyTree.getDependency(key);
	}

	@Override
	protected void configure() {
		this.bindListener(Matchers.any(), this.dependencyTree);
	}
}
