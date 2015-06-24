package br.com.edsilfer.content_provider;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Provides a simple content provider suggestions as specified by Android documentation
 * http://developer.android.com/guide/topics/search/adding-recent-query-suggestions.html
 */
public class RecentSuggestionsProvider extends SearchRecentSuggestionsProvider {

    public final static String AUTHORITY = "br.com.edsilfer.content_provider.RecentSuggestionsProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public RecentSuggestionsProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}