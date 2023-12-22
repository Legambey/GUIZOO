package fr.nsi.ui;

import fr.nsi.panel.IPanel;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.awt.*;

public class PanelManager {
    private final Stage stage;
    private final GridPane contentPane = new GridPane();

    public PanelManager(Stage stage) {
        this.stage = stage;
    }

    public void init() {
        Frame f = new Frame();

        Toolkit t = f.getToolkit();
        Dimension d = t.getScreenSize();
        int w = d.width;
        int h = d.height;
        System.out.println("dimension : " + w + "x" + h);
        double x = 1.8;
        double a = w / x + 350;
        double b = (h + 40) / x;


        this.stage.setTitle("GUI ZOO");
        this.stage.setMinWidth((double) w / 2 + 350);
        this.stage.setMinHeight((double) (h + 40) / 2);
        this.stage.setMinWidth(350);
        this.stage.setMinHeight(0);
        this.stage.setWidth(a);
        this.stage.setHeight(b);
        this.stage.setWidth(1280);
        this.stage.setHeight(720);
        this.stage.centerOnScreen();
        this.stage.getIcons().add(new Image("images/icon.png"));

        GridPane layout = new GridPane();
        //background color gray
        layout.setStyle("-fx-background-color: #2b2d31;");
        Scene scene = new Scene(layout);
        this.stage.setScene(scene);


        layout.add(this.contentPane, 0, 1);
        GridPane.setVgrow(this.contentPane, Priority.ALWAYS);
        GridPane.setHgrow(this.contentPane, Priority.ALWAYS);

        this.stage.show();
    }

    public void showPanel(IPanel panel) {
        this.contentPane.getChildren().clear();
        this.contentPane.getChildren().add(panel.getLayout());
        if (panel.getStylesheetPath() != null) {
            this.stage.getScene().getStylesheets().clear();
            this.stage.getScene().getStylesheets().add(panel.getStylesheetPath());
        }
        panel.init(this);
        panel.onShow();
    }

    public Stage getStage() {
        return stage;
    }
}
