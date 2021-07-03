package yugioh.view;

import javafx.scene.control.Label;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import yugioh.controller.ImportAndExport;
import yugioh.model.cards.Card;

import java.util.List;

public class ImportAndExportView {

    public static void run() {
        var label = (Label) LoginView.importAndExportScene.lookup("#label");
        var dropped = (Label) LoginView.importAndExportScene.lookup("#dropped");
        var dragTarget = (VBox) LoginView.importAndExportScene.lookup("#dragTarget");
        label.setText("Drag the file to me.");
        dropped.setText("");
        dragTarget.setOnDragOver(event -> {
            if (event.getGestureSource() != dragTarget && event.getDragboard().hasFiles())
                event.acceptTransferModes(TransferMode.COPY);
            event.consume();
        });

        dragTarget.setOnDragDropped(event -> {
            var dragboard = event.getDragboard();
            var success = dragboard.hasFiles();
            if (success)
                dragboard.getFiles().forEach(file -> ImportAndExport.getInstance().importFile(file));
            event.setDropCompleted(success);
            event.consume();
        });
    }

    public static void mainMenu() {
        LoginView.stage.setScene(LoginView.mainScene);
        LoginView.stage.centerOnScreen();
    }

    public static void showCards(List<Card> cards) {

    }
}
