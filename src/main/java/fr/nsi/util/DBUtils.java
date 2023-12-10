package fr.nsi.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class DBUtils {
    public static String request(Connection connection, String request) throws SQLException {
        RequestResponse response;
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(request);

            if(stmt.getUpdateCount() == -1){
                response = new RequestResponse(false, -1, new HashMap<>());

                ResultSet rs = stmt.getResultSet();
                ResultSetMetaData rsMetadata = rs.getMetaData();
                int columnCount = rsMetadata.getColumnCount();

                while(rs.next()){
                    for (int i = 1; i <= columnCount; i++) {
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
}