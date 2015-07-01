package br.com.customsearchable;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.customsearchable.adapter.SearchAdapter;
import br.com.customsearchable.contract.CustomSearchableConstants;
import br.com.customsearchable.model.CustomSearchableInfo;
import br.com.customsearchable.model.ResultItem;
import br.com.customsearchable.util.ManifestParser;
import br.com.customsearchable.util.RecyclerViewOnItemClickListener;

/**
 * This activity controls the maint flow for the Custom Searchable behaviour. Its onCreate method
 * initialize the main UI components - such as the app bar and result list (RecyclerView). It should
 * be called through an intent and it's responses are also sent as intents
 */
public class SearchActivity extends AppCompatActivity {
    // CONSTANTS
    private static final String TAG = "SearchActivity";
    public static final int VOICE_RECOGNITION_CODE = 1;

    // UI ELEMENTS
    private RecyclerView searchResultList;
    private EditText searchInput;
    private RelativeLayout voiceInput;
    private RelativeLayout dismissDialog;
    private ImageView micIcon;

    private String query;
    private String providerName;
    private String providerAuthority;
    private String searchableActivity;
    private Boolean isRecentSuggestionsProvider = Boolean.TRUE;

    // Activity Callbacks __________________________________________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.custom_searchable);
        this.getWindow().setStatusBarColor(getResources().getColor(R.color.textPrimaryColor));

        this.query = "";
        this.searchResultList = (RecyclerView) this.findViewById(R.id.cs_result_list);
        this.searchInput = (EditText) this.findViewById(R.id.custombar_text);
        this.voiceInput = (RelativeLayout) this.findViewById(R.id.custombar_mic_wrapper);
        this.dismissDialog = (RelativeLayout) this.findViewById(R.id.custombar_return_wrapper);
        this.micIcon = (ImageView) this.findViewById(R.id.custombar_mic);
        this.micIcon.setSelected(Boolean.FALSE);

        initializeUiConfiguration();

        // Initialize result list
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        searchResultList.setLayoutManager(linearLayoutManager);

        SearchAdapter adapter = new SearchAdapter(new ArrayList<ResultItem>());
        searchResultList.setAdapter(adapter);

        this.searchInput.setMaxLines(1);

        implementSearchTextListener();
        implementDismissListener();
        implementVoiceInputListener();
        implementResultListOnItemClickListener();

