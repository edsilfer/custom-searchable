package br.com.customsearchable;

import android.content.Context;
import android.content.Intent;
import android.content.SearchRecentSuggestionsProvider;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.customsearchable.adapter.SearchAdapter;
import br.com.customsearchable.contract.CustomSearchableConstants;
import br.com.customsearchable.model.CustomSearchableInfo;
import br.com.customsearchable.model.ResultItem;
import br.com.customsearchable.util.RecyclerViewOnItemClickListener;


/**
 * Created by edgar on 6/14/15.
 */
public class SearchActivity extends AppCompatActivity {

    // CONSTANTS
    public static final String ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android";

    public static final String PROVIDER_NAME = "name";
    public static final String PROVIDER_AUTHORITY = "authorities";

    public static final String SEARCHABLE_ACT_NAME = "name";
    public static final String SEARCHABLE_ACT_VALUE = "value";

    public static final int VOICE_RECOGNITION_CODE = 1;

    // UI ELEMENTS
    private RecyclerView searchResultList;
    private EditText searchInput;
    private RelativeLayout voiceInput;
    private RelativeLayout dismissDialog;

    private Map provider;
    private Map searchableActivity;
    private String query;


    // Activity Callbacks __________________________________________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start layout resources with default values
        new CustomSearchableInfo(this);

        this.setContentView(R.layout.custom_searchable);
        this.getWindow().setStatusBarColor(getResources().getColor(R.color.textPrimaryColor));

        this.query = "";
        this.searchResultList = (RecyclerView) this.findViewById(R.id.cs_result_list);
        this.searchInput = (EditText) this.findViewById(R.id.custombar_text);
        this.voiceInput = (RelativeLayout) this.findViewById(R.id.custombar_mic_wrapper);
        this.dismissDialog = (RelativeLayout) this.findViewById(R.id.custombar_return_wrapper);

        initializeUiConfiguration ();

        // Initialize result list
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        searchResultList.setLayoutManager(linearLayoutManager);

        SearchAdapter adapter = new SearchAdapter(new ArrayList<br.com.customsearchable.model.ResultItem>());
        searchResultList.setAdapter(adapter);

        this.searchInput.setMaxLines(1);

        implementSearchTextListener();
        implementDismissListener();
        implementVoiceInputListener();
        implementResultListOnItemClickListener();

