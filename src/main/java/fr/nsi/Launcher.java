package fr.nsi;

import fr.nsi.pages.ChooseMenu;
import fr.nsi.ui.PanelManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {
    private static Launcher instance;
    public PanelManager panelManager;

    public Launcher() {
        instance = this;
    }
    public static Launcher getInstance() {
        return instance;
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.panelManager = new PanelManager(this, stage);
        this.panelManager.init();
        this.panelManager.showPanel(new ChooseMenu());

    }
}
