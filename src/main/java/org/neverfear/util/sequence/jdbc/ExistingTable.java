package org.neverfear.util.sequence.jdbc;

import oracle.jdbc.OracleDriver;

import java.sql.*;

final class ExistingTable implements Table {
    private static final String ORACLE_SQL_TABLE_EXISTS = "SELECT count(*) " +
            "FROM ALL_OBJECTS " +
            "WHERE OBJECT_TYPE = 'TABLE' " +
            "AND UPPER(OWNER) = UPPER(?) " +
            "AND UPPER(OBJECT_NAME) = UPPER(?)";

    private final String schema;
    private final String name;

    ExistingTable(String schema, String name) {
        this.schema = schema;
        this.name = name;
    }

    @Override
    public String getTableName() {
        return name;
    }

    private static void ensureColumn(ResultSet columnRs, String expectedName, int expectedTypeCode) throws SQLException {
        String columnName = columnRs.getString(4);
        int columnTypeCode = columnRs.getInt(5);
        if (!expectedName.equalsIgnoreCase(columnName)) {
            throw new SQLException("Column " + expectedName + " not found");
        }
        if (expectedTypeCode != columnTypeCode) {
            throw new SQLException("Column " + expectedName + " does not have the expected type of " + ResultSetIntrospector.typeCode(expectedTypeCode) + " but was " + ResultSetIntrospector.typeCode(columnTypeCode));
        }
    }

    @Override
    public void ensureExists(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet columns = metaData.getColumns(connection.getCatalog(), schema.toUpperCase(), name.toUpperCase(), null);
        if (!columns.next()) {
            throw new SQLException("Table " + schema + "." + name + " does not exist");
        }
        ensureColumn(columns, "name", Types.VARCHAR);

        if (!columns.next()) {
            throw new SQLException("Table " + schema + "." + name + " does not have the required number of columns");
        }
        ensureColumn(columns, "value", Types.DECIMAL);
    }

    @Override
    public long getAndIncrement(String sequenceName) {
        return 0;
    }

    public static void main(String... args) throws SQLException {
        System.setProperty("oracle.jdbc.Trace", "true");
        System.setProperty("java.util.logging.config.file", "src/main/resources/jdbc/ojdbc_logging.properties");
        DriverManager.registerDriver(new OracleDriver());
        String connectionURL = "jdbc:oracle:thin:dev/dev@localhost:1521:xe";
        Connection connection = DriverManager.getConnection(connectionURL);
        try {
            ExistingTable table = new ExistingTable("dev", "seq");
            table.ensureExists(connection);
            System.out.println("Success");
        } finally {
            connection.close();
        }
    }
}
