package tech.vishnu.fowllaguagecomics.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import tech.vishnu.fowllaguagecomics.Comic;
import tech.vishnu.fowllaguagecomics.R;
import tech.vishnu.fowllaguagecomics.services.ComicLoaderService;
import tech.vishnu.fowllaguagecomics.utils.Executors;

public class MainActivity extends AppCompatActivity {
    public static final String COMIC_ID = "comic_id";
    public static final int RESULT_CODE = 2;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final Random RANDOM_NUMBER_GENERATOR = new Random();

    @Bind(R.id.pager) ViewPager viewPager;
    @Bind(R.id.older_comic) ImageView olderComicButton;
    @Bind(R.id.newer_comic) ImageView newerComicButton;
    private int size = 0;
    private ComicLoaderService comicLoaderService;
    private ComicPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ComicLoaderService.createInstance(this);
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
            Intent intent = new Intent(this, SearchActivity.class);
            startActivityForResult(intent, 2);
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
