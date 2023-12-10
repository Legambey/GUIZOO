package fr.nsi.content;

import fr.nsi.pages.App;
import fr.nsi.ui.PanelManager;
import fr.nsi.util.DBUtils;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;

public class RequestPage extends ContentPanel {
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
        gridPane.setStyle("-fx-background-color: #ff0000");

        setCanTakeAllSize(gridPane);
        Label label = new Label("Entrez votre requete SQL ici:");

        TextArea textArea = new TextArea();
        textArea.setText("Ce launcher est en cours de développement");
        setCanTakeAllSize(textArea);
        GridPane.setConstraints(textArea, 0, 1);
        GridPane.setHalignment(textArea, HPos.LEFT);

        GridPane gridPane1 = new GridPane();
        gridPane1.setStyle("-fx-background-color: #000000");
        Label label1 = new Label("Bienvenue sur le launcher de NSI");
        setCanTakeAllSize(label1);
        gridPane1.getChildren().addAll(label1);
        vBox.getChildren().addAll(gridPane, gridPane1);

        Button button = new Button("Executer");
        setCanTakeAllSize(button);
        GridPane.setConstraints(button, 1, 1);
        GridPane.setHalignment(button, HPos.LEFT);
        button.setOnAction(event -> {
            System.out.println("Executed: " + textArea.getText());
            try {
                String response = DBUtils.request(App.getConnection(), textArea.getText());
                label1.setText(response);
                System.out.println(response);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        gridPane.getChildren().addAll(label, textArea, button);
    }

    @Override
    public String getName() {
        return null;
    }
}