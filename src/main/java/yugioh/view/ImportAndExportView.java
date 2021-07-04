package yugioh.view;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import yugioh.controller.ImportAndExport;
import yugioh.model.cards.Card;

import java.util.List;

public class ImportAndExportView {

    public static void run() {
        ImageView image = (ImageView) LoginView.importAndExportScene.lookup("#cardPicture");
        image.setImage(new Image(ImportAndExportView.class.getResourceAsStream("cardimages/empty.jpg")));
        var dragTarget = (VBox) LoginView.importAndExportScene.lookup("#dragTarget");
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

    public void mainMenu() {
        LoginView.stage.setScene(LoginView.mainScene);
        LoginView.stage.centerOnScreen();
    }

    public static void showCards(List<Card> cards) {

    }
}
