package fr.nsi.ui;

import com.goxr3plus.fxborderlessscene.borderless.BorderlessScene;
import fr.nsi.panel.IPanel;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import fr.nsi.Launcher;

import java.awt.*;

public class PanelManager {
    private final Launcher launcher;
    private final Stage stage;
    private final GridPane contentPane = new GridPane();
    private GridPane layout;

    public PanelManager(Launcher launcher, Stage stage) {
        this.launcher = launcher;
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
        this.stage.setMinWidth(w / 2 + 350);
        this.stage.setMinHeight((h + 40) / 2);
        this.stage.setMinWidth(350);
        this.stage.setMinHeight(0);
        this.stage.setWidth(a);
        this.stage.setHeight(b);
        this.stage.setWidth(1280);
        this.stage.setHeight(720);
        this.stage.centerOnScreen();
        this.stage.getIcons().add(new Image("images/icon.png"));

        this.layout = new GridPane();
        //background color gray
        this.layout.setStyle("-fx-background-color: #777676;");
        Scene scene = new Scene(this.layout);
        this.stage.setScene(scene);


        this.layout.add(this.contentPane, 0, 1);
        GridPane.setVgrow(this.contentPane, Priority.ALWAYS);
        GridPane.setHgrow(this.contentPane, Priority.ALWAYS);

        this.stage.show();
    }

    public void setHeightAndWidht(double height, double width) {
        stage.setMaxHeight(height);
        stage.setMaxWidth(width);
    }

    public void setTitle(String title) {
        this.stage.setTitle(title);
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

    public Launcher getLauncher() {
        return launcher;
    }
}
