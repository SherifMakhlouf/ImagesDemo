package com.example.images.features.search.data.repository.flickr;

import android.support.annotation.NonNull;

import com.example.images.features.search.data.Paginated;
import com.example.images.features.search.data.Result;
import com.example.images.features.search.data.repository.ImagesRepository;
import com.example.images.util.IoUtils;
import com.example.pipe.Pipe;
import com.example.pipe.Source;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

import static com.example.images.features.search.data.Result.error;
import static com.example.images.features.search.data.Result.success;

/**
 * Provides images using Flikr service.
 */
public class FlickrImagesRepository implements ImagesRepository {

    private static final String API_KEY = "3e7cc266ae2b0e0d78e279ce8e361736";
    private static final String URL = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=%s&format=json&nojsoncallback=1&safe_search=1&page=%d&text=%s;";

    private final Executor executor;
    private final ResponseDeserializer responseDeserializer;

    public FlickrImagesRepository(Executor executor, ResponseDeserializer responseDeserializer) {
        this.executor = executor;
        this.responseDeserializer = responseDeserializer;
    }

    @Override
    public Pipe<Result<Paginated<List<Image>>>> queryImages(@NonNull String query,
                                                            int pageNumber) {
        Source<Result<Paginated<List<Image>>>> resultSource = new Source<>();

        executor.execute(() -> {
            java.net.URL url = buildUrl(query, pageNumber);

            try {
                String result = performGetRequest(url);

                resultSource.push(
                        success(responseDeserializer.parseResult(result))
                );
            } catch (IOException | JSONException e) {
                resultSource.push(
                        error(e)
                );
            }
        });

        return Pipe.fromSource(resultSource);
    }

    private String performGetRequest(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            return IoUtils.readToString(in);
        } finally {
            urlConnection.disconnect();
        }
    }

    @NonNull
    private URL buildUrl(String query, int pageNumber) {
        try {
            return new URL(String.format(
                    Locale.getDefault(),
                    URL,
                    API_KEY,
                    pageNumber,
                    URLEncoder.encode(query, "UTF-8")
            ));
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

}
