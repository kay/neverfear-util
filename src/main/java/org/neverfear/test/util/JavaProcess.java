package org.neverfear.test.util;


import org.junit.rules.TestRule;

import java.util.List;

import static java.io.File.pathSeparator;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

public class JavaProcess implements TestRule{

    /**
     * The system property that contains the classpath for this JVM
     */
    private static final String JAVA_CLASS_PATH_PROPERTY = "java.class.path";

    public static List<String> classPath() {
        final String[] classpath = System.getProperty(JAVA_CLASS_PATH_PROPERTY)
                .split(pathSeparator);
        return unmodifiableList(asList(classpath));
    }

    private final ProcessRule processRule;
    private final String mainClassName;

    public JavaProcess(ProcessRule processRule, String mainClassName) {
        this.processRule = processRule;
        this.mainClassName = mainClassName;
    }

    private void buildCommand() {
        this.processRule.command().append("-cp").append(System.getProperty(JAVA_CLASS_PATH_PROPERTY)).append(mainClassName);
    }

    public static JavaProcess create(Class<?> mainClass) {
return create(mainClass.getName());
    }
    public static JavaProcess create(String mainClassName) {
        return new JavaProcess(ProcessRule.create("java"), mainClassName);
    }
}