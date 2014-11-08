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

import static org.junit.Assert.assertThat;
import static org.neverfear.test.util.Matchers.isBefore;

import java.util.List;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Scopes;

/**
 * @author doug@neverfear.org
 * 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DependencyOrdererTest {

	// Uses member injection
	public static class Root_UsesMemberInjection {

		@SuppressWarnings("unused")
		@Inject
		private A_ManyUsesConstructorInjection a;

		public Root_UsesMemberInjection() {}

	}

	public static class A_ManyUsesConstructorInjection {

		@Inject
		public A_ManyUsesConstructorInjection(
				final B_UsesProvidesAnnotations b,
				final C_UsesConstructorInjection c,
				final D_UsesProviderInstance d,
				final E_DefaultConstructor e) {}

	}

	// Uses @Provides annotations
	public static class B_UsesProvidesAnnotations {

		public B_UsesProvidesAnnotations(final C_UsesConstructorInjection c) {}

	}

	// Uses constructor injection
	public static class C_UsesConstructorInjection {

		@Inject
		public C_UsesConstructorInjection(final D_UsesProviderInstance d) {}

	}

	// Uses Provider
	public static class D_UsesProviderInstance {

		private D_UsesProviderInstance() {}

		public static Provider<D_UsesProviderInstance> provider() {
			return new Provider<D_UsesProviderInstance>() {

				@Override
				public D_UsesProviderInstance get() {
					return new D_UsesProviderInstance();
				}

			};
		}

	}

	public static class E_DefaultConstructor {

	}

	private static class ModuleA
		extends AbstractModule {

		@Provides
		public B_UsesProvidesAnnotations provideB(final C_UsesConstructorInjection c) {
			return new B_UsesProvidesAnnotations(c);
		}

		@Override
		protected void configure() {
			bind(A_ManyUsesConstructorInjection.class).in(Scopes.SINGLETON);
			bind(C_UsesConstructorInjection.class);
			bind(D_UsesProviderInstance.class).toProvider(D_UsesProviderInstance.provider());
		}

	}

	private static class ModuleB
		extends AbstractModule {

		@Override
		protected void configure() {
			bind(E_DefaultConstructor.class).asEagerSingleton();
		}

	}

	private static final Key<?> KEY_ROOT = Key.get(Root_UsesMemberInjection.class);
	private static final Key<?> KEY_A = Key.get(A_ManyUsesConstructorInjection.class);
	private static final Key<?> KEY_B = Key.get(B_UsesProvidesAnnotations.class);
	private static final Key<?> KEY_C = Key.get(C_UsesConstructorInjection.class);
	private static final Key<?> KEY_D = Key.get(D_UsesProviderInstance.class);
	private static final Key<?> KEY_E = Key.get(E_DefaultConstructor.class);

	private DependencyOrderer subject;
	private Injector injector;

	@Before
	public void before() {
		this.subject = new DependencyOrderer();
		this.injector = Guice.createInjector(new ModuleA(), this.subject, new ModuleB());
		this.injector.getInstance(Root_UsesMemberInjection.class);
	}

	/**
	 * <pre>
	 * root --> A -+--> B --+
	 *             |        |
	 *             +--------+--> C --+
	 *             |                 |
	 *             +-----------------+--> D
	 *             |
	 *             +--> E
	 * </pre>
	 */
	@Test
	public void testDepthFirstOrder() {

		final List<GuiceDependency> depthFirst = this.subject.depthFirst();
		final List<Key<?>> depthFirstKeys = Lists.newArrayList(Iterables.transform(depthFirst,
			new Function<GuiceDependency, Key<?>>() {

				@Override
				public Key<?> apply(final GuiceDependency input) {
					return input.getKey();
				}
			}));

		assertThat(depthFirstKeys, isBefore(KEY_E, KEY_A));
		assertThat(depthFirstKeys, isBefore(KEY_D, KEY_A));
		assertThat(depthFirstKeys, isBefore(KEY_C, KEY_A));
		assertThat(depthFirstKeys, isBefore(KEY_B, KEY_A));

		assertThat(depthFirstKeys, isBefore(KEY_C, KEY_B));
		assertThat(depthFirstKeys, isBefore(KEY_D, KEY_C));

		assertThat(depthFirstKeys, isBefore(KEY_A, KEY_ROOT));
	}
}
