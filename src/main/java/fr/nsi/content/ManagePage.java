package fr.nsi.content;

import fr.nsi.pages.App;
import fr.nsi.ui.PanelManager;
import fr.nsi.util.DBUtils;
import fr.nsi.util.RowData;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ManagePage extends ContentPanel {
    TableView<RowData> tableView;

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
            tableSelector.setValue(tableSelector.getItems().get(0));
            tableSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    changeTableView(centerPane, tableView, DBUtils.request(App.getConnection(), "select * from " + newValue).getAsTableView());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            changeTableView(centerPane, tableView, DBUtils.request(App.getConnection(), "select * from " + tableSelector.getItems().get(0)).getAsTableView());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        centerPane.add(tableSelector, 0, 0);
    }

    @Override
    public String getName() {
        return null;
    }

    static void changeTableView(GridPane container, TableView<RowData> oldTable, TableView<RowData> newTable) {
        if(oldTable != null) container.getChildren().remove(oldTable);
        container.add(newTable, 0, 1);
    }}
