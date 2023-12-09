package fr.nsi.pages;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import fr.nsi.Main;
import fr.nsi.content.ContentPanel;
import fr.nsi.content.Home;
import fr.nsi.panel.Panel;
import fr.nsi.ui.PanelManager;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.File;

public class App extends Panel {
    GridPane sidemenu = new GridPane();
    GridPane navContent = new GridPane();

    Node activeLink = null;
    ContentPanel currentPage = null;

    Button homeBtn, settingsBtn;

    private static File dbFile;

    public App(File dbFile){
        App.dbFile = dbFile;
    }
    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getStylesheetPath() {
        return "css/app.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);
        System.out.println(dbFile.getAbsolutePath());
        // Layout
        this.layout.getStyleClass().add("app-layout");
        setCanTakeAllSize(this.layout);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHalignment(HPos.LEFT);
        columnConstraints.setMinWidth(350);
        columnConstraints.setMaxWidth(350);
        this.layout.getColumnConstraints().addAll(columnConstraints, new ColumnConstraints());

        // Side menu
        this.layout.add(sidemenu, 0, 0);
        sidemenu.getStyleClass().add("sidemenu");
        setLeft(sidemenu);
        setCenterH(sidemenu);
        setCenterV(sidemenu);

        // Background Image
        GridPane bgImage = new GridPane();
        setCanTakeAllSize(bgImage);
        this.layout.add(bgImage, 1, 0);

        // Nav content
        this.layout.add(navContent, 1, 0);
        navContent.getStyleClass().add("nav-content");


        Image logoIroxxy = new Image(Main.class.getResource("/images/icon.png").toExternalForm());
        ImageView imageViewIroxxy = new ImageView(logoIroxxy);
        GridPane.setVgrow(imageViewIroxxy, Priority.ALWAYS);
        GridPane.setHgrow(imageViewIroxxy, Priority.ALWAYS);
        GridPane.setValignment(imageViewIroxxy, VPos.CENTER);
        imageViewIroxxy.setTranslateX(34);
        imageViewIroxxy.setFitHeight(28);
        imageViewIroxxy.setFitWidth(28);

        // Navigation
        homeBtn = new Button("          Survie Iroxxy");
        homeBtn.getStyleClass().add("sidemenu-nav-btn");
        homeBtn.setGraphic(imageViewIroxxy);
        setCanTakeAllSize(homeBtn);
        setTop(homeBtn);
        homeBtn.setTranslateY(90d);
        homeBtn.setOnMouseEntered(e -> this.layout.setCursor(Cursor.HAND));
        homeBtn.setOnMouseExited(e -> this.layout.setCursor(Cursor.DEFAULT));
        homeBtn.setOnMouseClicked(e -> setPage(new Home(), homeBtn));
/*
        settingsBtn = new Button("Paramètres");
        settingsBtn.getStyleClass().add(saver.get("theme") == null ? "sidemenu-nav-btn-dark" : Integer.parseInt(saver.get("theme")) == 0 ? "sidemenu-nav-btn" : "sidemenu-nav-btn-dark");
        settingsBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.GEARS));
        setCanTakeAllSize(settingsBtn);
        setTop(settingsBtn);
        settingsBtn.setTranslateY(130d);
        settingsBtn.setOnMouseClicked(e -> setPage(new Settings(), settingsBtn));
*/
        sidemenu.getChildren().addAll(homeBtn);

        // Pseudo + avatar
        GridPane userPane = new GridPane();
        setCanTakeAllWidth(userPane);
        userPane.setMaxHeight(80);
        userPane.setMinWidth(80);
        setBottom(userPane);

        ImageView avatarView = new ImageView();
        avatarView.setPreserveRatio(true);
        avatarView.setFitHeight(300d);
        setCanTakeAllSize(avatarView);
        avatarView.setTranslateY(-20d);
        setCenterH(avatarView);
        userPane.getChildren().add(avatarView);



        Button logoutBtn = new Button();
        FontAwesomeIconView logoutIcon = new FontAwesomeIconView(FontAwesomeIcon.SIGN_OUT);
        logoutIcon.getStyleClass().add("logout-icon");
        setCanTakeAllSize(logoutBtn);
        setBottom(logoutBtn);
        logoutBtn.setTranslateY(-25d);
        setRight(logoutBtn);
        logoutBtn.getStyleClass().add("logout-btn");
        logoutBtn.setGraphic(logoutIcon);
        logoutBtn.setOnMouseClicked(e -> {
            if (currentPage instanceof Home) {
                this.panelManager.showPanel(new ChooseMenu());
            }
        });
        userPane.getChildren().add(logoutBtn);

        sidemenu.getChildren().addAll(userPane);
    }

    @Override
    public void onShow() {
        super.onShow();
        setPage(new Home(), homeBtn);
    }

    public void setPage(ContentPanel panel, Node navButton) {
        if (activeLink != null)
            activeLink.getStyleClass().remove("active");
        activeLink = navButton;
        activeLink.getStyleClass().add("active");

        this.navContent.getChildren().clear();
        if (panel != null) {
            this.navContent.getChildren().add(panel.getLayout());
            currentPage = panel;
            if (panel.getStylesheetPath() != null) {
                this.panelManager.getStage().getScene().getStylesheets().clear();
                this.panelManager.getStage().getScene().getStylesheets().addAll(
                        this.getStylesheetPath(),
                        panel.getStylesheetPath()
                );
            }
            panel.init(this.panelManager);
            panel.onShow();
        }
    }
}