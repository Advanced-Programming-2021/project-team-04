package view;

public class ChainView {
    private static ChainView singleInstance = null;
    public ChainView() {

    }
    public static ChainView getInstance() {
        if (singleInstance == null)
            singleInstance = new ChainView();
        return singleInstance;
    }
}
