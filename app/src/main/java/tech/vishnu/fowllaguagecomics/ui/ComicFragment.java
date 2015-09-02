package tech.vishnu.fowllaguagecomics.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import tech.vishnu.fowllaguagecomics.Comic;
import tech.vishnu.fowllaguagecomics.R;
import tech.vishnu.fowllaguagecomics.services.ComicLoaderService;

public class ComicFragment extends Fragment {
    public static final String POSITION = "position";
    private static final String LOG_TAG = ComicFragment.class.getSimpleName();

    @Bind(R.id.comic_image) ImageView comicImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_comic, container, false);
        ButterKnife.bind(this, rootView);
        int position = getArguments().getInt(POSITION, 0);
        Comic comic = ComicLoaderService.getInstance().getSavedComics().get(position);
        setupComic(comic);
        return rootView;
    }

    private void setupComic(Comic comic) {
        Log.d(LOG_TAG, "Setting up image: " + comic.imageUrl);
        Picasso.with(getActivity()).load(comic.imageUrl).into(comicImageView);
    }
}
