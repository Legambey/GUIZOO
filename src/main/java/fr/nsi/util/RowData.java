package fr.nsi.util;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

import java.util.HashMap;
import java.util.Map;

public class RowData {
    private final Map<String, SimpleObjectProperty<Object>> columnValues = new HashMap<>();

    public ObservableValue<Object> getColumnValue(String columnName) {
        return columnValues.get(columnName);
    }

    public void setColumnValue(String columnName, Object value) {
        columnValues.put(columnName, new SimpleObjectProperty<>(value));
    }
}
