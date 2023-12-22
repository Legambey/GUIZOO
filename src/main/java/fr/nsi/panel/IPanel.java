package fr.nsi.panel;

import fr.nsi.ui.PanelManager;
import javafx.scene.layout.GridPane;

public interface IPanel {
    void init(PanelManager panelManager);

    GridPane getLayout();

    void onShow();

    String getStylesheetPath();
}
