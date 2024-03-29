package com.fowllanguagecomics.android.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fowllaguagecomics.android.R;
import com.fowllanguagecomics.android.models.Comic;
import com.fowllanguagecomics.android.services.ComicLoaderService;
import com.fowllanguagecomics.android.ui.fragments.ComicFragment;
import com.fowllanguagecomics.android.ui.adapters.ComicPagerAdapter;
import com.fowllanguagecomics.android.utils.Executors;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

import static com.fowllanguagecomics.android.ui.activities.ComicsListActivity.IS_FAVORITES_SCREEN;

public class MainActivity extends AppCompatActivity {
    public static final String COMIC_ID = "comic_id";
    public static final int RESULT_CODE = 2;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final Random RANDOM_NUMBER_GENERATOR = new Random();

    @Bind(R.id.pager) ViewPager viewPager;
    @Bind(R.id.older_comic) ImageView olderComicButton;
    @Bind(R.id.newer_comic) ImageView newerComicButton;
    @Bind(R.id.tool_bar) Toolbar toolbar;
    @Bind(R.id.drawer_layout) DrawerLayout drawerLayout;
    private int size = 0;
    private ComicLoaderService comicLoaderService;
    private ComicPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ComicLoaderService.createInstance(this);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open_drawer, R.string.close_drawer);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        comicLoaderService = ComicLoaderService.getInstance();
        fetchComicsFromServer();
        if (comicLoaderService.hasSavedComics()) {
            Log.d(LOG_TAG, "Showing saved comics.");
            setupComics();
        }
    }

    private void setupComics() {
        size = comicLoaderService.getSavedComics().size();
        pagerAdapter = new ComicPagerAdapter(getSupportFragmentManager(), size);
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float offset, int offsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == size - 1) {
                    olderComicButton.setVisibility(View.INVISIBLE);
                } else {
                    olderComicButton.setVisibility(View.VISIBLE);
                }

                if (position == 0) {
                    newerComicButton.setVisibility(View.INVISIBLE);
                } else {
                    newerComicButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        viewPager.setCurrentItem(0);
        newerComicButton.setVisibility(View.INVISIBLE);
    }

    private void fetchComicsFromServer() {
        Log.d(LOG_TAG, "Fetching comics from server.");
        ListenableFuture<List<Comic>> future = comicLoaderService.fetchComics();
        Futures.addCallback(future, new FutureCallback<List<Comic>>() {
            @Override
            public void onSuccess(List<Comic> comics) {
                if (comics.size() > size) {
                    Log.d(LOG_TAG, "Showing fetched comics.");
                    Toast.makeText(MainActivity.this, R.string.new_comic_notice, Toast.LENGTH_SHORT).show();
                    setupComics();
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        }, Executors.ui);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent intent = new Intent(this, ComicsListActivity.class);
            startActivityForResult(intent, RESULT_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int currentItem = viewPager.getCurrentItem();
        ComicFragment fragment = pagerAdapter.getItem(currentItem);
        if (!fragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.older_comic)
    public void scrollToNextComic() {
        Log.d(LOG_TAG, "Scrolling to next comic.");
        viewPager.arrowScroll(View.FOCUS_RIGHT);
    }

    @OnLongClick(R.id.older_comic)
    public boolean scrollToLastComic() {
        Log.d(LOG_TAG, "Scrolling to last comic.");
        viewPager.setCurrentItem(size - 1, true);
        return true;
    }

    @OnClick(R.id.newer_comic)
    public void scrollToPreviousComic() {
        Log.d(LOG_TAG, "Scrolling to previous comic.");
        viewPager.arrowScroll(View.FOCUS_LEFT);
    }

    @OnLongClick(R.id.newer_comic)
    public boolean scrollToFirstComic() {
        Log.d(LOG_TAG, "Scrolling to first comic.");
        viewPager.setCurrentItem(0, true);
        return true;
    }

    @OnClick(R.id.random)
    public void scrollToRandomComic() {
        Log.d(LOG_TAG, "Scrolling to random comic.");
        int randomComicPosition = RANDOM_NUMBER_GENERATOR.nextInt(size);
        viewPager.setCurrentItem(randomComicPosition, false);
    }

    @OnClick(R.id.email)
    public void onEmailClick() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.email_address));
        startActivity(Intent.createChooser(intent, getString(R.string.choose_email_application)));
    }

    @OnClick(R.id.favorites)
    public void onFavoritesClick() {
        Intent intent = new Intent(this, ComicsListActivity.class);
        intent.putExtra(IS_FAVORITES_SCREEN, true);
        startActivityForResult(intent, RESULT_CODE);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @OnClick(R.id.shop)
    public void onShopClick() {
        openBrowser("http://www.fowllanguagecomics.com/shop/");
    }

    @OnClick(R.id.website)
    public void onWebsiteClick() {
        openBrowser("http://www.fowllanguagecomics.com/");
    }

    @OnClick(R.id.about)
    public void onAboutClick() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.patreon)
    public void onPatreonClick() {
        openBrowser("https://www.patreon.com/fowllanguage");
    }

    private void openBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, "Search result: " + requestCode);
        if (resultCode == RESULT_CODE) {
            int position = data.getIntExtra(COMIC_ID, 1);
            Log.d(LOG_TAG, "Opening comic: " + (size - position));
            viewPager.setCurrentItem(size - position, false);
        }
    }
}
