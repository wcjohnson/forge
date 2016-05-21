package shandalike.screens;

import forge.Singletons;
import forge.gui.framework.FScreen;

public class ShandalikeUIUtil {
    public static void showMap() {
    	System.out.println(String.format("[Shandalike] Showing map."));
        Singletons.getControl().setCurrentScreen(FScreen.SHANDALIKE);
        Singletons.getView().getFrame().validate();
    }
}
