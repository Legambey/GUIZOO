package fr.nsi.pages;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import fr.nsi.Main;
import fr.nsi.content.ContentPanel;
import fr.nsi.content.ManagePage;
import fr.nsi.content.RequestPage;
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
import java.sql.*;
import java.util.*;

public class App extends Panel {
    GridPane sidemenu = new GridPane();
    GridPane navContent = new GridPane();

    Node activeLink = null;
    ContentPanel currentPage = null;
    Button homeBtn, manageBtn;

    static File dbFile;
    public App(File dbFile){
        try {
            App.dbFile = dbFile;
            Connection connection = getConnection();

            //Get all DB tables
            String selectTablesQuery = "SELECT name from sqlite_master WHERE type='table'";
            ResultSet results = connection.createStatement().executeQuery(selectTablesQuery);

            //Define a dictionary that will contain all the tables and their data
            Map<String, List<String>> architecture = new HashMap<>();
            while (results.next()){
                architecture.put(results.getString("name"), new ArrayList<>());
            }

            //Iterate all tables
            for(String table : architecture.keySet()) {
                //Get all columns of the table
                DatabaseMetaData metaData = connection.getMetaData();
                ResultSet resultSet = metaData.getColumns(null, null, table, null);

                while (resultSet.next()) {
                    architecture.get(table).add(resultSet.getString("COLUMN_NAME"));
                }
            }
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
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



        // Navigation
        homeBtn = new Button("Requêtes SQL");
        homeBtn.getStyleClass().add("sidemenu-nav-btn");
        setCanTakeAllSize(homeBtn);
        setTop(homeBtn);
        homeBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.DATABASE));
        homeBtn.setTranslateY(90d);
        homeBtn.setOnMouseEntered(e -> this.layout.setCursor(Cursor.HAND));
        homeBtn.setOnMouseExited(e -> this.layout.setCursor(Cursor.DEFAULT));
        homeBtn.setOnMouseClicked(e -> setPage(new RequestPage(), homeBtn));

        manageBtn = new Button("Gerer la bdd");
        manageBtn.getStyleClass().add("sidemenu-nav-btn");
        manageBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.GEARS));
        setCanTakeAllSize(manageBtn);
        setTop(manageBtn);
        manageBtn.setTranslateY(130d);
        manageBtn.setOnMouseEntered(e -> this.layout.setCursor(Cursor.HAND));
        manageBtn.setOnMouseExited(e -> this.layout.setCursor(Cursor.DEFAULT));
        manageBtn.setOnMouseClicked(e -> setPage(new ManagePage(), manageBtn));

        sidemenu.getChildren().addAll(homeBtn,manageBtn);

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
        logoutBtn.setOnMouseEntered(e -> this.layout.setCursor(Cursor.HAND));
        logoutBtn.setOnMouseExited(e -> this.layout.setCursor(Cursor.DEFAULT));
        logoutBtn.setOnMouseClicked(e -> {
            if (currentPage instanceof RequestPage) {
                this.panelManager.showPanel(new ChooseMenu());
            }
        });
        userPane.getChildren().add(logoutBtn);

        sidemenu.getChildren().addAll(userPane);
    }

    @Override
    public void onShow() {
        super.onShow();
        setPage(new RequestPage(), homeBtn);
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