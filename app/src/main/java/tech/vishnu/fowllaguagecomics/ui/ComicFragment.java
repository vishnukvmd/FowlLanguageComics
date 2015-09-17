package tech.vishnu.fowllaguagecomics.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import tech.vishnu.fowllaguagecomics.Comic;
import tech.vishnu.fowllaguagecomics.R;
import tech.vishnu.fowllaguagecomics.services.ComicLoaderService;
import tech.vishnu.fowllaguagecomics.utils.Executors;
import tech.vishnu.fowllaguagecomics.utils.FileUtils;

public class ComicFragment extends Fragment {
    public static final String POSITION = "position";
    private static final String LOG_TAG = ComicFragment.class.getSimpleName();

    @Bind(R.id.comic_image) ImageView comicImageView;
    @Bind(R.id.bonus_panel) ImageView bonusPanelImageView;
    @Bind(R.id.comic_progress_bar) ProgressBar comicImageProgressBar;
    @Bind(R.id.bonus_panel_progress_bar) ProgressBar bonusPanelProgressBar;
    @Bind(R.id.comic_title) TextView titleTextView;
    @Bind(R.id.comic_root) View comicRootView;
    @Bind(R.id.comic_image_section) View comicImageSection;
    @Bind(R.id.bonus_panel_section) View bonusPanelSection;
    private Comic comic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_comic, container, false);
        ButterKnife.bind(this, rootView);
        int position = getArguments().getInt(POSITION, 0);
        List<Comic> savedComics = ComicLoaderService.getInstance().getSavedComics();
        comic = savedComics.get(savedComics.size() - position - 1);
        setupComic(comic);
        return rootView;
    }

    private void setupComic(final Comic comic) {
        Log.d(LOG_TAG, "Setting up image: " + comic.imageUrl);
        comicImageProgressBar.setVisibility(View.VISIBLE);
        ComicLoaderService.getInstance().getPicasso()
                .load(comic.imageUrl)
                .into(comicImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        comicImageProgressBar.setVisibility(View.GONE);
                        titleTextView.setText(comic.title);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    public boolean onBackPressed() {
        if (comicImageSection.getVisibility() == View.VISIBLE) {
            return false;
        } else {
            flipCard();
            return true;
        }
    }

    @OnClick(R.id.comic_image)
    public void onComicLongClick() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("#" + comic.id + " " + comic.title);
        CharSequence[] items;
        final boolean isFavoriteComic = ComicLoaderService.getInstance().isFavorite(comic);
        if (!comic.bonusPanelUrl.isEmpty()) {
            items = new CharSequence[4];
            items[3] = getActivity().getString(R.string.bonus_panel);
        } else {
            items = new CharSequence[3];
        }
        items[0] = getActivity().getString(R.string.share);
        if (isFavoriteComic) {
            items[1] = getActivity().getString(R.string.remove_from_favorites);
        } else {
            items[1] = getActivity().getString(R.string.add_to_favorites);
        }
        items[2] = getActivity().getString(R.string.buy_comic);
        alertDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        shareImage(comicImageView);
                        break;
                    case 1:
                        if (isFavoriteComic) {
                            removeFromFavorites();
                        } else {
                            favoriteComic();
                        }
                        break;
                    case 2:
                        buyComic();
                        break;
                    case 3:
                        flipCard();
                        break;
                }
            }
        });
        alertDialog.show();
    }

    @OnClick(R.id.bonus_panel)
    public void onBonusPanelLongClick() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("#" + comic.id + " " + comic.title + " - Bonus Panel");
        CharSequence[] items = new CharSequence[2];
        items[0] = getActivity().getString(R.string.share);
        items[1] = getActivity().getString(R.string.back);
        alertDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        shareImage(bonusPanelImageView);
                        break;
                    case 1:
                        flipCard();
                        break;
                }
            }
        });
        alertDialog.show();
    }

    @OnLongClick({R.id.comic_image, R.id.bonus_panel})
    public boolean flipCard() {
        if (comic.bonusPanelUrl.isEmpty()) {
            Toast.makeText(getActivity(), R.string.no_toast, Toast.LENGTH_SHORT).show();
            return true;
        }

        FlipAnimation flipAnimation = new FlipAnimation(comicImageSection, bonusPanelSection);

        if (comicImageSection.getVisibility() == View.GONE) {
            flipAnimation.reverse();
        } else {
            bonusPanelProgressBar.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(comic.bonusPanelUrl)
                    .into(bonusPanelImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            bonusPanelProgressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
        comicRootView.startAnimation(flipAnimation);
        return true;
    }

    private void buyComic() {
        String url = "http://www.fowllanguagecomics.com/shop/?id=" + comic.flcId;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private void favoriteComic() {
        ComicLoaderService.getInstance().addToFavorites(comic);
        Toast.makeText(getActivity(), R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
    }

    private void removeFromFavorites() {
        ComicLoaderService.getInstance().removeFromFavorites(comic);
        Toast.makeText(getActivity(), R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
    }

    private void shareImage(ImageView imageView) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();

        ListenableFuture<Uri> future = FileUtils.saveImageToDisk(getActivity(), imageView);
        Futures.addCallback(future, new FutureCallback<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                progressDialog.dismiss();
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                Log.d(LOG_TAG, "Saved file to:" + uri);
                startActivity(Intent.createChooser(share, "Share Image"));
            }

            @Override
            public void onFailure(Throwable t) {
                progressDialog.dismiss();
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
                alertDialog.setTitle(R.string.something_went_wrong);
                alertDialog.setPositiveButton(R.string.ok, null);
                alertDialog.show();
            }
        }, Executors.ui);
    }
}