        getManifestConfig();
    }

    // Receives the intent with the speech-to-text information and sets it to the InputText
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case VOICE_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    searchInput.setText(text.get(0));
                }
                break;
            }
        }
    }

    // Sends an intent with the typed query to the searchable Activity
    private void sendSuggestionIntent(ResultItem item) {
        try {
            Intent sendIntent = new Intent(this, Class.forName(searchableActivity));
            sendIntent.setAction(Intent.ACTION_VIEW);
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            Bundle b = new Bundle();
            b.putParcelable(CustomSearchableConstants.CLICKED_RESULT_ITEM, item);

            sendIntent.putExtras(b);
            startActivity(sendIntent);
            finish();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Sends an intent with the typed query to the searchable Activity
    private void sendSearchIntent () {
        try {
            Intent sendIntent = new Intent(this, Class.forName(searchableActivity));
            sendIntent.setAction(Intent.ACTION_SEARCH);
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            sendIntent.putExtra(SearchManager.QUERY, query);

            // If it is set one-line mode, directly saves the suggestion in the provider
            if (!CustomSearchableInfo.getIsTwoLineExhibition()) {
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, providerAuthority, SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES);
                suggestions.saveRecentQuery(query, null);
            }

            startActivity(sendIntent);
            finish();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Listeners implementation ____________________________________________________________________
    private void implementSearchTextListener() {
        // Gets the event of pressing search button on soft keyboard
        TextView.OnEditorActionListener searchListener = new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    sendSearchIntent();
                }
                return true;
            }
        };

        searchInput.setOnEditorActionListener(searchListener);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (!"".equals(searchInput.getText().toString())) {
                    query = searchInput.getText().toString();

                    setClearTextIcon();

                    if (isRecentSuggestionsProvider) {
                        // Provider is descendant of SearchRecentSuggestionsProvider
                        mapResultsFromRecentProviderToList();
                    } else {
                        // Provider is custom and shall follow the contract
                        mapResultsFromCustomProviderToList();
                    }
                } else {
                    setMicIcon();
                }
            }

            // DO NOTHING
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            // DO NOTHING
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }

    // Finishes this activity and goes back to the caller
    private void implementDismissListener () {
        this.dismissDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // Implements speech-to-text
    private void implementVoiceInputListener () {
        this.voiceInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (micIcon.isSelected()) {
                    searchInput.setText("");
                    query = "";
                    micIcon.setSelected(Boolean.FALSE);
                    micIcon.setImageResource(R.drawable.mic_icon);
                } else {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now");

                    SearchActivity.this.startActivityForResult(intent, VOICE_RECOGNITION_CODE);
                }
            }
        });
    }

    // Sends intent to searchableActivity with the selected result item
    private void implementResultListOnItemClickListener () {
        searchResultList.addOnItemTouchListener(new RecyclerViewOnItemClickListener(this,
                new RecyclerViewOnItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ResultItem clickedItem = ((SearchAdapter) searchResultList.getAdapter()).getItem(position);
                        sendSuggestionIntent(clickedItem);
                    }
                }));
    }

    // Util ________________________________________________________________________________________
    // Retrieve the priority provider, searchable activity and provider authority from the AndroidManifest.xml
    private void getManifestConfig () {
        try {
            Map<String, String> providers = ManifestParser.getProviderNameAndAuthority(this);

            OUTER: for (String key : providers.keySet()) {
                providerAuthority = providers.get(key).toString();
                providerName = key;

                if (Class.forName(providerName).getSuperclass().equals(SearchRecentSuggestionsProvider.class)) {
                    isRecentSuggestionsProvider = Boolean.TRUE;

                    break OUTER;
                } else {
                    isRecentSuggestionsProvider = Boolean.FALSE;
                }
            }

            searchableActivity = ManifestParser.getSearchableActivity(this);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Look for search suggestions in client's provider (the one the implements the RecentSuggestionsProvider interface)
    private Cursor queryRecentSuggestionsProvider () {
        Uri uri = Uri.parse("content://".concat(providerAuthority.concat("/suggestions")));

        String[] selection;

        if (CustomSearchableInfo.getIsTwoLineExhibition()) {
            selection = SearchRecentSuggestions.QUERIES_PROJECTION_2LINE;
        } else {
            selection = SearchRecentSuggestions.QUERIES_PROJECTION_1LINE;
        }

        String[] selectionArgs = new String[] {"%" + query + "%"};

        return SearchActivity.this.getContentResolver().query(
                uri,
                selection,
                "display1 LIKE ?",
                selectionArgs,
                "date DESC"
        );
    }

    // Look for search suggestions in client's provider (Custom one)
    private Cursor queryCustomSuggestionProvider () {
        Uri uri = Uri.parse("content://".concat(providerAuthority).concat("/suggestions/").concat(query));

        String[] selection = {"display1"};
        String[] selectionArgs = new String[] {"%" + query + "%"};

        return SearchActivity.this.getContentResolver().query(
                uri,
                SearchRecentSuggestions.QUERIES_PROJECTION_1LINE,
                "display1 LIKE ?",
                selectionArgs,
                "date DESC"
        );
    }

    // Given provider is custom and must follow the column contract
    private void mapResultsFromCustomProviderToList () {
        new AsyncTask<Void, Void, List>() {
            @Override
            protected void onPostExecute(List resultList) {
                SearchAdapter adapter = new SearchAdapter(resultList);
                searchResultList.setAdapter(adapter);
            }

            @Override
            protected List doInBackground(Void[] params) {
                Cursor results = results = queryCustomSuggestionProvider();
                List<ResultItem> resultList = new ArrayList<>();

                Integer headerIdx = results.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1);
                Integer subHeaderIdx = results.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2);
                Integer leftIconIdx = results.getColumnIndex(SearchManager.SUGGEST_COLUMN_ICON_1);
                Integer rightIconIdx = results.getColumnIndex(SearchManager.SUGGEST_COLUMN_ICON_2);

                while (results.moveToNext()) {
                    String header = results.getString(headerIdx);
                    String subHeader = (subHeaderIdx == -1) ? null : results.getString(subHeaderIdx);
                    Integer leftIcon = (leftIconIdx == -1) ? 0 : results.getInt(leftIconIdx);
                    Integer rightIcon = (rightIconIdx == -1) ? 0 : results.getInt(rightIconIdx);

                    ResultItem aux = new ResultItem(header, subHeader, leftIcon, rightIcon);
                    resultList.add(aux);
                }

                results.close();
                return resultList;
            }
        }.execute();
    }

    // Given provider is descendant of SearchRecentSuggestionsProvider (column scheme differs)
    private void mapResultsFromRecentProviderToList () {
        new AsyncTask<Void, Void, List>() {
            @Override
            protected void onPostExecute(List resultList) {
                SearchAdapter adapter = new SearchAdapter(resultList);
                searchResultList.setAdapter(adapter);
            }

            @Override
            protected List doInBackground(Void[] params) {
                Cursor results = queryRecentSuggestionsProvider();
                List<ResultItem> resultList = new ArrayList<>();

                Integer headerIdx = results.getColumnIndex("display1");
                Integer subHeaderIdx = results.getColumnIndex("display2");
                Integer leftIconIdx = results.getColumnIndex(SearchManager.SUGGEST_COLUMN_ICON_1);
                Integer rightIconIdx = results.getColumnIndex(SearchManager.SUGGEST_COLUMN_ICON_2);

                while (results.moveToNext()) {
                    String header = results.getString(headerIdx);
                    String subHeader = (subHeaderIdx == -1) ? null : results.getString(subHeaderIdx);
                    Integer leftIcon = (leftIconIdx == -1) ? 0 : results.getInt(leftIconIdx);
                    Integer rightIcon = (rightIconIdx == -1) ? 0 : results.getInt(rightIconIdx);

                    ResultItem aux = new ResultItem(header, subHeader, leftIcon, rightIcon);
                    resultList.add(aux);
                }

                results.close();
                return resultList;
            }
        }.execute();
    }

    // UI __________________________________________________________________________________________
    // Identifies if client have set any of the configuration attributes, if yes, reset UI element source, toherwise keep defaul value
    private void initializeUiConfiguration () {
        // Set activity background transparency
        if (CustomSearchableInfo.getTransparencyColor() != CustomSearchableConstants.UNSET_RESOURCES) {
            LinearLayout activityWrapper = (LinearLayout) this.findViewById(R.id.custom_searchable_wrapper);
            activityWrapper.setBackgroundColor(CustomSearchableInfo.getTransparencyColor());
        }

        if (CustomSearchableInfo.getPrimaryColor() != CustomSearchableConstants.UNSET_RESOURCES) {
            RelativeLayout headerWrapper = (RelativeLayout) this.findViewById(R.id.cs_header);
            headerWrapper.setBackgroundColor(CustomSearchableInfo.getPrimaryColor());
        }

        if (CustomSearchableInfo.getSearchTextSize() != CustomSearchableConstants.UNSET_RESOURCES) {
            searchInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, CustomSearchableInfo.getSearchTextSize());
        }

        if (CustomSearchableInfo.getTextPrimaryColor() != CustomSearchableConstants.UNSET_RESOURCES) {
            searchInput.setTextColor(CustomSearchableInfo.getTextPrimaryColor());
        }

        if (CustomSearchableInfo.getTextHintColor() != CustomSearchableConstants.UNSET_RESOURCES) {
            searchInput.setHintTextColor(CustomSearchableInfo.getTextHintColor());
        }

        if (CustomSearchableInfo.getBarDismissIcon() != CustomSearchableConstants.UNSET_RESOURCES) {
            ImageView dismissIcon = (ImageView) this.findViewById(R.id.custombar_return);
            dismissIcon.setImageResource(CustomSearchableInfo.getBarDismissIcon());
        }

        if (CustomSearchableInfo.getBarMicIcon() != CustomSearchableConstants.UNSET_RESOURCES) {
            ImageView micIcon = (ImageView) this.findViewById(R.id.custombar_mic);
            micIcon.setImageResource(CustomSearchableInfo.getBarMicIcon());
        }

        if (CustomSearchableInfo.getBarHeight() != CustomSearchableConstants.UNSET_RESOURCES) {
            RelativeLayout custombar = (RelativeLayout) this.findViewById(R.id.cs_header);
            android.view.ViewGroup.LayoutParams params = custombar.getLayoutParams();
            params.height = CustomSearchableInfo.getBarHeight().intValue();
            custombar.setLayoutParams(params);
        }
    }

    // Set X as the icon for the right icon in the app bar
    private void setClearTextIcon () {
        micIcon.setSelected(Boolean.TRUE);
        micIcon.setImageResource(R.drawable.delete_icon);
        micIcon.invalidate();
    }

    // Set the micrphone icon as the right icon in the app bar
    private void setMicIcon () {
        micIcon.setSelected(Boolean.FALSE);
        micIcon.setImageResource(R.drawable.mic_icon);
        micIcon.invalidate();
    }
}

