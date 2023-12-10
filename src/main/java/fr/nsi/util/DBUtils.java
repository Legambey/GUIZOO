package fr.nsi.util;

import javafx.util.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DBUtils {
    public static String request(Connection connection, String request) throws SQLException {
        System.out.println("Executed : " + request);
        RequestResponse response;
        try {
            //Execute the request
            Statement stmt = connection.createStatement();
            stmt.execute(request);

            //Check if the request was a SELECT
            if(stmt.getUpdateCount() == -1){
                response = new RequestResponse(false, -1, new HashMap<>());

                //Get the results and some information about it
                ResultSet rs = stmt.getResultSet();
                ResultSetMetaData rsMetadata = rs.getMetaData();
                int columnCount = rsMetadata.getColumnCount();

                //Iterate them
                while(rs.next()){
                    for (int i = 1; i <= columnCount; i++) {
                        //Store them in a Map whose keys are the columns and the values are a list of data
                        String columnName = rsMetadata.getColumnName(i);
                        response.getData().computeIfAbsent(columnName, k -> new ArrayList<>());
                        response.getData().get(columnName).add(rs.getObject(i));
                    }
                }
            }
            else {
                response = new RequestResponse(true, stmt.getUpdateCount(), null);
            }
        }
        catch (SQLException e){
            connection.close();
            return e.getMessage();
        }
        connection.close();
        return response.getAsString();
    }

    public static void insert(Connection connection, String table, Object... values) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("INSERT INTO ").append(table).append(" VALUES");

        for (Object value : values) {
            queryBuilder.append("'").append(value).append("', ");
        }
        queryBuilder.reverse().replace(0, 1, "").reverse();
        request(connection, queryBuilder.toString());
    }

    public static void update(Connection connection, String table, Pair<String, Object>[] values, Pair<String, Object>[] conditions) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("UPDATE ").append(table).append(" SET ");

        for (Pair<String, Object> value : values) {
            queryBuilder.append(value.getKey()).append(" = ").append(value.getValue()).append(", ");
        }

        queryBuilder.append(" WHERE ");
        for (Pair<String, Object> cond : conditions) {
            queryBuilder.append(cond.getKey()).append(" = ").append(cond.getValue()).append(" AND ");
        }
        queryBuilder.append("1=1");
        request(connection, queryBuilder.toString());
    }

    public static void delete(Connection connection, String table, Pair<String, Object>[] conditions) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("DELETE FROM ").append(table).append(" WHERE ");

        for (Pair<String, Object> cond : conditions) {
            queryBuilder.append(cond.getKey()).append(" = ").append(cond.getValue()).append(" AND ");
        }
        queryBuilder.append("1=1");
        request(connection, queryBuilder.toString());
    }
}