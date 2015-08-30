package org.neverfear.util.sequence.jdbc;

import oracle.jdbc.OracleDriver;
import org.neverfear.util.sequence.api.Sequence;
import org.neverfear.util.sequence.api.SequenceException;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;

public class JdbcSequence implements Sequence, Closeable {

    private final Object openCloseLock = new Object();
    private final String connectionURL;
    private final String tableName;

    private Connection connection = null;

    public JdbcSequence(String connectionURL) {
        this.connectionURL = connectionURL;
        this.tableName = null;
    }

    public void open() throws SQLException {
        synchronized (openCloseLock) {
            connection = DriverManager.getConnection(connectionURL);


        }
    }


    @Override
    public void close() throws IOException {
        synchronized (openCloseLock) {
            Connection connection = this.connection;
            if (connection == null) {
                return;
            }

            try {
                if (connection.isClosed()) {
                    return;
                }
                connection.close();
            } catch (SQLException e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public long next() throws SequenceException {
        try {
            Statement statement = connection.createStatement();
            //statement.ex
        } catch (SQLException e) {
            throw new SequenceException(e);
        }
        throw new AssertionError();
    }
}
