package com.example.images.features.search.data.repository.flickr;

import com.example.images.features.search.data.Paginated;
import com.example.images.features.search.data.repository.ImagesRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Deserializes response received from Flickr API.
 */
public class ResponseDeserializer {

    /**
     * @return deserialized response.
     */
    public Paginated<List<ImagesRepository.Image>> parseResult(String result) throws JSONException {
        JSONObject root = new JSONObject(result);
        JSONObject photos = root.getJSONObject("photos");
        JSONArray photoList = photos.getJSONArray("photo");

        ArrayList<ImagesRepository.Image> images = new ArrayList<>();

        for (int i = 0; i < photoList.length(); i++) {
            JSONObject photoDetails = photoList.getJSONObject(i);

            images.add(
                    new ImagesRepository.Image(
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

        return new Paginated<>(
                photos.getInt("page"),
                photos.getInt("pages"),
                images
        );
    }

}
