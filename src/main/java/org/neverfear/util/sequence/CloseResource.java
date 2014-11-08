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

import java.io.Closeable;
import java.io.IOException;

/**
 * @author doug@neverfear.org
 * 
 */
final class CloseResource
	implements Runnable {

	private final Closeable resource;

	public CloseResource(final Closeable resource) {
		super();
		this.resource = resource;
	}

	@Override
	public void run() {
		try {
			this.resource.close();
		} catch (final IOException ignore) {
			/*
			 * Left intentionally blank
			 */
		}
	}

	public static void closeOnExit(final Closeable resource) {
		Runtime.getRuntime()
				.addShutdownHook(new Thread(new CloseResource(resource)));
	}
}