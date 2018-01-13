package com.example.images.domain;

import android.support.annotation.NonNull;

import com.example.pipe.Pipe;

import java.util.List;

/**
 * Business logic of image search.
 */
public class ImageSearchInteractor {

    /**
     * Searches for images which match given query.
     * <p>
     * Results are being emitted in {@link #searchResults()}.
     */
    public void search(@NonNull String query) {
        throw new UnsupportedOperationException();
    }

    /**
     * Requests new page with results if they are available.
     */
    public void requestNextPage() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return pipe which emits search results for current query. When new page is requested, pipe
     * emits a list with new values being appended at the end.
     */
    public Pipe<List<?>> searchResults() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return pipe which emits {@code true} when more results are available. Emits {@code false} if
     * there are no more results available.
     */
    public Pipe<Boolean> morePagesAvailable() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return pipe which emits {@code true} if results are being loaded. Emits {@code false} if
     * nothing is being loaded at the moment.
     */
    public Pipe<Boolean> loadingResults() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return pipe which emits {@code true} if next page is being loaded. Emits {@code false} if
     * nothing is being loaded at the moment.
     */
    public Pipe<Boolean> loadingNextPage() {
        throw new UnsupportedOperationException();
    }

}
