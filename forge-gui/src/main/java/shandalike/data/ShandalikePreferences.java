/*
 * Forge: Play Magic: the Gathering.
 * Copyright (C) 2011  Forge Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package shandalike.data;

import java.io.Serializable;

import forge.properties.PreferencesStore;
import shandalike.Constants;

@SuppressWarnings("serial")
public class ShandalikePreferences extends PreferencesStore<ShandalikePreferences.Pref> implements Serializable {

    /**
     * Preference identifiers, and their default values.
     */
    public enum Pref {
        // Currently chosen quest and deck
        CURRENT_WORLD("DEFAULT");

        private final String strDefaultVal;

        /**
         * Instantiates a new q pref.
         *
         * @param s0 {@link java.lang.String}
         */
        Pref(final String s0) {
            this.strDefaultVal = s0;
        }

        /**
         * Gets the default.
         *
         * @return {@link java.lang.String}
         */
        public String getDefault() {
            return this.strDefaultVal;
        }
    }

    public enum DifficultyPrefs {

    }

    /** Instantiates a QuestPreferences object. */
    public ShandalikePreferences() {
        super(Constants.USER_SHANDALIKE_PREFS_FILE, Pref.class);
    }

    @Override
    protected Pref[] getEnumValues() {
        return Pref.values();
    }

    @Override
    protected Pref valueOf(final String name) {
        try {
            return Pref.valueOf(name);
        }
        catch (final Exception e) {
            return null;
        }
    }

    @Override
    protected String getPrefDefault(final Pref key) {
        return key.getDefault();
    }

    /**
     * Returns a preference value according to a difficulty index.
     */
    public String getPref(final DifficultyPrefs pref, final int difficultyIndex) {
        String newQPref = pref.toString();

        switch (difficultyIndex) {
        case 0:
            newQPref += "_0";
            break;
        case 1:
            newQPref += "_1";
            break;
        case 2:
            newQPref += "_2";
            break;
        case 3:
            newQPref += "_3";
            break;
        default:
            throw new IllegalArgumentException(String.format("Difficulty index %d out of bounds, preference %s", difficultyIndex, newQPref));
        }

        return getPref(Pref.valueOf(newQPref));
    }

    /**
     * Returns a difficulty-indexed preference value, as an int.
     */
    public int getPrefInt(final DifficultyPrefs pref, final int difficultyIndex) {
        return Integer.parseInt(this.getPref(pref, difficultyIndex));
    }

    public String validatePreference(final Pref qpref, final int val) {
        return null;
    }
}
