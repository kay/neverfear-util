package org.neverfear.util.sequence.jdbc;

import oracle.jdbc.OracleDriver;

import java.sql.*;

final class AutomaticTable implements Table {
    private static final int ORACLE_ERR_OBJECT_EXISTS = 955;
    private static final String ORACLE_SQL_CREATE_TABLE = "CREATE TABLE %s.%s (" +
            "name VARCHAR(255), value NUMERIC" +
            ")";
    private final Table table;
    private final String sql;
    private final String name;

    AutomaticTable(String schema, String name) {
        this.table = new ExistingTable(schema, name);
        this.sql = String.format(ORACLE_SQL_CREATE_TABLE, schema, name);
        this.name = name;
    }

    @Override
    public String getTableName() {
        return name;
    }

    @Override
    public void ensureExists(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sql);
        try {
            statement.executeUpdate();
        } catch (SQLSyntaxErrorException e) {
            if (e.getErrorCode() == ORACLE_ERR_OBJECT_EXISTS) {
                // Already exists
                table.ensureExists(connection);
                return;
            }
            throw e;
        } finally {
            statement.close();
        }
    }

    @Override
    public long getAndIncrement(String sequenceName) {
        return table.getAndIncrement(sequenceName);
    }

    public static void main(String... args) throws SQLException {
        System.setProperty("oracle.jdbc.Trace", "true");
        System.setProperty("java.util.logging.config.file", "src/main/resources/jdbc/ojdbc_logging.properties");
        DriverManager.registerDriver(new OracleDriver());
        String connectionURL = "jdbc:oracle:thin:dev/dev@localhost:1521:xe";
        Connection connection = DriverManager.getConnection(connectionURL);
        try {
            AutomaticTable table = new AutomaticTable("dev", "seq");
            table.ensureExists(connection);
            System.out.println("Success");
        } finally {
            connection.close();
        }
    }
}
