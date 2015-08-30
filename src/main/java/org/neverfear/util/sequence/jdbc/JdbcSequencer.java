package org.neverfear.util.sequence.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

interface JdbcSequencer {
    void create(Connection connection) throws SQLException;

    void reset(Connection connection) throws SQLException;

    void drop(Connection connection) throws SQLException;

    long next(Connection connection) throws SQLException;
}
