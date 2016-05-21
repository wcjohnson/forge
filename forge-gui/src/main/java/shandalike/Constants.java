package shandalike;

import forge.properties.ForgeConstants;

public final class Constants {
    // Shandalike data dirs
    public static final String USER_SHANDALIKE_DIR = ForgeConstants.USER_DIR + "shandalike" + ForgeConstants.PATH_SEPARATOR;
    public static final String USER_SHANDALIKE_PREFS_FILE = USER_SHANDALIKE_DIR + "shandalike.preferences";
    public static final String USER_SHANDALIKE_SAVE_DIR	= USER_SHANDALIKE_DIR + "saves" + ForgeConstants.PATH_SEPARATOR;
    public static final String GLOBAL_SHANDALIKE_DIR = ForgeConstants.RES_DIR + "shandalike" + ForgeConstants.PATH_SEPARATOR;
    public static final String GLOBAL_SHANDALIKE_WORLDS_DIR = GLOBAL_SHANDALIKE_DIR + "world" + ForgeConstants.PATH_SEPARATOR;
    public static final String[] DIFFICULTY_LEVEL_NAME = { "Apprentice", "Magician", "Sorcerer", "Wizard" };
}
