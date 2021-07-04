package yugioh.view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import yugioh.controller.ImportAndExport;
import yugioh.model.cards.Card;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ImportAndExportView {

    private static final FileChooser FILE_CHOOSER = new FileChooser();
    private static final DirectoryChooser DIRECTORY_CHOOSER = new DirectoryChooser();

    private static Card selectedCard = null;

    public static void run() {
        setImage();
        setDropTarget();
        setFileAndDirectoryChooser();
    }

    private static void setImage() {
        ImageView image = (ImageView) LoginView.importAndExportScene.lookup("#cardPicture");
        image.setImage(new Image(ImportAndExportView.class.getResourceAsStream("cardimages/empty.jpg")));
    }

    private static void setFileAndDirectoryChooser() {
        var userHome = new File(System.getProperty("user.home"));
        FILE_CHOOSER.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("resource files", "*.json", "*.csv", "*.JSON", "*.CSV"));
        FILE_CHOOSER.setInitialDirectory(userHome);
        FILE_CHOOSER.setTitle("Choose a card resource file to import");
        DIRECTORY_CHOOSER.setInitialDirectory(userHome);
        DIRECTORY_CHOOSER.setTitle("Choose the directory to export the card to");
    }

    private static void setDropTarget() {
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

    public void importButtonClicked() {
        try {
            Arrays.stream(Objects.requireNonNull(FILE_CHOOSER.showOpenDialog(new Popup()).listFiles()))
                    .forEach(f -> ImportAndExport.getInstance().importFile(f));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exportButtonClicked() {
        try {
            List<File> directories = Arrays.stream(Objects.requireNonNull(DIRECTORY_CHOOSER.showDialog(new Popup()).listFiles()))
                    .filter(File::isDirectory).collect(Collectors.toList());
            FILE_CHOOSER.setInitialDirectory(directories.get(0));
            directories.forEach(d -> ImportAndExport.getInstance().exportCard(d, selectedCard));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
