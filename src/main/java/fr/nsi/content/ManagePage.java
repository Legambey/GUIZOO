package fr.nsi.content;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import fr.nsi.pages.App;
import fr.nsi.ui.PanelManager;
import fr.nsi.util.DBUtils;
import fr.nsi.util.RequestResponse;
import fr.nsi.util.RowData;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        ScrollPane scrollPane = new ScrollPane();
        setCanTakeAllSize(scrollPane);

        VBox vBox = new VBox();
        setCanTakeAllSize(vBox);
        vBox.minWidthProperty().bind(scrollPane.widthProperty());
        vBox.minHeightProperty().bind(scrollPane.heightProperty());
        addContent(vBox);

        centerPane.getChildren().addAll(scrollPane);
        scrollPane.setContent(vBox);
    }

    void addContent(VBox vbox){
        GridPane gridPane = new GridPane();

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
                    changeTableView(gridPane, tableView, response.getAsTableView());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            response = DBUtils.request(App.getConnection(), "select * from " + tableSelector.getItems().get(0), tableSelector.getItems().get(0));
            System.out.println(response.getErrorMessage());
            changeTableView(gridPane, tableView, response.getAsTableView());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        gridPane.add(tableSelector, 1, 0);
        vbox.getChildren().add(gridPane);
    }

    @Override
    public String getName() {
        return null;
    }

    static void changeTableView(GridPane container, TableView<RowData> oldTable, TableView<RowData> newTable) throws SQLException {
        if(oldTable != null) container.getChildren().remove(oldTable);
        container.add(newTable, 1, 1);

        GridPane insertPane = new GridPane();
        insertPane.setTranslateY(10);
        container.add(insertPane, 1, 2);
        int columnIndex = 1;
        List<TextArea> textAreas = new ArrayList<>();
        for (String column : DBUtils.getColumnNames(App.getConnection(), response.getTable(), true)) {
            TextArea textArea = new TextArea();
            textAreas.add(textArea);
            textArea.setPromptText(column);
            textArea.setMaxWidth(100);
            textArea.setMaxHeight(50);
            insertPane.add(textArea, columnIndex, 0);
            columnIndex++;
        }
        Button button = getAddButton(container, newTable, textAreas);
        button.getStyleClass().add("del-btn");
        FontAwesomeIconView addIcon = new FontAwesomeIconView(FontAwesomeIcon.PLUS);
        addIcon.getStyleClass().add("del-icon");
        button.setGraphic(addIcon);
        button.setMinWidth(50);
        button.setMinHeight(50);
        button.setMaxHeight(50);
        insertPane.add(button, 0, 0);


        GridPane deletePane = new GridPane();
        deletePane.setTranslateY(26);
        container.add(deletePane, 0, 1);
        int rowIndex = 0;
        Map<Button, RowData> rowButtons = new HashMap<>();
        for (RowData row : newTable.getItems()){
            Button deleteButton = new Button();
            deleteButton.getStyleClass().add("del-btn");
            FontAwesomeIconView delIcon = new FontAwesomeIconView(FontAwesomeIcon.TRASH);
            delIcon.getStyleClass().add("del-icon");
            deleteButton.setGraphic(delIcon);
            deleteButton.setMinWidth(24);
            deleteButton.setMinHeight(24);
            deleteButton.setMaxHeight(24);
            rowButtons.put(deleteButton, row);
            deletePane.add(deleteButton, 0, rowIndex);
            rowIndex++;
        }
    }

    private static Button getAddButton(GridPane container, TableView<RowData> newTable, List<TextArea> textAreas) {
        Button button = new Button();
        button.setMinWidth(100);
        button.setMaxHeight(50);
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
