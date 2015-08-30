package org.neverfear.util.sequence.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

interface Table {
    String getTableName();

    void ensureExists(Connection connection) throws SQLException;

    long getAndIncrement(String sequenceName);

}
