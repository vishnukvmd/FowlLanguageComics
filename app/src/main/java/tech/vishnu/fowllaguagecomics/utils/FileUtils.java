package tech.vishnu.fowllaguagecomics.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class FileUtils {
    private static final String LOG_TAG = FileUtils.class.getSimpleName();

    public static ListenableFuture<Uri> saveImageToDisk(final Context context, final ImageView imageView) {
        final SettableFuture<Uri> future = SettableFuture.create();
        Executors.background.execute(new Runnable() {
            @Override
            public void run() {
                String path = context.getExternalCacheDir().getAbsolutePath() + "/image.jpg";
                imageView.setDrawingCacheEnabled(true);
                Bitmap bitmap = imageView.getDrawingCache();
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(path));
                    Uri uri = Uri.parse("file://" + path);
                    future.set(uri);
                } catch (FileNotFoundException e) {
                    Log.e(LOG_TAG, "Error saving file", e);
                    future.setException(e);
                }
            }
        });
        return future;
    }
}
