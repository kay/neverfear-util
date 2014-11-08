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

import java.lang.annotation.Annotation;

import com.google.inject.Scope;
import com.google.inject.Scopes;
import com.google.inject.spi.BindingScopingVisitor;

/**
 * @author doug@neverfear.org
 * 
 */
final class IsSingleton
	implements BindingScopingVisitor<Boolean> {

	@Override
	public Boolean visitEagerSingleton() {
		return Boolean.TRUE;
	}

	@Override
	public Boolean visitScope(final Scope scope) {
		return Boolean.valueOf(Scopes.SINGLETON.equals(scope));
	}

	@Override
	public Boolean visitScopeAnnotation(final Class<? extends Annotation> scopeAnnotation) {
		return Boolean.FALSE;
	}

	@Override
	public Boolean visitNoScoping() {
		return Boolean.FALSE;
	}
}