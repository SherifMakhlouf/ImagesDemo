package com.example.images.features.search.data.repository;

import android.support.annotation.NonNull;

import com.example.images.features.search.data.Paginated;
import com.example.images.features.search.data.Result;
import com.example.images.util.IoUtils;
import com.example.pipe.Pipe;
import com.example.pipe.Source;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

import static com.example.images.features.search.data.Result.error;

/**
 * Provides images using Flikr service.
 */
public class FlickrImagesRepository implements ImagesRepository {

    private static final String API_KEY = "3e7cc266ae2b0e0d78e279ce8e361736";
    private static final String URL = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=%s&format=json&nojsoncallback=1&safe_search=1&page=%d&text=%s;";

    private final ExecutorService executor;

    public FlickrImagesRepository(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public Pipe<Result<Paginated<List<Image>>>> queryImages(@NonNull String query,
                                                            int pageNumber) {
        Source<Result<Paginated<List<Image>>>> resultSource = new Source<>();

        executor.execute(() -> {
            java.net.URL url = buildUrl(query, pageNumber);

            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    String result = IoUtils.readToString(in);

                    resultSource.push(
                            parseResult(result)
                    );
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException | JSONException e) {
                resultSource.push(
                        error(e)
                );
            }
        });

        return Pipe.fromSource(resultSource);
    }

    private Result<Paginated<List<Image>>> parseResult(String result) throws JSONException {
        JSONObject root = new JSONObject(result);
        JSONObject photos = root.getJSONObject("photos");
        JSONArray photoList = photos.getJSONArray("photo");

        ArrayList<Image> images = new ArrayList<>();

        for (int i = 0; i < photoList.length(); i++) {
            JSONObject photoDetails = photoList.getJSONObject(i);

            images.add(
                    new Image(
                            String.format(
                                    Locale.getDefault(),
                                    "http://farm%d.static.flickr.com/%s/%s_%s.jpg",
                                    photoDetails.getInt("farm"),
                                    photoDetails.getString("server"),
                                    photoDetails.getString("id"),
                                    photoDetails.getString("secret")
                            )
                    )
            );
        }

        return Result.success(new Paginated<>(
                photos.getInt("page"),
                photos.getInt("pages"),
                images
        ));
    }

    @NonNull
    private URL buildUrl(String query, int pageNumber) {
        try {
            return new URL(String.format(
                    Locale.getDefault(),
                    URL,
                    API_KEY,
                    pageNumber,
                    query
            ));
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

}
