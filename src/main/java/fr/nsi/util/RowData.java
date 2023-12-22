package fr.nsi.util;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

import java.util.HashMap;
import java.util.Map;

public class RowData {
    private final Map<String, SimpleObjectProperty<String>> columnValues = new HashMap<>();

    public ObservableValue<String> getColumnValue(String columnName) {
        return columnValues.get(columnName);
    }

    public void setColumnValue(String columnName, String value) {
        columnValues.put(columnName, new SimpleObjectProperty<>(value));
    }
}
