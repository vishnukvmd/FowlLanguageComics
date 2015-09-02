package tech.vishnu.fowllaguagecomics.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
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

    @OnLongClick(R.id.comic_image)
    public boolean shareImage() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();

        ListenableFuture<Uri> future = FileUtils.saveImageToDisk(getActivity(), comicImageView);
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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(R.string.something_went_wrong);
                alertDialog.setPositiveButton(R.string.ok, null);
                alertDialog.show();
            }
        }, Executors.ui);
        return true;
    }
}
