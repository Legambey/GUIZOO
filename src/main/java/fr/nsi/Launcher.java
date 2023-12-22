package fr.nsi;

import fr.nsi.pages.ChooseMenu;
import fr.nsi.ui.PanelManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {
    public PanelManager panelManager;

    @Override
    public void start(Stage stage) {
        this.panelManager = new PanelManager(stage);
        this.panelManager.init();
        this.panelManager.showPanel(new ChooseMenu());
    }
}
