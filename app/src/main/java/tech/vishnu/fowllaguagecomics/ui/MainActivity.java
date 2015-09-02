package tech.vishnu.fowllaguagecomics.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tech.vishnu.fowllaguagecomics.Comic;
import tech.vishnu.fowllaguagecomics.R;
import tech.vishnu.fowllaguagecomics.services.ComicLoaderService;
import tech.vishnu.fowllaguagecomics.utils.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final Random RANDOM_NUMBER_GENERATOR = new Random();
    private int size = 0;
    private ComicLoaderService comicLoaderService;
    private ComicPagerAdapter pagerAdapter;

    @Bind(R.id.pager) ViewPager viewPager;

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
            size = comicLoaderService.getSavedComics().size();
            pagerAdapter = new ComicPagerAdapter(getSupportFragmentManager(), size);
            viewPager.setAdapter(pagerAdapter);
        }
    }

    private void fetchComicsFromServer() {
        Log.d(LOG_TAG, "Fetching comics from server.");
        ListenableFuture<List<Comic>> future = comicLoaderService.fetchComics();
        Futures.addCallback(future, new FutureCallback<List<Comic>>() {
            @Override
            public void onSuccess(List<Comic> comics) {
                if (comics.size() > size) {
                    size = comics.size();
                    Log.d(LOG_TAG, "Showing fetched comics.");
                    Toast.makeText(MainActivity.this, R.string.new_comic_notice, Toast.LENGTH_SHORT).show();
                    viewPager.invalidate();
                    viewPager.setCurrentItem(0, true);
                    pagerAdapter.setCount(size);
                    pagerAdapter.notifyDataSetChanged();
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

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.next)
    public void scrollToNextComic() {
        Log.d(LOG_TAG, "Scrolling to next comic.");
        viewPager.arrowScroll(View.FOCUS_RIGHT);
    }

    @OnClick(R.id.previous)
    public void scrollToPreviousComic() {
        Log.d(LOG_TAG, "Scrolling to previous comic.");
        viewPager.arrowScroll(View.FOCUS_LEFT);
    }

    @OnClick(R.id.random)
    public void scrollToRandomComic() {
        Log.d(LOG_TAG, "Scrolling to random comic.");
        int randomComicPosition = RANDOM_NUMBER_GENERATOR.nextInt(size);
        viewPager.setCurrentItem(randomComicPosition, true);
    }

}
