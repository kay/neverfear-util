package org.neverfear.test.util;


import com.google.common.base.Joiner;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.*;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.*;
import java.util.List;
import java.util.Map;

import static java.lang.System.in;
import static java.lang.System.out;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static java.io.File.pathSeparator;

public class ProcessRuleIT {
    public static class Ls {
        public static void main(String... args) {
            final File cwd;
            if (args.length > 0) {
                cwd = new File(args[0]);
            } else {
                cwd = new File(".");
            }

            for (File file : cwd.listFiles()) {
                out.println(file.getName());
            }
        }
    }

    public static class Cat {
        public static void main(String... args) throws IOException {
            byte[] buf = new byte[1024];
            for (int count = in.read(buf); count != -1; count = in.read(buf)) {
                out.write(buf, 0, count);
                out.flush();
            }
        }
    }

    public static class Env {
        public static void main(String... args) {
            Map<String, String> env = System.getenv();
            for (Map.Entry<String, String> entry : env.entrySet()) {
                out.format("%s=%s%n", entry.getKey(), entry.getValue());
            }
            out.flush();
        }
    }

    /**
     * The system property that contains the classpath for this JVM
     */
    private static final String JAVA_CLASS_PATH_PROPERTY = "java.class.path";

    private static final String SAMPLE_VARIABLE = "SAMPLE_VARIABLE";
    private static final String SAMPLE_FILE = "sample_file";

    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public TestRule timeout = new DisableOnDebug(Timeout.seconds(1));

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    //    @Rule
//    public ProcessRule ls = ProcessRule.create("/bin/ls", "-l")
//            .workingDirectory(temporaryFolder.getRoot()
//                    .getPath());
    @Rule
    public ProcessRule ls = ProcessRule.create("java", "-cp", System.getProperty(JAVA_CLASS_PATH_PROPERTY), Ls.class.getName())
            .workingDirectory(temporaryFolder.getRoot()
                    .getPath());

    @Rule
    public ProcessRule cat = ProcessRule.create("java", "-cp", System.getProperty(JAVA_CLASS_PATH_PROPERTY), Cat.class.getName());
//    @Rule
//    public ProcessRule cat = ProcessRule.exec("/bin/cat");

    //    @Rule
//    public ProcessRule env = ProcessRule.create("/usr/bin/env")
//            .env(SAMPLE_VARIABLE, 10L);
    @Rule
    public ProcessRule env = ProcessRule.create("java", "-cp", System.getProperty(JAVA_CLASS_PATH_PROPERTY), Env.class.getName())
            .env(SAMPLE_VARIABLE, 10L);

    @BeforeClass
    public static void beforeClass() throws IOException {
        temporaryFolder.newFile(SAMPLE_FILE)
                .createNewFile();
    }

    /**
     * Shows that the {@link ProcessRule#workingDirectory(String)} works
     * correctly.
     */
    @Test
    public void givenLs_andSampleFile_whenReadOutput_expectSampleFile() throws Exception {
        boolean found = false;
        final Process process = this.ls.get();
        final InputStream inputStream = process.getInputStream();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(SAMPLE_FILE)) {
                    found = true;
                }
            }
        }
        assertTrue(found);
    }

    /**
     * Shows that {@link ProcessRule#env(String, Object)} works correctly.
     */
    @Test
    public void givenEnv_andSampleVariable_whenReadOutput_expectSampleVariableIsTen() throws Exception {
        String found = null;
        final Process process = this.env.get();
        final InputStream inputStream = process.getInputStream();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(SAMPLE_VARIABLE)) {
                    found = line;
                }
            }
        }
        assertEquals(SAMPLE_VARIABLE + "=10", found);
    }

    /**
     * Shows that {@link ProcessRule#exec(String)} works correctly.
     */
    @Test
    public void givenCat_whenWriteLine_expectReadBackSameLine() throws Exception {
        final Process process = this.cat.get();
        final InputStream inputStream = process.getInputStream();
        final OutputStream outputStream = process.getOutputStream();
        try (BufferedReader stdout = new BufferedReader(new InputStreamReader(inputStream));
             PrintWriter stdin = new PrintWriter(outputStream)) {
            stdin.println("I expect cat to echo this back to me");
            stdin.flush();

            assertEquals("I expect cat to echo this back to me", stdout.readLine());
        }
    }

    /**
     * Shows that the user is free to destroy the process.
     */
    @Test
    public void invokeStop_expectShouldMakeNoDifference() {
        final Process process = this.ls.get();
        process.destroy();
    }

    /**
     * Shows that the user may wait for the exit code
     */
    @Test
    public void givenLs_whenWaitForExitCode_expectZero() throws Exception {
        final Process process = this.ls.get();
        assertEquals(0, process.waitFor());
    }

    @Test
    public void givenEmptyCommandLine_whenCreate_expectIllegalArgumentException() {
        this.expectedException.expect(IllegalArgumentException.class);
        this.expectedException.expectMessage("supply a command");
        ProcessRule.create();
    }

    @Test
    public void givenEmptyCommandLine_whenExec_expectIllegalArgumentException() {
        this.expectedException.expect(IllegalArgumentException.class);
        this.expectedException.expectMessage("supply a command");
        ProcessRule.exec("");
    }

    @Test
    public void givenNullKey_whenEnv_expectIllegalArgumentException() {
        this.expectedException.expect(IllegalArgumentException.class);
        this.expectedException.expectMessage("null key");

        final String key = null;
        final Object value = 10L;

        ProcessRule.exec("java")
                .env(key, value);
    }

    @Test
    public void givenNullValue_whenEnv_expectIllegalArgumentException() {
        this.expectedException.expect(IllegalArgumentException.class);
        this.expectedException.expectMessage("null value");

        final String key = SAMPLE_VARIABLE;
        final Object value = null;

        ProcessRule.exec("java")
                .env(key, value);
    }

    @Test
    public void givenUnknownDirectory_whenWorkingDirectory_expectIllegalArgumentException() {
        this.expectedException.expect(IllegalArgumentException.class);
        this.expectedException.expectMessage("does not exist");

        ProcessRule.exec("java")
                .workingDirectory("i/do.not/exist");
    }

    @Test
    public void givenAppliedRule_andAppendToInMemoryBuffer_whenEvaluate_expectEachPhaseOfLifeCycleLogged()
            throws Throwable {
        /*
         * Given
		 */
        File logFile = new File("target/logs/ProcessRuleIT.log");
        logFile.getParentFile().mkdirs();
        logFile.createNewFile();

        final ProcessRule rule = ProcessRule.create("java", "-cp", System.getProperty(JAVA_CLASS_PATH_PROPERTY), Ls.class.getName(), "directory with space")
                .beVerbose(logFile)
                .redirectStdErr();
        ProcessRule.CommandLine commandLine = rule.command();
        final Statement statement = rule.apply(mock(Statement.class),
                mock(Description.class));

        try (final BufferedReader reader = new BufferedReader(new FileReader(logFile))) {

			/*
             * When
			 */
            statement.evaluate();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Starting: " + commandLine)) {
                    break;
                }
                System.out.println(line);
            }

			/*
             * Then
			 */
            assertThat(line, containsString("Starting: " + commandLine));
            assertThat(reader.readLine(), containsString("Started: " + commandLine));
            assertThat(reader.readLine(), containsString("Stopping: " + commandLine));
            assertThat(reader.readLine(), containsString("Stopped: " + commandLine));
        }
    }


}
