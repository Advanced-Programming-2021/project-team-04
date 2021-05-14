package view;

import controller.ImportAndExport;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImportAndExportView extends ViewMenu {
    @Override
    public void run() {
        String command;
        while (!(command = IO.getInstance().getInputMessage()).matches("(?:menu )?exit")) {
            if (command.startsWith("import "))
                importCard(command);
            else if (command.startsWith("export card "))
                exportCard(command);
            else IO.getInstance().printInvalidCommand();
        }
    }

    private void importCard(String input) {
        Matcher matcher = Pattern.compile("import (?:m(?:onster)?|s(?:pell)?) (.*)").matcher(input.toLowerCase()); //TODO is there anything better i could do?
        matcher.find();
        ImportAndExport.getInstance().importCard(matcher.group(1), matcher.group(2));
    }

    private void exportCard(String input) {
        Matcher matcher = Pattern.compile("export(?: c(?:ard)?)? (.*)").matcher(input);
        matcher.find();
        ImportAndExport.getInstance().exportCard(matcher.group(1));
    }

    @Override
    public void showCurrentMenu() {
        IO.getInstance().printImportExportMenuName();
    }
}
