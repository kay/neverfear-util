package org.neverfear.test.util;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

public class ProcessRule implements TestRule {

    public static final class CommandLine {
        private final List<String> command;
        private final ProcessRule parent;

        public CommandLine(ProcessRule parent) {
            this.parent = parent;
            this.command = parent.processBuilder.command();
        }

        public CommandLine append(String token) {
            this.command.add(token);
            return this;
        }

        public CommandLine appendAll(Iterable<String> tokens) {
            for (String token : tokens) {
                append(token);
            }
            return this;
        }

        public CommandLine appendAll(Object... tokens) {
            for (Object token : tokens) {
                append(Objects.toString(token));
            }
            return this;
        }

        public ProcessRule done() {
            return this.parent;
        }

        public String toString() {
            return toCommand(parent.processBuilder.command());
        }
    }

    private final Thread hook = new Thread(() -> {
        stop();
    });

    private final ProcessBuilder processBuilder;
    private Process process = null;

    private boolean verbose = false;
    private File logFile = null;
    private PrintStream outStream;

    /**
     * The fully qualified command line complete with quoting. This is loaded
     * lazily and done so once to avoid long command lines taking a lot time to
     * generate.
     */
    private String commandLine = null;

    private ProcessRule(final List<String> command) {
        if (command.isEmpty()) {
            throw new IllegalArgumentException("Please supply a command");
        }
        this.processBuilder = new ProcessBuilder(command);
    }

    public ProcessRule env(final String key, final Object value) {
        if (key == null) {
            throw new IllegalArgumentException("null key");
        }
        if (value == null) {
            throw new IllegalArgumentException("null value");
        }

        this.processBuilder.environment()
                .put(key, value.toString());
        return this;
    }

    public ProcessRule workingDirectory(final String workingDirectory) {
        final File directory = new File(workingDirectory);
        if (!directory.exists()) {
            throw new IllegalArgumentException(workingDirectory + " does not exist");
        }

        this.processBuilder.directory(directory);
        return this;
    }

    public ProcessRule redirectStdErr() {
        this.processBuilder.redirectErrorStream(true);
        return this;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        Runtime.getRuntime()
                .addShutdownHook(this.hook);
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                start();
                try {
                    base.evaluate();
                } finally {
                    stop();
                }

            }
        };
    }

    private static boolean containsWhitespace(final String string) {
        for (int index = 0; index < string.length(); index++) {
            final char ch = string.charAt(index);
            if (Character.isWhitespace(ch)) {
                return true;
            }
        }
        return false;
    }

    private static String toCommand(final List<String> command) {
        final StringBuilder builder = new StringBuilder();
        for (final String bit : command) {
            final boolean needQuotes = containsWhitespace(bit);
            if (needQuotes) {
                builder.append('"');
            }
            builder.append(bit);
            if (needQuotes) {
                builder.append('"');
            }
            builder.append(" ");
        }
        assert !command.isEmpty() : "Should have been caught during construction time";
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }


    private PrintStream out() {
        if (outStream == null) {
            if (this.logFile == null) {
                outStream = System.out;
            } else {
                try {
                    outStream = new PrintStream(logFile);
                } catch (FileNotFoundException e) {
                    throw new IllegalStateException("Cannot find " + logFile, e);
                }
            }
        }
        return outStream;
    }

    private void debug(String format, Object... args) {
        if (verbose) {
            PrintStream log = out();
            log.print("[" + ProcessRule.class.getSimpleName() + "] ");
            log.format(format, args);
            log.println();
        }
    }

    private void start() throws IOException {
        debug("Starting: %s", this.commandLine);

        this.process = this.processBuilder.start();

        debug("Started: %s", this.commandLine);
    }

    private void stop() {
        if (this.process != null) {
            debug("Stopping: %s", this.commandLine);

            this.process.destroy();
            this.process = null;

            debug("Stopped: %s", this.commandLine);
        }
    }

    public Process get() {
        return this.process;
    }

    public CommandLine command() {
        return new CommandLine(this);
    }

    public ProcessRule beVerbose() {
        return beVerbose((File) null);
    }

    public ProcessRule beVerbose(String logFilePath) {
        return beVerbose(new File(logFilePath));
    }

    public ProcessRule beVerbose(File logFile) {
        this.logFile = logFile;
        this.verbose = true;
        this.commandLine = toCommand(this.processBuilder.command());
        return this;
    }

    public static ProcessRule exec(final String command) {
        final StringTokenizer st = new StringTokenizer(command);
        final String[] commandArray = new String[st.countTokens()];
        for (int i = 0; st.hasMoreTokens(); i++) {
            commandArray[i] = st.nextToken();
        }
        return create(commandArray);
    }

    public static ProcessRule create(final String... command) {
        return new ProcessRule(Arrays.asList(command));
    }

}
