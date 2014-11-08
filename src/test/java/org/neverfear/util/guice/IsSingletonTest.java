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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;

/**
 * @author doug@neverfear.org
 * 
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IsSingletonTest {

	public static class MyNormalBinding {}

	public static class MySingletonBindingScopes {}

	public static class MySingletonBindingAnnotation {}

	public static class MySingletonMethodAnnotation {}

	public static class MyEagerSingleton {}

	@Singleton
	public static class MySingletonClassAnnotation {}

	private static class Module
		extends AbstractModule {

		@Singleton
		@Provides
		public MySingletonMethodAnnotation provideSingleton() {
			return new MySingletonMethodAnnotation();
		}

		@Override
		protected void configure() {
			bind(MyNormalBinding.class);
			bind(MySingletonBindingScopes.class).in(Scopes.SINGLETON);
			bind(MySingletonBindingAnnotation.class).in(Singleton.class);
			bind(MyEagerSingleton.class).asEagerSingleton();
			bind(MySingletonClassAnnotation.class);
		}

	}

	private static final Key<?> KEY_NORMAL = Key.get(MyNormalBinding.class);
	private static final Key<?> KEY_BINDING_SCOPE = Key.get(MySingletonBindingScopes.class);
	private static final Key<?> KEY_BINDING_ANNOTATION = Key.get(MySingletonBindingAnnotation.class);
	private static final Key<?> KEY_BINDING_EAGER = Key.get(MyEagerSingleton.class);
	private static final Key<?> KEY_CLASS_ANNOTATION = Key.get(MySingletonClassAnnotation.class);
	private static final Key<?> KEY_METHOD_ANNOTATION = Key.get(MySingletonMethodAnnotation.class);

	private Injector injector;

	@Before
	public void before() {
		this.injector = Guice.createInjector(new Module());
	}

	@Test
	public void givenBindingWithoutAnySingletonCriteria_whenVisit_expectFalse() {
		assertFalse(this.injector.getBinding(KEY_NORMAL)
				.acceptScopingVisitor(new IsSingleton())
				.booleanValue());
	}

	@Test
	public void givenBindingAsSingletonScope_whenVisit_expectTrue() {
		assertTrue(this.injector.getBinding(KEY_BINDING_SCOPE)
				.acceptScopingVisitor(new IsSingleton())
				.booleanValue());
	}

	@Test
	public void givenBindingAsSingletonAnnotation_whenVisit_expectTrue() {
		assertTrue(this.injector.getBinding(KEY_BINDING_ANNOTATION)
				.acceptScopingVisitor(new IsSingleton())
				.booleanValue());
	}

	@Test
	public void givenBindingAsEagerSingleton_whenVisit_expectTrue() {
		assertTrue(this.injector.getBinding(KEY_BINDING_EAGER)
				.acceptScopingVisitor(new IsSingleton())
				.booleanValue());
	}

	@Test
	public void givenClassAnnotation_whenVisit_expectTrue() {
		assertTrue(this.injector.getBinding(KEY_CLASS_ANNOTATION)
				.acceptScopingVisitor(new IsSingleton())
				.booleanValue());
	}

	@Test
	public void givenProvidesMethodAnnotation_whenVisit_expectTrue() {
		assertTrue(this.injector.getBinding(KEY_METHOD_ANNOTATION)
				.acceptScopingVisitor(new IsSingleton())
				.booleanValue());
	}

}
