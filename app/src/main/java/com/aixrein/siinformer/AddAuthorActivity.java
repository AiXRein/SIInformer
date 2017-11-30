package com.aixrein.siinformer;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Build;
import android.preference.ListPreference;
import android.provider.BaseColumns;
import android.provider.DocumentsContract;
import android.renderscript.Element;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddAuthorActivity extends AppCompatActivity {

    private SearchView authorsSearchView;
    private Cursor queryCursor;
    List<HashMap <String, String>> authorsQuerySearchMap;
    private MyHttpHelper myHttpHelper = new MyHttpHelper();
    Context context;
    private int search_hint;
    String link = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_author);
        setupActionBar();
        final Context context = this;
        final boolean authorsFirstLetter = false;
        authorsSearchView=(SearchView) findViewById(R.id.authorSearchView);
        authorsSearchView.setQueryHint(getString(R.string.search_hint));

        authorsSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                String responce = myHttpHelper.postData(query);

                //Log.i("Query Responce", responce);

                //Toast.makeText(getBaseContext(), myHttpHelper.postData(query), Toast.LENGTH_LONG).show();
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(context);
                dlgAlert.setMessage(responce);
                dlgAlert.setTitle("Responce");

                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
               // Toast.makeText(getBaseContext(), newText, Toast.LENGTH_LONG).show();
               if (newText.length() == 1) {
                   HashMap<String,String> linkMap = MyHttpHelper.getAlphabetList();

                   //Toast.makeText(getBaseContext(), linkMap.get(newText.charAt(0)), Toast.LENGTH_LONG).show();
                   Log.i("Ключ Ссылки",newText.substring(0,1).toUpperCase());
                   link = "http://samlib.ru" + linkMap.get(newText.substring(0,1).toUpperCase());
                   Log.i("Ссылка",link);
               }
                final CursorAdapter suggestionAdapter = new SimpleCursorAdapter(context,
                        R.layout.search_suggestion_row,
                        null,
                        new String[]{SearchManager.SUGGEST_COLUMN_TEXT_1,SearchManager.SUGGEST_COLUMN_TEXT_2},
                        new int[]{R.id.text_author_name,R.id.text_author_info},
                        0);
                final ArrayList<HashMap<String, String>> suggestions = new ArrayList<HashMap<String, String>>();

                authorsSearchView.setSuggestionsAdapter(suggestionAdapter);
                authorsSearchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                    @Override
                    public boolean onSuggestionSelect(int position) {
                        return false;
                    }

                    @Override
                    public boolean onSuggestionClick(int position) {
                        authorsSearchView.setQuery(suggestions.get(position).get("Name"), false);
                        authorsSearchView.clearFocus();
                        MyHttpHelper.searchAuthor(suggestions.get(position).get("Name"));
                        return true;
                    }
                });

                /*MyApp.AutoCompleteSearch.search(newText, new Callback<AutoCompleteSearch>() {
                    @Override
                    public void success(AutoCompleteSearch autocomplete, HttpConnection.Response response) {
                        suggestions.clear();
                        suggestions.addAll(autocomplete.suggestions);

                        String[] columns = {
                                BaseColumns._ID,
                                SearchManager.SUGGEST_COLUMN_TEXT_1,
                                SearchManager.SUGGEST_COLUMN_TEXT_2,
                                SearchManager.SUGGEST_COLUMN_INTENT_DATA
                        };

                        MatrixCursor cursor = new MatrixCursor(columns);

                        for (int i = 0; i < autocomplete.suggestions.size(); i++) {
                            String[] tmp = {Integer.toString(i), autocomplete.suggestions.get(i), autocomplete.suggestions.get(i)};
                            cursor.addRow(tmp);
                        }
                        suggestionAdapter.swapCursor(cursor);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(SearchFoodActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.w("autocompleteService", error.getMessage());
                    }
                });*/

                MyHttpHelper.searchAuthorSuggestion(link);
                return false;
            }
        });

    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_add_author).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default

        return true;
    }

    @Override
    public boolean onSearchRequested() {
        Bundle appData = new Bundle();
        appData.putBoolean(SearchableActivity.JARGON, true);
        startSearch(null, false, appData, false);
        return true;
    }*/
}




