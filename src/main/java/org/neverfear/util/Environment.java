package org.neverfear.util;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.io.File;
import java.util.List;

public final class Environment {
	public static final String CLASSPATH = System.getProperty("java.class.path");
	public static final String PATHSEP = File.pathSeparator;
	public static final List<String> CLASSPATH_LIST = unmodifiableList(asList(CLASSPATH.split(PATHSEP)));

	private Environment() {
	}

}
