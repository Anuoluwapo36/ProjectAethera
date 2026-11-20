package com.example.projectaethera.model;



/**

 * One row in the settings screen RecyclerView.

 * Can be an account header, section title, navigation item, or logout button.

 * Fields: type, title, subtitle, action.

 */

public class SettingsItem {



    /** Row showing the account/profile summary at the top. */

    public static final int TYPE_ACCOUNT = 0;

    /** Row that is just a section heading (e.g. "Preferences"). */

    public static final int TYPE_SECTION = 1;

    /** Row the user can tap to open a settings screen. */

    public static final int TYPE_NAV = 2;

    /** Row for the logout button. */

    public static final int TYPE_LOGOUT = 3;



    /**

     * What happens when the user taps a navigation row.

     */

    public enum Action {

        EDIT_PROFILE,

        NOTIFICATIONS,

        PRIVACY,

        APPEARANCE,

        SECURITY,

        ABOUT,

        HELP,

        LOGOUT

    }



    /** Which kind of row this is (account, section, nav, logout). */

    private final int type;

    /** Main label text shown on the row. */

    private final String title;

    /** Smaller text under the title (optional). */

    private final String subtitle;

    /** Which screen or action to open when tapped. */

    private final Action action;



    /**

     * Private constructor — use factory methods like account(), section(), nav().

     *

     * @param type     row type constant

     * @param title    main text

     * @param subtitle secondary text

     * @param action   tap action

     */

    private SettingsItem(int type, String title, String subtitle, Action action) {

        this.type = type;

        this.title = title;

        this.subtitle = subtitle;

        this.action = action;

    }



    /**

     * Creates the account header row at the top of settings.

     *

     * @return account-type settings item

     */

    public static SettingsItem account() {

        return new SettingsItem(TYPE_ACCOUNT, null, null, Action.EDIT_PROFILE);

    }



    /**

     * Creates a non-clickable section heading row.

     *

     * @param title section title text

     * @return section-type settings item

     */

    public static SettingsItem section(String title) {

        return new SettingsItem(TYPE_SECTION, title, null, null);

    }



    /**

     * Creates a tappable row that opens a settings page.

     *

     * @param title    main label

     * @param subtitle description under the label

     * @param action   what to do when tapped

     * @return navigation-type settings item

     */

    public static SettingsItem nav(String title, String subtitle, Action action) {

        return new SettingsItem(TYPE_NAV, title, subtitle, action);

    }



    /**

     * Creates the logout button row.

     *

     * @return logout-type settings item

     */

    public static SettingsItem logout() {

        return new SettingsItem(TYPE_LOGOUT, null, null, Action.LOGOUT);

    }



    /**

     * @return row type (TYPE_ACCOUNT, TYPE_SECTION, etc.)

     */

    public int getType() {

        return type;

    }



    /**

     * @return main title text

     */

    public String getTitle() {

        return title;

    }



    /**

     * @return subtitle text

     */

    public String getSubtitle() {

        return subtitle;

    }



    /**

     * @return action when this row is tapped

     */

    public Action getAction() {

        return action;

    }



    /**

     * Used by settings search to filter visible rows.

     *

     * @param query search text typed by the user

     * @return true if this row should stay visible for the query

     */

    public boolean matchesQuery(String query) {

        if (query == null || query.isEmpty()) {

            return true;

        }

        String q = query.toLowerCase();

        if (type == TYPE_SECTION || type == TYPE_ACCOUNT || type == TYPE_LOGOUT) {

            return true;

        }

        return (title != null && title.toLowerCase().contains(q))

                || (subtitle != null && subtitle.toLowerCase().contains(q));

    }

}

