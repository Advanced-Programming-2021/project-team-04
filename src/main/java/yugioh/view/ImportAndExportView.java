package yugioh.view;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.util.Duration;
import yugioh.controller.ImportAndExport;
import yugioh.controller.ShopController;
import yugioh.model.cards.Card;
import yugioh.model.cards.MonsterCard;
import yugioh.model.cards.SpellAndTrapCard;

import java.io.File;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Objects;

public class ImportAndExportView {

    private static final FileChooser FILE_CHOOSER = new FileChooser();
    private static final DirectoryChooser DIRECTORY_CHOOSER = new DirectoryChooser();
    private static Card firstCard;
    private static Card secondCard;
    private static int navigate;
    private static List<Card> allCards = new ArrayList<>();
    private static Card selectedCard = null;

    public static void run() {
        setImage();
        setDropTarget();
        setFileAndDirectoryChooser();
    }

    private static void setImage() {
        ImageView image = (ImageView) LoginView.importAndExportScene.lookup("#cardPicture");
        image.setImage(new Image(Objects.requireNonNull(ImportAndExportView.class.getResourceAsStream("cardimages/empty.jpg"))));
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

    public void goBack() {
        LoginView.stage.setScene(LoginView.importAndExportScene);
        LoginView.stage.centerOnScreen();
    }

    private static void showCards() {
        firstCard = allCards.get(navigate * 2);
        if ((navigate * 2 + 1) == allCards.size()) secondCard = null;
        else secondCard = allCards.get(navigate * 2 + 1);
        setImages();
    }

    private static void setImages() {
        ImageView second = (ImageView) LoginView.importScene.lookup("#second");
        ImageView first = (ImageView) LoginView.importScene.lookup("#first");
        Image secondImage;
        Image firstImage;
        if (secondCard == null)
            secondImage = new Image(Objects.requireNonNull(ImportAndExportView.class.getResourceAsStream("cardimages/empty.jpg")));
        else if (!secondCard.isOriginal() || secondCard.isConverted())
            secondImage = new Image(Objects.requireNonNull(ImportAndExportView.class.getResourceAsStream("cardimages/JonMartin.jpg")));
        else secondImage = new Image(Objects.requireNonNull(ImportAndExportView.class.getResourceAsStream("cardimages/" + secondCard.getName() + ".jpg")));
        if (!firstCard.isOriginal() || firstCard.isConverted())
            firstImage = new Image(Objects.requireNonNull(ImportAndExportView.class.getResourceAsStream("cardimages/JonMartin.jpg")));
        else firstImage = new Image(Objects.requireNonNull(ImportAndExportView.class.getResourceAsStream("cardimages/" + firstCard.getName() + ".jpg")));
        first.setImage(firstImage);
        second.setImage(secondImage);
        Tooltip.install(first, getToolTip(firstCard));
        if (secondCard != null) Tooltip.install(second, getToolTip(secondCard));
        else Tooltip.install(second, new Tooltip("Empty"));
    }

    private static Tooltip getToolTip(Card card) {
        StringBuilder cardInformation = new StringBuilder(card.getName()).append("\n");
        if (!(card instanceof MonsterCard)) {
            SpellAndTrapCard spellAndTrapCard = (SpellAndTrapCard) card;
            cardInformation.append(spellAndTrapCard.isSpell() ? "Spell Card" : "Trap Card");
        } else {
            MonsterCard monsterCard = (MonsterCard) card;
            cardInformation.append("Monster Card\nATK: ").append(monsterCard.getClassAttackPower()).
                    append("\nDEF: ").append(monsterCard.getClassDefensePower());
        }
        cardInformation.append("\nPRICE: ").append(card.getPrice());
        Tooltip tooltip = new Tooltip(cardInformation.toString());
        tooltip.setShowDelay(Duration.seconds(0));
        tooltip.setStyle("-fx-font-size: 17");
        return tooltip;
    }

    public void mainMenu() {
        LoginView.stage.setScene(LoginView.mainScene);
        LoginView.stage.centerOnScreen();
    }

    public void importButtonClicked() {
        try {
            ImportAndExport.getInstance().importFile(Objects.requireNonNull(FILE_CHOOSER.showOpenDialog(new Popup())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectFirst() {
        selectedCard = firstCard;
        exportButtonClicked();
    }

    public void selectSecond() {
        if (secondCard == null) return;
        selectedCard = secondCard;
        exportButtonClicked();
    }

    public void exportButtonClicked() {
        try {
            File directory = Objects.requireNonNull(DIRECTORY_CHOOSER.showDialog(new Popup()));
            if (!directory.isDirectory()) throw new InputMismatchException();
            FILE_CHOOSER.setInitialDirectory(directory);
            ImportAndExport.getInstance().exportCard(directory, selectedCard);
        } catch (Exception e) {
            e.printStackTrace();
            //TODO throws exception when window is closed, probably should ignore exception
        }
    }

    public void exportButton() {
        LoginView.stage.setScene(LoginView.importScene);
        allCards = ShopController.getAllCards();
        navigate = 0;
        showCards();
        LoginView.importScene.lookup("#back").setDisable(true);
    }

    public void back() {
        if (navigate == (allCards.size() - 1) / 2) LoginView.importScene.lookup("#next").setDisable(false);
        if (navigate == 1) LoginView.importScene.lookup("#back").setDisable(true);
        navigate--;
        showCards();
    }

    public void next() {
        if (navigate == 0) LoginView.importScene.lookup("#back").setDisable(false);
        if (navigate == ((allCards.size() - 1) / 2) - 1) LoginView.importScene.lookup("#next").setDisable(true);
        navigate++;
        showCards();
    }

}
