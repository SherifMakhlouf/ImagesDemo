package com.example.images.features.search.data.repository;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.example.images.features.search.data.Paginated;
import com.example.images.features.search.data.Result;
import com.example.pipe.Pipe;

import java.util.List;

/**
 * Provides access to the images.
 */
public interface ImagesRepository {

    /**
     * Searches for the images which match the given query.
     *
     * @param query      search query.
     * @param pageNumber number of the page to load.
     * @return {@link Pipe} which would emit the value as soon as it is available.
     */
    Pipe<Result<Paginated<List<Image>>>> queryImages(
            @NonNull String query,
            @IntRange(from = 1) int pageNumber
    );

    /**
     * Image which is stored on remote server.
     */
    final class Image {

        /**
         * Url of the image.
         */
        @NonNull
        public final String url;

        public Image(@NonNull String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return "Image{" +
                    "url='" + url + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Image image = (Image) o;

            return url.equals(image.url);
        }

        @Override
        public int hashCode() {
            return url.hashCode();
        }

    }

}
