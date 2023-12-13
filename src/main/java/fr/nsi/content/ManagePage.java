package fr.nsi.content;

import fr.nsi.pages.App;
import fr.nsi.ui.PanelManager;
import fr.nsi.util.DBUtils;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ManagePage extends ContentPanel {
    TableView tableView;

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        GridPane centerPane = new GridPane();
        this.layout.add(centerPane, 0, 0);
        setCanTakeAllSize(centerPane);

        try {
            ComboBox<String> tableBox = new ComboBox<>();
            ResultSet tablesResultSet = App.getConnection().getMetaData().getTables(null, null, "%", new String[]{"TABLE"});
            while(tablesResultSet.next()) {
                String tableName = tablesResultSet.getString("TABLE_NAME");
                tableBox.getItems().add(tableName);
            }
            tableBox.setValue(tableBox.getItems().get(0));
            tableBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    changeTableView(centerPane, tableView, DBUtils.request(App.getConnection(), "select * from " + newValue).getAsTableView());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            changeTableView(centerPane, tableView, DBUtils.request(App.getConnection(), "select * from " + tableBox.getItems().get(0)).getAsTableView());

            centerPane.add(tableBox, 0, 0);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return null;
    }

    static void changeTableView(GridPane container, TableView oldTable, TableView newTable) {
        if(oldTable != null) container.getChildren().remove(oldTable);
        container.add(newTable, 0, 1);
    }}
