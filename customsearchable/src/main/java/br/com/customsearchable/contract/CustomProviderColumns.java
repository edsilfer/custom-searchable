package br.com.customsearchable.contract;

/**
 * This class provides the contract for custom content providers implementations. The constants of this class
 * represents the columns that the search mechanism will look for when querying into your content provider
 * A description of these columns can be found under the link: http://developer.android.com/guide/topics/search/adding-custom-suggestions.html#HandlingSuggestionQuery
 * Be aware that not all the functionalities provided by those columns might be implemented
 */
public class CustomProviderColumns {
    public static final String _ID = "_ID";
    public static final String SUGGEST_COLUMN_TEXT_1 = "SUGGEST_COLUMN_TEXT_1";
    public static final String SUGGEST_COLUMN_TEXT_2 = "SUGGEST_COLUMN_TEXT_2";
    public static final String SUGGEST_COLUMN_ICON_1 = "SUGGEST_COLUMN_ICON_1";
    public static final String SUGGEST_COLUMN_ICON_2 = "SUGGEST_COLUMN_ICON_2";
    public static final String SUGGEST_COLUMN_INTENT_ACTION = "SUGGEST_COLUMN_INTENT_ACTION";
    public static final String SUGGEST_COLUMN_INTENT_DATA = "SUGGEST_COLUMN_INTENT_DATA";
    public static final String SUGGEST_COLUMN_INTENT_DATA_ID = "SUGGEST_COLUMN_INTENT_DATA_ID";
    public static final String SUGGEST_COLUMN_INTENT_EXTRA_DATA = "SUGGEST_COLUMN_INTENT_EXTRA_DATA";
    public static final String SUGGEST_COLUMN_QUERY = "SUGGEST_COLUMN_QUERY";
    public static final String SUGGEST_COLUMN_SHORTCUT_ID = "SUGGEST_COLUMN_SHORTCUT_ID";
    public static final String SUGGEST_COLUMN_SPINNER_WHILE_REFRESHING = "SUGGEST_COLUMN_SPINNER_WHILE_REFRESHING";
}
