package fr.nsi.pages;

import fr.nsi.panel.Panel;
import fr.nsi.ui.PanelManager;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;

import java.io.File;

public class ChooseMenu extends Panel {
    @Override
    public String getStylesheetPath() {
        return "css/login.css";
    }
    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);
        Button chooseButton = new Button();

        GridPane loginPanel = new GridPane();
        loginPanel.getStyleClass().add("login-panel");
        GridPane mainPanel = new GridPane();
        mainPanel.getStyleClass().add("main-panel");

        loginPanel.setMaxWidth(600);
        loginPanel.setMinWidth(600);
        loginPanel.setMaxHeight(200);
        loginPanel.setMinHeight(200);

        GridPane.setVgrow(loginPanel, Priority.ALWAYS);
        GridPane.setHgrow(loginPanel, Priority.ALWAYS);
        GridPane.setValignment(loginPanel, VPos.CENTER);
        GridPane.setHalignment(loginPanel, HPos.CENTER);

        RowConstraints bottomConstraints = new RowConstraints();
        bottomConstraints.setValignment(VPos.BOTTOM);
        bottomConstraints.setMaxHeight(55);
        loginPanel.getRowConstraints().addAll(new RowConstraints(), bottomConstraints);
        loginPanel.add(mainPanel, 0, 0);

        GridPane.setVgrow(mainPanel, Priority.ALWAYS);
        GridPane.setHgrow(mainPanel, Priority.ALWAYS);

        Label label = new Label("Choisir une Base de données");
        label.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 30f));
        GridPane.setVgrow(label, Priority.ALWAYS);
        GridPane.setHgrow(label, Priority.ALWAYS);
        GridPane.setValignment(label, VPos.TOP);
        label.getStyleClass().add("no-account");

        label.setTranslateY(27);
        setCenterH(label);

        Separator connectseparator = new Separator();
        GridPane.setVgrow(connectseparator, Priority.ALWAYS);
        GridPane.setHgrow(connectseparator, Priority.ALWAYS);
        GridPane.setValignment(connectseparator, VPos.TOP);
        GridPane.setHalignment(connectseparator, HPos.CENTER);
        connectseparator.setTranslateY(80);
        connectseparator.setMinWidth(350);
        connectseparator.setMaxWidth(350);
        connectseparator.getStyleClass().add("connect-separator");

        // Microsoft login button
        ImageView view = new ImageView(new Image("images/bdd.png"));
        view.setPreserveRatio(true);
        view.setFitHeight(50d);
        setCanTakeAllSize(chooseButton);
        setCenterH(chooseButton);
        setCenterV(chooseButton);
        chooseButton.getStyleClass().add("ms-login-btn-dark");
        chooseButton.setMaxWidth(200);
        chooseButton.setTranslateY(50d);
        chooseButton.setGraphic(view);
        chooseButton.setOnMouseEntered(e -> this.layout.setCursor(Cursor.HAND));
        chooseButton.setOnMouseExited(e -> this.layout.setCursor(Cursor.DEFAULT));
        chooseButton.setOnMouseClicked(e -> {
            final FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier de base de données", "*.db"));
            chooser.setTitle("Choisir un fichier");
            final File file = chooser.showOpenDialog(this.panelManager.getStage());
            if (file != null) {
                this.panelManager.showPanel(new App(file));
            }
        });

        mainPanel.getChildren().addAll(label, connectseparator, chooseButton);
        this.layout.getChildren().add(loginPanel);
    }
}
