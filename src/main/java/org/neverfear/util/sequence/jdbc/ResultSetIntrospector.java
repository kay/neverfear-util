package org.neverfear.util.sequence.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class ResultSetIntrospector {
    public static String typeCode(int code) {
        switch (code) {
            case Types.BIT:
                return "BIT";
            case Types.TINYINT:
                return "TINYINT";
            case Types.SMALLINT:
                return "SMALLINT";
            case Types.INTEGER:
                return "INTEGER";
            case Types.BIGINT:
                return "BIGINT";
            case Types.FLOAT:
                return "FLOAT";
            case Types.REAL:
                return "REAL";
            case Types.DOUBLE:
                return "DOUBLE";
            case Types.NUMERIC:
                return "NUMERIC";
            case Types.DECIMAL:
                return "DECIMAL";
            case Types.CHAR:
                return "CHAR";
            case Types.VARCHAR:
                return "VARCHAR";
            case Types.LONGVARCHAR:
                return "LONGVARCHAR";
            case Types.DATE:
                return "DATE";
            case Types.TIME:
                return "TIME";
            case Types.TIMESTAMP:
                return "TIMESTAMP";
            case Types.BINARY:
                return "BINARY";
            case Types.VARBINARY:
                return "VARBINARY";
            case Types.LONGVARBINARY:
                return "LONGVARBINARY";
            case Types.NULL:
                return "NULL";
            case Types.OTHER:
                return "OTHER";
            case Types.JAVA_OBJECT:
                return "JAVA_OBJECT";
            case Types.DISTINCT:
                return "DISTINCT";
            case Types.STRUCT:
                return "STRUCT";
            case Types.ARRAY:
                return "ARRAY";
            case Types.BLOB:
                return "BLOB";
            case Types.CLOB:
                return "CLOB";
            case Types.REF:
                return "REF";
            case Types.DATALINK:
                return "DATALINK";
            case Types.BOOLEAN:
                return "BOOLEAN";
            case Types.ROWID:
                return "ROWID";
            case Types.NCHAR:
                return "NCHAR";
            case Types.NVARCHAR:
                return "NVARCHAR";
            case Types.LONGNVARCHAR:
                return "LONGNVARCHAR";
            case Types.NCLOB:
                return "NCLOB";
            case Types.SQLXML:
                return "SQLXML";
            case Types.REF_CURSOR:
                return "REF_CURSOR";
            case Types.TIME_WITH_TIMEZONE:
                return "TIME_WITH_TIMEZONE";
            case Types.TIMESTAMP_WITH_TIMEZONE:
                return "TIMESTAMP_WITH_TIMEZONE";
            default:
                assert false : "Unknown type code: " + code;
                return Integer.toString(code);
        }
    }

    public static void print(ResultSet rs) throws SQLException {
        ResultSetMetaData tableMeta = rs.getMetaData();
        while (rs.next()) {
            int rowIndex = rs.getRow();
            for (int i = 0; i < tableMeta.getColumnCount(); i++) {
                int columnIndex = i + 1;
                String columnName = tableMeta.getColumnName(columnIndex);
                int typeNumber = tableMeta.getColumnType(columnIndex);
                String typeName = tableMeta.getColumnTypeName(columnIndex);
                String tableName = tableMeta.getTableName(columnIndex);
                Object value = rs.getObject(columnIndex);
                if (tableName.isEmpty()) {
                    System.out.format("[%d] %s:%d (%s:%d)=%s%n",
                            rowIndex,
                            columnName,
                            columnIndex,
                            typeName,
                            typeNumber,
                            Objects.toString(value));
                } else {
                    System.out.format("[%d] %s.%s:%d (%s:%d)=%s%n",
                            rowIndex,
                            tableName,
                            columnName,
                            columnIndex,
                            typeName,
                            typeNumber,
                            Objects.toString(value));
                }
            }
        }
    }
}
