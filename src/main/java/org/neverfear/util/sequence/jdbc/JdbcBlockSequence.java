package org.neverfear.util.sequence.jdbc;

import oracle.jdbc.OracleDriver;
import org.neverfear.util.sequence.AllocationException;
import org.neverfear.util.sequence.api.BlockSequenceAllocator;
import org.neverfear.util.sequence.api.RangeSequence;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcBlockSequence implements BlockSequenceAllocator, Closeable {
    private final Connection connection;
    private final JdbcSequencer sequence;
    private final int blockSize;

    public JdbcBlockSequence(String connectionURL, String name, int blockSize) throws SQLException {
        this(new OracleSequencer(name), connectionURL, blockSize);
    }

    private JdbcBlockSequence(JdbcSequencer sequencer, String connectionURL, int blockSize) throws SQLException {
        this.connection = DriverManager.getConnection(connectionURL);
        this.sequence = sequencer;
        this.sequence.create(connection);
        this.blockSize = blockSize;
    }

    @Override
    public RangeSequence allocate() throws AllocationException {
        try {
            long first = sequence.next(connection);
            return new RangeSequence(first, blockSize);
        } catch (SQLException e) {
            throw new AllocationException("Failed to allocate next block", e);
        }
    }

    @Override
    public void close() throws IOException {
        IOException exception = null;

        try {
            sequence.drop(this.connection);
        } catch (SQLException e) {
            exception = new IOException(e);
        }

        try {
            this.connection.close();
        } catch (SQLException e) {
            if (exception == null) {
                exception = new IOException(e);
            } else {
                exception.addSuppressed(e);
            }
        }

        if (exception != null) {
            throw exception;
        }
    }

    public static void main(String... args) throws Exception {
        System.setProperty("oracle.jdbc.Trace", "true");
        System.setProperty("java.util.logging.config.file", "src/main/resources/jdbc/ojdbc_logging.properties");
        DriverManager.registerDriver(new OracleDriver());
        String connectionURL = "jdbc:oracle:thin:dev/dev@localhost:1521:xe";
        try (JdbcBlockSequence seq = new JdbcBlockSequence(connectionURL, "sequence_b", 100)) {
            for (int i = 0; i < 5; i++) {
                RangeSequence range = seq.allocate();
                System.out.println(range);
                System.out.println(range.next());
                System.out.println(range);
                System.out.println();
            }
        }
    }

}
