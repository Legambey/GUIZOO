package fr.nsi.util;

import javafx.scene.control.TextArea;
import javafx.util.Pair;

import java.sql.*;
import java.util.*;

public class DBUtils {
    public static RequestResponse request(Connection connection, String request, String table) throws SQLException {
        System.out.println("Executed : " + request);
        RequestResponse response;
        try {
            //Execute the request
            Statement stmt = connection.createStatement();
            stmt.execute(request);

            //Check if the request is a SELECT
            if(stmt.getUpdateCount() == -1) {
                ResultSet rs = stmt.getResultSet();
                response = new RequestResponse(false, "", table, false, -1, new HashMap<>());

                if (table != null) {
                    List<String> columns = getColumnNames(connection, table, false);
                    while (rs.next()) {
                        int i = 1;
                        for (String columnName : columns) {
                            response.getData().computeIfAbsent(columnName, k -> new ArrayList<>());
                            response.getData().get(columnName).add(rs.getObject(i));
                            i++;
                        }
                    }
                }
                else {
                    ResultSetMetaData rsMetadata = rs.getMetaData();
                    int columnCount = rsMetadata.getColumnCount();
                    while (rs.next()) {
                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = rsMetadata.getColumnName(i);
                            response.getData().computeIfAbsent(columnName, k -> new ArrayList<>());
                            response.getData().get(columnName).add(rs.getObject(i));
                        }
                    }
                    response.setTable(rsMetadata.getTableName(1));
                }
            }
            else {
                response = new RequestResponse(false, "", table, true, stmt.getUpdateCount(), null);
            }
        }
        catch (SQLException e){
            connection.close();
            return new RequestResponse(true, e.getMessage(), table, false, -1, null);
        }
        connection.close();
        return response;
    }

    public static RequestResponse requestExample(Connection connection, TextArea textArea) throws SQLException {
        String[] requests = {"SELECT DISTINCT fonction FROM LesCages;",
                "SELECT nomA FROM LesAnimaux WHERE type = 'leopard';",
                "SELECT LesAnimaux.nomA, nomM, noCage FROM LesAnimaux JOIN LesMaladies ON (LesAnimaux.nomA=LesMaladies.nomA);",
                "SELECT DISTINCT e.nomE, fonction FROM LesEmployes AS e JOIN LesGardiens AS g ON (e.nomE = g.nomE) JOIN LesCages AS c ON (g.noCage = c.noCage) WHERE e.adresse IS 'Calvi';"};
        Random r = new Random();
        String request = requests[r.nextInt(requests.length)];
        textArea.setText(request);
        return request(connection, request, null);
    }

    public static RequestResponse insert(Connection connection, String table, Object... values) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("INSERT INTO ").append(table).append(" VALUES(");

        for (Object value : values) {
            queryBuilder.append("'").append(value).append("', ");
        }
        queryBuilder.replace(queryBuilder.length() - 2, queryBuilder.length(), "").append(")");
        return request(connection, queryBuilder.toString(), table);
    }

    public static RequestResponse update(Connection connection, String table, Pair<String, Object>[] values, List<Pair<String, Object>> conditions) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("UPDATE ").append(table).append(" SET ");

        for (Pair<String, Object> value : values) {
            queryBuilder.append(value.getKey()).append(" = ").append(value.getValue()).append(", ");
        }

        queryBuilder.append(" WHERE ");
        addConditions(conditions, queryBuilder);
        return request(connection, queryBuilder.toString(), table);
    }

    public static RequestResponse delete(Connection connection, String table, List<Pair<String, Object>> conditions) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("DELETE FROM ").append(table).append(" WHERE ");

        addConditions(conditions, queryBuilder);
        return request(connection, queryBuilder.toString(), table);
    }

    public static List<String> getColumnNames(Connection connection, String tableName, boolean closeConn) throws SQLException {
        List<String> columnNames = new ArrayList<>();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, tableName, null);
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                columnNames.add(columnName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if(closeConn) connection.close();
        return columnNames;
    }

    public static List<String> getPrimaryKey(Connection connection, String tableName){
        List<String> keys = new ArrayList<>();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);

            while (primaryKeys.next()) {
                String key = primaryKeys.getString("COLUMN_NAME");
                keys.add(key);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return keys;
    }

    private static void addConditions(List<Pair<String, Object>> conditions, StringBuilder queryBuilder) {
        if(conditions.isEmpty()) queryBuilder.replace(queryBuilder.length() - 7, queryBuilder.length(), "");
        else {
            for (Pair<String, Object> cond : conditions) {
                queryBuilder.append(cond.getKey()).append(" = '").append(cond.getValue()).append("'").append(" AND ");
            }
        }
        queryBuilder.append("1=1");
    }


}