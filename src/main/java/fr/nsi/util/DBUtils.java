package fr.nsi.util;

import javafx.scene.control.TextArea;
import javafx.util.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DBUtils {
    public static RequestResponse request(Connection connection, String request) throws SQLException {
        System.out.println("Executed : " + request);
        RequestResponse response;
        try {
            //Execute the request
            Statement stmt = connection.createStatement();
            stmt.execute(request);

            //Check if the request is a SELECT
            if(stmt.getUpdateCount() == -1){
                response = new RequestResponse(false, "", false, -1, new HashMap<>());

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
                response = new RequestResponse(false, "", true, stmt.getUpdateCount(), null);
            }
        }
        catch (SQLException e){
            connection.close();
            return new RequestResponse(true, e.getMessage(), false, -1, new HashMap<String, List<Object>>());
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
        return request(connection, request);
    }

    public static void insert(Connection connection, String table, Object... values) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("INSERT INTO ").append(table).append(" VALUES(");

        for (Object value : values) {
            queryBuilder.append("'").append(value).append("', ");
        }
        queryBuilder.reverse().replace(0, 1, "").reverse().append(")");
        request(connection, queryBuilder.toString());
    }

    public static void update(Connection connection, String table, Pair<String, Object>[] values, Pair<String, Object>[] conditions) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("UPDATE ").append(table).append(" SET ");

        for (Pair<String, Object> value : values) {
            queryBuilder.append(value.getKey()).append(" = ").append(value.getValue()).append(", ");
        }

        queryBuilder.append(" WHERE ");
        addConditions(conditions, queryBuilder);
        request(connection, queryBuilder.toString());
    }

    public static void delete(Connection connection, String table, Pair<String, Object>[] conditions) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("DELETE FROM ").append(table).append(" WHERE ");

        addConditions(conditions, queryBuilder);
        request(connection, queryBuilder.toString());
    }

    private static void addConditions(Pair<String, Object>[] conditions, StringBuilder queryBuilder) {
        if(conditions.length == 0) queryBuilder.replace(queryBuilder.length() - 7, queryBuilder.length(), "");
        else {
            for (Pair<String, Object> cond : conditions) {
                queryBuilder.append(cond.getKey()).append(" = ").append(cond.getValue()).append(" AND ");
            }
        }
        queryBuilder.append("1=1");
    }
}