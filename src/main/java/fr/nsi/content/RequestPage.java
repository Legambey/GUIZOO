package fr.nsi.content;

import fr.nsi.pages.App;
import fr.nsi.ui.PanelManager;
import fr.nsi.util.DBUtils;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;

public class RequestPage extends ContentPanel {
    TableView tableView;

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

    private void addContent(VBox vBox) {
        GridPane gridPane = new GridPane();
        gridPane.setTranslateY(10);

        setCanTakeAllSize(gridPane);

        TextArea textArea = new TextArea();
        textArea.setPromptText("Entrer votre requête ici");
        setCanTakeAllSize(textArea);
        GridPane.setConstraints(textArea, 0, 1);
        GridPane.setHalignment(textArea, HPos.LEFT);

        GridPane gridPane1 = new GridPane();
        gridPane1.setStyle("-fx-background-color: #000000");
        Label label1 = new Label("");
        setCanTakeAllSize(label1);
        gridPane1.getChildren().addAll(label1);
        vBox.getChildren().addAll(gridPane, gridPane1);

        Button button = new Button("Executer");
        button.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 12));
        setCanTakeAllSize(button);
        GridPane.setHalignment(button, HPos.CENTER);
        button.setTranslateX(-90);
        GridPane.setConstraints(button, 0,0);
        button.setMinWidth(140);
        button.setMaxHeight(40);
        button.getStyleClass().add("btn");
        button.setOnMouseEntered(e -> this.layout.setCursor(Cursor.HAND));
        button.setOnMouseExited(e -> this.layout.setCursor(Cursor.DEFAULT));
        button.setOnAction(event -> {
            try {
                if(textArea.getText().isEmpty()) label1.setText("");
                else {
                    if (tableView != null) vBox.getChildren().remove(tableView);
                    tableView = DBUtils.request(App.getConnection(), textArea.getText()).getAsTableView();
                    vBox.getChildren().add(tableView);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        Button button2 = new Button("Utiliser un exemple");
        button2.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 12));
        setCanTakeAllSize(button2);
        GridPane.setHalignment(button2, HPos.CENTER);
        button2.setTranslateX(90);
        GridPane.setConstraints(button2, 0,0);
        button2.setMinWidth(140);
        button2.setMaxHeight(40);
        button2.getStyleClass().add("btn");
        button2.setOnMouseEntered(e -> this.layout.setCursor(Cursor.HAND));
        button2.setOnMouseExited(e -> this.layout.setCursor(Cursor.DEFAULT));
        button2.setOnAction(event -> {
            try {
                if (tableView != null) vBox.getChildren().remove(tableView);
                tableView =DBUtils.requestExample(App.getConnection(), textArea).getAsTableView();
                vBox.getChildren().add(tableView);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        gridPane.getChildren().addAll(textArea, button, button2);
    }

    @Override
    public String getName() {
        return null;
    }
}