        //showSoftKeyboard();
        // Retrieve searchable activity and provider from AndroidManifest.xml
        getManifestConfig();
    }

    // Receives the intent with the speech-to-text information
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
    private void sendSearchIntent(ResultItem item) {
        try {
            Intent sendIntent = new Intent(this, Class.forName(searchableActivity.get(SEARCHABLE_ACT_VALUE).toString()));
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

    // Listeners implementation ____________________________________________________________________
    private void implementSearchTextListener() {
        searchInput.clearFocus();

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (!"".equals(searchInput.getText())) {
                    Log.i("Typed Search", searchInput.getText().toString());
                    query = searchInput.getText().toString();

                    try {
                        if (Class.forName(provider.get(PROVIDER_AUTHORITY).toString()).getSuperclass().equals(SearchRecentSuggestionsProvider.class)) {
                            // Provider is descendant of SearchRecentSuggestionsProvider
                            mapResultsFromRecentProviderToList ();
                        } else {
                            // Provider is custom and shall follow the contract
                            mapResultsFromCustomProviderToList ();
                        }
                    } catch (ClassNotFoundException e) {
                        Log.e("Provider Error: ", "Could not check the provider type");
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return true;
            }
        });
    }

    private void implementDismissListener () {
        this.dismissDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                finish();
            }
        });
    }

    private void implementVoiceInputListener () {
        this.voiceInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now");

                SearchActivity.this.startActivityForResult(intent, VOICE_RECOGNITION_CODE);
            }
        });
    }

    private void implementResultListOnItemClickListener () {
        searchResultList.addOnItemTouchListener(new RecyclerViewOnItemClickListener(this,
                new RecyclerViewOnItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ResultItem clickedItem = ((SearchAdapter) searchResultList.getAdapter()).getItem(position);
                        sendSearchIntent(clickedItem);
                    }
                }));
    }

    // Util ________________________________________________________________________________________
    // Retrieves searchable activity and search provider from client's manifest
    private void getManifestConfig() {
        provider = new HashMap();
        searchableActivity = new HashMap();

        try {
            AssetManager am = this.createPackageContext(this.getApplicationContext().getPackageName(), 0).getAssets();
            XmlResourceParser xrp = am.openXmlResourceParser("AndroidManifest.xml");

            xrp.next();
            int eventType = xrp.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("provider")) {
                    String name = xrp.getAttributeValue(ANDROID_NAMESPACE, PROVIDER_NAME);
                    String authorities = xrp.getAttributeValue(ANDROID_NAMESPACE, PROVIDER_AUTHORITY);

                    if (provider.isEmpty()) {
                        provider.put("name", name);
                        provider.put("authorities", authorities);
                    } else if (Class.forName(authorities).getSuperclass().equals(SearchRecentSuggestionsProvider.class)) {
                        // Gives priority to RecentSuggestionsProvider
                        provider.put("name", name);
                        provider.put("authorities", authorities);
                    }
                }

                if (eventType == XmlPullParser.START_TAG && xrp.getName().equalsIgnoreCase("meta-data")) {
                    String name = xrp.getAttributeValue(ANDROID_NAMESPACE, SEARCHABLE_ACT_NAME);
                    String value = xrp.getAttributeValue(ANDROID_NAMESPACE, SEARCHABLE_ACT_VALUE);

                    searchableActivity.put("name", name);
                    searchableActivity.put("value", value);
                }

                eventType = xrp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Log.e("Class not found: ", "The content provider class provider in the <provider> element of the application" +
                    "AndroiManifest.xml could not be found");
            e.printStackTrace();
        }
    }

    // Look for search suggestions in client's provider
    private Cursor queryRecentSuggestionsProvider () {
        Uri uri = Uri.parse("content://" + provider.get(PROVIDER_AUTHORITY) + "/suggestions");

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

    // Look for search suggestions in client's provider
    private Cursor queryCustomSuggestionProvider () {
        Uri uri = Uri.parse("content://" + provider.get(PROVIDER_AUTHORITY) + "/suggestions");

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

    // Given provider is custom and shall follow the column contract
    private void mapResultsFromCustomProviderToList () {
        Cursor results = results = queryCustomSuggestionProvider();
        List<ResultItem> resultList = new ArrayList<>();

        Integer headerIdx = results.getColumnIndex("SUGGEST_COLUMN_TEXT_1");
        Integer subHeaderIdx = results.getColumnIndex("SUGGEST_COLUMN_TEXT_2");

        while (results.moveToNext()) {
            String header = results.getString(headerIdx);
            String subHeader = results.getString(subHeaderIdx);

            // TODO: get the icons from the cursor

            ResultItem aux = new ResultItem(header, subHeader, -1, -1);
            resultList.add(aux);
        }

        results.close();

        SearchAdapter adapter = new SearchAdapter(resultList);
        searchResultList.setAdapter(adapter);
    }

    // Given provider is descendant of SearchRecentSuggestionsProvider (column scheme differs)
    private void mapResultsFromRecentProviderToList () {
        // TODO: handle the two possible providers that this method can scan for
        // TODO: implement functionalities for all columns that might come from the provider
        Cursor results = queryRecentSuggestionsProvider();
        List<ResultItem> resultList = new ArrayList<>();

        Integer headerIdx = results.getColumnIndex("display1");
        Integer subHeaderIdx = results.getColumnIndex("display2");

        while (results.moveToNext()) {
            String header = results.getString(headerIdx);
            String subHeader = (subHeaderIdx == -1) ? "Empty" : results.getString(subHeaderIdx);

            //TODO: get the icons from the cursor

            ResultItem aux = new ResultItem(header, subHeader, -1, -1);
            resultList.add(aux);
        }

        results.close();

        SearchAdapter adapter = new SearchAdapter(resultList);
        searchResultList.setAdapter(adapter);
    }

    // UI __________________________________________________________________________________________
    private void initializeUiConfiguration () {
        // Set activity background transparency
        LinearLayout activityWrapper = (LinearLayout) this.findViewById(R.id.custom_searchable_wrapper);
        activityWrapper.setBackgroundColor(CustomSearchableInfo.getTransparencyColor());

        RelativeLayout headerWrapper = (RelativeLayout) this.findViewById(R.id.cs_header);
        headerWrapper.setBackgroundColor(CustomSearchableInfo.getPrimaryColor());

        searchInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, CustomSearchableInfo.getSearchTextSize());
        searchInput.setTextColor(CustomSearchableInfo.getTextPrimaryColor());
        searchInput.setHintTextColor(CustomSearchableInfo.getTextHintColor());

        ImageView dismissIcon = (ImageView) this.findViewById(R.id.custombar_return);
        ImageView micIcon = (ImageView) this.findViewById(R.id.custombar_mic);

        micIcon.setImageResource(CustomSearchableInfo.getBarMicIcon());
        dismissIcon.setImageResource(CustomSearchableInfo.getBarDismissIcon());
    }

    private void showSoftKeyboard () {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
    }
}
