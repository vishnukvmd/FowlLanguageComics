package tech.vishnu.fowllaguagecomics.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import tech.vishnu.fowllaguagecomics.Comic;
import tech.vishnu.fowllaguagecomics.R;
import tech.vishnu.fowllaguagecomics.services.ComicLoaderService;

public class ComicFragment extends Fragment {
    public static final String POSITION = "position";
    private static final String LOG_TAG = ComicFragment.class.getSimpleName();

    @Bind(R.id.comic_image) ImageView comicImageView;
    @Bind(R.id.progress_bar) ProgressBar progressBar;
    @Bind(R.id.comic_title) TextView titleTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_comic, container, false);
        ButterKnife.bind(this, rootView);
        int position = getArguments().getInt(POSITION, 0);
        List<Comic> savedComics = ComicLoaderService.getInstance().getSavedComics();
        Comic comic = savedComics.get(savedComics.size() - position - 1);
        setupComic(comic);
        return rootView;
    }

    private void setupComic(final Comic comic) {
        Log.d(LOG_TAG, "Setting up image: " + comic.imageUrl);
        progressBar.setVisibility(View.VISIBLE);
        Picasso.with(getActivity())
                .load(comic.imageUrl)
                .into(comicImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                        titleTextView.setText(comic.title);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }
}
