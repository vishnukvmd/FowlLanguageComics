package tech.vishnu.fowllaguagecomics.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import tech.vishnu.fowllaguagecomics.Comic;
import tech.vishnu.fowllaguagecomics.utils.Executors;

public class ComicLoaderService {
    private static final String URL = "https://fowllanguage.gscheffler.de";
    private static final String LOG_TAG = ComicLoaderService.class.getSimpleName();
    private static final String COMICS_KEY = "comics";
    private static final Gson GSON = new Gson();

    private final SettableFuture<List<Comic>> future = SettableFuture.create();
    private final List<Comic> comics = new ArrayList<>();
    private final SharedPreferences store;

    private static ComicLoaderService _;

    private ComicLoaderService(Context context) {
        this.store = context.getSharedPreferences(COMICS_KEY, Context.MODE_PRIVATE);
        if (hasSavedComics()) {
            Log.d(LOG_TAG, "Loading data from disk");
            String comicsString = store.getString(COMICS_KEY, "[]");
            Log.d(LOG_TAG, "Data loaded from disk: " + comicsString);
            ComicResponse savedData = GSON.fromJson(comicsString, ComicResponse.class);
            comics.addAll(savedData.comics);
        }
    }

    public static void createInstance(Context context) {
        Log.d(LOG_TAG, "Creating instance");
        _ = new ComicLoaderService(context);
    }

    public static ComicLoaderService getInstance() {
        return _;
    }

    public synchronized List<Comic> getSavedComics() {
        return comics;
    }

    public synchronized boolean hasSavedComics() {
        return store.contains(COMICS_KEY);
    }

    public synchronized ListenableFuture<List<Comic>> fetchComics() {
        Executors.http.execute(new Runnable() {
            @Override
            public void run() {
                fetchComicsFromServer();
            }
        });

        return Futures.transform(future, new Function<List<Comic>, List<Comic>>() {
            @Override
            public List<Comic> apply(List<Comic> comics) {
                return comics;
            }
        });
    }

    private void fetchComicsFromServer() {
        OkHttpClient client = new OkHttpClient();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(URL)
                .setClient(new OkClient(client))
                .setConverter(new GsonConverter(GSON))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new RestAdapter.Log() {
                    @Override
                    public void log(String message) {
                        Log.d(LOG_TAG, message);
                    }
                })
                .build();
        ComicFetchApi comicFetchApi = restAdapter.create(ComicFetchApi.class);
        comicFetchApi.fetchComics(new Callback<ComicResponse>() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void success(ComicResponse data, retrofit.client.Response response) {
                Log.d(LOG_TAG, response.getBody().toString());
                synchronized (future) {
                    store.edit().putString(COMICS_KEY, GSON.toJson(data)).commit();
                    comics.clear();
                    comics.addAll(data.comics);
                    future.set(data.comics);
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private interface ComicFetchApi {
        @GET("/comics.json")
        void fetchComics(Callback<ComicResponse> callback);
    }

    class ComicResponse {
        List<Comic> comics;

        public ComicResponse(List<Comic> comics) {
            this.comics = comics;
        }
    }
}
