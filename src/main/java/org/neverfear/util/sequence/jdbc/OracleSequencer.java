package org.neverfear.util.sequence.jdbc;

import oracle.jdbc.OracleDriver;

import java.sql.*;

final class OracleSequencer implements JdbcSequencer {
    private static final int DEFAULT_CACHE_SIZE = 100;
    public static final int ORACLE_ERROR_OBJECT_EXISTS = 955;
    private final String sqlCreate;
    private final String sqlDrop;
    private final String sqlSelect;

    public OracleSequencer(String name) {
        this(name, 0, 1);
    }

    public OracleSequencer(String name, long initial, long incrementBy) {
        this(name, initial, incrementBy, DEFAULT_CACHE_SIZE);
    }

    public OracleSequencer(String name, long initial, long incrementBy, int cacheSize) {
        this.sqlCreate = String.format("CREATE SEQUENCE %s START WITH %d INCREMENT BY %d CACHE %d MINVALUE 0",
                name, initial, incrementBy, cacheSize);
        this.sqlDrop = String.format("DROP SEQUENCE %s", name);
        this.sqlSelect = String.format("SELECT %s.NEXTVAL FROM dual", name);
    }

    @Override
    public void create(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            statement.executeUpdate(sqlCreate);
        } catch (SQLSyntaxErrorException e) {
            int errorCode = e.getErrorCode();
            if (errorCode == ORACLE_ERROR_OBJECT_EXISTS) {
                // TODO: Validate it's setting match mine
                return;
            }
            throw e;
        } finally {
            statement.close();
        }
    }

    @Override
    public void reset(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            statement.executeUpdate(sqlDrop);
            statement.executeUpdate(sqlCreate);
        } finally {
            statement.close();
        }
    }

    @Override
    public void drop(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            statement.executeUpdate(sqlDrop);
        } finally {
            statement.close();
        }
    }


    @Override
    public long next(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        try {
            ResultSet rs = statement.executeQuery(sqlSelect);
            if (!rs.next()) {
                assert false : "Result set contains no rows";
            }

            return rs.getLong(1);
        } finally {
            statement.close();
        }
    }

    public static void main(String... args) throws SQLException {
        System.setProperty("oracle.jdbc.Trace", "true");
        System.setProperty("java.util.logging.config.file", "src/main/resources/jdbc/ojdbc_logging.properties");
        DriverManager.registerDriver(new OracleDriver());
        String connectionURL = "jdbc:oracle:thin:dev/dev@localhost:1521:xe";
        Connection connection = DriverManager.getConnection(connectionURL);
        try {
            OracleSequencer seq = new OracleSequencer("sequence_a");
            seq.create(connection);
            seq.create(connection);
            seq.reset(connection);
            seq.create(connection);
            for (int i = 0; i < 12; i++) {
                System.out.println(seq.next(connection));
            }
            seq.drop(connection);
            System.out.println("Success");
        } finally {
            connection.close();
        }
    }
}
