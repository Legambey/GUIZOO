package fr.nsi.util;

import java.util.List;
import java.util.Map;

public class RequestResponse{
    boolean updated;
    int updateCount;
    Map<String, List<Object>> data;

    public RequestResponse(boolean updated, int updateCount, Map<String, List<Object>> data) {
        this.updated = updated;
        this.updateCount = updateCount;
        this.data = data;
    }

    public String getAsString(){
        if (updated) return updateCount + "colonnes affectées";

        StringBuilder repr = new StringBuilder();
        List<String> columns = getColumns();

        for (String column : columns) {
            repr.append("[").append(column).append("] | ");
        }
        repr.append("\n");

        for (int i = 0; i < data.get(columns.get(0)).size(); i++) {
            for (String column : columns) {
                repr.append(data.get(column).get(i)).append(" ");
            }
            repr.append("\n");
        }

        return repr.toString();
    }

    public List<String> getColumns(){
        return data.keySet().stream().toList();
    }

    public Map<String, List<Object>> getData() {
        return data;
    }
}
