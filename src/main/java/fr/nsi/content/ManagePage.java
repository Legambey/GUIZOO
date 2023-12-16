package fr.nsi.content;

import fr.nsi.pages.App;
import fr.nsi.ui.PanelManager;
import fr.nsi.util.DBUtils;
import fr.nsi.util.RequestResponse;
import fr.nsi.util.RowData;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManagePage extends ContentPanel {
    TableView<RowData> tableView;
    static RequestResponse response;
    static String selectedTable;

    @Override
    public String getStylesheetPath() {
        return "css/content/pages.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        GridPane centerPane = new GridPane();
        this.layout.add(centerPane, 0, 0);
        setCanTakeAllSize(centerPane);

        ComboBox<String> tableSelector = new ComboBox<>();

        try {
            ResultSet tablesResultSet = App.getConnection().getMetaData().getTables(null, null, "%", new String[]{"TABLE"});
            while(tablesResultSet.next()) {
                String tableName = tablesResultSet.getString("TABLE_NAME");
                tableSelector.getItems().add(tableName);
            }
            selectedTable = tableSelector.getItems().get(0);
            tableSelector.setValue(selectedTable);
            tableSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    selectedTable = newValue;
                    response = DBUtils.request(App.getConnection(), "select * from " + newValue, newValue);
                    changeTableView(centerPane, tableView, response.getAsTableView());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            response = DBUtils.request(App.getConnection(), "select * from " + tableSelector.getItems().get(0), tableSelector.getItems().get(0));
            System.out.println(response.getErrorMessage());
            changeTableView(centerPane, tableView, response.getAsTableView());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        centerPane.add(tableSelector, 0, 0);
    }

    @Override
    public String getName() {
        return null;
    }

    static void changeTableView(GridPane container, TableView<RowData> oldTable, TableView<RowData> newTable) throws SQLException {
        if(oldTable != null) container.getChildren().remove(oldTable);
        container.add(newTable, 0, 1);
        GridPane columnNames = new GridPane();
        container.add(columnNames, 0, 2);
        int rowIndex = 0;
        List<TextArea> textAreas = new ArrayList<>();
        for (String column : DBUtils.getColumnNames(App.getConnection(), response.getTable(), true)) {
            TextArea textArea = new TextArea();
            textAreas.add(textArea);
            textArea.setPromptText(column);
            columnNames.add(textArea, rowIndex, 2);
            rowIndex++;
        }
        Button button = getAddButton(container, newTable, textAreas);
        columnNames.add(button, rowIndex, 2);
    }

    private static Button getAddButton(GridPane container, TableView<RowData> newTable, List<TextArea> textAreas) {
        Button button = new Button("Ajouter");
        button.setMinWidth(100);
        button.setOnAction(event -> {
            for (TextArea textArea : textAreas) {
                System.out.println(textArea.getText());
            }
            try {
                DBUtils.insert(App.getConnection(), selectedTable, textAreas.stream().map(TextArea::getText).toArray()).getAsTableView();
                changeTableView(container, newTable, DBUtils.request(App.getConnection(), "select * from " + selectedTable, selectedTable).getAsTableView());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return button;
    }
}
