package tech.vishnu.fowllaguagecomics.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import tech.vishnu.fowllaguagecomics.Comic;
import tech.vishnu.fowllaguagecomics.R;
import tech.vishnu.fowllaguagecomics.services.ComicLoaderService;

import static tech.vishnu.fowllaguagecomics.ui.MainActivity.COMIC_ID;
import static tech.vishnu.fowllaguagecomics.ui.MainActivity.RESULT_CODE;

public class ComicsListActivity extends AppCompatActivity {
    public static final String IS_FAVORITES_SCREEN = "favorites_screen";
    private static final String LOG_TAG = ComicsListActivity.class.getSimpleName();
    @Bind(R.id.listview) ListView listView;
    private ArrayAdapter<String> listAdapter;
    private final List<String> list = new ArrayList<>();
    private final List<Comic> comicsToBeDisplayed = new ArrayList<>();
    private boolean isFavoritesScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comic_list);
        ButterKnife.bind(this);

        isFavoritesScreen = getIntent().getBooleanExtra(IS_FAVORITES_SCREEN, false);
        setupListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setSearchActionMenuItem(menu);

        MenuItem item = menu.findItem(R.id.action_search);
        if (item != null) {
            SearchView searchView = (SearchView) item.getActionView();
            if (isFavoritesScreen) {
                searchView.setQueryHint(getString(R.string.search_hint_favorites));
            } else {
                searchView.setQueryHint(getString(R.string.search_hint));
            }
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void setSearchActionMenuItem(Menu menu) {
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setOnQueryTextListener(getQueryTextListener());
        searchView.setOnCloseListener(getOnCloseListener());
        searchMenuItem.expandActionView();
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, getSearchViewActionListener());
    }

    private SearchView.OnQueryTextListener getQueryTextListener() {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(LOG_TAG, "Query submit: " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(LOG_TAG, "Query change: " + newText);
                query(newText.trim().toLowerCase());
                return true;
            }
        };
    }

    private SearchView.OnCloseListener getOnCloseListener() {
        return new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finish();
                return true;
            }
        };
    }

    private MenuItemCompat.OnActionExpandListener getSearchViewActionListener() {
        return new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return true;
            }
        };
    }

    private void query(String searchString) {
        setupList(searchString);
        listAdapter.notifyDataSetChanged();
    }

    private void setupList(String searchString) {
        list.clear();
        comicsToBeDisplayed.clear();
        ArrayList<Comic> comics;
        if (isFavoritesScreen) {
            comics = Lists.newArrayList(ComicLoaderService.getInstance().getFavoriteComics());
        } else {
            comics = Lists.newArrayList(ComicLoaderService.getInstance().getSavedComics());
        }
        Collections.reverse(comics);
        for (Comic comic : comics) {
            if (String.valueOf(comic.id).contains(searchString) ||
                    comic.title.toLowerCase().contains(searchString) ||
                    comic.keywords.toLowerCase().contains(searchString)) {
                list.add("#" + comic.id + " " + comic.title);
                comicsToBeDisplayed.add(comic);
            }
        }
    }

    private void setupListView() {
        setupList("");
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, list);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Comic comic = comicsToBeDisplayed.get(position);
                Log.d(LOG_TAG, "Selected comic: " + comic);
                Intent data = new Intent();
                data.putExtra(COMIC_ID, comic.id);
                setResult(RESULT_CODE, data);
                finish();
            }
        });
        TextView emptyView = (TextView) findViewById(R.id.empty_view);
        if (isFavoritesScreen) {
            emptyView.setText(R.string.no_favorites);
        } else {
            emptyView.setText(R.string.no_results);
        }
        listView.setEmptyView(emptyView);
    }
}
