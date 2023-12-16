package fr.nsi.util;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

public class TableViewUtils {
    public static List<String> getColumns(TableView<RowData> table){
        List<String> columns = new ArrayList<>();
        for (Object o : table.getColumns()) {
            if(o instanceof TableColumn<?, ?> column){
                columns.add(column.getText());
            }
        }

        return columns;
    }
}
