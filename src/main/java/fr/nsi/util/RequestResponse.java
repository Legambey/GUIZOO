package fr.nsi.util;

import fr.nsi.pages.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Pair;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class RequestResponse{
    boolean isError, updated;
    String errorMessage;
    String table;
    int updateCount;
    Map<String, List<Object>> data;

    public RequestResponse(boolean isError, String errorMessage, String table, boolean updated, int updateCount, Map<String, List<Object>> data) {
        this.isError = isError;
        this.errorMessage = errorMessage;
        this.table = table;
        this.updated = updated;
        this.updateCount = updateCount;
        this.data = data;
    }

    public String getAsString(){
        if (isError()) return errorMessage;
        if (updated()) return updateCount + "colonnes affectées";

        StringBuilder repr = new StringBuilder();
        List<String> columns = getColumns();

        for (String column : columns) {
            repr.append("[").append(column).append("] | ");
        }
        repr.append("\n");

        for (int i = 0; i < data.get(columns.get(0)).size(); i++) {
            for (String column : columns) {
                repr.append(data.get(column).get(i)).append(" | ");
            }
            repr.append("\n");
        }

        return repr.toString();
    }

    public TableView<RowData> getAsTableView() throws SQLException {
        if (isError() || updated()) return null;

        // Create TableView
        TableView<RowData> tableView = new TableView<>();
        tableView.setEditable(true);

        for (String columnName : DBUtils.getColumnNames(App.getConnection(), getTable(), true)) {
            TableColumn<RowData, String> column = getTableColumn(columnName);
            tableView.getColumns().add(column);
        }

        // Populate TableView
        ObservableList<RowData> rowData = FXCollections.observableArrayList();
        for (int i = 0; i < data.get(getColumns().get(0)).size(); i++) {
            RowData row = new RowData();
            for (String columnName : data.keySet()) {
                row.setColumnValue(columnName, data.get(columnName).get(i).toString());
            }
            rowData.add(row);
        }
        tableView.setItems(rowData);
        return tableView;
    }

    public boolean isError() {
        return isError;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public String getTable() {
        return table;
    }
    private boolean updated() {
        return updated;
    }
    public List<String> getColumns(){
        return data.keySet().stream().toList();
    }
    public Map<String, List<Object>> getData() {
        return data;
    }

    public void setTable(String table) {
        this.table = table;
    }

    private TableColumn<RowData, String> getTableColumn(String columnName) {
        TableColumn<RowData, String> column = new TableColumn<>(columnName);
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setOnEditCommit(event -> {
            try {
                RowData row = event.getRowValue();
                String primaryKey = DBUtils.getPrimaryKeys(App.getConnection(), getTable()).get(0);
                Object pkData = row.getColumnValue(primaryKey).getValue();
                row.setColumnValue(columnName, event.getNewValue());
                Pair<String, ?> value = new Pair<>(columnName, event.getNewValue());
                DBUtils.updateOne(App.getConnection(), getTable(), value, new Pair<>(primaryKey, pkData));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        column.setCellValueFactory(cellData -> cellData.getValue().getColumnValue(columnName));
        return column;
    }
}

